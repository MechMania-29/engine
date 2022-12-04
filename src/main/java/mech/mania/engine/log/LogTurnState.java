package mech.mania.engine.log;

import com.fasterxml.jackson.annotation.JsonProperty;
import mech.mania.engine.character.CharacterState;

import java.util.Arrays;
import java.util.List;

import static mech.mania.engine.Config.TOTAL_CHARACTERS;

public class LogTurnState {
    @JsonProperty("turn")
    private int turn;
    @JsonProperty("characters")
    private List<CharacterState> modifiedCharacterStates;

    public LogTurnState(int turn) {
        this.turn = turn;
        this.modifiedCharacterStates = Arrays.asList(new CharacterState[TOTAL_CHARACTERS]);

        for (int i = 0; i < TOTAL_CHARACTERS; i++) {
            modifiedCharacterStates.set(i, null);
        }
    }

    public List<CharacterState> getModifiedCharacterStates() {
        return modifiedCharacterStates;
    }
}
