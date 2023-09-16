package mech.mania.engine.player;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mech.mania.engine.character.CharacterClassType;
import mech.mania.engine.character.CharacterState;
import mech.mania.engine.character.action.AbilityAction;
import mech.mania.engine.character.action.AttackAction;
import mech.mania.engine.log.LogScores;
import mech.mania.engine.log.LogStats;
import mech.mania.engine.network.Client;
import mech.mania.engine.network.SendMessage;
import mech.mania.engine.network.SendMessageType;
import mech.mania.engine.player.input.*;
import mech.mania.engine.util.Position;
import mech.mania.engine.character.action.MoveAction;

import java.io.IOException;
import java.util.*;

public class Player {
    private int port;
    private boolean isComputer;
    private boolean isZombie;
    private Client client;

    public Player(int port, boolean isZombie) throws IOException {
        this.port = port;
        this.isComputer = port <= 0;
        this.isZombie = isZombie;
        client = null;

        if (!isComputer) {
            client = new Client(port);
        }
    }

    public boolean isZombie() {
        return isZombie;
    }

    public void finish(LogScores scores, LogStats stats) {
        if (client == null) return;

        ObjectMapper objectMapper = new ObjectMapper();

        FinishInput finishInput = new FinishInput(scores, stats, stats.turns());
        client.send(new SendMessage(isZombie, SendMessageType.FINISH, objectMapper.valueToTree(finishInput)));

        client.close();
    }

    public Map<CharacterClassType, Integer> getChosenClassesInput(ChooseClassesInput chooseClassesInput) {
        List<CharacterClassType> possibleChoices = chooseClassesInput.choices();
        int numToPick = chooseClassesInput.numToPick();
        int maxPerSameClass = chooseClassesInput.maxPerSameClass();

        Map<CharacterClassType, Integer> chosenClasses;
        if (!isComputer) {
            chosenClasses = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();

            // We wrap the entirety of handling user input in a try catch
            try {
                SendMessage sendMessage = new SendMessage(isZombie, SendMessageType.CHOOSE_CLASSES_PHASE, mapper.valueToTree(chooseClassesInput));
                client.send(sendMessage);

                String response = client.receive();
                System.out.println(response);
                System.out.println(2);
                chosenClasses = mapper.readValue(response, new TypeReference<>() {
                });
                System.out.println(1);
                System.out.println(chosenClasses);
            } catch (Exception e) {
                // do nothing
            }
        } else {
            // Otherwise, for computer, pick one of the valid moves and just do it
            Random rand = new Random();

            chosenClasses = new HashMap<>();

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
        }

        return chosenClasses;

    }

    public List<MoveAction> getMoveInput(MoveInput moveInput) {
        Map<String, List<MoveAction>> possibleMoves = moveInput.possibleMoves();
        Map<String, CharacterState> characterStates = moveInput.characterStates();

        List<MoveAction> moveActions;
        if (!isComputer) {
            moveActions = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();

            // We wrap the entirety of handling user input in a try catch
            try {
                SendMessage sendMessage = new SendMessage(isZombie, SendMessageType.MOVE_PHASE, mapper.valueToTree(moveInput));
                client.send(sendMessage);

                String response = client.receive();
                moveActions = mapper.readValue(response, new TypeReference<>() {
                });
            } catch (Exception e) {
                // do nothing
            }
        } else {
            // Otherwise, for computer, pick one of the valid moves and just do it
            Random rand = new Random();

            moveActions = new ArrayList<>();

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
        }

        return moveActions;
    }

    public List<AttackAction> getAttackInput(AttackInput attackInput) {
        List<AttackAction> attackActions;
        if (!isComputer) {
            attackActions = new ArrayList<>();

            ObjectMapper mapper = new ObjectMapper();

            // We wrap the entirety of handling user input in a try catch
            try {
                SendMessage sendMessage = new SendMessage(isZombie, SendMessageType.ATTACK_PHASE, mapper.valueToTree(attackInput));
                client.send(sendMessage);

                String response = client.receive();
                attackActions = mapper.readValue(response, new TypeReference<>() {
                });
            } catch (Exception e) {
                // do nothing
            }
        } else {
            // Otherwise, for computer, pick one of the valid moves and just do it
            Random rand = new Random();

            attackActions = new ArrayList<>();
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
        }

        return attackActions;
    }

    public List<AbilityAction> getAbilityInput(AbilityInput abilityInput) {
        List<AbilityAction> actions;
        if (!isComputer) {
            actions = new ArrayList<>();

            ObjectMapper mapper = new ObjectMapper();

            // We wrap the entirety of handling user input in a try catch
            try {
                SendMessage sendMessage = new SendMessage(isZombie, SendMessageType.ABILITY_PHASE, mapper.valueToTree(abilityInput));
                client.send(sendMessage);

                String response = client.receive();
                actions = mapper.readValue(response, new TypeReference<>() {});
            } catch (Exception e) {
                // do nothing
            }
        } else {
            // Otherwise, for computer, pick one of the valid moves and just do it
            Random rand = new Random();

            actions = new ArrayList<>();
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
        }

        return actions;
    }
}
