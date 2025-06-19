package com.merfonteen.productcatalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPageResponseDto {
    private List<ProductResponseDto> products;
    private Integer currentPage;
    private Integer totalPages;
    private Long totalElements;
    private Boolean isLastPage;
}
