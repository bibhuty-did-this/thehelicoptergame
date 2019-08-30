package ai.cuebook.thehelicoptergame.dao.redis.repositories;

import ai.cuebook.thehelicoptergame.entity.kafka.WarEvent;

import java.util.Map;

public interface WarEventRepository  {

    void eventSave(WarEvent warEvent);
    WarEvent findEventById(String id);
    Map<String,WarEvent> findAllEvents();
    void updateEvent(WarEvent warEvent);
    void deleteEvent(String id);

}
