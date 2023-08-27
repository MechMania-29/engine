package mech.mania.engine.character;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mech.mania.engine.character.action.AttackAction;
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
    private int health;
    private int moveSpeed;
    private int attackRange;
    private int attackCooldown;
    private int attackCooldownLeft = 0;
    private int stunnedEffectLeft = 0;

    private AttackAction attackAction;

    public CharacterState(String id, Position position, boolean isZombie) {
        this.id = id;
        this.position = position;
        this.isZombie = isZombie;
        this.health = isZombie ? ZOMBIE_HEALTH : HUMAN_HEALTH;
        this.moveSpeed = isZombie ? ZOMBIE_MOVE_SPEED : HUMAN_MOVE_SPEED;
        this.attackRange = isZombie ? ZOMBIE_ATTACK_RANGE : HUMAN_ATTACK_RANGE;
        this.attackCooldown = isZombie ? ZOMBIE_ATTACK_COOLDOWN : HUMAN_ATTACK_COOLDOWN;
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

    public int getHealth() {
        return health;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public boolean canMove() {
        return stunnedEffectLeft == 0;
    }

    protected boolean isStunned() {
        return stunnedEffectLeft == 0;
    }

    public boolean canAttack() {
        return attackCooldownLeft == 0 && stunnedEffectLeft == 0;
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

    public void setHealth(int health) {
        this.health = health;
    }

    public void resetAttackCooldownLeft() {
        // This is +1 because the reset is immediately decremented at the end of the turn
        this.attackCooldownLeft = this.attackCooldown + 1;
    }

    public void stun() {
        this.stunnedEffectLeft = STUNNED_DURATION;
    }

    public void applyCooldownAndEffectDecay() {
        if (this.attackCooldownLeft > 0) {
            this.attackCooldownLeft -= 1;
        }

        if (this.stunnedEffectLeft > 0) {
            this.stunnedEffectLeft -= 1;
        }
    }

    public void clearActions() {
        this.attackAction = null;
    }

    public void setAttackAction(AttackAction attackAction) {
        this.attackAction = attackAction;
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

        if (previousCharacterState == null || isStunned() != previousCharacterState.isStunned() || !DIFF_MODE_ENABLED) {
            diff.put("isStunned", mapper.valueToTree(isStunned()));
        }

        if (previousCharacterState == null || attackAction != previousCharacterState.attackAction || !DIFF_MODE_ENABLED) {
            diff.put("attackAction", mapper.valueToTree(attackAction));
        }

        if (diff.isEmpty()) {
            return null;
        }

        return diff;
    }
}

