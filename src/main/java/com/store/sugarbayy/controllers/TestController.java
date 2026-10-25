package com.store.sugarbayy.controllers;

import com.store.sugarbayy.dtos.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/test")
public class TestController {
    @PostMapping("/email")
    public ResponseEntity<SuccessResponse<String>> checkEmail(@RequestBody String email) throws BadRequestException {
        String emailRegex =
                "^(?!\\.)(?!.*\\.\\.)([a-z0-9_'+.\\-]*[a-z0-9_+\\-])@([a-z0-9][a-z0-9\\-]*\\.)+[a-z]{2,}$";
        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("Invalid Email Address");
        }
        return ResponseEntity.ok(SuccessResponse.of("Email is valid"));
    }
}
