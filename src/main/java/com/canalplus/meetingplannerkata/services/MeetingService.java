package com.canalplus.meetingplannerkata.services;

import com.canalplus.meetingplannerkata.domain.dtos.MeetingDto;
import com.canalplus.meetingplannerkata.domain.entities.Meeting;
import com.canalplus.meetingplannerkata.domain.entities.Room;
import com.canalplus.meetingplannerkata.enums.MeetingTypeEnum;
import com.canalplus.meetingplannerkata.exceptions.MeetingStartDateTimeException;
import com.canalplus.meetingplannerkata.exceptions.MeetingTypeException;
import com.canalplus.meetingplannerkata.exceptions.NoRoomsAvailableException;
import com.canalplus.meetingplannerkata.repositories.MeetingRepository;
import com.canalplus.meetingplannerkata.repositories.RoomRepository;
import com.canalplus.meetingplannerkata.utils.MeetingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author IBRAHIM
 */

@Service
public class MeetingService {

    private static final double ACCEPTED_PERCENTAGE_PERSON = 0.7;
    private static final List<String> VCEQ = List.of("ecran", "cam", "pieuvre");
    private static final List<String> RCEQ = List.of("ecran", "tableau", "pieuvre");
    private static final List<String> SPEQ = List.of("tableau");

    private final Logger log = LoggerFactory.getLogger(MeetingService.class);
    private final MeetingRepository meetingRepository;
    private final RoomRepository roomRepository;
    private final MongoTemplate mongoTemplate;


    public MeetingService(MeetingRepository meetingRepository, RoomRepository roomRepository, MongoTemplate mongoTemplate) {
        this.meetingRepository = meetingRepository;
        this.roomRepository = roomRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public Meeting createMeetingReservation(MeetingDto meetingDto) throws MeetingStartDateTimeException, MeetingTypeException, NoRoomsAvailableException {
        verifyMeetingRequest(meetingDto);
        List<Room> availableRooms = getAvailableRooms(meetingDto);
        if (availableRooms.isEmpty()) {
            log.error("[ERROR] No room's available with time=[{}] and participants number=[{}]", meetingDto.getStartTime(), meetingDto.getNumberContributor());
            throw new NoRoomsAvailableException("No rooms available");
        }
        Optional<Room> room = getBestMatchRoom(meetingDto, availableRooms);
        if (room.isPresent()) {
            Meeting meeting = Meeting.builder()
                    .name(meetingDto.getName())
                    .typeMeeting(meetingDto.getTypeMeeting())
                    .startTime(meetingDto.getStartTime())
                    .numberContributor(meetingDto.getNumberContributor())
                    .room(room.get())
                    .build();
            meetingRepository.save(meeting);
            log.info("[INFO] Meeting reservation [{}] was been saved ", meeting);
            return meeting;
        }
        return null;
    }

    private void verifyMeetingRequest(MeetingDto meeting) throws MeetingStartDateTimeException, MeetingTypeException {
        verifySlotReservation(meeting.getStartTime());
        verifyMeetingType(meeting.getTypeMeeting());
    }

    private void verifyMeetingType(String typeMeeting) throws MeetingTypeException {
        if (Arrays.stream(MeetingTypeEnum.values()).noneMatch(meetingTypeEnum -> meetingTypeEnum.name().equals(typeMeeting))) {
            log.error("[ERROR] Meeting type=[{}] not recognised", typeMeeting);
            throw new MeetingTypeException("Meeting type not recognised");
        }
    }

    private void verifySlotReservation(LocalDateTime meetingStartTime) throws MeetingStartDateTimeException {
        if (meetingStartTime.getDayOfWeek() == DayOfWeek.SATURDAY || meetingStartTime.getDayOfWeek() == DayOfWeek.SUNDAY
                || meetingStartTime.getHour() > 20 || meetingStartTime.getHour() < 8) {
            log.error("[ERROR] Slot must be between 08h and 20h");
            throw new MeetingStartDateTimeException("Invalid date/time reservation");
        }
    }

    private Optional<Room> getBestMatchRoom(MeetingDto meeting, List<Room> availableRooms) {
        Optional<Room> room = Optional.empty();
        switch (meeting.getTypeMeeting()) {
            case "VC":
                room = searchBestRoomWithAvailableEquipment(availableRooms, meeting, VCEQ);
                break;
            case "SPEC":
                room = searchBestRoomWithAvailableEquipment(availableRooms, meeting, SPEQ);
                break;
            case "RS":
                room = searchBestRoomWithAvailableEquipment(availableRooms, meeting, new ArrayList<>());
                break;
            case "RC":
                room = searchBestRoomWithAvailableEquipment(availableRooms, meeting, RCEQ);
                break;
            default:
                break;
        }
        return room;
    }

    private Optional<Room> searchBestRoomWithAvailableEquipment(List<Room> availableRooms, MeetingDto meetingDto, List<String> equipments) {

        HashMap<Room, Integer> roomScore = new HashMap<>();
        availableRooms.forEach(room -> {
            List<String> roomMaterial = new ArrayList<>(Arrays.asList(room.getEquipments().trim().toLowerCase().split(",")));
            roomScore.put(room, 0);
            equipments.forEach(material -> {
                if (roomMaterial.contains(material.trim())) {
                    roomScore.put(room, roomScore.get(room) + 1);
                } else {
                    roomScore.put(room, roomScore.get(room) - 1);
                }
            });
        });

        Integer bestScore;
        Optional<Room> bestMatch;
        if (roomScore.containsValue(0)) {
            bestMatch = roomScore.entrySet()
                    .stream()
                    .filter(entry -> Objects.equals(entry.getValue(), 0))
                    .map(Map.Entry::getKey).collect(Collectors.toList()).stream().min(Comparator.comparingInt(Room::getNormalMaximalCapacity));
        } else {
            bestMatch = Optional.ofNullable(Collections.max(roomScore.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey());
        }

        bestScore = roomScore.get(bestMatch.orElse(null));


        if (bestScore >= equipments.size()) {
            return bestMatch;
        } else {
            Map<String, Integer> remainingEquipment = getRemainingEquipment(meetingDto.getStartTime());
            List<Object> foundEquipment = remainingEquipment.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() > 0 && getMeetingEquipments(Objects.requireNonNull(MeetingMapper.newInstance().convert(meetingDto))).contains(entry.getKey()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            if (foundEquipment.isEmpty()) {
                return Optional.empty();
            }

        }

        return bestMatch;

    }

    private Map<String, Integer> getRemainingEquipment(LocalDateTime startTime) {
        final List<Meeting> outstandingMeetings = meetingRepository.getOutStandingMeetings(mongoTemplate, startTime);
        HashMap<String, Integer> remainingEquipment = new HashMap<>();
        remainingEquipment.put("tableau", 2);
        remainingEquipment.put("ecran", 5);
        remainingEquipment.put("cam", 4);
        remainingEquipment.put("pieuvre", 4);
        outstandingMeetings.forEach(meeting -> {
            List<String> requiredEquipment = getMeetingEquipments(meeting);
            List<String> reserveEquipment = new ArrayList<>(requiredEquipment);
            reserveEquipment.removeAll(Arrays.asList(meeting.getRoom().getEquipments().toLowerCase().split(",")));
            reserveEquipment.forEach(equipment -> remainingEquipment.put(equipment, remainingEquipment.get(equipment) - 1));
        });
        return remainingEquipment;
    }

    private List<String> getMeetingEquipments(Meeting meeting) {
        List<String> meetingEquipments = new ArrayList<>();
        switch (meeting.getTypeMeeting()) {
            case "VC":
                meetingEquipments = VCEQ;
                break;
            case "SPEC":
                meetingEquipments = SPEQ;
                break;
            case "RC":
                meetingEquipments = RCEQ;
                break;
            default:
                break;
        }
        return meetingEquipments;
    }


    private List<Room> getAvailableRooms(MeetingDto meeting) {
        List<Room> unavailableRooms = meetingRepository.getUnavailableRooms(meeting.getStartTime(), meeting.getStartTime().minusHours(1), meeting.getStartTime().plusHours(1), mongoTemplate)
                .stream()
                .map(Meeting::getRoom)
                .collect(Collectors.toList());
        List<Room> roomList = roomRepository.findAll();
        roomList.removeAll(unavailableRooms);

        return roomList
                .stream()
                .filter(room -> room.getNormalMaximalCapacity() * ACCEPTED_PERCENTAGE_PERSON >= meeting.getNumberContributor())
                .collect(Collectors.toList());
    }
}
