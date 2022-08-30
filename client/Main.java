package client;

import com.google.gson.Gson;
import myutil.Connection;
import myutil.JsonRequest;
import myutil.Request;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Gson gson = new Gson();
        JsonRequest request = new JsonRequest();

        if (args[0].equals("-in")) {
            try (Reader reader = Files.newBufferedReader(Paths.get(".\\data\\" + args[1]))) {
                request = gson.fromJson(reader, JsonRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            request.parse(args);
        }

        Connection connection = new Connection("127.0.0.1", 23456);

        try (connection) {
            System.out.println("Client started!");
            connection.sendMessage(request);
            System.out.println("Sent: " + request);
            String serverMsg = connection.readMessage();
            System.out.println("Received: " + serverMsg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
