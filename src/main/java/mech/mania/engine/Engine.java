package mech.mania.engine;

import mech.mania.engine.character.CharacterState;
import mech.mania.engine.character.Position;
import mech.mania.engine.character.action.MoveAction;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static mech.mania.engine.Config.TOTAL_CHARACTERS;
import static mech.mania.engine.Config.TURNS;

public class Engine {
    public static void main(String[] args) {
        Random rand = new Random();

        GameState gameState = new GameState();

        System.out.println(gameState);

        for (int i = 0; i < TURNS; i++) {
            List<CharacterState> currentCharacterStates = gameState.getCharacterStates();

            MoveAction[] moveActions = new MoveAction[TOTAL_CHARACTERS];
            for (int j = 0; j < TOTAL_CHARACTERS; j++) {
                Position currentPosition = currentCharacterStates.get(j).getPosition();
                int destX = currentPosition.getX() + rand.nextInt(-1, 1 + 1);
                int destY = currentPosition.getY() + rand.nextInt(-1, 1 + 1);
                moveActions[j] = new MoveAction(j, new Position(destX, destY));
            }

            gameState.runTurn(Arrays.asList(moveActions));
            System.out.println(gameState);
        }
    }
}