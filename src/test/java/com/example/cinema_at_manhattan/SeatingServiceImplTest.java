package com.example.cinema_at_manhattan;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.cinema_at_manhattan.dto.SeatDTO;
import com.example.cinema_at_manhattan.entities.ReservedSeat;
import com.example.cinema_at_manhattan.entities.Room;
import com.example.cinema_at_manhattan.repository.ReservedSeatRepository;
import com.example.cinema_at_manhattan.repository.RoomRepository;
import com.example.cinema_at_manhattan.services.impl.SeatingServiceImpl;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SeatingServiceImplTest {

  @InjectMocks private SeatingServiceImpl seatingService;

  @Mock private RoomRepository roomRepository;

  @Mock private ReservedSeatRepository reservedSeatRepository;

  private final long roomId = 1L;
  private final long userId = 123L;
  private long[][] seatLayout;
  private Room room;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    room = new Room();
    room.setId(roomId);
    room.setLength(5);
    room.setWidth(5);
    room.setMinDistance(1);
    seatLayout = new long[5][5];
  }

  @Test
  void testGetAvailableSeatsCoordinates_WhenNoCache() throws BadRequestException {
    when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
    when(reservedSeatRepository.findAllByRoomId(roomId)).thenReturn(Collections.emptyList());

    List<int[]> availableSeats = seatingService.getAvailableSeatsCoordinates(userId, roomId);

    assertEquals(25, availableSeats.size());
    for (int[] seat : availableSeats) {
      assertEquals(0L, seatLayout[seat[0]][seat[1]]);
    }
  }

  @Test
  void testBookingSeat_Success() throws BadRequestException {
    when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
    when(reservedSeatRepository.save(any(ReservedSeat.class)))
        .thenAnswer(
            invocation -> {
              ReservedSeat reservedSeat = invocation.getArgument(0);
              reservedSeat.setID(1L);
              return reservedSeat;
            });

    SeatDTO seatDTO = new SeatDTO();
    seatDTO.setRoomId(roomId);
    seatDTO.setRow(2);
    seatDTO.setCol(2);
    ReservedSeat reservedSeat = seatingService.bookingSeat(userId, seatDTO);
    assertNotNull(reservedSeat);
    assertEquals(userId, reservedSeat.getUserId());
    assertEquals(2, reservedSeat.getRow());
    assertEquals(2, reservedSeat.getCol());
  }

  @Test
  void testBookingSeat_InvalidCoordinates() {
    SeatDTO seatDTO = new SeatDTO();
    seatDTO.setRoomId(roomId);
    seatDTO.setRow(-1);
    seatDTO.setCol(2);

    BadRequestException thrown =
        assertThrows(BadRequestException.class, () -> seatingService.bookingSeat(userId, seatDTO));
    assertNotNull(thrown);
  }

  @Test
  void testCancelReservation_Success() throws BadRequestException {
    when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
    ReservedSeat reservedSeat = new ReservedSeat();
    reservedSeat.setRoomId(roomId);
    reservedSeat.setUserId(userId);
    reservedSeat.setRow(2);
    reservedSeat.setCol(2);
    when(reservedSeatRepository.deleteReservedSeatByUserIdAndRoomIdAndRowAndCol(
            userId, roomId, 2, 2))
        .thenReturn(List.of(reservedSeat));

    SeatDTO seatDTO = new SeatDTO();
    seatDTO.setRoomId(roomId);
    seatDTO.setRow(2);
    seatDTO.setCol(2);

    ReservedSeat deletedSeat = seatingService.cancelReservation(userId, seatDTO);

    assertNotNull(deletedSeat);
    assertEquals(userId, deletedSeat.getUserId());
  }

  @Test
  void testCancelReservation_SeatNotFound() {
    when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
    when(reservedSeatRepository.deleteReservedSeatByUserIdAndRoomIdAndRowAndCol(
            userId, roomId, 2, 2))
        .thenReturn(Collections.emptyList());

    SeatDTO seatDTO = new SeatDTO();
    seatDTO.setRoomId(roomId);
    seatDTO.setRow(2);
    seatDTO.setCol(2);

    BadRequestException thrown =
        assertThrows(
            BadRequestException.class,
            () -> seatingService.cancelReservation(userId, seatDTO));
    assertEquals("Seat not found for cancellation!", thrown.getMessage());
  }
}
