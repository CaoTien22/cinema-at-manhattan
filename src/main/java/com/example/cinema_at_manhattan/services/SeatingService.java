package com.example.cinema_at_manhattan.services;

import com.example.cinema_at_manhattan.dto.SeatDTO;
import com.example.cinema_at_manhattan.entities.ReservedSeat;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface SeatingService {
  List<int[]> getAvailableSeatsCoordinates(long seatingUserId, long roomId)
      throws BadRequestException;

  ReservedSeat bookingSeat(long bookingUserId, SeatDTO seatDTO) throws BadRequestException;

  ReservedSeat cancelReservation(long reservationUserId, SeatDTO seatDTO)
      throws BadRequestException;
}
