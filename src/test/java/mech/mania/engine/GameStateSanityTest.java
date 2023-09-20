package mech.mania.engine;

import mech.mania.engine.character.CharacterState;
import mech.mania.engine.player.ComputerPlayer;
import mech.mania.engine.player.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static mech.mania.engine.Config.STARTING_ZOMBIES;
import static mech.mania.engine.Config.TOTAL_CHARACTERS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameStateSanityTest {
    @Test
    public void CharactersAreCreated() {
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

        assertEquals(STARTING_ZOMBIES, zombiesCount);
        assertEquals(TOTAL_CHARACTERS - STARTING_ZOMBIES, humansCounts);
        assertEquals(TOTAL_CHARACTERS, totalCount);
    }
}
