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
import com.merfonteen.productcatalog.util.RequestRateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private RequestRateLimiter requestRateLimiter;

    @InjectMocks
    private ProductServiceImpl productService;

    private final Long productId = 1L;

    private Product product;
    private ProductResponseDto responseDto;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(productId)
                .name("Test Product")
                .price(BigDecimal.valueOf(99.99))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        responseDto = ProductResponseDto.builder()
                .id(productId)
                .name("Test Product")
                .price(BigDecimal.valueOf(99.99))
                .build();
    }

    @Test
    void testGetProduct_ShouldReturnProductDtoIfExists() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(responseDto);

        ProductResponseDto result = productService.getProduct(productId);

        assertThat(result).isEqualTo(responseDto);
        verify(productRepository).findById(productId);
        verify(productMapper).toDto(product);
    }

    @Test
    void testGetProduct_ShouldThrowNotFoundExceptionIfNotExists() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProduct(productId));

        verify(productRepository).findById(productId);
        verifyNoInteractions(productMapper);
    }

    @Test
    void testGetProducts_ShouldReturnPaginatedProductList() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<Product> products = List.of(product);

        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        List<ProductResponseDto> dtoList = List.of(
                ProductResponseDto.builder().id(1L).name("Test Product").price(BigDecimal.valueOf(99.99)).build()
        );

        when(productRepository.findAll(pageable)).thenReturn(productPage);
        when(productMapper.toDtos(products)).thenReturn(dtoList);

        ProductPageResponseDto result = productService.getProducts(page, size);

        assertThat(result).isNotNull();
        assertThat(result.getProducts()).hasSize(1);

        verify(productRepository).findAll(pageable);
        verify(productMapper).toDtos(products);
    }

    @Test
    void testGetProductsByCategory_ShouldReturnProductsByCategory() {
        String category = "electronics";

        Product secondProduct = Product.builder()
                .id(2L)
                .name("Second Product")
                .category("electronics")
                .price(BigDecimal.valueOf(199.99))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<Product> products = List.of(product, secondProduct);

        List<ProductResponseDto> dtoList = List.of(
                ProductResponseDto.builder().id(1L).name("Test Product").price(BigDecimal.valueOf(99.99)).build(),
                ProductResponseDto.builder().id(2L).name("Second Product").price(BigDecimal.valueOf(199.99)).build()
        );

        when(productRepository.findAllByCategory(category)).thenReturn(products);
        when(productMapper.toDtos(products)).thenReturn(dtoList);

        List<ProductResponseDto> result = productService.getProductsByCategory(category);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Test Product");
        assertThat(result.get(1).getName()).isEqualTo("Second Product");

        verify(productRepository).findAllByCategory(category);
        verify(productMapper).toDtos(products);
    }

    @Test
    void testCreateProduct_ShouldCreateNewProductSuccessfully() {
        Long userId = 123L;

        ProductRequestDto requestDto = ProductRequestDto.builder()
                .name("New Product")
                .price(BigDecimal.valueOf(50.0))
                .description("desc")
                .category("books")
                .stock(5)
                .build();

        Product savedProduct = Product.builder()
                .id(1L)
                .name("New Product")
                .price(BigDecimal.valueOf(50.0))
                .description("desc")
                .category("books")
                .stock(5)
                .createdAt(LocalDateTime.now())
                .build();

        ProductResponseDto expectedDto = ProductResponseDto.builder()
                .id(1L)
                .name("New Product")
                .price(BigDecimal.valueOf(50.0))
                .build();

        when(productRepository.findByName("New Product")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productMapper.toDto(savedProduct)).thenReturn(expectedDto);

        ProductResponseDto result = productService.createProduct(requestDto, userId);

        assertThat(result).isEqualTo(expectedDto);
        verify(productRepository).findByName("New Product");
        verify(productRepository).save(any(Product.class));
        verify(requestRateLimiter).limitRequestsByUserId(userId);
        verify(productMapper).toDto(savedProduct);
    }

    @Test
    void testCreateProduct_ShouldThrowBadRequestIfProductExists() {
        Long userId = 123L;
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .name("Existing Product")
                .price(BigDecimal.valueOf(99.0))
                .build();

        when(productRepository.findByName("Existing Product"))
                .thenReturn(Optional.of(product));

        Exception exception = assertThrows(BadRequestException.class, () -> productService.createProduct(requestDto, userId));

        assertEquals("Product with name 'Existing Product' already exists", exception.getMessage());
        verify(productRepository).findByName("Existing Product");
        verifyNoMoreInteractions(productRepository);
        verifyNoInteractions(requestRateLimiter);
        verifyNoInteractions(productMapper);
    }

    @Test
    void testUpdateProduct_ShouldUpdateProductSuccessfully() {
        Long productId = 1L;
        Long userId = 99L;

        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .name("Updated Name")
                .price(BigDecimal.valueOf(111.0))
                .stock(20)
                .build();

        Product existing = Product.builder()
                .id(productId)
                .name("Old Name")
                .price(BigDecimal.valueOf(10.0))
                .stock(5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Product saved = Product.builder()
                .id(productId)
                .name("Updated Name")
                .price(BigDecimal.valueOf(111.0))
                .stock(20)
                .updatedAt(LocalDateTime.now())
                .build();

        ProductResponseDto dto = ProductResponseDto.builder()
                .id(productId)
                .name("Updated Name")
                .price(BigDecimal.valueOf(111.0))
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenReturn(saved);
        when(productMapper.toDto(saved)).thenReturn(dto);

        ProductResponseDto result = productService.updateProduct(productId, updateDto, userId);

        assertThat(result).isEqualTo(dto);
        assertThat(existing.getName()).isEqualTo("Updated Name");
        assertThat(existing.getPrice()).isEqualTo(BigDecimal.valueOf(111.0));
        assertThat(existing.getStock()).isEqualTo(20);

        verify(productRepository).findById(productId);
        verify(requestRateLimiter).limitRequestsByUserId(userId);
        verify(productRepository).save(existing);
        verify(productMapper).toDto(saved);
    }

    @Test
    void testUpdateProduct_ShouldThrowExceptionIfProductNotFound() {
        Long id = 50L;
        Long userId = 150L;
        ProductUpdateDto updateDto = ProductUpdateDto.builder().name("test").build();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.updateProduct(id, updateDto, userId));

        verify(productRepository).findById(id);
        verifyNoInteractions(requestRateLimiter);
        verifyNoMoreInteractions(productRepository);
        verifyNoInteractions(productMapper);
    }

    @Test
    void testDeleteProduct_ShouldDeleteProductSuccessfully() {
        Long id = 1L;
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        productService.deleteProduct(id);

        verify(productRepository).findById(id);
        verify(productRepository).delete(product);
    }

    @Test
    void testDeleteProduct_ShouldThrowExceptionIfProductNotFound() {
        Long id = 999L;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.deleteProduct(id));

        verify(productRepository).findById(id);
        verify(productRepository, never()).delete(any());
    }

}