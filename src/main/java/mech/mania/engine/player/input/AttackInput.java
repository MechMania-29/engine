package mech.mania.engine.player.input;

import mech.mania.engine.character.CharacterState;
import mech.mania.engine.character.action.AttackAction;
import mech.mania.engine.character.action.MoveAction;
import mech.mania.engine.terrain.TerrainState;

import java.util.List;
import java.util.Map;

public record AttackInput(Map<String, List<AttackAction>> possibleAttacks,
                          int turn,
                          Map<String, CharacterState> characterStates,
                          Map<String, TerrainState> terrainStates) {}
