package mech.mania.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static mech.mania.engine.Config.TURNS;

public class Engine {
    private static void printState(GameState gameState, String id) {
        System.out.println(gameState);

        String debug = System.getenv("DEBUG");
        System.out.println("Debug is " + debug);
        if (debug != null && !debug.equals("1") && !debug.equals("true")) {
            return;
        }
        System.out.println("Debug accepted");

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

        printState(gameState, id);

        while (gameState.getTurn() < TURNS) {
            gameState.runTurn();
            printState(gameState, id);
        }

        String output = System.getProperty("output") == null ?
                "gamelogs/game_" + id + ".json" :
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