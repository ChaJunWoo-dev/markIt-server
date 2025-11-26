package com.cha.markit.controller;

import com.cha.markit.dto.response.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("/api/user")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(new UserResponse(userId));
    }
}
