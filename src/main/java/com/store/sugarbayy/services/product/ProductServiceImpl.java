package com.store.sugarbayy.services.product;

import com.store.sugarbayy.configurations.Common;
import com.store.sugarbayy.configurations.exceptions.ApiException;
import com.store.sugarbayy.dtos.product.ProductInfo;
import com.store.sugarbayy.dtos.product.ProductModifyRequest;
import com.store.sugarbayy.entities.Category;
import com.store.sugarbayy.entities.Product;
import com.store.sugarbayy.entities.ProductDrop;
import com.store.sugarbayy.repos.CategoryRepo;
import com.store.sugarbayy.repos.ProductDropRepo;
import com.store.sugarbayy.repos.ProductRepo;
import com.store.sugarbayy.services.objectStore.ImageStorageService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final ProductDropRepo productDropRepo;
    private final EntityManager entityManager;
    private final ImageStorageService imageStorageService;

    @Override
    public Page<ProductInfo> getProducts(Pageable pageable) {
        return productRepo.fetchProducts(pageable);
    }

    @Override
    @Transactional
    public ProductInfo addProduct(ProductModifyRequest request) {
        // create new product
        Product product = Product.builder()
                .name(Common.toSentenceCase(request.name()))
                .description(Common.toSentenceCase(request.description()))
                .category(this.getReferencedCategory(request.categoryId()))
                .drop(this.getReferencedDrop(request.dropId()))
                .images(new ArrayList<>())
                .stockQuantity(request.stockQuantity())
                .price(request.price())
                .build();

        // persist without images
        product = productRepo.save(product);

        // if images are passed, upload within transaction
        if (request.images() != null && !request.images().isEmpty()) {
            // upload image to r2 bucket
            List<String> uploadedImageKeys = imageStorageService.uploadImages(product, request.images().toArray(MultipartFile[]::new));
            // register cleanup if failure
            imageStorageService.registerImageCleanup(uploadedImageKeys, null);
            // set images to product at the end
            product.setImages(uploadedImageKeys);
        }

        return ProductInfo.fromEntity(product);
    }

    @Override
    @Transactional
    public ProductInfo updateProduct(ProductModifyRequest request, Long productId) {
        // check existing
        Product existingProduct = productRepo.findById(productId)
                .orElseThrow(() ->  new ApiException("Product does not exist", HttpStatus.NOT_FOUND));

        // update product
        existingProduct.setName(Common.toSentenceCase(request.name()));
        existingProduct.setDescription(Common.toSentenceCase(request.description()));
        existingProduct.setCategory(this.getReferencedCategory(request.categoryId()));
        existingProduct.setDrop(this.getReferencedDrop(request.dropId()));
        existingProduct.setStockQuantity(request.stockQuantity());
        existingProduct.setPrice(request.price());

        // handle multipart files update
        List<String> presentImageKeys = new ArrayList<>(
                Optional.ofNullable(existingProduct.getImages())
                        .orElse(Collections.emptyList())
        );

        List<MultipartFile> newImages = request.images();
        List<String> imagesToBeDeletedKeys = request.imagesToDelete();

        if (newImages != null && !newImages.isEmpty()) {
            List<String> newImageKeys = imageStorageService.uploadImages(existingProduct, newImages.toArray(MultipartFile[]::new));
            imageStorageService.registerImageCleanup(newImageKeys, null);
            presentImageKeys.addAll(newImageKeys);
        }
        if (imagesToBeDeletedKeys != null && !imagesToBeDeletedKeys.isEmpty()) {
            imageStorageService.registerImageCleanup(null, imagesToBeDeletedKeys);
            presentImageKeys.removeAll(imagesToBeDeletedKeys);
        }
        existingProduct.setImages(presentImageKeys);

        return ProductInfo.fromEntity(existingProduct);
    }

    @Override
    @Transactional
    public int deleteProduct(Long productId) {
        Product existingProduct = productRepo.findById(productId).orElse(null);
        if (existingProduct == null) return 0;
        List<String> imageKeys = existingProduct.getImages();
        productRepo.delete(existingProduct);
        if (imageKeys != null && !imageKeys.isEmpty()) {
            imageStorageService.registerImageCleanup(null, imageKeys);
            return imageKeys.size();
        }
        return 0;
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
