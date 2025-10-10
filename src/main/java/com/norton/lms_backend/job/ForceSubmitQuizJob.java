package com.norton.lms_backend.job;

import com.norton.lms_backend.model.entity.TakeQuiz;
import com.norton.lms_backend.model.entity.UserAnswer;
import com.norton.lms_backend.repository.AppUserRepository;
import com.norton.lms_backend.repository.LeaderboardRepository;
import com.norton.lms_backend.repository.QuizRepository;
import com.norton.lms_backend.repository.TakeQuizRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ForceSubmitQuizJob implements Job {
    private final TakeQuizRepository takeQuizRepository;
    private final AppUserRepository appUserRepository;
    private final QuizRepository quizRepository;
    private final LeaderboardRepository leaderboardRepository;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        Long userId = dataMap.getLong("userId");
        Long quizId = dataMap.getLong("quizId");

        log.info("Starting ForceSubmitQuizJob for userId: {} and quizId: {}", userId, quizId);

        try {
            // Validate user and quiz exist
            var user = appUserRepository.findById(userId)
                    .orElseThrow(() -> new JobExecutionException("User not found with ID: " + userId));

            var quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new JobExecutionException("Quiz not found with ID: " + quizId));

            // Find the unsubmitted quiz session for this user and quiz
            Optional<TakeQuiz> takeQuizOpt = takeQuizRepository
                    .findByQuizAndUserAndIsSubmitted(quiz, user, false)
                    .stream()
                    .findFirst();

            if (takeQuizOpt.isEmpty()) {
                log.warn("No unsubmitted quiz session found for userId: {} and quizId: {}", userId, quizId);
                return;
            }

            TakeQuiz takeQuiz = takeQuizOpt.get();

            // Calculate score based on existing answers
            int totalScore = 0;
            List<UserAnswer> userAnswers = takeQuiz.getUserAnswer();

            if (userAnswers != null && !userAnswers.isEmpty()) {
                totalScore = userAnswers.stream()
                        .filter(UserAnswer::getIsCorrect)
                        .mapToInt(ua -> ua.getQuestion().getScore())
                        .sum();
            }

            // Force submit the quiz
            takeQuiz.setIsSubmitted(true);
            takeQuiz.setScore(totalScore);
            takeQuizRepository.save(takeQuiz);

            log.info("Quiz session ID: {} force submitted with score: {}", takeQuiz.getId(), totalScore);

            // Update leaderboard
            updateLeaderboard(user);

        } catch (Exception e) {
            log.error("Failed to force submit quiz for userId: {} and quizId: {}", userId, quizId, e);
//            throw new JobExecutionException("Failed to execute ForceSubmitQuizJob", e);
        }
    }

    /**
     * Updates the leaderboard for the given user
     */
    private void updateLeaderboard(com.norton.lms_backend.model.entity.AppUser user) {
        try {
            leaderboardRepository.findByStudent(user).ifPresent(leaderboard -> {
                Integer totalQuizPoints = takeQuizRepository.getTotalHighestScoresByUser(user);
                leaderboard.setQuizPoints(totalQuizPoints);
                leaderboardRepository.save(leaderboard);
                log.debug("Updated leaderboard for user: {}", user.getEmail());
            });
        } catch (Exception e) {
            log.error("Failed to update leaderboard for user: {}", user.getEmail(), e);
        }
    }
}
