package mech.mania.engine.character.action;

// The base action class
public class Action {
    private int executingCharacterId; // The id of the character executing this action
    private boolean isValid = true; // Whether this action is valid

    public Action(int executingCharacterId) {
        this.executingCharacterId = executingCharacterId;
    }

    public int getExecutingCharacterId() {
        return executingCharacterId;
    }

    // Invalidates the current action, making it not valid
    public void invalidate() {
        isValid = false;
    }
}
