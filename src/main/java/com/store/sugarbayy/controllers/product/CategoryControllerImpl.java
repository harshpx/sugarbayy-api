package com.store.sugarbayy.controllers.product;

import com.store.sugarbayy.configurations.exceptions.ApiException;
import com.store.sugarbayy.dtos.common.SuccessResponse;
import com.store.sugarbayy.dtos.product.CategoryIdList;
import com.store.sugarbayy.dtos.product.CategoryInfo;
import com.store.sugarbayy.dtos.product.CategoryName;
import com.store.sugarbayy.services.product.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product/category")
public class CategoryControllerImpl implements CategoryController {
    private final CategoryService categoryService;

    @Override
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<List<CategoryInfo>>> listCategories() {
        var data = categoryService.getCategories();
        return ResponseEntity.ok(SuccessResponse.of(data));
    }

    @Override
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<CategoryInfo>> addCategory(@RequestBody @Valid CategoryName request) {
        var data = categoryService.addCategory(request.name());
        return ResponseEntity.ok(SuccessResponse.of(data));
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<String>> deleteCategory(@PathVariable(name = "id") Long id) {
        if (id == null || id <= 0) throw new ApiException("Invalid id is passed", HttpStatus.BAD_REQUEST);
        int deleteCount = categoryService.deleteCategoryById(id);
        return ResponseEntity.ok(
                deleteCount == 1
                ? new SuccessResponse<>("Category deleted successfully")
                : new SuccessResponse<>(false, "Category delete unsuccessful / may not exist")
        );
    }

    @Override
    @DeleteMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<String>> bulkDeleteCategories(
            @RequestBody @Valid CategoryIdList request
    ) {
        int deleteCount = categoryService.bulkDeleteCategoriesById(request.list());
        String responseString = deleteCount + "/" + request.list().size() + " categories deleted successfully";
        return ResponseEntity.ok(
                deleteCount > 0
                ? new SuccessResponse<>(responseString)
                : new SuccessResponse<>(false, responseString)
        );
    }
}
