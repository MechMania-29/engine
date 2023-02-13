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
    @JsonProperty("terrain")
    private Map<String, Map<String, JsonNode>> terrainStateDiffs;

    public LogTurnState(int turn, Map<String, Map<String, JsonNode>> characterStateDiffs, Map<String, Map<String, JsonNode>> terrainStateDiffs) {
        this.turn = turn;
        this.characterStateDiffs = characterStateDiffs;
        this.terrainStateDiffs = terrainStateDiffs;
    }
}
