package com.example.cinema_at_manhattan.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
class AuthResponse {
  private String jwt;
}
