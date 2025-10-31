package com.norton.lms_backend.service;

import com.norton.lms_backend.exception.NotFoundException;
import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.CompleteContent;
import com.norton.lms_backend.repository.CompleteContentRepository;
import com.norton.lms_backend.repository.TakeQuizRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.norton.lms_backend.model.dto.response.LeaderboardResponse;
import com.norton.lms_backend.model.dto.response.PagedResponse;
import com.norton.lms_backend.model.dto.response.PaginationInfo;
import com.norton.lms_backend.model.entity.Leaderboard;
import com.norton.lms_backend.repository.LeaderboardRepository;
import com.norton.lms_backend.repository.specification.LeaderboardSpecification;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {
    private final LeaderboardRepository leaderboardRepository;
    private final CompleteContentRepository completeContentRepository;
    private final TakeQuizRepository takeQuizRepository;

    private AppUser getCurrentUser() {
        return (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public PagedResponse<LeaderboardResponse> getAllLeaderboards(Integer page, Integer size, String name) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Specification<Leaderboard> spec = Specification.unrestricted();
        spec = spec.and(LeaderboardSpecification.orderByTotalPointsDesc());

        if (name != null && !name.isEmpty()) {
            spec = spec.and(LeaderboardSpecification.studentNameContains(name));
        }

        Page<Leaderboard> leaderboards = leaderboardRepository.findAll(spec, pageable);

        return PagedResponse.<LeaderboardResponse>builder()
                .items(leaderboards.getContent().stream().map(l -> l.toResponse()).toList())
                .pagination(new PaginationInfo(leaderboards))
                .build();
    }

    @Override
    public LeaderboardResponse getLeaderboardForCurrentStudent() {
        return leaderboardRepository.findByStudent(getCurrentUser()).orElseThrow(() -> new NotFoundException("Student doesn't have a leaderboard")).toResponse();
    }

    @Override
    public void updateLeaderboardCoursePoint() {
        Leaderboard foundLeaderboard = leaderboardRepository.findByStudent(getCurrentUser())
                .orElse(Leaderboard.builder().student(getCurrentUser()).build());

        List<CompleteContent> completeContents = completeContentRepository.findByStudent(getCurrentUser());
        int totalCoursePoints = completeContents.stream().mapToInt(cc -> cc.getCourseContent().getPoints()).sum();
        foundLeaderboard.setCoursePoints(totalCoursePoints);
        leaderboardRepository.save(foundLeaderboard);
    }

    @Override
    public void updateLeaderboardQuizPoint() {
        Leaderboard foundLeaderboard = leaderboardRepository.findByStudent(getCurrentUser())
                .orElse(Leaderboard.builder().student(getCurrentUser()).build());

        foundLeaderboard.setQuizPoints(takeQuizRepository.getTotalHighestScoresByUser(getCurrentUser()));
        leaderboardRepository.save(foundLeaderboard);
    }

    @Override
    public Integer getLeaderboardForCurrentStudentRank() {
        Leaderboard foundLeaderboard = leaderboardRepository.findByStudent(getCurrentUser())
                .orElse(null);
        if (foundLeaderboard == null) {
            leaderboardRepository.saveAndFlush(Leaderboard.builder().student(getCurrentUser()).build());
        }
        return leaderboardRepository.findRankByStudent(getCurrentUser());
    }
}
