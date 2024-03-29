package mech.mania.engine.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import mech.mania.engine.player.PlayerErrorLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static mech.mania.engine.Config.TIMEOUT_MILIS_INIT;
import static mech.mania.engine.Config.TIMEOUT_MILIS_TURN;

public class Client {

    private ServerSocket serverSocket;
    private Socket clientSocket;

    public Client(int port, PlayerErrorLogger errorLogger) {
        serverSocket = null;
        clientSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(TIMEOUT_MILIS_INIT);

            clientSocket = serverSocket.accept();
            clientSocket.setSoTimeout(TIMEOUT_MILIS_TURN);
        } catch (Exception e) {
            errorLogger.log(String.format("Failed to connect to port %d, %s", port, e));
        }
    }

    private String read() {
        if (clientSocket == null || clientSocket.isClosed()) {
            return "null";
        }

        String read = "";

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            read = in.readLine();
        } catch (SocketTimeoutException e) {
            read = "null";
        } catch (IOException e) {
            // do nothing
        }

        return read;
    }

    private void write(String string) {
        if (clientSocket == null || clientSocket.isClosed()) {
            return;
        }
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(string);
        } catch (IOException e) {
            // do nothing
        }
    }

    public String receive() {
        return read();
    }

    public void send(SendMessage sendMessage) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String rawMessage = objectMapper.writeValueAsString(sendMessage);
            write(rawMessage);
        } catch (Exception e) {
            // do nothing
        }
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (Exception e) {

        }
    }
}
