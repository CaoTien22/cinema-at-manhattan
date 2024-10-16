package com.example.cinema_at_manhattan.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reserved_seat")
@Getter
@Setter
public class ReservedSeat {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long ID;

  @Column(name = "row_num")
  private int row;

  @Column(name = "col_num")
  private int col;

  @Column(name = "user_id")
  private long userId;

  @Column(name = "room_id")
  private long roomId;
}
