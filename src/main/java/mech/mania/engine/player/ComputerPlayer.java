package mech.mania.engine.player;

import mech.mania.engine.character.CharacterClassType;
import mech.mania.engine.character.CharacterState;
import mech.mania.engine.character.action.AbilityAction;
import mech.mania.engine.character.action.AttackAction;
import mech.mania.engine.character.action.MoveAction;
import mech.mania.engine.log.LogScores;
import mech.mania.engine.log.LogStats;
import mech.mania.engine.player.input.*;
import mech.mania.engine.util.Position;

import java.util.*;

public class ComputerPlayer extends Player {
    public ComputerPlayer(boolean isZombie) {
        super(isZombie);
    }

    @Override
    public boolean isZombie() {
        return isZombie;
    }

    @Override
    public Map<CharacterClassType, Integer> getChosenClassesInput(ChooseClassesInput chooseClassesInput) {
        logStartAction();
        List<CharacterClassType> possibleChoices = chooseClassesInput.choices();
        int numToPick = chooseClassesInput.numToPick();
        int maxPerSameClass = chooseClassesInput.maxPerSameClass();

        Map<CharacterClassType, Integer> chosenClasses = new HashMap<>();

        Random rand = new Random();

        int soFar = 0;

        while (soFar < numToPick) {
            CharacterClassType selected = possibleChoices.get(rand.nextInt(0, possibleChoices.size()));
            if (!chosenClasses.containsKey(selected)) {
                chosenClasses.put(selected, 0);
            }

            int currentCount = chosenClasses.get(selected);
            if (currentCount < maxPerSameClass) {
                chosenClasses.put(selected, currentCount + 1);
                soFar += 1;
            }
        }

        return chosenClasses;
    }

    @Override
    public List<MoveAction> getMoveInput(MoveInput moveInput) {
        logStartAction();
        Map<String, List<MoveAction>> possibleMoves = moveInput.possibleMoves();
        Map<String, CharacterState> characterStates = moveInput.characterStates();

        List<MoveAction> moveActions = new ArrayList<>();
        Random rand = new Random();

        if (isZombie) {
            // Go towards human
            for (String id : possibleMoves.keySet()) {
                List<MoveAction> possibleMovesForThisCharacter = possibleMoves.get(id);

                if (possibleMovesForThisCharacter.isEmpty()) {
                    continue;
                }

                MoveAction best = null;
                int score = Integer.MAX_VALUE;

                for (MoveAction possibleMove : possibleMovesForThisCharacter) {
                    Position destination = possibleMove.getDestination();
                    int thisScore = 0;
                    for (CharacterState target : characterStates.values()) {
                        // Only target those that are not us
                        if (target.isZombie() == isZombie) {
                            continue;
                        }

                        // Add score
                        thisScore += Math.abs(destination.getX() - target.getPosition().getX()) +
                                Math.abs(destination.getY() - target.getPosition().getY());
                    }

                    if (thisScore < score) {
                        score = thisScore;
                        best = possibleMove;
                    }
                }

                moveActions.add(best);
            }
        } else {
            for (String id : possibleMoves.keySet()) {
                List<MoveAction> possibleMovesForThisCharacter = possibleMoves.get(id);

                if (possibleMovesForThisCharacter.isEmpty()) {
                    continue;
                }

                MoveAction[] moves = possibleMovesForThisCharacter.toArray(new MoveAction[0]);
                MoveAction moveAction = moves[rand.nextInt(moves.length)];

                moveActions.add(moveAction);
            }
        }

        logEndAction();

        return moveActions;
    }

    @Override
    public List<AttackAction> getAttackInput(AttackInput attackInput) {
        logStartAction();
        List<AttackAction> attackActions = new ArrayList<>();
        Random rand = new Random();

        Map<String, List<AttackAction>> possibleAttackActions = attackInput.possibleAttacks();

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

        logEndAction();

        return attackActions;
    }

    @Override
    public List<AbilityAction> getAbilityInput(AbilityInput abilityInput) {
        logStartAction();
        List<AbilityAction> actions = new ArrayList<>();
        Random rand = new Random();
        Map<String, List<AbilityAction>> possibleAbilityActions = abilityInput.possibleAbilities();

        for (String id : possibleAbilityActions.keySet()) {
            List<AbilityAction> possibleAbilityActionsForThisCharacter = possibleAbilityActions.get(id);
            if (possibleAbilityActionsForThisCharacter.isEmpty()) {
                continue;
            }

            AbilityAction abilityAction = possibleAbilityActionsForThisCharacter.get(
                    rand.nextInt(possibleAbilityActionsForThisCharacter.size())
            );

            actions.add(abilityAction);
        }

        logEndAction();

        return actions;
    }

    @Override
    public void finish(LogScores scores, LogStats stats) {
        // Does nothing for computer
    }
}
