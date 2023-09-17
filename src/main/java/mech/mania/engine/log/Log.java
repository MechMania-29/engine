package mech.mania.engine.log;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Log {
    @JsonProperty("setup")
    private LogSetupState setupState;
    @JsonProperty("scores")
    private LogScores scores;
    @JsonProperty("stats")
    private LogStats stats;
    @JsonProperty("errors")
    private LogErrors errors;
    @JsonProperty("turns")
    private List<LogTurnState> turnStates;

    public Log() {
        this.setupState = new LogSetupState();
        this.turnStates = new ArrayList<>();
    }

    public void storeDiffs(Map<String, Map<String, JsonNode>> characterStateDiffs,
                           Map<String, Map<String, JsonNode>> terrainStateDiffs) {
        int turn = turnStates.size();
        LogTurnState turnState = new LogTurnState(turn, characterStateDiffs, terrainStateDiffs);

        turnStates.add(turnState);
    }

    public void storeResults(LogScores scores, LogStats stats, LogErrors errors) {
        this.scores = scores;
        this.stats = stats;
        this.errors = errors;
    }

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);;
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
