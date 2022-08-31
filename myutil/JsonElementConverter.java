package myutil;

import com.beust.jcommander.IStringConverter;
import com.google.gson.JsonPrimitive;

public class JsonElementConverter implements IStringConverter<JsonPrimitive> {
    @Override
    public JsonPrimitive convert(String value) {
        return new JsonPrimitive(value);
    }
}
