package ai.cuebook.thehelicoptergame.dto.request;

import ai.cuebook.thehelicoptergame.enums.Actions;
import ai.cuebook.thehelicoptergame.enums.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequest {
    private Player playerNo;
    private Actions action;
}
