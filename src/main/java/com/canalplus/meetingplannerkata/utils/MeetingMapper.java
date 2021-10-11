package com.canalplus.meetingplannerkata.utils;

import com.canalplus.meetingplannerkata.domain.dtos.MeetingDto;
import com.canalplus.meetingplannerkata.domain.entities.Meeting;
import lombok.Data;
import org.springframework.core.convert.converter.Converter;

/**
 * @author IBRAHIM
 */
@Data(staticConstructor = "newInstance")
public class MeetingMapper implements Converter<MeetingDto, Meeting> {

    @Override
    public Meeting convert(MeetingDto source) {

        return Meeting.builder()
                .name(source.getName())
                .numberContributor(source.getNumberContributor())
                .typeMeeting(source.getTypeMeeting())
                .startTime(source.getStartTime())
                .build();


    }
}
