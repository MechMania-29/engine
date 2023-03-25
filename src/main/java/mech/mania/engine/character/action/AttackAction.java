package mech.mania.engine.character.action;

import java.util.Objects;

public class AttackAction extends Action {
    private final String attackingId;
    private final AttackActionType type;

    public AttackAction(String executingCharacterId, String attackingId, AttackActionType type) {
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
}
