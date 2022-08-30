package server;

import com.google.gson.Gson;
import myutil.Connection;
import myutil.Message;
import myutil.Request;
import myutil.Response;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {

        try {
            Server server = new Server("127.0.0.1", 23456);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
