package com.example.cinema_at_manhattan.authentication;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class AuthRequest {
  private String username;
  private String password;
}
