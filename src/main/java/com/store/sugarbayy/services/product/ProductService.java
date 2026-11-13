package com.store.sugarbayy.services.product;

import com.store.sugarbayy.dtos.product.ProductInfo;
import com.store.sugarbayy.dtos.product.ProductModifyRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductInfo> getProducts(Pageable pageable);
    ProductInfo addProduct(ProductModifyRequest request);
    ProductInfo updateProduct(ProductModifyRequest request, Long ProductId);
    int deleteProduct(Long productId);
}
