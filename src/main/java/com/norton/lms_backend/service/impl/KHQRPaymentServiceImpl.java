package com.norton.lms_backend.service.impl;

import com.norton.lms_backend.exception.BadRequestException;
import com.norton.lms_backend.exception.NotFoundException;
import com.norton.lms_backend.model.dto.request.BakongAccountRequest;
import com.norton.lms_backend.model.dto.response.*;
import com.norton.lms_backend.model.entity.*;
import com.norton.lms_backend.model.enumeration.CourseAvailability;
import com.norton.lms_backend.model.enumeration.PaymentStatus;
import com.norton.lms_backend.repository.CoursePaymentRepository;
import com.norton.lms_backend.repository.CourseRepository;
import com.norton.lms_backend.repository.PaymentRepository;
import com.norton.lms_backend.service.KHQRPaymentService;
import com.norton.lms_backend.utils.SequentialBillNumberGenerator;
import kh.org.nbc.bakong_khqr.BakongKHQR;
import kh.org.nbc.bakong_khqr.model.IndividualInfo;
import kh.org.nbc.bakong_khqr.model.KHQRCurrency;
import kh.org.nbc.bakong_khqr.model.KHQRData;
import kh.org.nbc.bakong_khqr.model.KHQRResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class KHQRPaymentServiceImpl implements KHQRPaymentService {
    @Qualifier("verifyBakongAccountWebClient")
    private final WebClient verifyBakongAccountWebClient;
    private final RestTemplate restTemplate;
    private final CourseRepository courseRepository;
    private final PaymentRepository paymentRepository;
    private final CoursePaymentRepository coursePaymentRepository;

    @Value("${bakong.api.base-url}")
    private String bakongApiBaseUrl;

    @Value("${bakong.api.token}")
    private String bakongApiToken;

    private AppUser getCurrentUser() {
        return (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public PaymentResponse generateCoursePayment(Long courseId) {
        Course foundCourse = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("course not found"));
        if (foundCourse.getCourseAvailability().equals(CourseAvailability.FREE)) {
            throw new BadRequestException("You can't pay for this course");
        }
        AppUser payer = getCurrentUser();
        AppUser author = foundCourse.getAuthor();

        log.info("Generating KHQR for amount: {} {}", foundCourse.getPrice(), "USD");

        // Create IndividualInfo for KHQR SDK
        IndividualInfo individualInfo = new IndividualInfo();
        individualInfo.setBakongAccountId(author.getBackongAccountId());
        individualInfo.setMerchantName(author.getFullName());
//        individualInfo.setMerchantCity(merchantCity);
        individualInfo.setAmount(foundCourse.getPrice().doubleValue());
        individualInfo.setCurrency(KHQRCurrency.USD);

        String generatedBillNumber = SequentialBillNumberGenerator.generateChronologicalBillNumber();
        individualInfo.setBillNumber(generatedBillNumber);

        if (author.getPhoneNumber() != null) {
            individualInfo.setMobileNumber(author.getPhoneNumber());
        }

        // Generate KHQR using SDK
        KHQRResponse<KHQRData> khqrResponse = BakongKHQR.generateIndividual(individualInfo);

        if (khqrResponse.getKHQRStatus().getCode() != 0) {
            log.error("Failed to generate KHQR: {}", khqrResponse.getKHQRStatus().getMessage());
            throw new RuntimeException("Failed to generate KHQR: " + khqrResponse.getKHQRStatus().getMessage());
        }

        KHQRData khqrData = khqrResponse.getData();

        // Save payment to database
        Payment payment = new Payment();
        payment.setAmount(foundCourse.getPrice());
        payment.setCurrency("USD");
        payment.setKhqrString(khqrData.getQr());
        payment.setMd5Hash(khqrData.getMd5());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setMerchantName(author.getFullName());
        payment.setBillNumber(generatedBillNumber);
        payment.setTo(author);
        payment.setFrom(payer);

        payment = paymentRepository.saveAndFlush(payment);

        CoursePayment coursePayment = CoursePayment.builder()
                .course(foundCourse)
                .payment(payment)
                .payer(payer)
                .isPaid(false)
                .build();

        CoursePayment foundCoursePayment = coursePaymentRepository.findByPayerIdAndCourseId(payer.getId(), foundCourse.getId()).orElse(null);
        if (foundCoursePayment != null) {
            coursePaymentRepository.delete(foundCoursePayment);
            coursePaymentRepository.flush();
        }
        coursePaymentRepository.save(coursePayment);

        log.info("Payment created with transaction ID: {}", payment.getTransactionId());

        return PaymentResponse.builder()
                .transactionId(payment.getTransactionId())
                .khqrString(payment.getKhqrString())
                .md5Hash(payment.getMd5Hash())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .merchantName(payment.getMerchantName())
                .billNumber(payment.getBillNumber())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    @Override
    public PaymentStatusResponse checkPaymentStatusByTransactionId(UUID transactionId) {
        log.info("Checking status for transaction: {}", transactionId);

        Payment payment = paymentRepository.findByTransactionId(transactionId.toString())
                .orElseThrow(() -> new RuntimeException("Payment not found: " + transactionId));
        CoursePayment coursePayment = coursePaymentRepository.findByPaymentId(payment.getId()).orElse(null);

        if (bakongApiToken == null || bakongApiToken.isEmpty()) {
            log.warn("Bakong API token not configured. Cannot check transaction status.");
            return PaymentStatusResponse.builder()
                    .status("UNKNOWN")
                    .hash(payment.getMd5Hash())
                    .amount(payment.getAmount())
                    .currency(payment.getCurrency())
                    .description("Bakong API token not configured. Please see README for setup instructions.")
                    .build();
        }

        try {
            // Call Bakong API to check transaction status
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + bakongApiToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("md5", payment.getMd5Hash());

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            String url = bakongApiBaseUrl + "/v1/check_transaction_by_md5";

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null) {
                Integer responseCode = (Integer) responseBody.get("responseCode");

                if (responseCode != null && responseCode == 0) {
                    // Transaction successful
                    Map<String, Object> data = (Map<String, Object>) responseBody.get("data");

                    payment.setStatus(PaymentStatus.COMPLETED);
                    paymentRepository.save(payment);

                    if (coursePayment != null) {
                        coursePayment.setIsPaid(true);
                        coursePaymentRepository.save(coursePayment);
                    }

                    log.info("Transaction completed: {}", transactionId);

                    return PaymentStatusResponse.builder()
                            .status("COMPLETED")
                            .hash((String) data.get("hash"))
                            .fromAccountId((String) data.get("fromAccountId"))
                            .toAccountId((String) data.get("toAccountId"))
                            .currency((String) data.get("currency"))
                            .amount(payment.getAmount())
                            .description((String) data.get("description"))
                            .build();
                } else {
                    // Transaction failed or not found
                    Integer errorCode = (Integer) responseBody.get("errorCode");
                    String message = (String) responseBody.get("responseMessage");

                    if (errorCode != null && errorCode == 1) {
                        // Not found yet
                        log.info("Transaction not found yet: {}", transactionId);
                        return PaymentStatusResponse.builder()
                                .status("PENDING")
                                .hash(payment.getMd5Hash())
                                .amount(payment.getAmount())
                                .currency(payment.getCurrency())
                                .description("Transaction not found. Payment may still be pending.")
                                .build();
                    } else if (errorCode != null && errorCode == 3) {
                        // Failed
                        payment.setStatus(PaymentStatus.FAILED);
                        paymentRepository.save(payment);

                        log.error("Transaction failed: {}", transactionId);
                        return PaymentStatusResponse.builder()
                                .status("FAILED")
                                .hash(payment.getMd5Hash())
                                .amount(payment.getAmount())
                                .currency(payment.getCurrency())
                                .description(message != null ? message : "Transaction failed")
                                .build();
                    }
                }
            }

            return PaymentStatusResponse.builder()
                    .status("PENDING")
                    .hash(payment.getMd5Hash())
                    .amount(payment.getAmount())
                    .currency(payment.getCurrency())
                    .description("Status check returned unexpected response")
                    .build();

        } catch (Exception e) {
            log.error("Error checking transaction status: {}", e.getMessage(), e);
            return PaymentStatusResponse.builder()
                    .status("ERROR")
                    .hash(payment.getMd5Hash())
                    .amount(payment.getAmount())
                    .currency(payment.getCurrency())
                    .description("Error checking status: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public BakongAccountResponse verifyBakongAccountId(String bakongAccountId) {
        return verifyAccount(bakongAccountId).block();
    }

    @Override
    public PagedResponse<CoursePaymentResponse> fetchPaymentByRole(Integer page, Integer size) {
        AppUser currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page - 1, size);

        return null;
    }

    public Mono<BakongAccountResponse> verifyAccount(String accountId) {
        // The API likely expects a POST request with the account ID in the body
        // The exact JSON structure can be found in the official documentation
        BakongAccountRequest request = new BakongAccountRequest(accountId);

        return verifyBakongAccountWebClient.post()
                .body(Mono.just(request), BakongAccountRequest.class)
                .retrieve()
                .bodyToMono(BakongAccountResponse.class);
    }
}
