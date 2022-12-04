package mech.mania.engine;

import mech.mania.engine.character.CharacterState;

import java.util.Arrays;
import java.util.List;

import static mech.mania.engine.Config.TOTAL_CHARACTERS;

public class GameState implements Cloneable {
    private List<CharacterState> characterStates = Arrays.asList(new CharacterState[TOTAL_CHARACTERS]);

    public GameState() {};

    public List<CharacterState> getCharacterStates() {
        return characterStates;
    }

    @Override
    public GameState clone() {
        try {
            return (GameState) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
