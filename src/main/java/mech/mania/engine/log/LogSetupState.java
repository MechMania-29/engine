package mech.mania.engine.log;

import com.fasterxml.jackson.annotation.JsonProperty;

import static mech.mania.engine.Config.*;

public class LogSetupState {
    @JsonProperty("theAnswerToLifeTheUniverseAndEverything")
    private final int theAnswerToLifeTheUniverseAndEverything;
    @JsonProperty("diffModeEnabled")
    private final boolean diffModeEnabled;
    @JsonProperty("boardSize")
    private final int boardSize;
    @JsonProperty("turns")
    private final int turns;
    @JsonProperty("totalCharacters")
    private final int totalCharacters;

    public LogSetupState() {
        this.theAnswerToLifeTheUniverseAndEverything = 42;
        this.diffModeEnabled = DIFF_MODE_ENABLED;
        this.boardSize = BOARD_SIZE;
        this.turns = TURNS;
        this.totalCharacters = TOTAL_CHARACTERS;
    }
}
