package com.norton.lms_backend.repository;

import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.CoursePayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoursePaymentRepository extends JpaRepository<CoursePayment, Long> {
    Optional<CoursePayment> findByPaymentId(Long paymentId);

    Optional<CoursePayment> findByPayerIdAndCourseId(Long payerId, Long courseId);

    Page<CoursePayment> findByPayer(AppUser payer, Pageable pageable);
}
