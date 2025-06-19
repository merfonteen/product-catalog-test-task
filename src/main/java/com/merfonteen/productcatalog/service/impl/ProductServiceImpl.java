package com.merfonteen.productcatalog.service.impl;

import com.merfonteen.productcatalog.dto.ProductPageResponseDto;
import com.merfonteen.productcatalog.dto.ProductRequestDto;
import com.merfonteen.productcatalog.dto.ProductResponseDto;
import com.merfonteen.productcatalog.dto.ProductUpdateDto;
import com.merfonteen.productcatalog.entity.Product;
import com.merfonteen.productcatalog.exception.BadRequestException;
import com.merfonteen.productcatalog.exception.NotFoundException;
import com.merfonteen.productcatalog.mapper.ProductMapper;
import com.merfonteen.productcatalog.repository.ProductRepository;
import com.merfonteen.productcatalog.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Transactional
    @Override
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
        Optional<Product> existingProduct = productRepository.findByName(productRequestDto.getName());
        if(existingProduct.isPresent()) {
            throw new BadRequestException(
                    String.format("Product with name '%s' already exists", productRequestDto.getName()));
        }

        Product.ProductBuilder productBuilder = Product.builder()
                .name(productRequestDto.getName())
                .price(productRequestDto.getPrice())
                .createdAt(LocalDateTime.now());

        Optional.ofNullable(productRequestDto.getDescription()).ifPresent(productBuilder::description);
        Optional.ofNullable(productRequestDto.getStock()).ifPresent(productBuilder::stock);
        Optional.ofNullable(productRequestDto.getCategory()).ifPresent(productBuilder::category);

        Product newProduct = productBuilder.build();
        Product savedProduct = productRepository.save(newProduct);
        log.info("Successfully saved to database product: '{}'", savedProduct);

        return productMapper.toDto(savedProduct);
    }

    @Transactional
    @Override
    public ProductResponseDto updateProduct(Long id, ProductUpdateDto productUpdateDto) {
        Product productToUpdate = findProductByIdOrThrowException(id);

        Optional.ofNullable(productUpdateDto.getName()).ifPresent(productToUpdate::setName);
        Optional.ofNullable(productUpdateDto.getDescription()).ifPresent(productToUpdate::setDescription);
        Optional.ofNullable(productUpdateDto.getCategory()).ifPresent(productToUpdate::setCategory);
        Optional.ofNullable(productUpdateDto.getStock()).ifPresent(productToUpdate::setStock);
        Optional.ofNullable(productUpdateDto.getPrice()).ifPresent(productToUpdate::setPrice);
        productToUpdate.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(productToUpdate);
        log.info("Product with id {} was updated successfully", updatedProduct.getId());

        return productMapper.toDto(updatedProduct);
    }

    @Transactional
    @Override
    public void deleteProduct(Long id) {
        Product productToDelete = findProductByIdOrThrowException(id);
        productRepository.delete(productToDelete);
        log.info("Product with id {} has been deleted", id);
    }

    private Product findProductByIdOrThrowException(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found by id: " + id));
    }
}
