package com.example.cinema_at_manhattan.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthenticationController {

  private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
  private final AuthenticationManager authenticationManager;

  private final JwtUtils jwtUtils;

  public AuthenticationController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
    this.authenticationManager = authenticationManager;
    this.jwtUtils = jwtUtils;
  }

  @PostMapping("/login")
  public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  authRequest.getUsername(), authRequest.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      String jwt = jwtUtils.generateToken(authRequest.getUsername());
      return ResponseEntity.ok(new AuthResponse(jwt));

    } catch (AuthenticationException e) {
      log.info(e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
    }
  }
}
