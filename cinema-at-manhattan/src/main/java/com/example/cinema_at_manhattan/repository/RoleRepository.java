package com.example.cinema_at_manhattan.repository;

import com.example.cinema_at_manhattan.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {}
