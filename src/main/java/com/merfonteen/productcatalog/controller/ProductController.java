package com.merfonteen.productcatalog.controller;

import com.merfonteen.productcatalog.dto.ProductPageResponseDto;
import com.merfonteen.productcatalog.dto.ProductRequestDto;
import com.merfonteen.productcatalog.dto.ProductResponseDto;
import com.merfonteen.productcatalog.dto.ProductUpdateDto;
import com.merfonteen.productcatalog.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequestMapping("/api/v1/products")
@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping
    public ResponseEntity<ProductPageResponseDto> getProducts(@RequestParam(required = false, defaultValue = "0") int page,
                                                              @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getProducts(page, size));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody @Valid ProductRequestDto productRequestDto) {
        ProductResponseDto product = productService.createProduct(productRequestDto);
        URI location = URI.create("/api/v1/products/" + product.getId());
        return ResponseEntity.created(location).body(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable("id") Long id,
                                                            @RequestBody @Valid ProductUpdateDto productUpdateDto) {
        return ResponseEntity.ok(productService.updateProduct(id, productUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
