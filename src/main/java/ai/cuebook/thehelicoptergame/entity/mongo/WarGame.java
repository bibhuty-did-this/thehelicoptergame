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
    private Long startTime;

    public WarGame(String player1, String player2, Long startTime) {
        this.player1 = player1;
        this.player2 = player2;
        this.startTime=startTime;
    }
}
