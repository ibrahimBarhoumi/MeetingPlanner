package com.canalplus.meetingplannerkata.controllers;

import com.canalplus.meetingplannerkata.domain.dtos.RoomDto;
import com.canalplus.meetingplannerkata.domain.entities.Room;
import com.canalplus.meetingplannerkata.repositories.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author IBRAHIM
 */
@RestController
@RequestMapping(value = "/api/meetingPlanner/rooms")
public class RoomController {

    private final RoomRepository roomRepository;

    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @PostMapping("")
    public ResponseEntity<Room> createRoom(@RequestBody RoomDto roomDto) {
        Room room = Room.builder().roomName(roomDto.getRoomName()).normalMaximalCapacity(roomDto.getNormalMaximalCapacity()).equipments(roomDto.getEquipments()).build();
        Room result = roomRepository.save(room);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<Page<Room>> getAllRooms(@RequestParam(required = false, defaultValue = "0") int page,
                                                  @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<Room> rooms = roomRepository.findAll(paging);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

}
