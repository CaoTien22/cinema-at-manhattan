package com.example.cinema_at_manhattan.services.impl;

import com.example.cinema_at_manhattan.dto.SeatDTO;
import com.example.cinema_at_manhattan.entities.ReservedSeat;
import com.example.cinema_at_manhattan.entities.Room;
import com.example.cinema_at_manhattan.repository.ReservedSeatRepository;
import com.example.cinema_at_manhattan.repository.RoomRepository;
import com.example.cinema_at_manhattan.services.SeatingService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


@Service
@AllArgsConstructor
public class SeatingServiceImpl implements SeatingService {
  private final RoomRepository roomRepository;
  private final ReservedSeatRepository reservedSeatRepository;
  private final Cache<Long, long[][]> seatLayoutCache =
      Caffeine.newBuilder().maximumSize(1000).expireAfterWrite(10, TimeUnit.MINUTES).build();
  private final Map<Long, Object> roomLocks = new ConcurrentHashMap<>();

  private Object getRoomLock(long roomId) {
    return roomLocks.computeIfAbsent(roomId, key -> new Object());
  }

  @Override
  public List<int[]> getAvailableSeatsCoordinates(long seatingUserId, long roomId)
      throws BadRequestException {
    long[][] seatLayout = seatLayoutCache.getIfPresent(roomId);
    if (seatLayout == null) {
      Room room = getRoomFromDb(roomId);
      seatLayout = createSeatLayout(roomId, room.getLength(), room.getWidth());
      seatLayoutCache.put(roomId, seatLayout);
    }
    Room room = getRoomFromDb(roomId);
    int minDistance = room.getMinDistance();
    Map<String, Long> reservedSeatsMap = createReservedSeatsMap(seatLayout);
    return findAvailableSeats(seatingUserId, seatLayout, reservedSeatsMap, minDistance);
  }

  @Override
  public ReservedSeat bookingSeat(long bookingUserId, SeatDTO seatDTO) throws BadRequestException {
    long roomId = seatDTO.getRoomId();
    int col = seatDTO.getCol();
    int row = seatDTO.getRow();
    Room roomFromDb = getRoomFromDb(roomId);
    if (row < 0 || col < 0 || row >= roomFromDb.getLength() || col >= roomFromDb.getWidth()) {
      throw new BadRequestException("Invalid seat coordinates");
    }
    synchronized (getRoomLock(roomId)) {
      List<int[]> availableCoordinates = getAvailableSeatsCoordinates(bookingUserId, roomId);
      for (int[] coordinate : availableCoordinates) {
        if (row == coordinate[0] && col == coordinate[1]) {
          ReservedSeat reservedSeat = new ReservedSeat();
          reservedSeat.setUserId(bookingUserId);
          reservedSeat.setRow(row);
          reservedSeat.setCol(col);
          reservedSeat.setRoomId(roomId);
          ReservedSeat savedSeat;
          try{
            savedSeat = reservedSeatRepository.save(reservedSeat);
            updateLayoutCache(bookingUserId, roomId, row, col);
          }catch (DataAccessException e){
            throw new BadRequestException("Can not reserve seat!");
          }
          return savedSeat;
        }
      }
    }
    throw new BadRequestException("Seat not available for booking!");
  }

  private void updateLayoutCache(long bookingUserId, long roomId, int row, int col) {
    long[][] seatLayout = seatLayoutCache.getIfPresent(roomId);
    if (seatLayout != null) {
      seatLayout[row][col] = bookingUserId;
      seatLayoutCache.put(roomId, seatLayout);
    }
  }

  @Transactional
  @Override
  public ReservedSeat cancelReservation(long reservationUserId, SeatDTO seatDTO)
      throws BadRequestException {
    long roomId = seatDTO.getRoomId();
    int row = seatDTO.getRow();
    int col = seatDTO.getCol();
    Room roomFromDb = getRoomFromDb(roomId);
    if (row < 0 || col < 0 || row >= roomFromDb.getLength() || col >= roomFromDb.getWidth()) {
      throw new BadRequestException("Invalid seat coordinates");
    }
    synchronized (getRoomLock(roomId)) {
      List<ReservedSeat> deletedSeats =
          reservedSeatRepository.deleteReservedSeatByUserIdAndRoomIdAndRowAndCol(
              reservationUserId, roomId, row, col);
      if (deletedSeats.isEmpty()) {
        throw new BadRequestException("Seat not found for cancellation!");
      }
      updateLayoutCache(0L, roomId, row, col);
      return deletedSeats.get(0);
    }
  }

  private long[][] createSeatLayout(long roomId, int length, int width) {
    long[][] seatLayout = new long[length][width];
    List<ReservedSeat> reservedSeats = reservedSeatRepository.findAllByRoomId(roomId);
    for (ReservedSeat reservedSeat : reservedSeats) {
      seatLayout[reservedSeat.getRow()][reservedSeat.getCol()] = reservedSeat.getUserId();
    }
    return seatLayout;
  }

  private Map<String, Long> createReservedSeatsMap(long[][] seatLayout) {
    Map<String, Long> reservedSeatsMap = new HashMap<>();
    for (int i = 0; i < seatLayout.length; i++) {
      for (int j = 0; j < seatLayout[i].length; j++) {
        if (seatLayout[i][j] != 0L) {
          reservedSeatsMap.put(i + "-" + j, seatLayout[i][j]);
        }
      }
    }
    return reservedSeatsMap;
  }

  private Room getRoomFromDb(long roomId) throws BadRequestException {
    Optional<Room> roomOptional = roomRepository.findById(roomId);
    if (roomOptional.isEmpty()) {
      throw new BadRequestException("Room not found!!!");
    }
    return roomOptional.get();
  }

  private List<int[]> findAvailableSeats(
      long seatingUserId,
      long[][] seatLayout,
      Map<String, Long> reservedSeatsMap,
      int minDistance) {
    List<int[]> availableSeats = new LinkedList<>();
    for (int row = 0; row < seatLayout.length; row++) {
      for (int col = 0; col < seatLayout[row].length; col++) {
        if (seatLayout[row][col] == 0L
            && canReserveSeat(seatingUserId, reservedSeatsMap, row, col, minDistance)) {
          availableSeats.add(new int[] {row, col});
        }
      }
    }
    return availableSeats;
  }

  private boolean canReserveSeat(
      long seatingUserId, Map<String, Long> reservedSeatsMap, int row, int col, int minDistance) {
    for (Map.Entry<String, Long> entry : reservedSeatsMap.entrySet()) {
      String[] coordinate = entry.getKey().split("-");
      int reservedRow = Integer.parseInt(coordinate[0]);
      int reservedCol = Integer.parseInt(coordinate[1]);
      long reservedUserId = entry.getValue();
      if (seatingUserId == reservedUserId) {
        continue;
      }
      int distance = Math.abs(reservedRow - row) + Math.abs(reservedCol - col);
      if (distance < minDistance) {
        return false;
      }
    }
    return true;
  }
}
