package com.example.cinema_at_manhattan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class CinemaAtManhattanApplication {
  public static void main(String[] args) {
    SpringApplication.run(CinemaAtManhattanApplication.class, args);
  }

}
