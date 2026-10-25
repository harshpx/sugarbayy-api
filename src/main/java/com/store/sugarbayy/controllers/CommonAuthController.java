package com.store.sugarbayy.controllers;

import com.store.sugarbayy.dtos.AccessTokenResponse;
import com.store.sugarbayy.dtos.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Null;
import org.springframework.http.ResponseEntity;

public interface CommonAuthController {
    ResponseEntity<SuccessResponse<AccessTokenResponse>> refresh(HttpServletRequest request);
    ResponseEntity<SuccessResponse<Null>> logout(HttpServletRequest request);
}
