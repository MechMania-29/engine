package mech.mania.engine.player;

import java.util.ArrayList;
import java.util.List;

public class PlayerErrorLogger {
    private final List<String> logs;

    public PlayerErrorLogger() {
        logs = new ArrayList<>();
    }


    public List<String> getLogs() {
        return logs;
    }

    public void log(String s) {
        logs.add(s);
        System.err.println(s);
    }
}
