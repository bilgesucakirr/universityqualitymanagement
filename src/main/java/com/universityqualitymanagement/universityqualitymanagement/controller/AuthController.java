package com.universityqualitymanagement.universityqualitymanagement.controller;

import com.universityqualitymanagement.universityqualitymanagement.dtos.request.LoginRequest;
import com.universityqualitymanagement.universityqualitymanagement.dtos.request.RegisterRequest;
import com.universityqualitymanagement.universityqualitymanagement.dtos.response.LoginResponse;
import com.universityqualitymanagement.universityqualitymanagement.dtos.response.UserResponse;
import com.universityqualitymanagement.universityqualitymanagement.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public UserResponse register(@RequestBody RegisterRequest request){
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserResponse me(@RequestHeader("Authorization") String authHeader){
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid token");
        }

        String token = authHeader.substring(7);
        return authService.getCurrUser(token);
    }

}
