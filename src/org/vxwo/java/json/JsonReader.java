package org.vxwo.java.json;

public class JsonReader {
	public static JsonValue read(String json) throws JsonException
    {
        return new JsonParser(json).decode();
    }
	public static JsonValue ReadObject (Object obj) throws JsonException
	{
		return new JsonObjParser (obj).Decode ();
	}
}
