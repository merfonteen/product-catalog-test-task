package com.merfonteen.productcatalog.mapper;

import com.merfonteen.productcatalog.dto.ProductResponseDto;
import com.merfonteen.productcatalog.entity.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponseDto toDto(Product product);

    Product toEntity(ProductResponseDto productResponseDto);

    List<ProductResponseDto> toDtos(List<Product> products);
}
