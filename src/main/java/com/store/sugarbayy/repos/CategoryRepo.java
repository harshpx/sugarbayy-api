package com.store.sugarbayy.repos;

import com.store.sugarbayy.dtos.product.CategoryInfo;
import com.store.sugarbayy.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {
    @Query("""
        select new com.store.sugarbayy.dtos.product.CategoryInfo(c.id, c.name)
        from Category c order by c.id desc
    """)
    List<CategoryInfo> fetchAll();

    @Query("""
        select count(c) > 0
        from Category c
        where lower(c.name) = lower(:name)
    """)
    boolean existsByNameIgnoreCase(@Param("name") String name);

    @Modifying
    @Query("delete from Category c where c.id = :id")
    int deleteCategoryById(@Param("id") Long id);

    @Modifying
    @Query("delete from Category c where c.id in (:ids)")
    int bulkDeleteByIds(@Param("ids") List<Long> ids);
}
