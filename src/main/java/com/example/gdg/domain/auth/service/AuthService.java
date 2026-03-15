package com.example.gdg.domain.auth.service;

import com.example.gdg.domain.auth.dto.req.LoginReq;
import com.example.gdg.domain.auth.dto.req.ReissueReq;
import com.example.gdg.domain.auth.dto.req.SignUpReq;
import com.example.gdg.domain.auth.dto.req.ChangePasswordReq;
import com.example.gdg.domain.auth.dto.res.AuthTokenRes;
import com.example.gdg.domain.auth.dto.res.MyInfoRes;
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
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
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
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        return authTokenService.createTokenPair(member.getId(), member.getEmail());
    }

    public AuthTokenRes reissue(ReissueReq request) {
        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            throw new IllegalArgumentException("리프레시 토큰은 필수입니다.");
        }
        return authTokenService.reissue(request.getRefreshToken());
    }

    @Transactional(readOnly = true)
    public MyInfoRes getMyInfo(Long memberId) {
        Member member = authMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        return MyInfoRes.builder()
                .email(member.getEmail())
                .build();
    }

    public void changePassword(Long memberId, ChangePasswordReq request) {
        validateChangePasswordRequest(request);

        Member member = authMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        member.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    private void validateSignUpRequest(SignUpReq request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }
        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
    }

    private void validateLoginRequest(LoginReq request) {
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }
    }

    private void validateChangePasswordRequest(ChangePasswordReq request) {
        if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
            throw new IllegalArgumentException("현재 비밀번호는 필수입니다.");
        }
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new IllegalArgumentException("새 비밀번호는 필수입니다.");
        }
    }
}
