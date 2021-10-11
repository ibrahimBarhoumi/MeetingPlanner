package com.canalplus.meetingplannerkata.domain.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author IBRAHIM
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "meeting")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Meeting extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private LocalDateTime startTime;
    private String typeMeeting;
    private int numberContributor;
    private Room room;
}
