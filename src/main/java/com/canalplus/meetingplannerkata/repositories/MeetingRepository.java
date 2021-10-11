package com.canalplus.meetingplannerkata.repositories;

import com.canalplus.meetingplannerkata.domain.entities.Meeting;
import com.canalplus.meetingplannerkata.repositories.common.ResourceRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author IBRAHIM
 */
public interface MeetingRepository extends ResourceRepository<Meeting, String> {

    default List<Meeting> getUnavailableRooms(LocalDateTime startTime, LocalDateTime startTimeOneHourLater, LocalDateTime startTimeInOneHourLater, MongoTemplate mongoTemplate) {
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("startTime").is(startTime), Criteria.where("startTime").is(startTimeOneHourLater), Criteria.where("startTime").is(startTimeInOneHourLater));
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Meeting.class);
    }

    default List<Meeting> getOutStandingMeetings(MongoTemplate mongoTemplate, LocalDateTime startTime) {
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("startTime").is(startTime));
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Meeting.class);
    }
}
