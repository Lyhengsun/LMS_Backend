package com.norton.lms_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.norton.lms_backend.model.dto.response.ApiResponse;
import com.norton.lms_backend.model.dto.response.LeaderboardResponse;
import com.norton.lms_backend.model.dto.response.PagedResponse;
import com.norton.lms_backend.service.LeaderboardService;
import com.norton.lms_backend.utils.ResponseUtils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/leaderboards")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<LeaderboardResponse>>> getAllLeaderboards(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestParam(required = false) String name
            ) {
        return ResponseUtils.createResponse("Fetch leaderboard successfully",
                leaderboardService.getAllLeaderboards(page, size, name));
    }

    @GetMapping("/current-student")
    public ResponseEntity<ApiResponse<LeaderboardResponse>> getLeaderboardForCurrentStudent() {
        return ResponseUtils.createResponse("Fetch leaderboard for current user successfully", leaderboardService.getLeaderboardForCurrentStudent());
    }
}
