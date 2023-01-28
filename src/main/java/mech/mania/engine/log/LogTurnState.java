package mech.mania.engine.log;

import com.fasterxml.jackson.annotation.JsonProperty;
import mech.mania.engine.character.CharacterState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mech.mania.engine.Config.TOTAL_CHARACTERS;

public class LogTurnState {
    @JsonProperty("turn")
    private int turn;
    @JsonProperty("characters")
    private Map<String, CharacterState> modifiedCharacterStates;

    public LogTurnState(int turn) {
        this.turn = turn;
        this.modifiedCharacterStates = new HashMap<>();
    }

    public Map<String, CharacterState> getModifiedCharacterStates() {
        return modifiedCharacterStates;
    }
}
