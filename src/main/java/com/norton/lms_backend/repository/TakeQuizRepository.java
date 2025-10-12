package com.norton.lms_backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.norton.lms_backend.model.entity.TakeQuiz;
import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.Quiz;

public interface TakeQuizRepository extends JpaRepository<TakeQuiz, Long> {
    List<TakeQuiz> findByQuizAndUser(Quiz quiz, AppUser user);

    List<TakeQuiz> findByQuizAndUserAndIsSubmitted(Quiz quiz, AppUser user, Boolean isSubmitted);

    @Query("""
        SELECT t FROM TakeQuiz t
        WHERE t.user = :user
        AND t.id IN (
            SELECT MAX(t2.id) FROM TakeQuiz t2
            WHERE t2.user = :user
            AND t2.score = (
                SELECT MAX(t3.score) FROM TakeQuiz t3
                WHERE t3.user = :user
                AND t3.quiz = t2.quiz
            )
            GROUP BY t2.quiz
        )
        ORDER BY t.quiz.id
        """)
    Page<TakeQuiz> findHighestScoresByUser(@Param("user") AppUser user, Pageable pageable);

    @Query("""
            SELECT t FROM TakeQuiz t
            WHERE t.user = :user
            AND t.quiz = :quiz
            AND t.score = (
                SELECT MAX(t2.score) FROM TakeQuiz t2
                WHERE t2.user = :user
                AND t2.quiz = :quiz
            )
            ORDER BY t.id DESC
            """)
    TakeQuiz findHighestScoreByUserAndQuiz(
            @Param("user") AppUser user,
            @Param("quiz") Quiz quiz
    );

    @Query("""
            SELECT COALESCE(SUM(maxScores.maxScore), 0) FROM (
                SELECT MAX(t.score) as maxScore
                FROM TakeQuiz t
                WHERE t.user = :user
                GROUP BY t.quiz
            ) maxScores
            """)
    Integer getTotalHighestScoresByUser(@Param("user") AppUser user);

    Integer countTakeQuizByUserAndQuiz(AppUser user, Quiz quiz);

    Integer countTakeQuizByUserId(Long userId);

    @Query("""
            SELECT AVG(
                CAST(t.score AS double) * 100.0 / 
                COALESCE(
                    (SELECT SUM(q.score) 
                     FROM Question q 
                     WHERE q.quiz = t.quiz), 
                    1
                )
            )
            FROM TakeQuiz t
            WHERE t.user = :user
            AND t.id IN (
                SELECT t2.id FROM TakeQuiz t2
                WHERE t2.user = :user
                AND t2.quiz = t.quiz
                AND t2.score = (
                    SELECT MAX(t3.score) FROM TakeQuiz t3
                    WHERE t3.user = :user
                    AND t3.quiz = t.quiz
                )
            )
            """)
    Double getAverageQuizProgressByUser(@Param("user") AppUser user);

    @Query("""
            SELECT AVG(
                CAST(t.score AS double) * 100.0 / 
                COALESCE(
                    (SELECT SUM(q.score) 
                     FROM Question q 
                     WHERE q.quiz = t.quiz), 
                    1
                )
            )
            FROM TakeQuiz t
            WHERE t.user = :user
            AND EXTRACT(YEAR FROM t.createdAt) = :year
            AND EXTRACT(MONTH FROM t.createdAt) = :month
            """)
    Double getAverageQuizProgressByMonthAndYear(
            @Param("user") AppUser user,
            @Param("month") Integer month,
            @Param("year") Integer year
    );

    TakeQuiz getTop1ByUserAndQuizOrderByCreatedAtDesc(AppUser user, Quiz quiz);

    TakeQuiz getTop1ByUserAndQuizOrderByScoreDesc(AppUser user, Quiz quiz);

    Integer countByQuizAuthorId(Long authorId);

    @Query("""
            SELECT COUNT(t)
            FROM TakeQuiz t
            WHERE t.quiz.author.id = :authorId
            AND CAST(t.createdAt AS date) = CURRENT_DATE
            """)
    Integer countTakeQuizCreatedTodayByQuizAuthor(@Param("authorId") Long authorId);

    @Query("""
            SELECT COUNT(t)
            FROM TakeQuiz t
            WHERE t.quiz.author.id = :authorId
            AND t.isSubmitted = true
            AND CAST(t.score AS double) > (
                SELECT SUM(q.score) * 0.5
                FROM Question q
                WHERE q.quiz = t.quiz
            )
            """)
    Integer countPassedQuizAttemptsByAuthor(@Param("authorId") Long authorId);

    @Query("""
            SELECT COUNT(t)
            FROM TakeQuiz t
            WHERE t.quiz.author.id = :authorId
            AND t.isSubmitted = true
            AND CAST(t.score AS double) <= (
                SELECT SUM(q.score) * 0.5
                FROM Question q
                WHERE q.quiz = t.quiz
            )
            """)
    Integer countFailedQuizAttemptsByAuthor(@Param("authorId") Long authorId);

    @Query("""
            SELECT t
            FROM TakeQuiz t
            WHERE t.quiz.author.id = :authorId
            AND t.createdAt >= :fromDate
            ORDER BY t.createdAt ASC
            """)
    List<TakeQuiz> findQuizAttemptsByAuthorFromDate(
            @Param("authorId") Long authorId,
            @Param("fromDate") java.time.LocalDateTime fromDate
    );

}
