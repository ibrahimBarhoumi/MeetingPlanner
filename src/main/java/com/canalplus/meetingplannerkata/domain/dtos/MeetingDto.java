package com.canalplus.meetingplannerkata.domain.dtos;

import com.canalplus.meetingplannerkata.domain.entities.Room;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author IBRAHIM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MeetingDto implements Serializable {

    private String name;
    private LocalDateTime startTime;
    private String typeMeeting;
    private int numberContributor;
    private Room room;
}
