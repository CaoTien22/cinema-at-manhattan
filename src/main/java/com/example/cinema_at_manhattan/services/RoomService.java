package com.example.cinema_at_manhattan.services;

import com.example.cinema_at_manhattan.dto.RoomDTO;
import com.example.cinema_at_manhattan.entities.Room;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface RoomService {
  Room createRoom(RoomDTO roomDTO);
  List<Room> getAllRoom();
  Room getRoomInfo(long id) throws BadRequestException;
}
