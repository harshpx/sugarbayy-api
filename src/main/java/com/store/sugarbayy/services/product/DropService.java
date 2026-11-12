package com.store.sugarbayy.services.product;

import com.store.sugarbayy.dtos.product.DropInfo;
import com.store.sugarbayy.dtos.product.DropModifyRequest;

import java.util.List;

public interface DropService {
    List<DropInfo> listDrops();
    DropInfo createDrop(DropModifyRequest request);
    DropInfo updateDrop(DropModifyRequest request, Long dropId);
    int deleteDrop(Long dropId);
}
