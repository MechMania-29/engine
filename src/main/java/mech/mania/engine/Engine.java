package mech.mania.engine;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mech.mania.engine.log.LogErrors;
import mech.mania.engine.log.LogScores;
import mech.mania.engine.log.LogStats;
import mech.mania.engine.player.ClientPlayer;
import mech.mania.engine.player.ComputerPlayer;
import mech.mania.engine.player.Player;
import mech.mania.engine.terrain.MapLoader;
import mech.mania.engine.terrain.SelectedMap;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Engine {
    private static final String USAGE = """
            Usage: java -jar engine.jar [PORT_1] [PORT_2]\s

            Env variables:
            OUTPUT = The location to which the gamelog will be output, defaults to gamelogs/game_DATE.json
            DEBUG = Set to 1 to enable debug output and debuglog output
            """;
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
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        if (args.length != 2) {
            System.err.println("No arguments specified");
            System.out.println(USAGE);
            System.exit(1);
            return;
        }

        int port1, port2;

        try {
            port1 = Integer.parseInt(args[0]);
            port2 = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Port numbers must be integers.");
            System.err.println(USAGE);
            System.exit(1);
            return;
        }

        System.out.println("Loading map...");
        List<List<Character>> map = MapLoader.LoadMap(SelectedMap.MAIN);

        System.out.println("Connecting clients...");

        CompletableFuture<Player> humanPlayerFuture = CompletableFuture.supplyAsync(
                () -> port1 > 0 ? new ClientPlayer(false, port1) : new ComputerPlayer(false)
        );

        CompletableFuture<Player> zombiePlayerFuture = CompletableFuture.supplyAsync(
                () -> port2 > 0 ? new ClientPlayer(true, port2) : new ComputerPlayer(true)
        );

        Player humanPlayer = humanPlayerFuture.get();
        Player zombiePlayer = zombiePlayerFuture.get();

        GameState gameState = new GameState(humanPlayer, zombiePlayer, map);

        System.out.println("Running game...");

        String id = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss").format(LocalDateTime.now());
        printState(gameState, id);

        while (!gameState.isFinished()) {
            gameState.runTurn();
            printState(gameState, id);
        }

        LogScores finalScores = gameState.getScores();
        LogStats finalStats = gameState.getStats();
        LogErrors finalErrors = gameState.getErrors();

        humanPlayer.finish(finalScores, finalStats, finalErrors);
        zombiePlayer.finish(finalScores, finalStats, finalErrors);

        System.out.printf("Game finished on turn %d with %d humans and %d zombies, %d-%d (H-Z)\n",
                gameState.getTurn(), finalStats.humansLeft(), finalStats.zombiesLeft(),
                finalScores.humans(), finalScores.zombies());

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