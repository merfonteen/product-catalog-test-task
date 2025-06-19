package com.merfonteen.productcatalog.service;

import com.merfonteen.productcatalog.dto.ProductPageResponseDto;
import com.merfonteen.productcatalog.dto.ProductRequestDto;
import com.merfonteen.productcatalog.dto.ProductResponseDto;
import com.merfonteen.productcatalog.dto.ProductUpdateDto;

import java.util.List;

public interface ProductService {
    ProductResponseDto getProduct(Long id);
    ProductPageResponseDto getProducts(int page, int size);
    List<ProductResponseDto> getProductsByCategory(String category);
    ProductResponseDto createProduct(ProductRequestDto productRequestDto);
    ProductResponseDto updateProduct(Long id, ProductUpdateDto productUpdateDto);
    void deleteProduct(Long id);
}
