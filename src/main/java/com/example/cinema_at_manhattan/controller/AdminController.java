package com.example.cinema_at_manhattan.controller;

import com.example.cinema_at_manhattan.dto.RoomDTO;
import com.example.cinema_at_manhattan.entities.Room;
import com.example.cinema_at_manhattan.services.RoomService;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class AdminController {
  private final RoomService roomService;

  @PostMapping("/room")
  public ResponseEntity<String> createRoom(@RequestBody RoomDTO roomDTO) {
    Room createdRoom = roomService.createRoom(roomDTO);
    if (createdRoom != null) {
      return ResponseEntity.ok("Room created successfully!");
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Failed to create room. Cinema might not exist.");
    }
  }

  @GetMapping("/all-room")
  public ResponseEntity<List<Room>> getAllRooms() {
    return ResponseEntity.ok(roomService.getAllRoom());
  }

  @GetMapping("/room")
  public ResponseEntity<?> getRoomById(@RequestParam long id) {
    try {
      Room roomInfo = roomService.getRoomInfo(id);
      return ResponseEntity.ok(roomInfo);
    } catch (BadRequestException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Room Not Found");
    }
  }
}
