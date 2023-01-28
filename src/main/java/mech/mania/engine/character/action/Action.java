package mech.mania.engine.character.action;

// The base action class
public class Action {
    private String executingCharacterId; // The id of the character executing this action
    private boolean isValid = true; // Whether this action is valid

    public Action(String executingCharacterId) {
        this.executingCharacterId = executingCharacterId;
    }

    public String getExecutingCharacterId() {
        return executingCharacterId;
    }

    // Invalidates the current action, making it not valid
    public void invalidate() {
        isValid = false;
    }
}
