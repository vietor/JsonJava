package org.vxwo.java.json;

public class JsonWriter {
	public static String write(JsonValue obj) throws JsonException
    {
        return new JsonSerializer(true, false).toJSON(obj);
    }
}
