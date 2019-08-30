package ai.cuebook.thehelicoptergame.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartGameRequest {
    String player1Name;
    String player2Name;
    Long startTime=System.currentTimeMillis();
}
