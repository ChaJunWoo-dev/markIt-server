package com.cha.markit.controller;

import com.cha.markit.dto.response.AuthResponse;
import com.cha.markit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> loginWithGoogle(@RequestBody Map<String, String> body) {
        String idToken = body.get("idToken");
        AuthResponse response = authService.authenticateWithGoogle(idToken);

        return ResponseEntity.ok(response);
    }
}

