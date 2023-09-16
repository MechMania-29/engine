package mech.mania.engine.player.input;

import mech.mania.engine.character.CharacterState;
import mech.mania.engine.character.action.MoveAction;
import mech.mania.engine.terrain.TerrainState;

import java.util.List;
import java.util.Map;

public record MoveInput(Map<String, List<MoveAction>> possibleMoves,
                        int turn,
                        Map<String, CharacterState> characterStates,
                        Map<String, TerrainState> terrainStates) {}
