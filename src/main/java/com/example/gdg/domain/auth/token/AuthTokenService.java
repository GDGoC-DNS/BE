package com.example.gdg.domain.auth.token;

import com.example.gdg.domain.auth.dto.res.AuthTokenRes;
import com.example.gdg.domain.auth.entity.RefreshToken;
import com.example.gdg.domain.auth.repository.RefreshTokenRepository;
import com.example.gdg.global.error.UnauthorizedException;
import com.example.gdg.global.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthTokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-token-validity-seconds:1209600}")
    private long refreshTokenValiditySeconds;

    public AuthTokenRes createTokenPair(Long memberId, String email) {
        String accessToken = jwtTokenProvider.generateAccessToken(memberId, email);
        String refreshToken = createAndSaveRefreshToken(memberId, email);
        return toResponse(accessToken, refreshToken);
    }

    public AuthTokenRes reissue(String refreshToken) {
        Claims claims = parseAndValidateRefreshClaims(refreshToken);
        Long memberId = Long.parseLong(claims.getSubject());
        String email = claims.get("email", String.class);
        String tokenId = claims.get("tokenId", String.class);

        RefreshToken savedToken = refreshTokenRepository.findByTokenIdAndMemberId(tokenId, memberId)
                .orElseThrow(() -> new UnauthorizedException("유효하지 않은 리프레시 토큰입니다."));

        if (Boolean.TRUE.equals(savedToken.getRevoked())) {
            throw new UnauthorizedException("이미 폐기된 리프레시 토큰입니다.");
        }
        if (savedToken.isExpired(LocalDateTime.now())) {
            throw new UnauthorizedException("만료된 리프레시 토큰입니다.");
        }
        if (!matchesToken(refreshToken, savedToken.getTokenHash())) {
            throw new UnauthorizedException("유효하지 않은 리프레시 토큰입니다.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(memberId, email);
        String newTokenId = UUID.randomUUID().toString();
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(memberId, email, newTokenId);

        savedToken.revoke(newTokenId);
        refreshTokenRepository.save(savedToken);

        RefreshToken rotatedToken = RefreshToken.builder()
                .memberId(memberId)
                .tokenId(newTokenId)
                .tokenHash(hashToken(newRefreshToken))
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenValiditySeconds))
                .build();
        refreshTokenRepository.save(rotatedToken);

        return toResponse(accessToken, newRefreshToken);
    }

    private Claims parseAndValidateRefreshClaims(String refreshToken) {
        try {
            Claims claims = jwtTokenProvider.parseClaims(refreshToken);
            String tokenType = claims.get("tokenType", String.class);
            if (!"REFRESH".equals(tokenType)) {
                throw new UnauthorizedException("리프레시 토큰 타입이 올바르지 않습니다.");
            }
            if (claims.get("tokenId", String.class) == null || claims.get("tokenId", String.class).isBlank()) {
                throw new UnauthorizedException("유효하지 않은 리프레시 토큰입니다.");
            }
            return claims;
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedException("유효하지 않은 리프레시 토큰입니다.");
        }
    }

    private String createAndSaveRefreshToken(Long memberId, String email) {
        String tokenId = UUID.randomUUID().toString();
        String refreshToken = jwtTokenProvider.generateRefreshToken(memberId, email, tokenId);

        RefreshToken savedToken = RefreshToken.builder()
                .memberId(memberId)
                .tokenId(tokenId)
                .tokenHash(hashToken(refreshToken))
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenValiditySeconds))
                .build();
        refreshTokenRepository.save(savedToken);

        return refreshToken;
    }

    private AuthTokenRes toResponse(String accessToken, String refreshToken) {
        return AuthTokenRes.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private boolean matchesToken(String rawToken, String storedHash) {
        // Backward compatibility for previously issued BCrypt-hashed refresh tokens.
        if (storedHash != null && storedHash.startsWith("$2")) {
            return passwordEncoder.matches(rawToken, storedHash);
        }
        return MessageDigest.isEqual(
                hashToken(rawToken).getBytes(StandardCharsets.UTF_8),
                storedHash.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("토큰 해시 알고리즘 초기화에 실패했습니다.", e);
        }
    }
}
