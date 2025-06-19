package com.merfonteen.productcatalog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDto {

    @NotNull(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @Positive
    private BigDecimal price;

    private String category;

    @Positive
    private Integer stock;
}
