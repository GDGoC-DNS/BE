package com.example.gdg.domain.auth.controller;

import com.example.gdg.domain.auth.dto.req.LoginReq;
import com.example.gdg.domain.auth.dto.req.ReissueReq;
import com.example.gdg.domain.auth.dto.req.SignUpReq;
import com.example.gdg.domain.auth.dto.res.AuthTokenRes;
import com.example.gdg.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authentication APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입 API", description = "회원가입 후 Access/Refresh 토큰 발급")
    public ResponseEntity<AuthTokenRes> signUp(@Valid @RequestBody SignUpReq request) {
        return ResponseEntity.ok(authService.signUp(request));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "로그인 후 Access/Refresh 토큰 발급")
    public ResponseEntity<AuthTokenRes> login(@Valid @RequestBody LoginReq request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급 API", description = "Refresh Token 검증 및 회전 후 Access/Refresh 토큰 재발급")
    public ResponseEntity<AuthTokenRes> reissue(@Valid @RequestBody ReissueReq request) {
        return ResponseEntity.ok(authService.reissue(request));
    }
}
