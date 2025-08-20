package com.norton.lms_backend.service.impl;

import com.norton.lms_backend.service.EmailSenderService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RequiredArgsConstructor
@Service
public class EmailSenderServiceImpl implements EmailSenderService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @SneakyThrows
    @Override
    public void sendEmail(String toEmail, String otp) {
        Context context = new Context();
        context.setVariable("otp", otp);
        String process = templateEngine.process("index", context);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setFrom(fromEmail);
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setSubject("Email verification OTP code");
        mimeMessageHelper.setText(process, true);

        javaMailSender.send(mimeMessage);
    }
}
