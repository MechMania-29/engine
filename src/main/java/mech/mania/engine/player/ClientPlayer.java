package mech.mania.engine.player;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mech.mania.engine.character.CharacterClassType;
import mech.mania.engine.character.action.AbilityAction;
import mech.mania.engine.character.action.AttackAction;
import mech.mania.engine.character.action.MoveAction;
import mech.mania.engine.log.LogErrors;
import mech.mania.engine.log.LogScores;
import mech.mania.engine.log.LogStats;
import mech.mania.engine.network.Client;
import mech.mania.engine.network.SendMessage;
import mech.mania.engine.GamePhase;
import mech.mania.engine.player.input.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ClientPlayer extends Player {
    private final Client client;

    public ClientPlayer(boolean isZombie, int port) {
        super(isZombie);

        client = new Client(port, getErrorLogger());
    }

    private void handleClientError(GamePhase phase, int turn, Exception e) {
        getErrorLogger().log(String.format("An error occurred handling input of %s player on turn %s during %s phase:\n%s",
                isZombie ? "zombie" : "human", turn, phase, e));
    }

    @Override
    public Map<CharacterClassType, Integer> getChosenClassesInput(ChooseClassesInput chooseClassesInput) {
        logStartAction();
        ObjectMapper mapper = new ObjectMapper();

        // We wrap the entirety of handling user input in a try catch
        try {
            SendMessage sendMessage = new SendMessage(isZombie, GamePhase.CHOOSE_CLASSES, mapper.valueToTree(chooseClassesInput));
            client.send(sendMessage);

            String response = client.receive();
            Map<CharacterClassType, Integer> chosenClasses = mapper.readValue(response, new TypeReference<>() {});

            if (chosenClasses == null) {
                throw new RuntimeException("Received null, expected a proper response");
            }

            logEndAction();

            return chosenClasses;
        } catch (Exception e) {
            handleClientError(GamePhase.CHOOSE_CLASSES, chooseClassesInput.turn(), e);
        }

        logEndAction();

        return Map.of();
    }

    @Override
    public List<MoveAction> getMoveInput(MoveInput moveInput) {
        logStartAction();
        ObjectMapper mapper = new ObjectMapper();

        // We wrap the entirety of handling user input in a try catch
        try {
            SendMessage sendMessage = new SendMessage(isZombie, GamePhase.MOVE, mapper.valueToTree(moveInput));
            client.send(sendMessage);

            String response = client.receive();
            List<MoveAction> moveActions = mapper.readValue(response, new TypeReference<>() {});

            if (moveActions == null) {
                throw new RuntimeException("Received null, expected a proper response");
            }

            logEndAction();

            return moveActions;
        } catch (Exception e) {
            handleClientError(GamePhase.MOVE, moveInput.turn(), e);
        }

        logEndAction();

        return List.of();
    }

    @Override
    public List<AttackAction> getAttackInput(AttackInput attackInput) {
        logStartAction();
        ObjectMapper mapper = new ObjectMapper();

        // We wrap the entirety of handling user input in a try catch
        try {
            SendMessage sendMessage = new SendMessage(isZombie, GamePhase.ATTACK, mapper.valueToTree(attackInput));
            client.send(sendMessage);

            String response = client.receive();
            List<AttackAction> attackActions = mapper.readValue(response, new TypeReference<>() {});

            if (attackActions == null) {
                throw new RuntimeException("Received null, expected a proper response");
            }

            logEndAction();

            return attackActions;
        } catch (Exception e) {
            handleClientError(GamePhase.ATTACK, attackInput.turn(), e);
        }

        logEndAction();

        return List.of();
    }

    @Override
    public List<AbilityAction> getAbilityInput(AbilityInput abilityInput) {
        logStartAction();
        ObjectMapper mapper = new ObjectMapper();

        // We wrap the entirety of handling user input in a try catch
        try {
            SendMessage sendMessage = new SendMessage(isZombie, GamePhase.ABILITY, mapper.valueToTree(abilityInput));
            client.send(sendMessage);

            String response = client.receive();
            List<AbilityAction> actions = mapper.readValue(response, new TypeReference<>() {});

            if (actions == null) {
                throw new RuntimeException("Received null, expected a proper response");
            }

            logEndAction();

            return actions;
        } catch (Exception e) {
            handleClientError(GamePhase.ABILITY, abilityInput.turn(), e);
        }

        logEndAction();

        return List.of();
    }

    @Override
    public void finish(LogScores scores, LogStats stats, LogErrors errors) {
        if (client == null) return;

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            FinishInput finishInput = new FinishInput(scores, stats, errors, stats.turns());
            client.send(new SendMessage(isZombie, GamePhase.FINISH, objectMapper.valueToTree(finishInput)));

            client.close();
        } catch (Exception e) {
            handleClientError(GamePhase.FINISH, stats.turns(), e);
        }
    }
}
