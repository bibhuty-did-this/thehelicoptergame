package ai.cuebook.thehelicoptergame.entity.mongo;

import ai.cuebook.thehelicoptergame.enums.Actions;
import ai.cuebook.thehelicoptergame.enums.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "WarAction")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarAction {

    @Id
    private String id;
    private Player playerNo;
    private Actions action;
    private String reaction;
    private String point;

    public WarAction(Player playerNo, Actions action, String reaction, String point) {
        this.playerNo = playerNo;
        this.action = action;
        this.reaction = reaction;
        this.point = point;
    }

    public WarAction(Player playerNo, Actions action) {
        this.playerNo=playerNo;
        this.action=action;
    }
}
