package org.vxwo.java.json;

public class JsonReader {
	public static JsonValue read(String json) throws JsonException
    {
        return new JsonParser(json).decode();
    }
}
