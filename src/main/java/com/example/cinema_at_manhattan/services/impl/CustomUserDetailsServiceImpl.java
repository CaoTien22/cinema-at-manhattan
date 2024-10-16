package com.example.cinema_at_manhattan.services.impl;

import com.example.cinema_at_manhattan.model.Role;
import com.example.cinema_at_manhattan.model.User;
import com.example.cinema_at_manhattan.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  public CustomUserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("User not found");
    }

    Set<GrantedAuthority> authorities = new HashSet<>();
    for (Role role : user.getRoles()) {
      authorities.add(new SimpleGrantedAuthority(role.getName()));
    }

    return new org.springframework.security.core.userdetails.User(
        user.getUsername(), user.getPassword(), authorities);
  }
}
