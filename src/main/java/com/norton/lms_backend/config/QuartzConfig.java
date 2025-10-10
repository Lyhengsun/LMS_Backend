package com.norton.lms_backend.config;

import com.norton.lms_backend.job.ResetLearningStreakJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail resetLearningStreakJobDetail() {
        return JobBuilder.newJob(ResetLearningStreakJob.class)
                .withIdentity("resetLearningStreakJob")
                .withDescription("Reset learning streaks for users who haven't learned today")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger resetLearningStreakTrigger() {
        // Runs every day at 23:59 (11:59 PM)
        return TriggerBuilder.newTrigger()
                .forJob(resetLearningStreakJobDetail())
                .withIdentity("resetLearningStreakTrigger")
                .withDescription("Trigger to reset learning streaks daily at 11:59 PM")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 59 23 * * ?"))
                .build();
    }
}
