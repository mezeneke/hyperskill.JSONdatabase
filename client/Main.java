package client;

import com.google.gson.Gson;
import myutil.Connection;
import myutil.Request;
import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Gson gson = new Gson();
        Request request = new Request();

        if (args[0].equals("-in")) {
            File file = new File(".\\data\\" + args[1]);

            try (Scanner reader = new Scanner(file);) {
                String jsonRequest = reader.nextLine();
                request = gson.fromJson(jsonRequest, Request.class);
            } catch (FileNotFoundException e) {
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
