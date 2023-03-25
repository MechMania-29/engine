package mech.mania.engine.player;

import mech.mania.engine.GameState;
import mech.mania.engine.character.CharacterState;
import mech.mania.engine.character.action.AttackAction;
import mech.mania.engine.util.Position;
import mech.mania.engine.character.action.MoveAction;

import java.util.*;

public class Player {
    private int port;
    private boolean isComputer;
    private boolean isZombie;

    public Player(int port, boolean isComputer, boolean isZombie) {
        this.port = port;
        this.isComputer = isComputer;
        this.isZombie = isZombie;
    }

    public boolean isZombie() {
        return isZombie;
    }

    public List<MoveAction> getMoveInput(Map<String, Map<String, Position>> possibleMoves, Map<String, CharacterState> characterStates) {
        List<MoveAction> moveActions;
        if (!isComputer) {
            //TODO: Connect to port and read in data, and we can send possibleMoves to help client
            moveActions = new ArrayList<>();
        } else {
            // Otherwise, for computer, pick one of the valid moves and just do it
            Random rand = new Random();

            moveActions = new ArrayList<>();

            if (isZombie) {
                // Go towards human
                for (String id : possibleMoves.keySet()) {
                    Map<String, Position> possibleMovesForThisCharacter = possibleMoves.get(id);

                    if (possibleMovesForThisCharacter.isEmpty()) {
                        continue;
                    }

                    Position best = null;
                    int score = Integer.MAX_VALUE;

                    for (Position possibleMove : possibleMovesForThisCharacter.values()) {
                        int thisScore = 0;
                        for (CharacterState target : characterStates.values()) {
                            // Only target those that are not us
                            if (target.isZombie() == isZombie) {
                                continue;
                            }

                            // Add score
                            thisScore += Math.abs(possibleMove.getX() - target.getPosition().getX()) +
                                    Math.abs(possibleMove.getY() - target.getPosition().getY());
                        }

                        if (thisScore < score) {
                            score = thisScore;
                            best = possibleMove;
                        }
                    }

                    moveActions.add(new MoveAction(id, best));
                }
            } else {
                for (String id : possibleMoves.keySet()) {
                    Map<String, Position> possibleMovesForThisCharacter = possibleMoves.get(id);

                    if (possibleMovesForThisCharacter.isEmpty()) {
                        continue;
                    }

                    Position[] moves = possibleMovesForThisCharacter.values().toArray(new Position[0]);
                    Position newPosition = moves[rand.nextInt(moves.length)];

                    moveActions.add(new MoveAction(id, newPosition));
                }
            }
        }

        return moveActions;
    }

    public List<AttackAction> getAttackInput(Map<String, List<AttackAction>> possibleAttackActions) {
        List<AttackAction> attackActions;
        if (!isComputer) {
            //TODO: Connect to port and read in data, and we can send possibleMoves to help client
            attackActions = new ArrayList<>();
        } else {
            // Otherwise, for computer, pick one of the valid moves and just do it
            Random rand = new Random();

            attackActions = new ArrayList<>();

            for (String id : possibleAttackActions.keySet()) {
                List<AttackAction> possibleAttackActionsForThisCharacter = possibleAttackActions.get(id);
                if (possibleAttackActionsForThisCharacter.isEmpty()) {
                    continue;
                }

                AttackAction attackAction = possibleAttackActionsForThisCharacter.get(
                        rand.nextInt(possibleAttackActionsForThisCharacter.size())
                );

                attackActions.add(attackAction);
            }
        }

        return attackActions;
    }
}
