package com.example.cinema_at_manhattan.repository;

import com.example.cinema_at_manhattan.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {}
