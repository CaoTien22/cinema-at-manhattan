package com.example.cinema_at_manhattan.authentication;

import com.example.cinema_at_manhattan.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {
  private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
  private final UserRepository userRepository;

  public JwtUtils(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    try {
      return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    } catch (Exception e) {
      throw new RuntimeException("Invalid JWT token", e);
    }
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public String generateToken(String username) {
    Map<String, Object> claims = new HashMap<>();
    userRepository.findByUsername(username).ifPresentOrElse(
        user -> {
          Long id = user.getId();
          claims.put("userId", id);
        },
        () -> {
          throw new BadCredentialsException("Invalid username or password");
        }
    );
    return createToken(claims, username);
  }

  private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 600000L))
        .signWith(secretKey)
        .compact();
  }

  public Boolean validateToken(String token, String username) {
    final String extractedUsername = extractUsername(token);
    return (extractedUsername.equals(username) && !isTokenExpired(token));
  }

  public Long extractUserId(String token) {
    Claims claims = extractAllClaims(token.substring(7));
    return claims.get("userId", Long.class);
  }
}
