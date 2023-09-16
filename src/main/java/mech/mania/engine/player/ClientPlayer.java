package mech.mania.engine.player;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mech.mania.engine.character.CharacterClassType;
import mech.mania.engine.character.action.AbilityAction;
import mech.mania.engine.character.action.AttackAction;
import mech.mania.engine.character.action.MoveAction;
import mech.mania.engine.log.LogScores;
import mech.mania.engine.log.LogStats;
import mech.mania.engine.network.Client;
import mech.mania.engine.network.SendMessage;
import mech.mania.engine.network.SendMessageType;
import mech.mania.engine.player.input.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientPlayer extends Player {
    private final Client client;

    public ClientPlayer(boolean isZombie, int port) throws IOException {
        super(isZombie);

        client = new Client(port);
    }

    @Override
    public Map<CharacterClassType, Integer> getChosenClassesInput(ChooseClassesInput chooseClassesInput) {
        Map<CharacterClassType, Integer> chosenClasses = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        // We wrap the entirety of handling user input in a try catch
        try {
            SendMessage sendMessage = new SendMessage(isZombie, SendMessageType.CHOOSE_CLASSES_PHASE, mapper.valueToTree(chooseClassesInput));
            client.send(sendMessage);

            String response = client.receive();
            chosenClasses = mapper.readValue(response, new TypeReference<>() {});
        } catch (Exception e) {
            // do nothing
        }

        return chosenClasses;

    }

    @Override
    public List<MoveAction> getMoveInput(MoveInput moveInput) {
        List<MoveAction> moveActions = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        // We wrap the entirety of handling user input in a try catch
        try {
            SendMessage sendMessage = new SendMessage(isZombie, SendMessageType.MOVE_PHASE, mapper.valueToTree(moveInput));
            client.send(sendMessage);

            String response = client.receive();
            moveActions = mapper.readValue(response, new TypeReference<>() {});
        } catch (Exception e) {
            // do nothing
        }

        return moveActions;
    }

    @Override
    public List<AttackAction> getAttackInput(AttackInput attackInput) {
        List<AttackAction> attackActions = new ArrayList<>();

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

        return attackActions;
    }

    @Override
    public List<AbilityAction> getAbilityInput(AbilityInput abilityInput) {
        List<AbilityAction> actions = new ArrayList<>();

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

        return actions;
    }

    @Override
    public void finish(LogScores scores, LogStats stats) {
        if (client == null) return;

        ObjectMapper objectMapper = new ObjectMapper();

        FinishInput finishInput = new FinishInput(scores, stats, stats.turns());
        client.send(new SendMessage(isZombie, SendMessageType.FINISH, objectMapper.valueToTree(finishInput)));

        client.close();
    }
}
