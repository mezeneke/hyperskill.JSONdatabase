package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import myutil.JsonRequest;
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
    private final JsonObject jsonDB;
    static final ReadWriteLock lock = new ReentrantReadWriteLock();
    static final Lock readLock = lock.readLock();
    static final Lock writeLock = lock.writeLock();
    final static Gson gson = new Gson();

    JsonDB() {
        this.db = new HashMap<>(SIZE);
        this.jsonDB = new JsonObject();
        this.file = new File(FILE_PATH);
    }

    public Response handleRequest(JsonRequest request) {

        Response response = new Response();
        return response = switch (request.getType()) {
            case "get" -> {
                JsonElement value = get(request.getKey());
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

    JsonElement get(JsonElement key) {
        JsonElement value;
        readLock.lock();
        try {
            if (key.isJsonPrimitive()) {
                value = jsonDB.get(key.getAsString());
            } else if (key.isJsonArray()) {
                value = jsonDB;
                JsonArray array = key.getAsJsonArray();

                for (int i = 0; i < array.size(); i++) {

                    String keyStr = array.get(i).getAsString();

                    if(value != null && value.isJsonObject()) {
                        if (value.getAsJsonObject().has(keyStr)) {
                            value = value.getAsJsonObject().get(keyStr);
                        }
                    } else {
                        value = null;
                    }
                }
            } else {
                value = null;
            }
        } finally {
            readLock.unlock();
        }
        return value;
    }

    void set(JsonElement key, JsonElement value) {
        writeLock.lock();
        try {
            jsonDB.add(key.getAsString(), value);
            storeDB();
        } finally {
            writeLock.unlock();
        }
    }

    String delete(JsonElement key) {
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
