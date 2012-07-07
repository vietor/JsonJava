package org.vxwo.java.json;

public class JsonWriter {
	public static String write(JsonValue obj) throws JsonException
    {
        return new JsonSerializer(true, false).toJSON(obj);
    }
	
	@SuppressWarnings("rawtypes")
	public static Object WriteObject(Class type, JsonValue obj) throws JsonException
    {
        return new JsonObjSerializer().ConvertToObject(type, obj);
    }
}
