package mech.mania.engine.character.action;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class AttackAction extends Action {
    private final String attackingId;
    private final AttackActionType type;

    @JsonCreator
    public AttackAction(@JsonProperty("executingCharacterId") String executingCharacterId,
                        @JsonProperty("attackingId") String attackingId,
                        @JsonProperty("type") AttackActionType type) {
        super(executingCharacterId);

        this.attackingId = attackingId;
        this.type = type;
    }

    public String getAttackingId() {
        return attackingId;
    }

    public AttackActionType getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AttackAction other)) {
            return false;
        }

        return Objects.equals(getExecutingCharacterId(), other.getExecutingCharacterId()) &&
                Objects.equals(attackingId, other.attackingId) &&
                type == other.type;
    }

    @Override
    public String toString() {
        return "AttackAction{" +
                "executingCharacterId=" + this.getExecutingCharacterId() + ", " +
                "attackingId=" + attackingId + ", " +
                "type=" + type +
                '}';
    }
}
