package com.example.leaveflow.controller;

import com.example.leaveflow.dto.LoginRequest;
import com.example.leaveflow.dto.LoginResponse;
import com.example.leaveflow.dto.RegisterRequest;
import com.example.leaveflow.dto.RegisterResponse;
import com.example.leaveflow.entity.UserAccount;
import com.example.leaveflow.security.JwtService;
import com.example.leaveflow.service.UserAccountService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserAccountService userAccountService;
    private final JwtService jwtService;

    public AuthController(UserAccountService userAccountService,
                          JwtService jwtService) {
        this.userAccountService = userAccountService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public RegisterResponse register(
            @RequestBody RegisterRequest request) {

        return userAccountService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        UserAccount account = userAccountService.login(request);
        String token =  jwtService.generateToken(account);
        return new LoginResponse(token);
    }
}