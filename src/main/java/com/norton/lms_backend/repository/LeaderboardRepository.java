package com.norton.lms_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.Leaderboard;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long>, JpaSpecificationExecutor<Leaderboard> {
    Optional<Leaderboard> findByStudent(AppUser student);

    @Query("SELECT DENSE_RANK() OVER (ORDER BY (l.quizPoints + l.coursePoints + l.assignmentPoints) DESC) " +
            "FROM Leaderboard l " +
            "WHERE l.student = :student")
    Integer findRankByStudent(@Param("student") AppUser student);
}
