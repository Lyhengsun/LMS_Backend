package com.norton.lms_backend.repository;

import com.norton.lms_backend.model.entity.CoursePayment;
import com.norton.lms_backend.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoursePaymentRepository extends JpaRepository<CoursePayment, Long> {
    Optional<CoursePayment> findByPaymentId(Long paymentId);

    Optional<CoursePayment> findByPayerIdAndCourseId(Long payerId, Long courseId);
}
