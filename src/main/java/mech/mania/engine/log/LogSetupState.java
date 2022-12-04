package mech.mania.engine.log;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LogSetupState {
    // TODO: Add any log setup states in here
    @JsonProperty("theAnswerToLifeTheUniverseAndEverything")
    private final int theAnswerToLifeTheUniverseAndEverything;

    public LogSetupState() {
        this.theAnswerToLifeTheUniverseAndEverything = 42;
    }
}
