package mech.mania.engine.player;

import mech.mania.engine.character.CharacterState;
import mech.mania.engine.character.action.AbilityAction;
import mech.mania.engine.character.action.AttackAction;
import mech.mania.engine.terrain.TerrainState;

import java.util.List;
import java.util.Map;

public record AbilityInput(Map<String, List<AbilityAction>> possibleAbilities,
                           int turn,
                           Map<String, CharacterState> characterStates,
                           Map<String, TerrainState> terrainStates) {}
