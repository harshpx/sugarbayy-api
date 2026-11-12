package com.store.sugarbayy.repos;

import com.store.sugarbayy.dtos.product.ProductInfo;
import com.store.sugarbayy.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    @Query("""
        select new com.store.sugarbayy.dtos.product.ProductInfo(
            p.id, p.name, p.description,
            p.category.id, p.drop.id,
            p.images, p.stockQuantity, p.price,
            p.createdAt, p.updatedAt
        )
        from Product p
        order by p.updatedAt desc, p.createdAt desc, p.id desc
    """)
    Page<ProductInfo> fetchProducts(Pageable pageable);
}
