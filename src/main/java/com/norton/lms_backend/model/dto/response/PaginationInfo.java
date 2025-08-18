package com.norton.lms_backend.model.dto.response;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationInfo {
    private Integer totalElements;
    private Integer currentPage;
    private Integer pageSize;
    private Integer totalPages;

    public PaginationInfo(Page<?> page) {
        this.totalElements = Long.valueOf(page.getTotalElements()).intValue();
        this.currentPage = page.getNumber() + 1;
        this.pageSize = page.getSize();
        this.totalPages = page.getTotalPages();
    }
}
