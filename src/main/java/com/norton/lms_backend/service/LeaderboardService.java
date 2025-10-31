package com.norton.lms_backend.service;

import com.norton.lms_backend.model.dto.response.LeaderboardResponse;
import com.norton.lms_backend.model.dto.response.PagedResponse;

public interface LeaderboardService {

    PagedResponse<LeaderboardResponse> getAllLeaderboards(Integer page, Integer size, String name);

    LeaderboardResponse getLeaderboardForCurrentStudent();

    void updateLeaderboardCoursePoint();

    void updateLeaderboardQuizPoint();

    Integer getLeaderboardForCurrentStudentRank();
}
