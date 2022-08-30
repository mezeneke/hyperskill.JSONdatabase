package myutil;

import com.google.gson.Gson;

public class Response implements Message {
    private String response;
    private String reason;
    private String value;

    public Response setResponse(String response) {
        this.response = response;
        return this;
    }

    public Response setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public Response setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String getJSON() {
        return new Gson().toJson(this);
    }
}
