package ai.cuebook.thehelicoptergame.manager;

import ai.cuebook.thehelicoptergame.entity.kafka.WarEvent;
import ai.cuebook.thehelicoptergame.enums.Actions;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerManager {
    public void processMessage(WarEvent warEvent){
        Actions action=warEvent.getAction();
        switch (action){
            case GO_DOWN:
                break;
            case DRIVE_FAST:
                break;
            case DROP_BOMB_PERSONEL:
                break;
            case ROTATE_LEFT:
                break;
            case ROTATE_RIGHT:
                break;
            case SHOOT_HELICOPTER:
                break;
            case SHOOT_BOMB_PERSONEL:
                break;
            default:
                break;
        }
    }
}
