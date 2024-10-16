package com.example.cinema_at_manhattan.services.impl;

import com.example.cinema_at_manhattan.dto.RoomDTO;
import com.example.cinema_at_manhattan.entities.Room;
import com.example.cinema_at_manhattan.repository.RoomRepository;
import java.util.List;
import java.util.Optional;

import com.example.cinema_at_manhattan.services.RoomService;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoomServiceImpl implements RoomService {
  private final RoomRepository roomRepository;

  @Override
  public Room createRoom(RoomDTO roomDTO) {
    Room room = new Room();
    room.setLength(roomDTO.getLength());
    room.setWidth(roomDTO.getWidth());
    room.setMinDistance(roomDTO.getMinDistance());
    return roomRepository.save(room);
  }

  @Override
  public List<Room> getAllRoom() {
    return roomRepository.findAll();
  }

  @Override
  public Room getRoomInfo(long id) throws BadRequestException {
    Optional<Room> roomOpt = roomRepository.findById(id);
    if (roomOpt.isPresent()) {
      return roomOpt.get();
    }
    throw new BadRequestException("Room not found");
  }
}
