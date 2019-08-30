package ai.cuebook.thehelicoptergame.dao.redis;

import ai.cuebook.thehelicoptergame.dao.redis.repositories.KeyValueRepository;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class KeyValueDAO implements KeyValueRepository {
    RedisTemplate<String, String> redisTemplate;
    private HashOperations hashOperations;

    public KeyValueDAO(RedisTemplate<String,String> redisTemplate) {
        this.redisTemplate=redisTemplate;
        hashOperations=redisTemplate.opsForHash();
    }

    @Override
    public void add(String key, String hash, String value) {
        hashOperations.put(key,hash,value);
    }

    @Override
    public void update(String key, String hash, String value) {
        add(key,hash,value);
    }

    @Override
    public void delete(String key, String hash) {
        hashOperations.delete(key,hash);
    }

    @Override
    public String getById(String key, String hash) {
        return (String) hashOperations.get(key,hash);
    }

    @Override
    public Map<String, String> findAll(String key) {
        Map<String,String > allEntries=hashOperations.entries(key);
        return allEntries;
    }
}
