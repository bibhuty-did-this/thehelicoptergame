package ai.cuebook.thehelicoptergame.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisValueResponse {
    private String key;
    private String value;
}
