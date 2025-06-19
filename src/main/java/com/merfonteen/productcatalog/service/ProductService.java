package com.merfonteen.productcatalog.service;

import com.merfonteen.productcatalog.dto.ProductPageResponseDto;
import com.merfonteen.productcatalog.dto.ProductResponseDto;

public interface ProductService {
    ProductResponseDto getProduct(Long id);
    ProductPageResponseDto getProducts(int page, int size);
}
