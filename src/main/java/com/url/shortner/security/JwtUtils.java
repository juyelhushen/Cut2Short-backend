package com.url.shortner.security;


import com.url.shortner.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtils {


    private final long JWT_EXPIRATION_MS = 86400000;   // 24 hours
    @Value("${Secret.key}")
    private String secretKey;

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimResolver) {
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    //standard login
    public String generate(int userId, String username, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userid",userId);
        claims.put("username", username);
        claims.put("role", role);
        return doGenerateToken(claims,username);
    }


    //OauthLogin

    public String generateFromOAuth2User(String username, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return doGenerateToken(claims, username);
    }
//    public String generateFromOAuth2User(OAuth2User oAuth2User, Role role) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("email", oAuth2User.getAttribute("email"));
//        claims.put("name", oAuth2User.getAttribute("name"));
//        claims.put("role", role);
//        return doGenerateToken(claims, oAuth2User.getAttribute("email"));
//    }
    private String doGenerateToken(Map<String, Object> claims,
                                   String username) {
        return Jwts
                .builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS ))
//                .claim("authorities", role)
                .signWith(getSignInKey())
                .compact();
    };

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }



    private SecretKey getSignInKey() {
        byte[] keyInBites = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyInBites);
    }


}
