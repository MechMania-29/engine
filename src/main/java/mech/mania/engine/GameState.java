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
        GameState newGameState = new GameState();
        List<CharacterState> newCharacterStates = newGameState.getCharacterStates();

        for (int i = 0; i < newCharacterStates.size(); i++) {
            newCharacterStates.set(i, characterStates.get(i).clone());
        }

        return newGameState;
    }
}
