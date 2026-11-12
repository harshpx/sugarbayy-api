package com.store.sugarbayy.services.product;

import com.store.sugarbayy.dtos.product.CategoryInfo;

import java.util.List;

public interface CategoryService {
    List<CategoryInfo> getCategories();
    CategoryInfo addCategory(String name);
    int deleteCategoryById(Long id);
    int bulkDeleteCategoriesById(List<Long> ids);
}
