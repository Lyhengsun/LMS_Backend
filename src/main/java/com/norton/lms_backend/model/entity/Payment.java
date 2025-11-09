package com.norton.lms_backend.model.entity;

import com.norton.lms_backend.model.enumeration.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(name = "khqr_string",nullable = false, columnDefinition = "TEXT")
    private String khqrString;

    @Column(name = "md5_hash",nullable = false, columnDefinition = "TEXT")
    private String md5Hash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "merchant_name",nullable = false, columnDefinition = "TEXT")
    private String merchantName;

    @Column(name = "bill_number",nullable = false)
    private String billNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_id", nullable = false)
    private AppUser from;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_id", nullable = false)
    private AppUser to;

    @PrePersist
    protected void onCreate() {
        if (transactionId == null) {
            transactionId = UUID.randomUUID().toString();
        }
    }
}
