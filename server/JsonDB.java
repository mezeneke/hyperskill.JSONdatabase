package server;

import com.google.gson.Gson;
import myutil.Request;
import myutil.Response;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JsonDB {
    final int SIZE = 100;
    final String CODE_SUCCESS = "OK";
    final String CODE_FAILURE = "ERROR";
    final String FILE_PATH = ".\\server\\data\\db.txt";
    final File file;
    private final Map<String, String> db;
    static final ReadWriteLock lock = new ReentrantReadWriteLock();
    static final Lock readLock = lock.readLock();
    static final Lock writeLock = lock.writeLock();
    final static Gson gson = new Gson();

    JsonDB() {
        this.db = new HashMap<>(SIZE);
        this.file = new File(FILE_PATH);
    }

    public Response handleRequest(Request request) {

        Response response = new Response();
        return response = switch (request.getType()) {
            case "get" -> {
                String value = get(request.getKey());
                if (value != null) {
                    response.setResponse(CODE_SUCCESS);
                    response.setValue(value);
                } else {
                    response.setResponse(CODE_FAILURE);
                    response.setReason("No such key");
                }
                yield response;
            }
            case "set" -> {
                set(request.getKey(), request.getValue());
                yield response.setResponse(CODE_SUCCESS);
            }
            case "delete" -> {
                if (delete(request.getKey()) != null) {
                    response.setResponse(CODE_SUCCESS);
                } else {
                    response.setResponse(CODE_FAILURE);
                    response.setReason("No such key");
                }
                yield response;
            }
            default -> {
                response.setResponse(CODE_FAILURE);
                response.setReason("No such operation");
                yield response;
            }
        };
    }

    String get(String key) {
        readLock.lock();
        String value;
        try {
            value = db.get(key);
        } finally {
            readLock.unlock();
        }
        return value;
    }

    void set(String key, String value) {
        writeLock.lock();
        try {
            db.put(key, value);
            storeDB();
        } finally {
            writeLock.unlock();
        }
    }

    String delete(String key) {
        writeLock.lock();
        String value;
        try {
            value = db.remove(key);
            storeDB();
        } finally {
            writeLock.unlock();
        }
        return value;
    }

    private void storeDB() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(gson.toJson(db));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
