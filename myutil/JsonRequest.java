package myutil;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class JsonRequest implements Message {

    @Parameter(names= "-t")
    private String type;
    @Parameter(names= "-k", converter = JsonElementConverter.class)
    private JsonElement key;
    @Parameter(names= "-v", converter = JsonElementConverter.class)
    private JsonElement value;

    public void parse(String[] args) {
        JCommander jBuilder = JCommander.newBuilder()
                .addObject(this)
                .build();
        jBuilder.parse(args);
    }

    public String getType() {
        return type;
    }

    public JsonElement getKey() {
        return key;
    }

    public JsonElement getValue() {
        return value;
    }

    @Override
    public String getJSON() {
        return new Gson().toJson(this);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
