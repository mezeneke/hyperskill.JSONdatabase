package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import myutil.JsonRequest;
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
    final String CODE_SUCCESS = "OK";
    final String CODE_FAILURE = "ERROR";
    final String FILE_PATH = ".\\server\\data\\db.txt";
    final File file;
    private final JsonObject jsonDB;
    static final ReadWriteLock lock = new ReentrantReadWriteLock();
    static final Lock readLock = lock.readLock();
    static final Lock writeLock = lock.writeLock();
    final static Gson gson = new Gson();

    JsonDB() {
        this.jsonDB = new JsonObject();
        this.file = new File(FILE_PATH);
    }

    public Response handleRequest(JsonRequest request) {

        Response response = new Response();

        response = switch (request.getType()) {

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
                if (set(request.getKey(), request.getValue())) {
                    response.setResponse(CODE_SUCCESS);
                } else {
                    response.setResponse(CODE_FAILURE);
                    response.setReason("No such key");
                }
                yield response;
            }
            case "delete" -> {
                if (delete(request.getKey())) {
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
        return response;
    }

    JsonElement get(JsonElement key) {
        JsonElement value = null;
        readLock.lock();
        try {
            if (key.isJsonPrimitive()) {
                value = jsonDB.get(key.getAsString());
            } else if (key.isJsonArray()) {
                value = getter(key);
            }
        } finally {
            readLock.unlock();
        }
        return value;
    }

    private JsonElement getter(JsonElement key) {

        JsonElement value;
        JsonArray array = key.getAsJsonArray();
        value = jsonDB;

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
        return value;
    }

    boolean set(JsonElement key, JsonElement value) {
        boolean success = false;
        writeLock.lock();
        try {
            if (key.isJsonPrimitive()) {
                jsonDB.add(key.getAsString(), value);
                success = true;
            } else if (key.isJsonArray()) {
                success = setPerKeyStructure(key, value);
            }
            storeDB();
        } finally {
            writeLock.unlock();
        }
        return success;
    }

    private boolean setPerKeyStructure(JsonElement keys, JsonElement value) {
        JsonArray keysAsJsonArray = keys.getAsJsonArray();
        JsonElement lastParentObject = setParentObjects(keysAsJsonArray);
        if (lastParentObject == null) {
            return false;
        }

        String keyLastElement = keysAsJsonArray.get(keysAsJsonArray.size() - 1).getAsString();
        lastParentObject.getAsJsonObject().add(keyLastElement, value);
        return true;
    }

    private JsonElement setParentObjects(JsonArray keys) {
        JsonElement element = jsonDB;

        for (int i = 0; i < keys.size() - 1; i++) {
            String keyStr = keys.get(i).getAsString();

            if (element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();

                if (!object.has(keyStr)) {
                    object.add(keyStr, new JsonObject());
                }
                element = object.get(keyStr);
            } else {
                element = null;
                break;
            }
        }
        return element;
    }

    boolean delete(JsonElement key) {
        boolean success = false;

        writeLock.lock();
        try {
            if (key.isJsonPrimitive()) {
                JsonElement removedElement = jsonDB.remove(key.getAsString());
                success = removedElement != null;
            } else if (key.isJsonArray()) {
                success = removePerKeys(key);
            }
            storeDB();
        } finally {
            writeLock.unlock();
        }
        return success;
    }

    boolean removePerKeys(JsonElement keys) {
        JsonArray keysAsJsonArray = keys.getAsJsonArray();
        String lastKeyStr = keysAsJsonArray.get(keysAsJsonArray.size() - 1).getAsString();

        JsonElement lastParentObject = getLastParentObject(keysAsJsonArray);
        if (lastParentObject != null) {
            JsonElement removedElement = lastParentObject.getAsJsonObject().remove(lastKeyStr);
            return removedElement != null;
        } else {
            return false;
        }
    }

    JsonElement getLastParentObject(JsonArray keys) {
        JsonArray keysAsJsonArray = keys.getAsJsonArray();
        JsonElement element = jsonDB;

        for (int i = 0; i < keysAsJsonArray.size() - 1; i++) {
            String keyStr = keysAsJsonArray.get(i).getAsString();

            if (element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();
                element = object.get(keyStr);
            } else {
                element = null;
                break;
            }
        }
        return element;
    }

    private void storeDB() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(gson.toJson(jsonDB));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
