package com.store.sugarbayy.services.product;

import com.store.sugarbayy.configurations.Common;
import com.store.sugarbayy.configurations.exceptions.ApiException;
import com.store.sugarbayy.dtos.product.CategoryInfo;
import com.store.sugarbayy.entities.Category;
import com.store.sugarbayy.repos.CategoryRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepo categoryRepo;

    @Override
    public List<CategoryInfo> getCategories() {
        return categoryRepo.fetchAll();
    }

    @Override
    @Transactional
    public CategoryInfo addCategory(String name) {
        if (categoryRepo.existsByNameIgnoreCase(name)) {
            throw new ApiException("Category with name: " + name + " already exists", HttpStatus.CONFLICT);
        }
        String cleanedName = Common.toSentenceCase(name);
        Category category = Category.builder().name(cleanedName).build();
        category = categoryRepo.save(category);
        return CategoryInfo.fromEntity(category);
    }

    @Override
    @Transactional
    public int deleteCategoryById(Long id) {
        return categoryRepo.deleteCategoryById(id);
    }

    @Override
    @Transactional
    public int bulkDeleteCategoriesById(List<Long> ids) {
        return categoryRepo.bulkDeleteByIds(ids);
    }
}
