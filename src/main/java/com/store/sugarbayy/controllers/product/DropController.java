package com.store.sugarbayy.controllers.product;

import com.store.sugarbayy.dtos.common.SuccessResponse;
import com.store.sugarbayy.dtos.product.DropInfo;
import com.store.sugarbayy.dtos.product.DropModifyRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DropController {
    ResponseEntity<SuccessResponse<List<DropInfo>>> listDrops();
    ResponseEntity<SuccessResponse<DropInfo>> addProductDrop(DropModifyRequest request);
    ResponseEntity<SuccessResponse<DropInfo>> modifyProductDrop(DropModifyRequest request, Long dropId);
    ResponseEntity<SuccessResponse<String>> deleteProductDrop(Long dropId);
}
