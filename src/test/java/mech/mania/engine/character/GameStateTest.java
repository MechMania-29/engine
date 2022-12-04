package mech.mania.engine.character;

import mech.mania.engine.GameState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static mech.mania.engine.Config.STARTING_ZOMBIES;
import static mech.mania.engine.Config.TOTAL_CHARACTERS;

public class GameStateTest {
    @Test
    public void BaseTest() {
        GameState gameState = new GameState();

        int zombiesCount = 0;
        int humansCounts = 0;
        int totalCount = 0;

        for (CharacterState characterState : gameState.getCharacterStates()) {
            if (characterState.isZombie()) {
                zombiesCount += 1;
            } else {
                humansCounts += 1;
            }

            totalCount += 1;
        }

        Assertions.assertEquals(STARTING_ZOMBIES, zombiesCount);
        Assertions.assertEquals(TOTAL_CHARACTERS - STARTING_ZOMBIES, humansCounts);
        Assertions.assertEquals(TOTAL_CHARACTERS, totalCount);
    }
}
