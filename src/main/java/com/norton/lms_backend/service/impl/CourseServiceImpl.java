package com.norton.lms_backend.service.impl;

import com.norton.lms_backend.exception.BadRequestException;
import com.norton.lms_backend.exception.NotFoundException;
import com.norton.lms_backend.model.dto.request.CourseContentRequest;
import com.norton.lms_backend.model.dto.request.CourseRequest;
import com.norton.lms_backend.model.dto.response.CourseContentResponse;
import com.norton.lms_backend.model.dto.response.CourseDraftResponse;
import com.norton.lms_backend.model.dto.response.CourseResponse;
import com.norton.lms_backend.model.dto.response.PagedResponse;
import com.norton.lms_backend.model.dto.response.PaginationInfo;
import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.Category;
import com.norton.lms_backend.model.entity.Course;
import com.norton.lms_backend.model.entity.CourseContent;
import com.norton.lms_backend.model.entity.CourseDraft;
import com.norton.lms_backend.model.entity.JoinCourse;
import com.norton.lms_backend.model.enumeration.CourseLevel;
import com.norton.lms_backend.model.enumeration.CourseProperty;
import com.norton.lms_backend.repository.CategoryRepository;
import com.norton.lms_backend.repository.CourseContentRepository;
import com.norton.lms_backend.repository.CourseDraftRepository;
import com.norton.lms_backend.repository.CourseRepository;
import com.norton.lms_backend.repository.JoinCourseRepository;
import com.norton.lms_backend.repository.specification.CourseDraftSpecification;
import com.norton.lms_backend.repository.specification.CourseSpecification;
import com.norton.lms_backend.service.CourseService;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final CourseContentRepository courseContentRepository;
    private final CourseDraftRepository courseDraftRepository;
    private final JoinCourseRepository joinCourseRepository;

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
        return courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course with id " + id + " not found")).toResponse();
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
                .items(courses.getContent().stream().map(Course::toResponse).toList())
                .pagination(new PaginationInfo(courses))
                .build();
    }

    @Override
    public CourseResponse updateCourse(Long id, CourseRequest courseRequest) {
        Course course = findCourseById(id);
        course.setCourseName(course.getCourseName());
        course.setCourseDescription(course.getCourseDescription());
        course.setLevel(courseRequest.getLevel());
        course.setMaxPoints(courseRequest.getMaxPoints());
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
    public PagedResponse<CourseDraftResponse> getCoursesByAuthorId(String name, CourseProperty courseProperty,
            Direction direction, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, courseProperty.getValue()));

        Specification<CourseDraft> spec = Specification.unrestricted();
        spec = spec.and(CourseDraftSpecification.fetchContents());
        spec = spec.and(CourseDraftSpecification.hasAuthorId(getCurrentUser().getId()));

        if (name != null && !name.isEmpty()) {
            spec = spec.and(CourseDraftSpecification.courseDraftNameContains(name));
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
    public PagedResponse<CourseDraftResponse> getUnapprovedCourse(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<CourseDraft> courseDrafts = courseDraftRepository.findAllByIsApproved(false, pageable);

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
}
