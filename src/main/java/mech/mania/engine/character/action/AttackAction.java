package mech.mania.engine.character.action;

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
}
