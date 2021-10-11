package com.canalplus.meetingplannerkata.domain.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author IBRAHIM
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "room")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Room extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String roomName;
    private int normalMaximalCapacity;
    private String equipments;
}
