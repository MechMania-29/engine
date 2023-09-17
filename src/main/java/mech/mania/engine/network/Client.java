package mech.mania.engine.network;

import com.fasterxml.jackson.databind.ObjectMapper;

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

    private final ServerSocket serverSocket;
    private final Socket clientSocket;

    public Client(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(TIMEOUT_MILIS_INIT);

        Socket tempClientSocket = null;
        try {
            tempClientSocket = serverSocket.accept();
            tempClientSocket.setSoTimeout(TIMEOUT_MILIS_TURN);
        } catch (Exception e) {
            tempClientSocket = null;
            System.err.printf("Failed to connect to port %d, %s\n", port, e);
        }

        clientSocket = tempClientSocket;
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
