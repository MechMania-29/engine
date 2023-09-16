package mech.mania.engine.player;

import mech.mania.engine.character.CharacterClassType;
import mech.mania.engine.character.action.AbilityAction;
import mech.mania.engine.character.action.AttackAction;
import mech.mania.engine.character.action.MoveAction;
import mech.mania.engine.log.LogScores;
import mech.mania.engine.log.LogStats;
import mech.mania.engine.player.input.*;

import java.util.List;
import java.util.Map;

public abstract class Player {
    protected boolean isZombie;

    public Player(boolean isZombie) {
        this.isZombie = isZombie;
    }

    public boolean isZombie() {
        return isZombie;
    }

    public abstract Map<CharacterClassType, Integer> getChosenClassesInput(ChooseClassesInput chooseClassesInput);

    public abstract List<MoveAction> getMoveInput(MoveInput moveInput);

    public abstract List<AttackAction> getAttackInput(AttackInput attackInput);

    public abstract List<AbilityAction> getAbilityInput(AbilityInput abilityInput);

    public abstract void finish(LogScores scores, LogStats stats);
}