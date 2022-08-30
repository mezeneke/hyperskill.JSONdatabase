package myutil;

import server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection implements AutoCloseable {
    private Server server;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public Connection(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        setStreams();
    }

    public Connection(String address, int port) {
        setSocket(address, port);
        setStreams();
    }

    private void setSocket(String address, int port) {
        try {
            this.socket = new Socket(address, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setStreams() {
        try {
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        try {
            outputStream.writeUTF(message.getJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readMessage() {
        try {
            return inputStream.readUTF();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    Server getServer() {
        return server;
    }

    @Override
    public void close() throws Exception {
        outputStream.close();
        inputStream.close();
        socket.close();
    }
}
