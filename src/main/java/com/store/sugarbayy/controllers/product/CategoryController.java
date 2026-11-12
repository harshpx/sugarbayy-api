package com.store.sugarbayy.controllers.product;

import com.store.sugarbayy.dtos.common.SuccessResponse;
import com.store.sugarbayy.dtos.product.CategoryIdList;
import com.store.sugarbayy.dtos.product.CategoryInfo;
import com.store.sugarbayy.dtos.product.CategoryName;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CategoryController {
    ResponseEntity<SuccessResponse<List<CategoryInfo>>> listCategories();
    ResponseEntity<SuccessResponse<CategoryInfo>> addCategory(CategoryName request);
    ResponseEntity<SuccessResponse<String>> deleteCategory(Long id);
    ResponseEntity<SuccessResponse<String>> bulkDeleteCategories(CategoryIdList ids);
}
