package server;

import myutil.Connection;
import myutil.Session;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ServerSocket serverSocket;
    private final ExecutorService executor;
    private final JsonDB db;
    private boolean running;

    Server(String address, int port) throws IOException {
        this.serverSocket = new ServerSocket(port, 50, InetAddress.getByName(address));
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.db = new JsonDB();
    }

    void start() {
        running = true;
        System.out.println("Server started!");

        try (serverSocket) {
            while(running) {
                Connection connection = new Connection(this, serverSocket.accept());
                executor.submit(new Session(connection, db));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exit() {

    }
}
