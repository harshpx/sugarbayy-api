package com.store.sugarbayy.controllers.product;

import com.store.sugarbayy.dtos.common.SuccessResponse;
import com.store.sugarbayy.dtos.product.ProductInfo;
import com.store.sugarbayy.dtos.product.ProductModifyRequest;
import com.store.sugarbayy.services.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductControllerImpl implements ProductController {
    private final ProductService productService;

    @Override
    @GetMapping
    public ResponseEntity<SuccessResponse<Page<ProductInfo>>> listProducts(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        var data = productService.getProducts(pageable);
        return ResponseEntity.ok(SuccessResponse.of(data));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SuccessResponse<ProductInfo>> addNewProduct(
            @RequestBody @Valid ProductModifyRequest request
    ) {
        var data = productService.addProduct(request);
        return ResponseEntity.ok(SuccessResponse.of(data));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{productId}")
    public ResponseEntity<SuccessResponse<ProductInfo>> modifyProduct(
            @RequestBody @Valid ProductModifyRequest request,
            @PathVariable(name = "productId") Long productId
    ) {
        var data = productService.updateProduct(request, productId);
        return ResponseEntity.ok(SuccessResponse.of(data));
    }
}
