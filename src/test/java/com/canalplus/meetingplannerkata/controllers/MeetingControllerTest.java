package com.canalplus.meetingplannerkata.controllers;

import com.canalplus.meetingplannerkata.domain.dtos.MeetingDto;
import com.canalplus.meetingplannerkata.domain.entities.Meeting;
import com.canalplus.meetingplannerkata.domain.entities.Room;
import com.canalplus.meetingplannerkata.repositories.MeetingRepository;
import com.canalplus.meetingplannerkata.repositories.RoomRepository;
import com.canalplus.meetingplannerkata.services.MeetingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author IBRAHIM
 */


@ExtendWith(SpringExtension.class)
@WebMvcTest(MeetingController.class)
class MeetingControllerTest {

    public static final String STRING_GOOD_DATE_TIME = "2021-03-04 08:00";
    @MockBean
    private MeetingService meetingService;
    @MockBean
    private MeetingRepository meetingRepository;
    @MockBean
    private RoomRepository roomRepository;
    @MockBean
    private MongoTemplate mongoTemplate;

    @Autowired
    MockMvc mockMvc;

    private Room room;
    private DateTimeFormatter formatter;
    private LocalDateTime dateTime;

    @BeforeEach
    void setUp() {
        room = buildRoom();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        dateTime = LocalDateTime.parse(STRING_GOOD_DATE_TIME, formatter);
    }

    @Test
    void doReservationTest() throws Exception {
        MeetingDto meetingDto = MeetingDto.builder().name("Meet 01").typeMeeting("RS").startTime(dateTime).build();
        Meeting meeting = Meeting.builder().typeMeeting(meetingDto.getTypeMeeting()).numberContributor(meetingDto.getNumberContributor()).name(meetingDto.getName()).startTime(meetingDto.getStartTime()).room(room).build();
        when(meetingService.createMeetingReservation(meetingDto)).thenReturn(meeting);
        when(meetingRepository.getUnavailableRooms(dateTime, dateTime.minusHours(1), dateTime.plusHours(1), mongoTemplate)).thenReturn(new ArrayList<>());
        doReturn(Collections.singletonList(room)).when(roomRepository).findAll();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/meetingPlanner/meetings"))
                .andExpect(status().isOk());
    }

    private Room buildRoom() {
        return Room.builder().roomName("Room 01").normalMaximalCapacity(10).build();
    }


}
