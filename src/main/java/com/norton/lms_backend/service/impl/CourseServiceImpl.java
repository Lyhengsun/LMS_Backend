package com.norton.lms_backend.service.impl;

import com.norton.lms_backend.exception.BadRequestException;
import com.norton.lms_backend.exception.NotFoundException;
import com.norton.lms_backend.model.dto.request.CourseContentRequest;
import com.norton.lms_backend.model.dto.request.CourseRequest;
import com.norton.lms_backend.model.dto.response.CourseContentProgressResponse;
import com.norton.lms_backend.model.dto.response.CourseContentResponse;
import com.norton.lms_backend.model.dto.response.CourseDraftResponse;
import com.norton.lms_backend.model.dto.response.CourseNoContentResponse;
import com.norton.lms_backend.model.dto.response.CourseProgressResponse;
import com.norton.lms_backend.model.dto.response.CourseResponse;
import com.norton.lms_backend.model.dto.response.PagedResponse;
import com.norton.lms_backend.model.dto.response.PaginationInfo;
import com.norton.lms_backend.model.entity.*;
import com.norton.lms_backend.model.enumeration.CourseLevel;
import com.norton.lms_backend.model.enumeration.CourseProperty;
import com.norton.lms_backend.repository.*;
import com.norton.lms_backend.repository.specification.CourseDraftSpecification;
import com.norton.lms_backend.repository.specification.CourseSpecification;
import com.norton.lms_backend.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final CourseContentRepository courseContentRepository;
    private final CourseDraftRepository courseDraftRepository;
    private final JoinCourseRepository joinCourseRepository;
    private final CompleteContentRepository completeContentRepository;
    private final LeaderboardRepository leaderboardRepository;
    private final UserLearningStreakRespository userLearningStreakRespository;

    private AppUser getCurrentUser() {
        return (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private Course findCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course with id " + id + " not found"));
    }

    private CourseDraft findCourseDraftById(Long id) {
        return courseDraftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course draft with id: " + id + " not found"));
    }

    private CourseContent findCourseContentById(Long id) {
        return courseContentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course content with id: " + id + " not found"));
    }

    private CourseResponse mapToCourseResponse(Course course) {
        CourseResponse courseResponse = course.toResponse();
        courseResponse.setStudentEnrolled(joinCourseRepository.countJoinCourseByCourseId(course.getId()));
        return courseResponse;
    }

    @Override
    public CourseDraftResponse createCourse(CourseRequest courseRequest) {
        CourseDraft course = courseRequest.toEntityDraft();
        Category category = categoryRepository.findById(courseRequest.getCourseCategoryId()).orElseThrow(
                () -> new NotFoundException("Category with id " + courseRequest.getCourseCategoryId() + " not found"));
        course.setCategory(category);
        course.setAuthor(getCurrentUser());
        return courseDraftRepository.save(course).toResponse();
    }

    @Override
    public CourseResponse getCourseById(Long id) {

        return mapToCourseResponse(courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course with id " + id + " not found")));
    }

    @Override
    public PagedResponse<CourseResponse> getAllCourses(String name, Long categoryId, CourseLevel level,
                                                       CourseProperty courseProperty,
                                                       Direction direction,
                                                       Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, courseProperty.getValue()));

        if (categoryId != null) {
            categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BadRequestException("Category with ID: " + categoryId + " doesn't exist"));
        }

        Specification<Course> spec = Specification.unrestricted();
        spec = spec.and(CourseSpecification.fetchContents()).and(CourseSpecification.isPublic(true));

        if (name != null && !name.isEmpty()) {
            spec = spec.and(CourseSpecification.courseNameContains(name));
        }

        if (categoryId != null) {
            spec = spec.and(CourseSpecification.hasCategoryId(categoryId));
        }

        if (level != null) {
            spec = spec.and(CourseSpecification.hasLevel(level));
        }

        Page<Course> courses = courseRepository.findAll(spec, pageable);

        return PagedResponse.<CourseResponse>builder()
                .items(courses.getContent().stream().map(this::mapToCourseResponse).toList())
                .pagination(new PaginationInfo(courses))
                .build();
    }

    @Override
    public CourseResponse updateCourse(Long id, CourseRequest courseRequest) {
        Course course = findCourseById(id);
        course.setCourseName(course.getCourseName());
        course.setCourseDescription(course.getCourseDescription());
        course.setLevel(courseRequest.getLevel());
        return courseRepository.save(course).toResponse();
    }

    @Override
    public void deleteCourse(Long id) {
        CourseDraft courseDraft = findCourseDraftById(id);
        if (!courseDraft.getAuthor().getId().equals(getCurrentUser().getId())) {
            throw new BadRequestException("You don't have permission to delete this course");
        }

        Course course = courseRepository.findByCourseDraft(courseDraft);

        if (course != null) {
            if (!course.getAuthor().getId().equals(getCurrentUser().getId())) {
                throw new BadRequestException("You don't have permission to delete this course");
            }
            courseRepository.delete(course);
        }

        if (course == null) {
            courseContentRepository.deleteAllByCourseDraftId(courseDraft.getId());
        }

        courseDraftRepository.delete(courseDraft);
    }

    @Override
    public PagedResponse<CourseResponse> getCoursesByCategoryId(Long categoryId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id " + categoryId + " not found"));
        Page<Course> courses = courseRepository.findByCategoryId(category.getId(), pageable);
        return PagedResponse.<CourseResponse>builder()
                .items(courses.getContent().stream().map(Course::toResponse).toList())
                .pagination(new PaginationInfo(courses))
                .build();
    }

    @Override
    public PagedResponse<CourseDraftResponse> getCoursesByAuthorId(String name, Long categoryId, CourseLevel level, CourseProperty courseProperty,
                                                                   Direction direction, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, courseProperty.getValue()));

        // Validate categoryId exists if provided (similar to getAllCourses)
        if (categoryId != null) {
            categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BadRequestException("Category with ID: " + categoryId + " doesn't exist"));
        }

        Specification<CourseDraft> spec = Specification.unrestricted();
        spec = spec.and(CourseDraftSpecification.fetchContents())
                .and(CourseDraftSpecification.hasAuthorId(getCurrentUser().getId()));

        if (name != null && !name.isEmpty()) {
            spec = spec.and(CourseDraftSpecification.courseDraftNameContains(name));
        }

        // Add categoryId filter
        if (categoryId != null) {
            spec = spec.and(CourseDraftSpecification.hasCategoryId(categoryId));
        }

        // Add level filter
        if (level != null) {
            spec = spec.and(CourseDraftSpecification.hasLevel(level));
        }

        Page<CourseDraft> courses = courseDraftRepository.findAll(spec, pageable);

        return PagedResponse.<CourseDraftResponse>builder()
                .items(courses.getContent().stream().map(CourseDraft::toResponse).toList())
                .pagination(new PaginationInfo(courses))
                .build();
    }

    @Override
    public CourseContentResponse createCourseContent(CourseContentRequest request) {
        if (request.getCourseDraftId() == null && request.getCourseId() == null) {
            throw new BadRequestException("At least, course id or course draft id need to be not null");
        }

        Course foundCourse = null;
        if (request.getCourseId() != null) {
            foundCourse = findCourseById(request.getCourseId());
        }

        CourseDraft foundDraftCourse = null;
        if (request.getCourseDraftId() != null) {
            foundDraftCourse = findCourseDraftById(request.getCourseDraftId());
            foundCourse = courseRepository.findByCourseDraft(foundDraftCourse);
        }

        CourseContent newCourseContent = request.toEntity();
        newCourseContent.setCourse(foundCourse);
        newCourseContent.setCourseDraft(foundDraftCourse);
        return courseContentRepository.save(newCourseContent).toResponse();
    }

    @Override
    public List<CourseContentResponse> getCourseContentsByCourseId(Long courseId) {
        Course foundCourse = this.findCourseById(courseId);
        List<CourseContent> courseContents = courseContentRepository.findByCourseOrderByCourseContentIndex(foundCourse);

        if (courseContents.size() <= 0) {
            return List.of();
        }
        return courseContents.stream().map((c) -> c.toResponse()).toList();
    }

    @Override
    public CourseDraftResponse getCourseByIdForAuthor(Long courseId) {
        CourseDraft foundCourse = courseDraftRepository.findByAuthorIdAndCourseId(getCurrentUser().getId(), courseId)
                .orElseThrow(() -> new NotFoundException("Course with ID: " + courseId + " doesn't exist"));

        return foundCourse.toResponse();
    }

    @Override
    public CourseResponse approveCourseById(Long courseId) {
        CourseDraft foundCourseDraft = findCourseDraftById(courseId);
        if (foundCourseDraft.getIsApproved()) {
            throw new BadRequestException("Course is already approved");
        }
        foundCourseDraft.setIsApproved(true);
        CourseDraft savedCourseDraft = courseDraftRepository.save(foundCourseDraft);
        Course savedCourse = courseRepository.save(savedCourseDraft.toCourse());

        List<CourseContent> updatedCourseContents = courseContentRepository
                .findByCourseDraftOrderByCourseContentIndex(savedCourseDraft).stream().map((c) -> {
                    c.setCourse(savedCourse);
                    return c;
                }).toList();

        courseContentRepository.saveAll(updatedCourseContents);
        return savedCourse.toResponse();
    }

    @Override
    public PagedResponse<CourseDraftResponse> getCourseForAdmin(Integer page, Integer size, String name,
                                                                Boolean isApproved,
                                                                Boolean isRejected) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Specification<CourseDraft> spec = Specification.unrestricted();
        spec = spec.and(CourseDraftSpecification.fetchContents());

        if (name != null && !name.isEmpty()) {
            spec = spec.and(CourseDraftSpecification.courseDraftNameContains(name));
        }

        if (isApproved != null) {
            spec = spec.and(CourseDraftSpecification.isApproved(isApproved));
        }

        if (isRejected != null) {
            spec = spec.and(CourseDraftSpecification.isRejected(isRejected));
        }

        Page<CourseDraft> courseDrafts = courseDraftRepository.findAll(spec, pageable);

        return PagedResponse.<CourseDraftResponse>builder()
                .items(courseDrafts.getContent().stream().map(c -> c.toResponse()).toList())
                .pagination(new PaginationInfo(courseDrafts))
                .build();
    }

    @Override
    public CourseResponse joinCourse(Long courseId) {
        Course foundCourse = findCourseById(courseId);
        JoinCourse newJoinCourse = JoinCourse.builder()
                .student(getCurrentUser())
                .course(foundCourse)
                .build();
        joinCourseRepository.save(newJoinCourse);
        return foundCourse.toResponse();
    }

    @Override
    public CourseContentResponse completeCourseContent(Long courseContentId) {
        CourseContent foundCourseContent = findCourseContentById(courseContentId);

        JoinCourse foundJoinCourse = joinCourseRepository.findByStudentAndCourse(getCurrentUser(), foundCourseContent.getCourse())
                .orElseThrow(() -> new NotFoundException(
                        "User hasn't joined course with ID" + foundCourseContent.getCourse().getId() + " yet"));

        if (completeContentRepository.findByStudentAndCourseContent(getCurrentUser(), foundCourseContent) != null) {
            return foundCourseContent.toResponse();
        }

        CompleteContent newCompleteContent = CompleteContent.builder().student(getCurrentUser())
                .courseContent(foundCourseContent).build();

        completeContentRepository.saveAndFlush(newCompleteContent);

        try {
            if (Objects.equals(courseContentRepository.countByCourse(foundCourseContent.getCourse()), completeContentRepository.countByStudentAndCourseContentCourse(getCurrentUser(), foundCourseContent.getCourse()))) {
                foundJoinCourse.setIsCompleted(true);
                joinCourseRepository.save(foundJoinCourse);
            }

            UserLearningStreak foundUserLearningStreak = userLearningStreakRespository.findByAppUser(getCurrentUser());
            if (foundUserLearningStreak == null) {
                userLearningStreakRespository.save(UserLearningStreak.builder().appUser(getCurrentUser()).build());
            } else {
                if (!foundUserLearningStreak.getLastDayLearning().toLocalDate().equals(LocalDate.now())) {
                    foundUserLearningStreak.setLearningStreakDay(foundUserLearningStreak.getLearningStreakDay() + 1);
                    foundUserLearningStreak.setLastDayLearning(LocalDateTime.now());
                    userLearningStreakRespository.save(foundUserLearningStreak);
                }
            }

            Leaderboard foundLeaderboard = leaderboardRepository.findByStudent(getCurrentUser())
                    .orElseThrow(() -> new NotFoundException(
                            "leaderboard for user: " + getCurrentUser().getFullName() + "doesn't exist"));
            foundLeaderboard.setCoursePoints(foundLeaderboard.getCoursePoints() + foundCourseContent.getPoints());
            leaderboardRepository.save(foundLeaderboard);
        } catch (Exception e) {
            log.error("Error occurred while completing contents", e);
        }

        return foundCourseContent.toResponse();
    }

    @Override
    public CourseProgressResponse getCourseProgressByCourseId(Long courseId) {
        Course foundCourse = findCourseById(courseId);
        List<CourseContent> courseContents = foundCourse.getContents();
        List<CompleteContent> completeContents = completeContentRepository
                .findByStudentAndCourseContentCourse(getCurrentUser(), foundCourse);
        List<Long> completedContentId = completeContents.stream().map(cc -> cc.getCourseContent().getId()).toList();

        List<CourseContentProgressResponse> courseContentProgressResponses = courseContents.stream()
                .map(cc -> cc.toProgressResponse(completedContentId.contains(cc.getId()))).toList();

        CourseNoContentResponse courseNoContentResponse = foundCourse.toNoContentResponse();

        return CourseProgressResponse.builder().course(courseNoContentResponse)
                .contentProgresses(courseContentProgressResponses)
                .maxCourseContentCount(courseNoContentResponse.getContentCount())
                .completedCourseContentCount(completeContents.size())
                .build();

    }

    @Override
    public void deleteCourseContentById(Long courseContentId) {
        CourseContent foundCourseContent = findCourseContentById(courseContentId);

        if (foundCourseContent.getCourse().getAuthor().getId() != getCurrentUser().getId()) {
            throw new BadRequestException("Only the author can delete his own course content");
        }

        courseContentRepository.deleteById(courseContentId);
    }

    @Override
    public CourseDraftResponse getCourseForAdminById(Long courseDraftId) {
        return findCourseDraftById(courseDraftId).toResponse();
    }

    @Override
    public CourseDraftResponse submitCourseDraft(Long courseDraftId) {
        CourseDraft courseDraft = findCourseDraftById(courseDraftId);

        // Validate that the current user is the author
        if (!courseDraft.getAuthor().getId().equals(getCurrentUser().getId())) {
            throw new BadRequestException("You don't have permission to submit this course");
        }

        // Check if already submitted
        if (courseDraft.getIsSubmitted()) {
            throw new BadRequestException("Course draft is already submitted");
        }

        // Set isSubmitted to true
        courseDraft.setIsSubmitted(true);

        // Save and return response
        return courseDraftRepository.save(courseDraft).toResponse();
    }

}
