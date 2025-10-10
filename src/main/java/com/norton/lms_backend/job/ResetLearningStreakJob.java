package com.norton.lms_backend.job;

import com.norton.lms_backend.model.entity.UserLearningStreak;
import com.norton.lms_backend.repository.UserLearningStreakRespository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResetLearningStreakJob implements Job {
    private final UserLearningStreakRespository userLearningStreaksRepository;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Starting ResetLearningStreakJob at {}", LocalDateTime.now());

        try {
            // Get all learning streaks
            List<UserLearningStreak> allStreaks = userLearningStreaksRepository.findStreaksToReset();
            int resetCount = 1;

            if (allStreaks != null && !allStreaks.isEmpty()) {
                for (UserLearningStreak streak : allStreaks) {
                    // Check if lastDayLearning is on a different day than today
//                LocalDate lastLearningDate = streak.getLastDayLearning().toLocalDate();
//
//                LocalDate today = LocalDate.now();
//                if (!lastLearningDate.isEqual(today)) {
//                    // Reset the streak
//                    streak.setLearningStreakDay(0);
//                    userLearningStreaksRepository.save(streak);
//                    resetCount++;
//
//                    log.debug("Reset learning streak for user ID: {}, last learning date: {}",
//                              streak.getAppUser().getId(), lastLearningDate);
//                }
                    streak.setLearningStreakDay(resetCount);
                    userLearningStreaksRepository.save(streak);
                }

                log.info("ResetLearningStreakJob completed. Reset {} out of {} learning streaks",
                        resetCount, allStreaks.size());
            }
        } catch (Exception e) {
            log.error("Failed to execute ResetLearningStreakJob", e);
            throw new JobExecutionException("Failed to reset learning streaks", e);
        }
    }
}
