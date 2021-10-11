package com.canalplus.meetingplannerkata.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author IBRAHIM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RoomDto implements Serializable {

    private String roomName;
    private int normalMaximalCapacity;
    private String equipments;
}
