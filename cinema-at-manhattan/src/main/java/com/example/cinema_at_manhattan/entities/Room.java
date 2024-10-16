package com.example.cinema_at_manhattan.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "cinema_room")
public class Room {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private int length;
  private int width;

  @Column(name = "min_distance")
  private int minDistance;
}
