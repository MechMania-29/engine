package mech.mania.engine;

import mech.mania.engine.character.CharacterState;
import mech.mania.engine.log.LogErrors;
import mech.mania.engine.player.ComputerPlayer;
import mech.mania.engine.player.DoNothingPlayer;
import mech.mania.engine.player.Player;
import mech.mania.engine.terrain.MapLoader;
import mech.mania.engine.terrain.SelectedMap;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

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

    @Test
    public void RunAGame() throws IOException {
        List<List<Character>> map = MapLoader.LoadMap(SelectedMap.MAIN);
        Player human = new ComputerPlayer(false);
        Player zombie = new ComputerPlayer(true);
        GameState gameState = new GameState(human, zombie, map);

        while (!gameState.isFinished()) {
            gameState.runTurn();
        }

        LogErrors errors = gameState.getErrors();
        assertEquals(0, errors.humanErrors().size(), String.format("Expected no human errors, got: %s", errors.humanErrors()));
        assertEquals(0, errors.zombieErrors().size(), String.format("Expected no zombie errors, got: %s", errors.humanErrors()));
    }
}
