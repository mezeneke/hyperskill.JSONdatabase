package myutil;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.gson.Gson;

public class Request implements Message {

    @Parameter(names= "-t")
    private String type;
    @Parameter(names= "-k")
    private String key;
    @Parameter(names= "-v")
    private String value;

    public void parse(String[] args) {
        JCommander jBuilder = JCommander.newBuilder()
                .addObject(this)
                .build();
        jBuilder.parse(args);
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
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
