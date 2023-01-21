package mech.mania.engine;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mech.mania.engine.character.CharacterState;
import mech.mania.engine.character.Position;
import mech.mania.engine.character.action.MoveAction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static mech.mania.engine.Config.TOTAL_CHARACTERS;
import static mech.mania.engine.Config.TURNS;

public class Engine {
    public static void main(String[] args) {
        Random rand = new Random();

        GameState gameState = new GameState();

        System.out.println(gameState);

        while (gameState.getTurn() < TURNS) {
            List<CharacterState> currentCharacterStates = gameState.getCharacterStates();

            ArrayList<MoveAction> moveActions = new ArrayList<>();
            for (int j = 0; j < TOTAL_CHARACTERS; j++) {
                if (rand.nextInt(5) == 0) {
                    Position currentPosition = currentCharacterStates.get(j).getPosition();
                    int destX = currentPosition.getX() + rand.nextInt(-1, 1 + 1);
                    int destY = currentPosition.getY() + rand.nextInt(-1, 1 + 1);
                    moveActions.add(new MoveAction(j, new Position(destX, destY)));
                }
            }

            gameState.runTurn(moveActions);
            System.out.println(gameState);
        }

        System.out.println(gameState.getLog());

        String output = System.getProperty("output") == null ?
                "gamelogs/game_" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + ".json" :
                System.getProperty("output");


        File file = new File(output);
        try {
            file.getParentFile().mkdirs();
        } catch (NullPointerException e) {
            throw new RuntimeException(e);
        }

        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            printWriter.println(gameState.getLog());
        } finally {
            printWriter.close();
        }
    }
}