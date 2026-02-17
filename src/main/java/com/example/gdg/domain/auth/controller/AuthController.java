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
    @Operation(
            summary = "회원가입 API",
            description = "이메일 형식을 검증하고 계정을 생성합니다.")
    public ResponseEntity<AuthTokenRes> signUp(@Valid @RequestBody SignUpReq request) {
        return ResponseEntity.ok(authService.signUp(request));
    }

    @PostMapping("/login")
    @Operation(
            summary = "로그인 API",
            description = "입력된 이메일과 비밀번호를 확인하고 access/refresh 토큰을 발급합니다.")
    public ResponseEntity<AuthTokenRes> login(@Valid @RequestBody LoginReq request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/reissue")
    @Operation(
            summary = "토큰 재발급 API",
            description = "refresh 토큰은 검증하고 새로운 access/refresh 토근을 발급합니다.")
    public ResponseEntity<AuthTokenRes> reissue(@Valid @RequestBody ReissueReq request) {
        return ResponseEntity.ok(authService.reissue(request));
    }

    @PutMapping("/password")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "비밀번호 변경 API", description = "이전 비밀번호를 확인하고 새로운 비밀번호로 변경합니다.")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordReq request, Authentication authentication) {
        authService.changePassword(Long.parseLong(authentication.getName()), request);
        return ResponseEntity.ok().build();
    }
}
