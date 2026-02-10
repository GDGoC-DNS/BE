package com.example.gdg.domain.auth.service;

import com.example.gdg.domain.auth.dto.req.LoginReq;
import com.example.gdg.domain.auth.dto.req.ReissueReq;
import com.example.gdg.domain.auth.dto.req.SignUpReq;
import com.example.gdg.domain.auth.dto.res.AuthTokenRes;
import com.example.gdg.domain.auth.repository.AuthMemberRepository;
import com.example.gdg.domain.auth.token.AuthTokenService;
import com.example.gdg.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AuthMemberRepository authMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public AuthTokenRes signUp(SignUpReq request) {
        validateSignUpRequest(request);

        if (authMemberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("email already exists.");
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        Member savedMember = authMemberRepository.save(member);
        return authTokenService.createTokenPair(savedMember.getId(), savedMember.getEmail());
    }

    public AuthTokenRes login(LoginReq request) {
        validateLoginRequest(request);

        Member member = authMemberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials.");
        }

        return authTokenService.createTokenPair(member.getId(), member.getEmail());
    }

    public AuthTokenRes reissue(ReissueReq request) {
        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            throw new IllegalArgumentException("refreshToken is required.");
        }
        return authTokenService.reissue(request.getRefreshToken());
    }

    private void validateSignUpRequest(SignUpReq request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("email is required.");
        }
        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("password is required.");
        }
    }

    private void validateLoginRequest(LoginReq request) {
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("password is required.");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("email is required.");
        }
    }
}
