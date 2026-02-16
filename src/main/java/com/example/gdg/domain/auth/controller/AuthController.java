package com.example.gdg.domain.auth.controller;

import com.example.gdg.domain.auth.dto.req.ChangePasswordReq;
import com.example.gdg.domain.auth.dto.req.LoginReq;
import com.example.gdg.domain.auth.dto.req.ReissueReq;
import com.example.gdg.domain.auth.dto.req.SignUpReq;
import com.example.gdg.domain.auth.dto.res.AuthTokenRes;
import com.example.gdg.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authentication APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "Sign up", description = "Create account and issue access/refresh tokens")
    public ResponseEntity<AuthTokenRes> signUp(@Valid @RequestBody SignUpReq request) {
        return ResponseEntity.ok(authService.signUp(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Issue access/refresh tokens")
    public ResponseEntity<AuthTokenRes> login(@Valid @RequestBody LoginReq request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/reissue")
    @Operation(summary = "Reissue token", description = "Verify refresh token and reissue access/refresh tokens")
    public ResponseEntity<AuthTokenRes> reissue(@Valid @RequestBody ReissueReq request) {
        return ResponseEntity.ok(authService.reissue(request));
    }

    @PutMapping("/password")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Change password", description = "Change password after current password verification")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordReq request, Authentication authentication) {
        authService.changePassword(Long.parseLong(authentication.getName()), request);
        return ResponseEntity.ok().build();
    }
}
