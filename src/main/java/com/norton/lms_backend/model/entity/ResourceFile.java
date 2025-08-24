package com.norton.lms_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "resource_files")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceFile extends BaseEntity {

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
