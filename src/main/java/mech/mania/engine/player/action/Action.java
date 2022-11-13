package mech.mania.engine.player.action;

// The base action class
public class Action {
    private int executingPlayerId; // The id of the player executing this action
    private boolean isValid = true; // Whether this action is valid

    public Action(int executorPlayerId) {
        this.executingPlayerId = executorPlayerId;
    }

    public int getExecutingPlayerId() {
        return executingPlayerId;
    }

    // Invalidates the current action, making it not valid
    public void invalidate() {
        isValid = false;
    }
}
