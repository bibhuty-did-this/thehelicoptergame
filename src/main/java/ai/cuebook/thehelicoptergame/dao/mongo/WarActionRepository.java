package ai.cuebook.thehelicoptergame.dao.redis.mongo;


import ai.cuebook.thehelicoptergame.entity.mongo.WarAction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WarActionRepository extends MongoRepository<WarAction,String> {
}
