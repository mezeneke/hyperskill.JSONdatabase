package server;

import myutil.Connection;
import myutil.Session;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println();

            boolean termination = false;
            executor.shutdown();
            try {
                termination = executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            if (termination) {
                System.out.println("Executor terminated");
            } else {
                System.out.println("Timeout elapsed before termination");
            }

        }
    }

    public void exit() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
