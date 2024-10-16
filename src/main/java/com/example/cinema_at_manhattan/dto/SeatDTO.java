package com.example.cinema_at_manhattan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatDTO {
  private int row;
  private int col;
  private long roomId;
}
