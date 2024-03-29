package mech.mania.engine.player.input;

import mech.mania.engine.character.CharacterClassType;

import java.util.List;

public record ChooseClassesInput(List<CharacterClassType> choices, int numToPick, int maxPerSameClass, int turn) {}
