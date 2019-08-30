package ai.cuebook.thehelicoptergame.dao.redis.mongo;

import ai.cuebook.thehelicoptergame.entity.mongo.WarGame;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WarGameRepository extends MongoRepository<WarGame,String> {
}
