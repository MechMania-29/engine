package mech.mania.engine.character;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mech.mania.engine.util.Diffable;
import mech.mania.engine.util.Position;

import java.util.HashMap;
import java.util.Map;

import static mech.mania.engine.Config.*;

// A current state of a character. Basically a character at a certain point in time.
public class CharacterState implements Cloneable, Diffable {
    private final String id;
    private Position position;
    private boolean isZombie;
    private int moveSpeed;
    private int attackRange;
    private int attackCooldown;
    private int attackCooldownLeft;
    private int health;

    public CharacterState(String id, Position position, boolean isZombie) {
        this.id = id;
        this.position = position;
        this.isZombie = isZombie;
        this.moveSpeed = isZombie ? ZOMBIE_MOVE_SPEED : HUMAN_MOVE_SPEED;
        this.attackRange = isZombie ? ZOMBIE_ATTACK_RANGE : HUMAN_ATTACK_RANGE;
        this.attackCooldown = isZombie ? ZOMBIE_ATTACK_COOLDOWN : HUMAN_ATTACK_COOLDOWN;
        this.attackCooldownLeft = 0;
        this.health = isZombie ? ZOMBIE_HEALTH : HUMAN_HEALTH;
    }

    public String getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isZombie() {
        return isZombie;
    }

    public int getMoveSpeed() {
        return moveSpeed;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public int getAttackCooldownLeft() {
        return attackCooldownLeft;
    }

    public int getHealth() {
        return health;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void makeZombie() {
        isZombie = true;
        moveSpeed = ZOMBIE_MOVE_SPEED;
        attackRange = ZOMBIE_ATTACK_RANGE;
        health = ZOMBIE_HEALTH;
    }

    public void decrementAttackCooldownLeft() {
        if (this.attackCooldownLeft == 0) {
            return;
        }
        this.attackCooldownLeft -= 1;
    }

    public void resetAttackCooldownLeft() {
        this.attackCooldownLeft = this.attackCooldown;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public CharacterState clone() {
        try {
            return (CharacterState) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, JsonNode> diff(Object previous) {
        if (this == previous) {
            return null;
        }
        CharacterState previousCharacterState = null;

        if (previous != null) {
            if (!(previous instanceof CharacterState cs)) {
                return null;
            }

            previousCharacterState = cs;

            if (!previousCharacterState.id.equals(id) && DIFF_MODE_ENABLED) {
                return null;
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, JsonNode> diff = new HashMap<>();

        if (previousCharacterState == null || position != previousCharacterState.position || !DIFF_MODE_ENABLED) {
            diff.put("position", mapper.valueToTree(position));
        }

        if (previousCharacterState == null || isZombie != previousCharacterState.isZombie || !DIFF_MODE_ENABLED) {
            diff.put("isZombie", mapper.valueToTree(isZombie));
        }

        if (previousCharacterState == null || health != previousCharacterState.health || !DIFF_MODE_ENABLED) {
            diff.put("health", mapper.valueToTree(health));
        }

        if (diff.isEmpty()) {
            return null;
        }

        return diff;
    }
}

