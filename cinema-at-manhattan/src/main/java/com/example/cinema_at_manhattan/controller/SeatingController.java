package com.example.cinema_at_manhattan.controller;

import com.example.cinema_at_manhattan.authentication.JwtUtils;
import com.example.cinema_at_manhattan.dto.SeatDTO;
import com.example.cinema_at_manhattan.entities.ReservedSeat;
import com.example.cinema_at_manhattan.services.SeatingService;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seating")
@AllArgsConstructor
public class SeatingController {
  private final SeatingService seatingService;
  private final JwtUtils jwtUtils;

  @GetMapping("/available-seats")
  public ResponseEntity<List<int[]>> getAvailableSeats(
      @RequestHeader("Authorization") String token, @RequestParam long roomId) {
    Long userId = jwtUtils.extractUserId(token);
    try {
      List<int[]> availableSeats = seatingService.getAvailableSeatsCoordinates(userId, roomId);
      return new ResponseEntity<>(availableSeats, HttpStatus.OK);
    } catch (BadRequestException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/reserve-seat")
  public ResponseEntity<?> reserveSeat(
      @RequestHeader("Authorization") String token, @RequestBody SeatDTO seatDto) {
    Long userId = jwtUtils.extractUserId(token);
    try {
      ReservedSeat reservedSeat =
          seatingService.bookingSeat(
              userId, seatDto);
      return new ResponseEntity<>(reservedSeat, HttpStatus.OK);
    } catch (BadRequestException e) {
      return new ResponseEntity<>("Cannot reserve seat", HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/cancel-seat")
  public ResponseEntity<?> cancelSeat(@RequestHeader("Authorization") String token, @RequestBody SeatDTO seatDto) {
    Long userId = jwtUtils.extractUserId(token);
    try {
      ReservedSeat canceledSeat = seatingService.cancelReservation(userId, seatDto);
      return new ResponseEntity<>(canceledSeat, HttpStatus.OK);
    } catch (BadRequestException e) {
      return new ResponseEntity<>("Cannot cancel seat", HttpStatus.BAD_REQUEST);
    }
  }
}
