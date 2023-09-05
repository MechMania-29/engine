package mech.mania.engine.player;

import mech.mania.engine.character.CharacterState;
import mech.mania.engine.character.action.AttackAction;
import mech.mania.engine.log.LogScores;
import mech.mania.engine.log.LogStats;
import mech.mania.engine.terrain.TerrainState;

import java.util.List;
import java.util.Map;

public record FinishInput(LogScores scores, LogStats stats) {}
