package com.norton.lms_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "course_payments", uniqueConstraints = {@UniqueConstraint(columnNames = {"course_id", "payment_id", "payer_id"})})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CoursePayment extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "payer_id", nullable = false)
    private AppUser payer;

    @Column(name = "is_paid", nullable = false)
    private Boolean isPaid;

    @PrePersist
    private void prePersist() {
        if (isPaid == null) {
            isPaid = false;
        }
    }
}
