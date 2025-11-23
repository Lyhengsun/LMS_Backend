package com.norton.lms_backend.service.impl;

import com.norton.lms_backend.exception.BadRequestException;
import com.norton.lms_backend.exception.NotFoundException;
import com.norton.lms_backend.model.dto.response.*;
import com.norton.lms_backend.model.entity.*;
import com.norton.lms_backend.repository.*;
import com.norton.lms_backend.service.CourseService;
import com.norton.lms_backend.service.DashboardService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final JoinCourseRepository joinCourseRepository;
    private final CompleteContentRepository completeContentRepository;
    private final TakeQuizRepository takeQuizRepository;
    private final QuizRepository quizRepository;
    private final UserLearningStreakRespository userLearningStreakRespository;
    private final CourseService courseService;
    private final CourseDraftRepository courseDraftRepository;
    private final CourseRepository courseRepository;


    private AppUser getCurrentUser() {
        return (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public StudentSummaryStatResponse getStudentSummaryStat() {
        // Get average course progress
        Double avgProgress = completeContentRepository.findAverageCourseProgressByStudentId(getCurrentUser().getId());
        Double avgQuizPerformance = takeQuizRepository.getAverageQuizProgressByUser(getCurrentUser());

        Integer enrolledCourse = joinCourseRepository.countJoinCourseByStudentId(getCurrentUser().getId());
        Integer courseProgress = avgProgress != null ? avgProgress.intValue() : 0;
        Integer quizzesTaken = takeQuizRepository.countTakeQuizByUserId(getCurrentUser().getId());
        Integer quizPerformance = avgQuizPerformance != null ? avgQuizPerformance.intValue() : 0;

        return StudentSummaryStatResponse.builder()
                .enrolledCourses(enrolledCourse)
                .courseProgress(courseProgress)
                .quizzesTaken(quizzesTaken)
                .quizPerformance(quizPerformance)
                .build();
    }

    @Override
    public StudentLearningInsightResponse getStudentLearningInsight() {
        UserLearningStreak foundUserLearningStreak = userLearningStreakRespository.findByAppUser(getCurrentUser());

        LocalDate today = LocalDate.now();
        LocalDate currentMonday = today.with(DayOfWeek.MONDAY);
        LocalDate lastMonth = today.minusMonths(1);
        Double lastMonthQuizProgress = takeQuizRepository.getAverageQuizProgressByMonthAndYear(getCurrentUser(), lastMonth.getMonthValue(), lastMonth.getYear());
        int lastMonthQuizProgressInt = lastMonthQuizProgress != null ? lastMonthQuizProgress.intValue() : 0;
        Double currentMonthQuizProgress = takeQuizRepository.getAverageQuizProgressByMonthAndYear(getCurrentUser(), today.getMonthValue(), today.getYear());
        int currentMonthQuizProgressInt = currentMonthQuizProgress != null ? currentMonthQuizProgress.intValue() : 0;

        int lessonsThisWeek = completeContentRepository.countCompletedContentByStudent(getCurrentUser().getId(), currentMonday);
        int quizImprovement = currentMonthQuizProgressInt - lastMonthQuizProgressInt;
        int streakDays = foundUserLearningStreak != null ? foundUserLearningStreak.getLearningStreakDay() : 1;
        return StudentLearningInsightResponse.builder()
                .lessonsThisWeek(lessonsThisWeek)
                .quizImprovement(quizImprovement)
                .streakDays(streakDays)
                .build();
    }

    @Override
    public PagedResponse<ContinueLearningResponse> getStudentContinueLearnings(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<JoinCourse> joinCourses = joinCourseRepository.findByStudentAndIsCompleted(getCurrentUser(), false, pageable);
        List<CourseProgressResponse> courseProgressResponseList = joinCourses.stream().map(jc -> courseService.getCourseProgressByCourseId(jc.getCourse().getId())).toList();

        List<ContinueLearningResponse> continueLearningResponseList = courseProgressResponseList.stream().map(c -> {
            return ContinueLearningResponse.builder()
                    .courseId(c.getCourse().getId())
                    .courseName(c.getCourse().getCourseName())
                    .categoryName(c.getCourse().getCategory().getName())
                    .progress(c.getCompletedCourseContentCount() * 100 / c.getMaxCourseContentCount())
                    .timeRemainingInMinutes(c.getContentProgresses().stream().filter(cp -> !cp.getCompleted()).map(CourseContentResponse::getDurationMinutes).reduce(0, Integer::sum))
                    .thumbnailUrl(c.getCourse().getCourseImageName())
                    .nextContent(c.getContentProgresses().stream().filter(cp -> !cp.getCompleted()).findFirst().orElse(null))
                    .build();
        }).toList();

        return PagedResponse.<ContinueLearningResponse>builder()
                .items(continueLearningResponseList)
                .pagination(new PaginationInfo(joinCourses))
                .build();

    }

    private QuizOverviewResponse mapToQuizOverviewResponse(TakeQuiz takeQuiz) {
        QuizResultResponse quizResultResponse = takeQuiz.toQuizResultResponse(takeQuizRepository.countTakeQuizByUserAndQuiz(getCurrentUser(), takeQuiz.getQuiz()));
        return QuizOverviewResponse.builder()
                .quizId(quizResultResponse.getQuizId())
                .quizName(quizResultResponse.getQuizName())
                .categoryName(quizResultResponse.getCategoryName())
                .bestScore(takeQuiz.getScore())
                .maxScore(quizResultResponse.getMaxScore())
                .attempts(quizResultResponse.getAttemptNumber())
                .maxAttempts(takeQuiz.getQuiz().getMaxAttempts())
                .build(); }

    @Override
    public PagedResponse<QuizOverviewResponse> getStudentQuizOverview(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<TakeQuiz> takeQuizzes = takeQuizRepository.findHighestScoresByUser(getCurrentUser(), pageable);

        return PagedResponse.<QuizOverviewResponse>builder()
                .items(takeQuizzes.stream().map(this::mapToQuizOverviewResponse).toList())
                .pagination(new PaginationInfo(takeQuizzes))
                .build();
    }

    @Override
    public InstructorStatsResponse getInstructorSummaryStat() {
        Integer myCourses = courseDraftRepository.countByAuthorId(getCurrentUser().getId());
        Integer coursesPublished = courseDraftRepository.countByAuthorIdAndIsApproved(getCurrentUser().getId(), true);
        Integer coursesDraft = courseDraftRepository.countByAuthorIdAndIsApproved(getCurrentUser().getId(), false);
        Integer coursesPending = courseDraftRepository.countByAuthorIdAndIsApprovedAndIsSubmitted(getCurrentUser().getId(), false, true);
        Integer quizzesCreated = quizRepository.countByAuthorId(getCurrentUser().getId());
        Integer quizTotalAttempts = takeQuizRepository.countByQuizAuthorId(getCurrentUser().getId());
        Integer quizzesAttemptedToday = takeQuizRepository.countTakeQuizCreatedTodayByQuizAuthor(getCurrentUser().getId());

        LocalDate currentMonday = LocalDate.now().with(DayOfWeek.MONDAY);
        Integer activeStudentsThisWeek = joinCourseRepository.countDistinctStudentsByAuthorFromDate(getCurrentUser().getId(), currentMonday);

        return InstructorStatsResponse.builder()
                .myCourses(myCourses)
                .coursesPublished(coursesPublished)
                .coursesDraft(coursesDraft)
                .coursesPending(coursesPending)
                .quizzesCreated(quizzesCreated)
                .quizTotalAttempts(quizTotalAttempts)
                .quizzesAttemptedToday(quizzesAttemptedToday)
                .activeStudentsThisWeek(activeStudentsThisWeek)
                .build();
    }

    private InstructorDashboardCourseResponse mapToInstructorDashboardCourseResponse(CourseDraft courseDraft) {
        String status = "Draft";
        if (courseDraft.getIsApproved()) {
            status = "Published";
        } else if (courseDraft.getIsSubmitted()) {
            status = "Pending Approval";
        } else if (courseDraft.getIsRejected()) {
            status = "Rejected";
        }

        Course foundPublishedCourse = null;
        if (courseDraft.getIsApproved()) {
            foundPublishedCourse = courseRepository.findByCourseDraft(courseDraft);
        }

        int enrollmentCount = 0;
        if (foundPublishedCourse != null) {
            enrollmentCount = joinCourseRepository.countJoinCourseByCourseId(foundPublishedCourse.getId());
        }

        return InstructorDashboardCourseResponse.builder()
                .courseId(courseDraft.getId())
                .courseName(courseDraft.getCourseName())
                .categoryName(courseDraft.getCategory().getName())
                .status(status)
                .enrollmentCount(enrollmentCount)
                .thumbnailUrl(courseDraft.getCourseImageName())
                .createdAt(courseDraft.getCreatedAt())
                .updatedAt(courseDraft.getEditedAt())
                .build();
    }

    @Override
    public PagedResponse<InstructorDashboardCourseResponse> getInstructorCoursesForDashboard(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CourseDraft> courses = courseDraftRepository.findAllByAuthorId(getCurrentUser().getId(), pageable);
        return PagedResponse.<InstructorDashboardCourseResponse>builder()
                .items(courses.stream().map(this::mapToInstructorDashboardCourseResponse).toList())
                .pagination(new PaginationInfo(courses))
                .build();
    }

    @Override
    public CourseStatusDistributionResponse getCourseStatusDistribution() {
        Integer published = courseDraftRepository.countByAuthorIdAndIsApproved(getCurrentUser().getId(), true);
        Integer pending = courseDraftRepository.countByAuthorIdAndIsApprovedAndIsSubmitted(getCurrentUser().getId(), false, true);
        Integer rejected = courseDraftRepository.countByAuthorIdAndIsRejected(getCurrentUser().getId(), true);
        Integer draft = courseDraftRepository.countByAuthorIdAndIsApproved(getCurrentUser().getId(), false) - pending - rejected;

        return CourseStatusDistributionResponse.builder()
                .published(published)
                .draft(draft)
                .pending(pending)
                .rejected(rejected)
                .build();
    }

    @Override
    public QuizPerformanceDistributionResponse getQuizPerformanceDistribution() {
        Integer passed = takeQuizRepository.countPassedQuizAttemptsByAuthor(getCurrentUser().getId());
        Integer failed = takeQuizRepository.countFailedQuizAttemptsByAuthor(getCurrentUser().getId());

        return QuizPerformanceDistributionResponse.builder()
                .passed(passed)
                .failed(failed)
                .build();
    }

    @Override
    public QuizAttemptsOverTimeResponse getQuizAttemptsOverTime(Integer days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        java.time.LocalDateTime startDateTime = startDate.atStartOfDay();

        List<TakeQuiz> attempts = takeQuizRepository.findQuizAttemptsByAuthorFromDate(
                getCurrentUser().getId(),
                startDateTime
        );

        // Group attempts by date
        java.util.Map<LocalDate, Long> attemptsByDate = attempts.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        takeQuiz -> takeQuiz.getCreatedAt().toLocalDate(),
                        java.util.stream.Collectors.counting()
                ));

        // Create list with all dates in range, filling in zeros for missing dates
        List<QuizAttemptsOverTimeResponse.DailyAttempt> dailyAttempts = new java.util.ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Long count = attemptsByDate.getOrDefault(date, 0L);
            dailyAttempts.add(QuizAttemptsOverTimeResponse.DailyAttempt.builder()
                    .date(date)
                    .count(count.intValue())
                    .build());
        }

        return QuizAttemptsOverTimeResponse.builder()
                .dailyAttempts(dailyAttempts)
                .build();
    }

    @Override
    public PagedResponse<StudentCourseProgressResponse> getStudentCourseProgressesByCourseId(Long courseId, Integer page, Integer size, String name) {
        Course foundCourse = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("course not found"));
        if (!foundCourse.getAuthor().getId().equals(getCurrentUser().getId())) {
            throw new BadRequestException("You are not the author of the course with ID: " + courseId);
        }
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<JoinCourse> joinCourses = null;
        if (name != null) {
            joinCourses = joinCourseRepository.findByCourseAndStudentFullNameContaining(foundCourse, name, pageable);
        } else {
            joinCourses = joinCourseRepository.findByCourse(foundCourse, pageable);
        }

        List<StudentCourseProgressResponse> studentCourseProgressResponseList = new ArrayList<>();
        List<CourseContent> courseContents = foundCourse.getContents();
        for (JoinCourse joinCourse : joinCourses) {
            AppUser student = joinCourse.getStudent();
            List<CompleteContent> completeContents = completeContentRepository.findByStudentAndCourseContentCourse(student, foundCourse);
            int progressInPercentage = (completeContents.size() / courseContents.size()) * 100;

            StudentCourseProgressResponse studentCourseProgressResponse = StudentCourseProgressResponse.builder()
                    .id(student.getId())
                    .fullName(student.getFullName())
                    .email(student.getEmail())
                    .progressInPercentage(progressInPercentage)
                    .enrolledDate(joinCourse.getCreatedAt().toLocalDate())
                    .build();
            studentCourseProgressResponseList.add(studentCourseProgressResponse);
        }

        PaginationInfo paginationInfo =  new PaginationInfo(joinCourses);
        return PagedResponse.<StudentCourseProgressResponse>builder()
                .items( studentCourseProgressResponseList)
                .pagination(paginationInfo)
                .build();
    }
}
