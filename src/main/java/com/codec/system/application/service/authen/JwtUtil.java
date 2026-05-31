package com.codec.system.application.service.authen;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private Long expiration;

  @Value("${jwt.refresh-expiration}")
  private Long refreshExpiration;

  private Key key;

  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
  }

  public String generateToken(String username, List<String> permissions, String userId, Integer accountType) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expiration);

    return Jwts.builder()
      .setSubject(username)
      .claim("userId", userId)
      .claim("accountType", accountType)
      .claim("permissions", permissions)
      .setIssuedAt(now)
      .setExpiration(expiryDate)
      .signWith(key, SignatureAlgorithm.HS256)
      .compact();
  }

  public String generateToken(String username) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expiration);

    return Jwts.builder()
      .setSubject(username)
      .setIssuedAt(now)
      .setExpiration(expiryDate)
      .signWith(key, SignatureAlgorithm.HS256)
      .compact();
  }

  public String generateRefreshToken(String username) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + refreshExpiration);
    return Jwts.builder()
      .setSubject(username)
      .setIssuedAt(now)
      .setExpiration(expiryDate)
      .signWith(key, SignatureAlgorithm.HS256)
      .compact();
  }

  public String getUsernameFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build()
      .parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public String getUserIdFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
      .setSigningKey(key)
      .build()
      .parseClaimsJws(token)
      .getBody();

    return claims.get("userId", String.class);
  }

  public Integer getAccountTypeFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
      .setSigningKey(key)
      .build()
      .parseClaimsJws(token)
      .getBody();

    return claims.get("accountType", Integer.class);
  }


  public List<String> getPermissionsFromToken(String token) {
    Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
      .parseClaimsJws(token).getBody();
    return claims.get("permissions", List.class);
  }

  public String checkPermission(String authHeader, String permission) {
    String token = authHeader.replace("Bearer ", "");
    if (validateToken(token)) {
      String userId = getUserIdFromToken(token); // bạn cần viết hàm này
      List<String> permissions = getPermissionsFromToken(token);

      if (!permissions.contains(permission)) {
        return "Api không có quyền truy cập";
      }
      return userId;
    } else {
      return "Token không hợp lệ";
    }
  }

  public String getUserId(String authHeader) {
    String token = authHeader.replace("Bearer ", "");
    if (validateToken(token)) {
      Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();

      return claims.get("userId", String.class);
    } else {
      throw new RuntimeException("Token không hợp lệ");
    }
  }

  public Integer getAccountType(String authHeader) {
    String token = authHeader.replace("Bearer ", "");
    if (validateToken(token)) {
      Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();

      return claims.get("accountType", Integer.class);
    } else {
      throw new RuntimeException("Token không hợp lệ");
    }
  }
}
