package com.canalplus.meetingplannerkata.services;

import com.canalplus.meetingplannerkata.domain.dtos.MeetingDto;
import com.canalplus.meetingplannerkata.domain.entities.Room;
import com.canalplus.meetingplannerkata.exceptions.MeetingStartDateTimeException;
import com.canalplus.meetingplannerkata.exceptions.MeetingTypeException;
import com.canalplus.meetingplannerkata.exceptions.NoRoomsAvailableException;
import com.canalplus.meetingplannerkata.repositories.MeetingRepository;
import com.canalplus.meetingplannerkata.repositories.RoomRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * @author IBRAHIM
 */
@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {

    public static final String STRING_GOOD_DATE_TIME = "2021-03-04 08:00";
    @InjectMocks
    private MeetingService underTest;
    @Mock
    private MeetingRepository meetingRepository;
    @Mock
    private RoomRepository roomRepository;
    @SpyBean
    private MongoTemplate mongoTemplate;

    private Room room;
    private DateTimeFormatter formatter;
    private LocalDateTime dateTime;

    @BeforeEach
    void setUp() {
        room = buildRoom();
        underTest = new MeetingService(meetingRepository, roomRepository, mongoTemplate);
        MockitoAnnotations.initMocks(this);
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        dateTime = LocalDateTime.parse(STRING_GOOD_DATE_TIME, formatter);
    }

    @Test()
    @DisplayName("Test Should throw exception when no room available in this slot time")
    void shouldThrowNoRoomAvailableExceptionWhenThereIsNoAvailableRoomsBefore8And20() {
        String str = "2021-03-04 07:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        MeetingDto meetingDto = MeetingDto.builder().name("Meet 01").typeMeeting("RS").startTime(dateTime).build();
        Assertions.assertThrows(MeetingStartDateTimeException.class, () -> {
            underTest.createMeetingReservation(meetingDto);
        });
    }

    @Test()
    @DisplayName("Test Should throw exception when no room available with the good capacity")
    void shouldThrowMeetingStartDateTimeExceptionWhenThereIsNoRoomWithCapacity() {
        MeetingDto meetingDto = MeetingDto.builder().name("Meet 01").typeMeeting("RS").startTime(dateTime).numberContributor(10).build();
        when(meetingRepository.getUnavailableRooms(dateTime, dateTime.minusHours(1), dateTime.plusHours(1), mongoTemplate)).thenReturn(new ArrayList<>());
        doReturn(Collections.singletonList(room)).when(roomRepository).findAll();
        Assertions.assertThrows(NoRoomsAvailableException.class, () -> {
            underTest.createMeetingReservation(meetingDto);
        });
    }


    @Test()
    @DisplayName("Test Should throw exception when meeting type not recognised")
    void shouldThrowMeetingTypeExceptionWhenMeetingTypeIsNotRecognised() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(STRING_GOOD_DATE_TIME, formatter);
        MeetingDto meetingDto = MeetingDto.builder().name("Meet 01").typeMeeting("AZAZA").startTime(dateTime).numberContributor(5).build();
        Assertions.assertThrows(MeetingTypeException.class, () -> {
            underTest.createMeetingReservation(meetingDto);
        });
    }

    @Test()
    @DisplayName("Test Should search and create meeting with best room match")
    void shouldCreateMeeting() throws MeetingTypeException, NoRoomsAvailableException, MeetingStartDateTimeException {
        MeetingDto meetingDto = MeetingDto.builder().name("Meet 01").typeMeeting("RS").startTime(dateTime).numberContributor(5).build();
        when(meetingRepository.getUnavailableRooms(dateTime, dateTime.minusHours(1), dateTime.plusHours(1), mongoTemplate)).thenReturn(new ArrayList<>());
        doReturn(Collections.singletonList(room)).when(roomRepository).findAll();
        Assertions.assertNotNull(underTest.createMeetingReservation(meetingDto));
        Assertions.assertEquals(underTest.createMeetingReservation(meetingDto).getRoom(), room);

    }

    private Room buildRoom() {
        return Room.builder().roomName("Room 01").normalMaximalCapacity(10).build();
    }

}
