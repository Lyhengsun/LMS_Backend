package com.norton.lms_backend.model.dto.response;

import com.norton.lms_backend.model.entity.Payment;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CoursePaymentResponse extends BaseEntityResponse {
    private String courseName;
    private Boolean isPaid;
    private AppUserResponse payer;
    private Payment payment;
}
