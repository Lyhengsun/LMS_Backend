package com.norton.lms_backend.service.impl;

import com.norton.lms_backend.exception.BadRequestException;
import com.norton.lms_backend.exception.NotFoundException;
import com.norton.lms_backend.model.dto.request.CourseContentRequest;
import com.norton.lms_backend.model.dto.request.CourseRequest;
import com.norton.lms_backend.model.dto.response.CourseContentResponse;
import com.norton.lms_backend.model.dto.response.CourseResponse;
import com.norton.lms_backend.model.dto.response.PagedResponse;
import com.norton.lms_backend.model.dto.response.PaginationInfo;
import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.Category;
import com.norton.lms_backend.model.entity.Course;
import com.norton.lms_backend.model.entity.CourseContent;
import com.norton.lms_backend.model.enumeration.CourseLevel;
import com.norton.lms_backend.model.enumeration.CourseProperty;
import com.norton.lms_backend.repository.CategoryRepository;
import com.norton.lms_backend.repository.CourseContentRepository;
import com.norton.lms_backend.repository.CourseRepository;
import com.norton.lms_backend.service.CourseService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final CourseContentRepository courseContentRepository;

    private AppUser getCurrentUser() {
        return (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private Course findCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course with id " + id + " not found"));
    }

    @Override
    public CourseResponse createCourse(CourseRequest courseRequest) {
        Course course = courseRequest.toEntity();
        Category category = categoryRepository.findById(courseRequest.getCourseCategoryId()).orElseThrow(
                () -> new NotFoundException("Category with id " + courseRequest.getCourseCategoryId() + " not found"));
        course.setCategory(category);
        course.setAuthor(getCurrentUser());
        return courseRepository.save(course).toResponse();
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

        Page<Course> courses = null;
        if ((name == null || name.isEmpty()) && categoryId == null && level == null) {
            courses = courseRepository.findAll(pageable);
        } else if (categoryId == null && level == null) {
            courses = courseRepository.searchCourses(name, pageable);
        } else if (categoryId == null && (name == null || name.isEmpty())) {
            courses = courseRepository.findByLevel(level, pageable);
        } else if ((name == null || name.isEmpty()) && level == null) {
            courses = courseRepository.findByCategoryId(categoryId, pageable);
        } else if ((name == null || name.isEmpty())) {
            courses = courseRepository.findByCategoryIdAndLevel(categoryId, level, pageable);
        } else if (level == null) {
            courses = courseRepository.searchCoursesByCategoryId(categoryId, name, pageable);
        } else if (categoryId == null) {
            courses = courseRepository.searchCoursesByLevel(name, level, pageable);
        } else {
            courses = courseRepository.searchCoursesByCategoryIdAndLevel(categoryId, level, name, pageable);
        }

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
        Course course = findCourseById(id);
        courseRepository.delete(course);
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
    public PagedResponse<CourseResponse> getCoursesByAuthorId(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Course> courses = courseRepository.findCoursesByAuthorId(getCurrentUser().getId(), pageable);
        return PagedResponse.<CourseResponse>builder()
                .items(courses.getContent().stream().map(Course::toResponse).toList())
                .pagination(new PaginationInfo(courses))
                .build();
    }

    @Override
    public CourseContentResponse createCourseContent(CourseContentRequest request) {
        Course foundCourse = findCourseById(request.getCourseId());

        CourseContent newCourseContent = request.toEntity();
        newCourseContent.setCourse(foundCourse);

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
}
