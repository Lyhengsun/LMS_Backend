package com.norton.lms_backend.repository;

import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.UserLearningStreak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLearningStreakRespository extends JpaRepository<UserLearningStreak, Long> {
    UserLearningStreak findByAppUser(AppUser appUser);

    // Optional: More efficient query to find only streaks that need resetting
    // Corrected query using CAST for LocalDateTime comparison
    @Query("SELECT uls FROM UserLearningStreak uls WHERE CAST(uls.lastDayLearning AS LocalDate) < CURRENT_DATE")
    List<UserLearningStreak> findStreaksToReset();
}
