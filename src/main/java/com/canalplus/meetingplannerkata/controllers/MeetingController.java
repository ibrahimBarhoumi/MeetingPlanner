package com.canalplus.meetingplannerkata.controllers;

import com.canalplus.meetingplannerkata.domain.dtos.MeetingDto;
import com.canalplus.meetingplannerkata.domain.entities.Meeting;
import com.canalplus.meetingplannerkata.services.MeetingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author IBRAHIM
 */
@RestController
@RequestMapping(value = "/api/meetingPlanner/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @PostMapping("")
    public ResponseEntity<?> doReserve(@RequestBody MeetingDto meetingDto) {
        try {
            Meeting reservedMeeting = meetingService.createMeetingReservation(meetingDto);
            return ResponseEntity.status(HttpStatus.OK).body(reservedMeeting);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }
}
