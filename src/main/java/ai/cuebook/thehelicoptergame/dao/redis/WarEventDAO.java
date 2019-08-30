package ai.cuebook.thehelicoptergame.dao.redis;

import ai.cuebook.thehelicoptergame.entity.kafka.WarEvent;
import ai.cuebook.thehelicoptergame.dao.redis.repositories.WarEventRepository;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class WarEventDAO implements WarEventRepository {
    RedisTemplate<String,WarEvent> redisTemplate;
    private HashOperations hashOperations;

    public WarEventDAO(RedisTemplate<String,WarEvent> redisTemplate) {
        this.redisTemplate=redisTemplate;
        hashOperations=redisTemplate.opsForHash();
    }

    @Override
    public void eventSave(WarEvent warEvent) {
        hashOperations.put("WarEvent","1",warEvent );
    }

    @Override
    public WarEvent findEventById(String id) {
        return (WarEvent) hashOperations.get("WarEvent",id);
    }

    @Override
    public Map<String,WarEvent> findAllEvents() {
        return hashOperations.entries("WarEvent");
    }

    @Override
    public void updateEvent(WarEvent warEvent) {
        eventSave(warEvent);
    }

    @Override
    public void deleteEvent(String id) {
        hashOperations.delete("WarEvent",id);
    }
}
