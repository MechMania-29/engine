package mech.mania.engine.log;

import static mech.mania.engine.Config.TURNS;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Arrays;
import java.util.List;

public class Log {
    @JsonProperty("setup")
    private LogSetupState setupState;
    @JsonProperty("turns")
    private List<LogTurnState> turnStates;

    public Log(LogSetupState setupState) {
        this.setupState = setupState;
        this.turnStates = Arrays.asList(new LogTurnState[TURNS + 1]);

        for (int i = 0; i < TURNS + 1; i++) {
            turnStates.set(i, new LogTurnState(i));
        }
    }

    public List<LogTurnState> getTurnStates() {
        return turnStates;
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
