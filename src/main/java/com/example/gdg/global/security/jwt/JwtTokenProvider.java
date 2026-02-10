package com.example.gdg.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:QWxhZGRpbjpPcGVuU2VzYW1lU2VjdXJlS2V5QmFzZTY0QWxhZGRpbjpPcGVuU2VzYW1l}")
    private String secret;

    @Value("${jwt.access-token-validity-seconds:3600}")
    private long accessTokenValiditySeconds;

    @Value("${jwt.refresh-token-validity-seconds:1209600}")
    private long refreshTokenValiditySeconds;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Long memberId, String email) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(accessTokenValiditySeconds);

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("email", email)
                .claim("role", "ROLE_USER")
                .claim("tokenType", "ACCESS")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(Long memberId, String email, String tokenId) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(refreshTokenValiditySeconds);

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("email", email)
                .claim("tokenId", tokenId)
                .claim("tokenType", "REFRESH")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        String tokenType = claims.get("tokenType", String.class);
        if (!"ACCESS".equals(tokenType)) {
            throw new IllegalArgumentException("Invalid token type.");
        }

        String memberId = claims.getSubject();
        String role = claims.get("role", String.class);
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                memberId, token, authorities
        );
    }
}
