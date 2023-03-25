package mech.mania.engine.character.action;

// The base action class
public class Action {
    private String executingCharacterId; // The id of the character executing this action

    public Action(String executingCharacterId) {
        this.executingCharacterId = executingCharacterId;
    }

    public String getExecutingCharacterId() {
        return executingCharacterId;
    }
}
