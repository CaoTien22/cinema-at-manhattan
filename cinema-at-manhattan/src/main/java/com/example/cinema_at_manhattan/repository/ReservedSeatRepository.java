package com.example.cinema_at_manhattan.repository;

import com.example.cinema_at_manhattan.entities.ReservedSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservedSeatRepository extends JpaRepository<ReservedSeat, Long> {
  List<ReservedSeat> findAllByRoomId(long roomId);

  List<ReservedSeat> deleteReservedSeatByUserIdAndRoomIdAndRowAndCol(long userId, long roomId, int row, int col);
}
