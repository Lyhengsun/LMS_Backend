package com.norton.lms_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.Leaderboard;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long>, JpaSpecificationExecutor<Leaderboard> {
    Optional<Leaderboard> findByStudent(AppUser student);
}
