package com.example.cinema_at_manhattan.authentication;

import com.example.cinema_at_manhattan.model.Role;
import com.example.cinema_at_manhattan.model.User;
import com.example.cinema_at_manhattan.repository.RoleRepository;
import com.example.cinema_at_manhattan.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
public class UserAndRoleInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) throws Exception {
    Role adminRole = createRoleIfNotFound("ROLE_ADMIN");
    Role userRole = createRoleIfNotFound("ROLE_USER");
    initUserForTest(adminRole, userRole);
  }

  private Role createRoleIfNotFound(String roleName) {
    return roleRepository.findByName(roleName)
        .orElseGet(() -> {
          Role role = new Role();
          role.setName(roleName);
          return roleRepository.save(role);
        });
  }

  private void initUserForTest(Role adminRole, Role userRole) {
    createUserIfNotFound("admin", "admin", Set.of(adminRole));
    createUserIfNotFound("user_1", "user_1", Set.of(userRole));
    createUserIfNotFound("user_2", "user_2", Set.of(userRole));
    createUserIfNotFound("user_3", "user_3", Set.of(userRole));
  }

  private void createUserIfNotFound(String username, String password, Set<Role> roles) {
    if (userRepository.findByUsername(username).isEmpty()) {
      User user = new User();
      user.setUsername(username);
      user.setPassword(passwordEncoder.encode(password));
      user.setRoles(roles);
      userRepository.save(user);
    }
  }
}

