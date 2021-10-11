package com.canalplus.meetingplannerkata;

import com.canalplus.meetingplannerkata.repositories.common.ResourceRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(repositoryBaseClass = ResourceRepositoryImpl.class)
public class MeetingPlannerKataApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetingPlannerKataApplication.class, args);
    }

}
