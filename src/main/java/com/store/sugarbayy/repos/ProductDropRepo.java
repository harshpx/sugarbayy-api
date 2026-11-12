package com.store.sugarbayy.repos;

import com.store.sugarbayy.dtos.product.DropInfo;
import com.store.sugarbayy.dtos.product.ProductInfo;
import com.store.sugarbayy.entities.ProductDrop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDropRepo extends JpaRepository<ProductDrop, Long> {
    @Query("""
        select new com.store.sugarbayy.dtos.product.DropInfo(
            d.id, d.name, d.description,
            d.coverPhoto, d.createdAt, d.updatedAt
        ) from ProductDrop d
    """)
    List<DropInfo> fetchDrops();

    @Query("""
        select count(d) > 0
        from ProductDrop d
        where lower(d.name) = lower(:name)
    """)
    boolean existsByNameIgnoreCase(@Param("name") String name);
}
