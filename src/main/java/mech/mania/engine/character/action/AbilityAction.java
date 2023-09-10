package mech.mania.engine.character.action;

import mech.mania.engine.util.Position;

import java.util.Objects;

public class AbilityAction extends Action {
    private final AbilityActionType type;
    private final Position positionalTarget;
    private final String characterIdTarget;

    public AbilityAction(String executingCharacterId, AbilityActionType type, Position positionalTarget, String characterIdTarget) {
        super(executingCharacterId);

        this.type = type;
        this.positionalTarget = positionalTarget;
        this.characterIdTarget = characterIdTarget;
    }

    public AbilityActionType getType() {
        return type;
    }

    public Position getPositionalTarget() {
        return positionalTarget;
    }

    public String getCharacterIdTarget() {
        return characterIdTarget;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbilityAction other)) {
            return false;
        }

        return Objects.equals(getExecutingCharacterId(), other.getExecutingCharacterId()) &&
                Objects.equals(positionalTarget, other.positionalTarget) &&
                Objects.equals(characterIdTarget, other.characterIdTarget) &&
                type == other.type;
    }
}
