package com.store.sugarbayy.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class SwaggerRedirectController {
    @GetMapping("/swagger-ui/index.html")
    public void swaggerEntry(HttpServletResponse response) throws IOException {
        noStore(response);
        response.sendRedirect("/auth/admin/apidoc/login");
    }

    private void noStore(HttpServletResponse response) {
        response.setHeader(
                "Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
    }
}
