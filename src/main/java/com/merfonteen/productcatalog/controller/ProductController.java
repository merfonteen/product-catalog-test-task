package com.merfonteen.productcatalog.controller;

import com.merfonteen.productcatalog.dto.ProductPageResponseDto;
import com.merfonteen.productcatalog.dto.ProductRequestDto;
import com.merfonteen.productcatalog.dto.ProductResponseDto;
import com.merfonteen.productcatalog.dto.ProductUpdateDto;
import com.merfonteen.productcatalog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequestMapping("/api/v1/products")
@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get product by ID", description = "Returns a product by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @Operation(summary = "Get products by category", description = "Returns a list of products by a certain category.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products found"),
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCategory(@PathVariable("category") String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    @Operation(summary = "Get all products", description = "Returns a paginated list of all products.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products found"),
    })
    @GetMapping
    public ResponseEntity<ProductPageResponseDto> getProducts(@RequestParam(required = false, defaultValue = "0") int page,
                                                              @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getProducts(page, size));
    }

    @Operation(summary = "Create product", description = "Returns a created product as a DTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Product already exists"),
    })
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody @Valid ProductRequestDto productRequestDto,
                                                            @RequestHeader("X-User-Id") Long currentUserId) {
        ProductResponseDto product = productService.createProduct(productRequestDto, currentUserId);
        URI location = URI.create("/api/v1/products/" + product.getId());
        return ResponseEntity.created(location).body(product);
    }


    @Operation(summary = "Update product", description = "Returns an updated product as a DTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable("id") Long id,
                                                            @RequestBody @Valid ProductUpdateDto productUpdateDto,
                                                            @RequestHeader("X-User-Id") Long currentUserId) {
        return ResponseEntity.ok(productService.updateProduct(id, productUpdateDto, currentUserId));
    }

    @Operation(summary = "Delete product", description = "Returns nothing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
