package mech.mania.engine.log;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

public class LogTurnState {
    @JsonProperty("turn")
    private int turn;
    @JsonProperty("characters")
    private Map<String, Map<String, JsonNode>> characterStateDiffs;

    public LogTurnState(int turn, Map<String, Map<String, JsonNode>> characterStateDiffs) {
        this.turn = turn;
        this.characterStateDiffs = characterStateDiffs;
    }
}
