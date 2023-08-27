package mech.mania.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static mech.mania.engine.Config.TURNS;

public class Engine {
    private static boolean isDebug() {
        String debug = System.getenv("DEBUG");
        return debug != null && (debug.equals("1") || debug.equals("true"));
    }
    private static void printState(GameState gameState, String id) {
        if (!isDebug()) {
            return;
        }

        System.out.printf("Running turn %d...%n", gameState.getTurn());
        System.out.println(gameState);

        String output = "debuglog/game_" + id + "/turn_" + gameState.getTurn() + ".txt";

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
            printWriter.println(gameState);
        } finally {
            printWriter.close();
        }
    }
    public static void main(String[] args) {
        String id = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss").format(LocalDateTime.now());

        GameState gameState = new GameState();

        System.out.println("Starting...");

        printState(gameState, id);

        while (gameState.getTurn() < TURNS) {
            gameState.runTurn();
            printState(gameState, id);
        }

        String output = System.getenv("OUTPUT") == null ?
                "gamelogs/game_" + id + ".json" :
                System.getenv("OUTPUT");

        System.out.printf("Writing to `%s`...%n", output);


        File file = new File(output);
        try {
            file.getParentFile().mkdirs();
        } catch (NullPointerException e) {
            // If failed, we are already good
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
        System.out.println("Finished");
    }
}