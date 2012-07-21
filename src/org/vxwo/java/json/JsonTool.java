package org.vxwo.java.json;

public final class JsonTool {

	public static JsonValue txtToValue(String json) throws JsonException {
		return new JsonParser(json).decode();
	}

	public static JsonValue objToValue(Object obj) throws JsonException {
		return new JsonObjParser(obj).Decode();
	}

	public static String valueToTxt(JsonValue obj) throws JsonException {
		return new JsonSerializer(true, false).toJSON(obj);
	}

	@SuppressWarnings("rawtypes")
	public static Object valueToObj(Class type, JsonValue obj)
			throws JsonException {
		return new JsonObjSerializer().ConvertToObject(type, obj);
	}

	@SuppressWarnings("rawtypes")
	public static Object deserialize(Class type, String json)
			throws JsonException {
		return valueToObj(type, txtToValue(json));
	}

	public static String serialize(Object obj) throws JsonException {
		return valueToTxt(objToValue(obj));
	}
}
