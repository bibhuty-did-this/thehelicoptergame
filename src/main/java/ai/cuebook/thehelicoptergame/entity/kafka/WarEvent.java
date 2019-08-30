package ai.cuebook.thehelicoptergame.entity.kafka;

import ai.cuebook.thehelicoptergame.dto.request.EventRequest;
import ai.cuebook.thehelicoptergame.enums.Actions;
import ai.cuebook.thehelicoptergame.enums.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarEvent{
    private Long timestamp=System.currentTimeMillis();
    private Player playerNo;
    private Actions action;
    private Integer movement;
    private Integer points;

    public void prepareWarEvent(EventRequest eventRequest){
        this.playerNo=eventRequest.getPlayerNo();
        this.action=eventRequest.getAction();
        this.movement=eventRequest.getMovement();
        this.points=eventRequest.getPoints();
    }

    @Override
    public String toString() {
        return "WarEvent{" +
                "timestamp=" + timestamp +
                ", playerNo=" + playerNo +
                ", action=" + action +
                ", movement=" + movement +
                ", points=" + points +
                '}';
    }
}
