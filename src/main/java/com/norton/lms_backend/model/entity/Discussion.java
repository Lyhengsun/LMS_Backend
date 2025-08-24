package com.norton.lms_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "discussions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Discussion extends BaseEntity {

    @Column(name = "title", nullable = false, length = 250)
    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private AppUser author;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}