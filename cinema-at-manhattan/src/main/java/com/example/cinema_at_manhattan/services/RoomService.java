package com.example.cinema_at_manhattan.services;

import com.example.cinema_at_manhattan.dto.RoomDTO;
import com.example.cinema_at_manhattan.entities.Room;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface RoomService {
  public Room createRoom(RoomDTO roomDTO);
  public List<Room> getAllRoom();
  public Room getRoomInfo(long id) throws BadRequestException;
}
