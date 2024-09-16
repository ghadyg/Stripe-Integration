package com.StripeIntegration.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class JWTUtil {
    private String KEY = "fihaskdfsalfhdsalfdsalkfdhasklfhsaldfhlasdfasdfl";


    public String issueToken(String subject, Collection<? extends GrantedAuthority> claims)
    {
        return issueToken(subject,Map.of("subjects",claims.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())));
    }

    public String issueToken(String subject, Map<String,Object> claims)
    {
            return Jwts.builder()
                    .claims(claims)
                    .subject(subject)
                    .issuedAt(Date.from(Instant.now()))
                    .expiration(Date.from(Instant.now().plus(14, ChronoUnit.DAYS)))
                    .signWith(getSignKey())
                    .compact();

    }
    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(KEY.getBytes());
    }

    private Claims getBody(String token)
    {
        return Jwts.parser().verifyWith(getSignKey()).build()
                .parseSignedClaims(token).getPayload();

    }

    public String getSubject(String token)
    {
        Claims claims = getBody(token);
        return claims.getSubject();
    }

    public boolean isTokenValid(String token)
    {
        Claims claims = getBody(token);
        Date expiration = claims.getExpiration();
        return !expiration.before(Date.from(Instant.now()));
    }
}
