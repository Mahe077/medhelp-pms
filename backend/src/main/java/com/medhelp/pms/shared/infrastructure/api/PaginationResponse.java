package com.medhelp.pms.shared.infrastructure.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponse {
    private int page;
    private int pageSize;
    private int totalPages;
    private long totalItems;
    private boolean hasNext;
    private boolean hasPrevious;

    public static PaginationResponse of(int page, int pageSize, long totalItems) {
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        return PaginationResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .totalItems(totalItems)
                .hasNext(page < totalPages)
                .hasPrevious(page > 1)
                .build();
    }
}

