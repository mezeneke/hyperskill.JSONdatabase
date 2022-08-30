package myutil;

import com.google.gson.Gson;
import server.JsonDB;

public class Session implements Runnable {
    static final Gson gson = new Gson();
    private final Connection connection;
    private final JsonDB db;

    public Session(Connection connection, JsonDB db) {
        this.connection = connection;
        this.db = db;
    }

    @Override
    public void run() {
        Request request;
        Response response;

        try (connection) {

            String json = connection.readMessage();
            request = gson.fromJson(json, Request.class);

            if (request.getType().equals("exit")) {
                connection.getServer().exit();
                response = new Response().setResponse("OK");
            } else {
                response = db.handleRequest(request);
            }
            connection.sendMessage(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
