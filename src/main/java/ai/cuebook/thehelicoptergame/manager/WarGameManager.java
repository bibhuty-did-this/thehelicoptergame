package ai.cuebook.thehelicoptergame.manager;

import ai.cuebook.thehelicoptergame.dao.redis.KeyValueDAO;
import ai.cuebook.thehelicoptergame.dao.redis.WarEventDAO;
import ai.cuebook.thehelicoptergame.dto.request.EventRequest;
import ai.cuebook.thehelicoptergame.dto.request.StartGameRequest;
import ai.cuebook.thehelicoptergame.entity.kafka.WarEvent;
import ai.cuebook.thehelicoptergame.entity.mongo.WarAction;
import ai.cuebook.thehelicoptergame.entity.mongo.WarGame;
import ai.cuebook.thehelicoptergame.enums.Actions;
import ai.cuebook.thehelicoptergame.enums.Player;
import ai.cuebook.thehelicoptergame.dao.redis.mongo.WarActionRepository;
import ai.cuebook.thehelicoptergame.dao.redis.mongo.WarGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WarGameManager {

    @Autowired
    private WarGameRepository warGameRepository;

    @Autowired
    private WarActionRepository warActionRepository;

    @Autowired
    private WarEventDAO warEventDAO;

    @Autowired
    private KeyValueDAO keyValueDAO;


    @Autowired
    private KafkaTemplate<String,WarEvent> publisher;

    private String topic="warEvent";


    public WarGame startWar(StartGameRequest startGameRequest) {
        WarGame warGame=new WarGame(startGameRequest.getPlayer1Name(),startGameRequest.getPlayer2Name(),startGameRequest.getStartTime());
        warGameRepository.save(warGame);
        return warGame;
    }

    public void startFirstRound() {
        WarAction player1Action=new WarAction(Player.FIRST_PLAYER, Actions.START_FIRST_ROUND_OFFENCE);
        WarAction player2Action=new WarAction(Player.SECOND_PLAYER, Actions.START_FIRST_ROUND_DEFENCE);

        keyValueDAO.add(Player.FIRST_PLAYER.toString(),"points","0");
        keyValueDAO.add(Player.SECOND_PLAYER.toString(),"points","0");

        warActionRepository.save(player1Action);
        warActionRepository.save(player2Action);
    }

    public void startSecondRound() {
        WarAction player1Action=new WarAction(Player.FIRST_PLAYER, Actions.START_SECOND_ROUND_DEFENCE);
        WarAction player2Action=new WarAction(Player.SECOND_PLAYER, Actions.START_SECOND_ROUND_OFFENCE);

        warActionRepository.save(player1Action);
        warActionRepository.save(player2Action);
    }

    public void pushEventToQueue(EventRequest eventRequest) {
        WarEvent warEvent=new WarEvent();
        warEvent.prepareWarEvent(eventRequest);
        publisher.send(topic,warEvent);
        new Thread(() -> {
            WarAction warAction=new WarAction(eventRequest.getPlayerNo(),eventRequest.getAction());
            warActionRepository.save(warAction);
        }).start();
    }
}
