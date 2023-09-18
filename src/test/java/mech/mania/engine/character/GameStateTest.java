package mech.mania.engine.character;

import mech.mania.engine.GameState;
import mech.mania.engine.player.ComputerPlayer;
import mech.mania.engine.player.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static mech.mania.engine.Config.STARTING_ZOMBIES;
import static mech.mania.engine.Config.TOTAL_CHARACTERS;

public class GameStateTest {
    @Test
    public void BaseTest() throws IOException {
        Player human = new ComputerPlayer(false);
        Player zombie = new ComputerPlayer(true);
        GameState gameState = new GameState(human, zombie, List.of());

        int zombiesCount = 0;
        int humansCounts = 0;
        int totalCount = 0;

        for (CharacterState characterState : gameState.getCharacterStates().values()) {
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
