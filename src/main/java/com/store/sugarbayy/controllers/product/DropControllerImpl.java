package com.store.sugarbayy.controllers.product;

import com.store.sugarbayy.dtos.common.SuccessResponse;
import com.store.sugarbayy.dtos.product.DropInfo;
import com.store.sugarbayy.dtos.product.DropModifyRequest;
import com.store.sugarbayy.services.product.DropService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product/drop")
public class DropControllerImpl implements DropController {
    private final DropService dropService;

    @Override
    @GetMapping
    public ResponseEntity<SuccessResponse<List<DropInfo>>> listDrops() {
        var data = dropService.listDrops();
        return ResponseEntity.ok(SuccessResponse.of(data));
    }

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<DropInfo>> addProductDrop(
            @ModelAttribute @Valid DropModifyRequest request
    ) {
        var data = dropService.createDrop(request);
        return ResponseEntity.ok(SuccessResponse.of(data));
    }

    @Override
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<DropInfo>> modifyProductDrop(
            @RequestBody @Valid @ModelAttribute DropModifyRequest request,
            @PathVariable(name = "id") Long id
    ) {
        var data = dropService.updateDrop(request, id);
        return ResponseEntity.ok(SuccessResponse.of(data));
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<String>> deleteProductDrop(
            @PathVariable(name = "id") Long id
    ) {
        int deleteCount = dropService.deleteDrop(id);
        return ResponseEntity.ok(
                deleteCount > 0
                ? new SuccessResponse<>("Product drop deleted successfully")
                : new SuccessResponse<>(false, "Product drop delete unsuccessful, may not exist")
        );
    }
}
