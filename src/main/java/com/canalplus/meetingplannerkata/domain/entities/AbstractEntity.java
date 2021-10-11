package com.canalplus.meetingplannerkata.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractEntity {
    @Id
    private String id;

}
