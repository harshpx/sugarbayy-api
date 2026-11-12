package com.store.sugarbayy.controllers.auth;

import com.store.sugarbayy.dtos.auth.AccessTokenResponse;
import com.store.sugarbayy.dtos.common.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Null;
import org.springframework.http.ResponseEntity;

public interface CommonAuthController {
    ResponseEntity<SuccessResponse<AccessTokenResponse>> refresh(HttpServletRequest request);
    ResponseEntity<SuccessResponse<Null>> logout(HttpServletRequest request);
}
