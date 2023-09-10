package mech.mania.engine.character;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mech.mania.engine.character.action.AbilityAction;
import mech.mania.engine.character.action.AttackAction;
import mech.mania.engine.util.Diffable;
import mech.mania.engine.util.Position;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mech.mania.engine.Config.*;

// A current state of a character. Basically a character at a certain point in time.
public class CharacterState implements Cloneable, Diffable {
    private final String id;
    private Position position;
    private boolean isZombie;
    private CharacterClassType classType;
    private List<CharacterClassAbility> abilities;
    private int health;
    private int moveSpeed;
    private int attackRange;
    private int attackCooldown;
    private int attackCooldownLeft = 0;
    private int abilityCooldownLeft = 0;
    private int stunnedEffectLeft = 0;

    private AttackAction attackAction;
    private AbilityAction abilityAction;

    public CharacterState(String id, Position position, boolean isZombie, CharacterClassType classType) {
        this.id = id;
        this.position = position;
        this.isZombie = isZombie;
        applyClass(classType);
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

    public List<CharacterClassAbility> getAbilities() {
        return abilities;
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

    public boolean isStunned() {
        return stunnedEffectLeft > 0;
    }

    public boolean canMove() {
        return !isStunned();
    }

    public boolean canAttack() {
        return attackCooldownLeft == 0 && !isStunned();
    }

    public boolean canAbility() {
        return abilityCooldownLeft == 0 && !isStunned();
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    private void applyClass(CharacterClassType classType) {
        this.classType = classType;

        CharacterClassData classData = CLASSES.get(classType);

        if (classData == null) {
            throw new RuntimeException(String.format("Invalid classType '%s'", classType.toString()));
        }

        this.health = classData.health();
        this.moveSpeed = classData.moveSpeed();
        this.attackRange = classData.attackRange();
        this.attackCooldown = classData.attackCooldown();
        this.abilities = classData.abilities();
    }

    public void makeZombie() {
        isZombie = true;
        applyClass(CharacterClassType.ZOMBIE);
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void resetAttackCooldownLeft() {
        // This is +1 because the reset is immediately decremented at the end of the turn
        this.attackCooldownLeft = this.attackCooldown + 1;
    }

    public void resetAbilityCooldownLeft() {
        // This is +1 because the reset is immediately decremented at the end of the turn
        this.abilityCooldownLeft = ABILITY_COOLDOWN + 1;
    }

    public void stun() {
        // This is +1 because the reset is immediately decremented at the end of the turn
        this.stunnedEffectLeft = STUNNED_DURATION + 1;
    }

    public void applyCooldownAndEffectDecay() {
        if (this.attackCooldownLeft > 0) {
            this.attackCooldownLeft -= 1;
        }

        if (this.abilityCooldownLeft > 0) {
            this.abilityCooldownLeft -= 1;
        }

        if (this.stunnedEffectLeft > 0) {
            this.stunnedEffectLeft -= 1;
        }
    }

    public void clearActions() {
        this.attackAction = null;
        this.abilityAction = null;
    }

    public void setAttackAction(AttackAction attackAction) {
        this.attackAction = attackAction;
    }

    public void setAbilityAction(AbilityAction abilityAction) {
        this.abilityAction = abilityAction;
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

        if (previousCharacterState == null || classType != previousCharacterState.classType || !DIFF_MODE_ENABLED) {
            diff.put("class", mapper.valueToTree(classType));
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

        if (previousCharacterState == null || abilityAction != previousCharacterState.abilityAction || !DIFF_MODE_ENABLED) {
            diff.put("abilityAction", mapper.valueToTree(abilityAction));
        }

        if (diff.isEmpty()) {
            return null;
        }

        return diff;
    }
}

