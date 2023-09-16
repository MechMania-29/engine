package mech.mania.engine.character;

import java.util.List;

public record CharacterClassData(int health, int moveSpeed, int attackRange, int attackCooldown, List<CharacterClassAbility> abilities) {}
