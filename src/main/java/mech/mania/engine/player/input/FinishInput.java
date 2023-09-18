package mech.mania.engine.player.input;

import mech.mania.engine.log.LogErrors;
import mech.mania.engine.log.LogScores;
import mech.mania.engine.log.LogStats;

public record FinishInput(LogScores scores, LogStats stats, LogErrors errors, int turn) {}
