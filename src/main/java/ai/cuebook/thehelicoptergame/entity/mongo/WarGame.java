package ai.cuebook.thehelicoptergame.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "WarGame")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarGame {
    @Id
    private String id;
    private String player1;
    private String player2;
    private int player1Offence;
    private int player1Defence;
    private int player2Offence;
    private int player2Defence;
    private Long firstRoundStartTime;
    private Long firstRoundEndTime;
    private Long secondRoungStartTime;
    private Long secondRoundEndTime;
    private Long startTime;

    public WarGame(String player1, String player2, Long startTime) {
        this.player1 = player1;
        this.player2 = player2;
        this.startTime=startTime;
    }
}
