package org.vxwo.java.json;

public final class JsonTool {

	public static JsonValue read(String json) throws JsonException {
		return new JsonParser(json).decode();
	}

	public static JsonValue readObject(Object obj) throws JsonException {
		return new JsonObjParser(obj).Decode();
	}

	public static String write(JsonValue obj) throws JsonException {
		return new JsonSerializer(true, false).toJSON(obj);
	}

	@SuppressWarnings("rawtypes")
	public static Object writeObject(Class type, JsonValue obj)
			throws JsonException {
		return new JsonObjSerializer().ConvertToObject(type, obj);
	}

	@SuppressWarnings("rawtypes")
	public static Object deserialize(Class type, String json)
			throws JsonException {
		return writeObject(type, readObject(json));
	}

	public static String serialize(Object obj) throws JsonException {
		return write(readObject(obj));
	}
}
