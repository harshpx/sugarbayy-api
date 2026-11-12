package com.store.sugarbayy.controllers.product;

import com.store.sugarbayy.dtos.common.SuccessResponse;
import com.store.sugarbayy.dtos.product.ProductInfo;
import com.store.sugarbayy.dtos.product.ProductModifyRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ProductController {
    ResponseEntity<SuccessResponse<Page<ProductInfo>>> listProducts(Pageable pageable);
    ResponseEntity<SuccessResponse<ProductInfo>> addNewProduct(ProductModifyRequest request);
    ResponseEntity<SuccessResponse<ProductInfo>> modifyProduct(ProductModifyRequest request, Long productId);
}
