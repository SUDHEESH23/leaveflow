package com.example.leaveflow.controller;

import com.example.leaveflow.dto.RegisterRequest;
import com.example.leaveflow.dto.RegisterResponse;
import com.example.leaveflow.service.UserAccountService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserAccountService userAccountService;

    public AuthController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping("/register")
    public RegisterResponse register(
            @RequestBody RegisterRequest request) {

        return userAccountService.register(request);
    }
}