package com.store.sugarbayy.services.product;

import com.store.sugarbayy.configurations.exceptions.ApiException;
import com.store.sugarbayy.dtos.product.ProductInfo;
import com.store.sugarbayy.dtos.product.ProductModifyRequest;
import com.store.sugarbayy.entities.Category;
import com.store.sugarbayy.entities.Product;
import com.store.sugarbayy.entities.ProductDrop;
import com.store.sugarbayy.repos.CategoryRepo;
import com.store.sugarbayy.repos.ProductDropRepo;
import com.store.sugarbayy.repos.ProductRepo;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final ProductDropRepo productDropRepo;
    private final EntityManager entityManager;

    @Override
    public Page<ProductInfo> getProducts(Pageable pageable) {
        return productRepo.fetchProducts(pageable);
    }

    @Override
    @Transactional
    public ProductInfo addProduct(ProductModifyRequest request) {
        // new product
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .category(this.getReferencedCategory(request.categoryId()))
                .drop(this.getReferencedDrop(request.dropId()))
                .images(request.images())
                .stockQuantity(request.stockQuantity())
                .price(request.price())
                .build();
        product = productRepo.save(product);
        return ProductInfo.fromEntity(product);
    }

    @Override
    @Transactional
    public ProductInfo updateProduct(ProductModifyRequest request, Long productId) {
        // check existing
        Product existingProduct = productRepo.findById(productId)
                .orElseThrow(() ->  new ApiException("Product does not exist", HttpStatus.NOT_FOUND));

        // update product
        existingProduct.setName(request.name());
        existingProduct.setDescription(request.description());
        existingProduct.setCategory(this.getReferencedCategory(request.categoryId()));
        existingProduct.setDrop(this.getReferencedDrop(request.dropId()));
        existingProduct.setImages(request.images());
        existingProduct.setStockQuantity(request.stockQuantity());
        existingProduct.setPrice(request.price());
        productRepo.save(existingProduct);
        return ProductInfo.fromEntity(existingProduct);
    }

    // helpers
    private Category getReferencedCategory(Long categoryId) {
        if (categoryId == null || !categoryRepo.existsById(categoryId)) return null;
        return entityManager.getReference(Category.class, categoryId);
    }

    private ProductDrop getReferencedDrop(Long dropId) {
        if (dropId == null || !productDropRepo.existsById(dropId)) return null;
        return entityManager.getReference(ProductDrop.class, dropId);
    }
}
