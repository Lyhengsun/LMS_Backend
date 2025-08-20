package com.norton.lms_backend.service;

public interface EmailSenderService {
    void sendEmail(String toEmail, String otp);
}
