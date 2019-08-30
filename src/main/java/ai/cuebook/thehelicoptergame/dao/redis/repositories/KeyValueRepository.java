package ai.cuebook.thehelicoptergame.dao.redis.repositories;


import java.util.Map;

public interface KeyValueRepository {
    void add(String key,String hash,String value);
    void update(String key,String hash, String value);
    void delete(String key,String hash);
    String getById(String key,String hash);
    Map<String, String > findAll(String key);

}
