package com.merfonteen.productcatalog.service.impl;

import com.merfonteen.productcatalog.dto.ProductPageResponseDto;
import com.merfonteen.productcatalog.dto.ProductResponseDto;
import com.merfonteen.productcatalog.entity.Product;
import com.merfonteen.productcatalog.exception.NotFoundException;
import com.merfonteen.productcatalog.mapper.ProductMapper;
import com.merfonteen.productcatalog.repository.ProductRepository;
import com.merfonteen.productcatalog.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Primary
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    @Override
    public ProductResponseDto getProduct(Long id) {
        Product product = findProductByIdOrThrowException(id);
        return productMapper.toDto(product);
    }

    @Override
    public ProductPageResponseDto getProducts(int page, int size) {
        if(size > 100) {
            size = 100;
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> productsPage = productRepository.findAll(pageRequest);
        List<ProductResponseDto> products = productMapper.toDtos(productsPage.getContent());

        return ProductPageResponseDto.builder()
                .products(products)
                .currentPage(productsPage.getNumber())
                .totalElements(productsPage.getTotalElements())
                .totalPages(productsPage.getTotalPages())
                .isLastPage(productsPage.isLast())
                .build();
    }

    private Product findProductByIdOrThrowException(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found by id: " + id));
    }
}
