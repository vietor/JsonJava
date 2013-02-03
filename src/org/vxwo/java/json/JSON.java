package org.vxwo.java.json;

public final class JSON {

	public static String serialize(Object obj) throws JsonException {
		return new JsonSerializer(true, false).serialize(obj);
	}

	@SuppressWarnings("rawtypes")
	public static Object deserialize(Class type, String json)
			throws JsonException {
		return Convert.toObject(type, new JsonParser(json).decode());
	}

	public static String encode(JsonObject obj) throws JsonException {
		return new JsonSerializer(true, false).serialize(obj);
	}

	public static JsonObject decode(String json) throws JsonException {
		return new JsonParser(json).decode();
	}
}
