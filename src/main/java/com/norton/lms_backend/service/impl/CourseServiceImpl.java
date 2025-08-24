package com.norton.lms_backend.service.impl;

import com.norton.lms_backend.exception.NotFoundException;
import com.norton.lms_backend.model.dto.request.CourseRequest;
import com.norton.lms_backend.model.dto.response.CourseResponse;
import com.norton.lms_backend.model.dto.response.PagedResponse;
import com.norton.lms_backend.model.dto.response.PaginationInfo;
import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.Category;
import com.norton.lms_backend.model.entity.Course;
import com.norton.lms_backend.repository.AppUserRepository;
import com.norton.lms_backend.repository.CategoryRepository;
import com.norton.lms_backend.repository.CourseRepository;
import com.norton.lms_backend.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final AppUserRepository appUserRepository;

    private Course findCourseById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Course with id "+ id + " not found"));
    }

    @Override
    public CourseResponse createCourse(CourseRequest courseRequest) {
        Course course = courseRequest.toEntity();
        Category category = categoryRepository.findById(courseRequest.getCourseCategoryId()).orElseThrow(() -> new NotFoundException("Category with id "+courseRequest.getCourseCategoryId()+" not found"));
        AppUser appUser = appUserRepository.findById(courseRequest.getAuthorId()).orElseThrow(() -> new NotFoundException("Author with id "+courseRequest.getAuthorId()+" not found"));
        course.setCategory(category);
        course.setAuthor(appUser);
        return courseRepository.save(course).toResponse();
    }

    @Override
    public CourseResponse getCourseById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Course with id "+ id + " not found")).toResponse();
    }

    @Override
    public PagedResponse<CourseResponse> getAllCourses(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Course> courses = courseRepository.findAll(pageable);
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
        course.setIsPublic(courseRequest.getIsPublic());
        return courseRepository.save(course).toResponse();
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = findCourseById(id);
        courseRepository.delete(course);
    }

    @Override
    public PagedResponse<CourseResponse> getCoursesByCategoryId(Long categoryId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category with id "+categoryId+" not found"));
        Page<Course> courses = courseRepository.findCoursesByCategoryId(category.getId(), pageable);
        return PagedResponse.<CourseResponse>builder()
                .items(courses.getContent().stream().map(Course::toResponse).toList())
                .pagination(new PaginationInfo(courses))
                .build();
    }

    @Override
    public PagedResponse<CourseResponse> getCoursesByAuthorId(Long authorId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page-1, size);
        AppUser appUser = appUserRepository.findById(authorId).orElseThrow(() -> new NotFoundException("Author with id "+authorId+" not found"));
        Page<Course> courses = courseRepository.findCoursesByCategoryId(appUser.getId(), pageable);
        return PagedResponse.<CourseResponse>builder()
                .items(courses.getContent().stream().map(Course::toResponse).toList())
                .pagination(new PaginationInfo(courses))
                .build();
    }
}
