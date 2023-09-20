package mech.mania.engine.player;

import mech.mania.engine.character.CharacterClassType;
import mech.mania.engine.character.CharacterState;
import mech.mania.engine.character.action.AbilityAction;
import mech.mania.engine.character.action.AttackAction;
import mech.mania.engine.character.action.MoveAction;
import mech.mania.engine.log.LogErrors;
import mech.mania.engine.log.LogScores;
import mech.mania.engine.log.LogStats;
import mech.mania.engine.player.input.AbilityInput;
import mech.mania.engine.player.input.AttackInput;
import mech.mania.engine.player.input.ChooseClassesInput;
import mech.mania.engine.player.input.MoveInput;
import mech.mania.engine.util.Position;

import java.util.*;

public class DoNothingPlayer extends Player {
    public DoNothingPlayer(boolean isZombie) {
        super(isZombie);
    }

    @Override
    public boolean isZombie() {
        return isZombie;
    }

    @Override
    public Map<CharacterClassType, Integer> getChosenClassesInput(ChooseClassesInput chooseClassesInput) {
        logStartAction();
        logEndAction();
        return Map.of();
    }

    @Override
    public List<MoveAction> getMoveInput(MoveInput moveInput) {
        logStartAction();
        logEndAction();
        return List.of();
    }

    @Override
    public List<AttackAction> getAttackInput(AttackInput attackInput) {
        logStartAction();
        logEndAction();
        return List.of();
    }

    @Override
    public List<AbilityAction> getAbilityInput(AbilityInput abilityInput) {
        logStartAction();
        logEndAction();
        return List.of();
    }

    @Override
    public void finish(LogScores scores, LogStats stats, LogErrors errors) {
        // Do nothing
    }
}
