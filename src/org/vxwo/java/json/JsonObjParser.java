package org.vxwo.java.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;

class JsonObjParser {
	private Object root;
	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	JsonObjParser(Object obj) {
		this.root = obj;
	}

	JsonValue Decode() throws JsonException {
		return ParseValue(root);
	}

	@SuppressWarnings("rawtypes")
	private JsonValue ParseValue(Object obj) throws JsonException {
		if (obj == null)
			return new JsonValue(JsonType.Null, null);

		Class type = obj.getClass();

		if (type.isArray()) {
			JsonValue child, result = new JsonValue(JsonType.Array, null);
			for (int index = 0; index < Array.getLength(obj); ++index) {
				child = ParseValue(Array.get(obj, index));
				if (child != null)
					result.append(child);
			}
			return result;
		}
		if (type.getSimpleName().equalsIgnoreCase("Char"))
			return new JsonValue(JsonType.Int, (Integer) obj);
		if (type.getSimpleName().equalsIgnoreCase("Byte"))
			return new JsonValue(JsonType.Int, (Integer) obj);
		if (type.getSimpleName().equalsIgnoreCase("Boolean"))
			return new JsonValue(JsonType.Boolean, obj);
		if (type.getSimpleName().equalsIgnoreCase("Integer"))
			return new JsonValue(JsonType.Int, obj);
		if (type.getSimpleName().equalsIgnoreCase("Long"))
			return new JsonValue(JsonType.Long, obj);
		if (type.getSimpleName().equalsIgnoreCase("Float"))
			return new JsonValue(JsonType.Double, ((Float) obj).doubleValue());
		if (type.getSimpleName().equalsIgnoreCase("Double"))
			return new JsonValue(JsonType.Double, obj);
		if (type.getSimpleName().equals("String"))
			return new JsonValue(JsonType.String, obj);
		if (type.getSimpleName().equals("Date"))
			return new JsonValue(JsonType.String, dateFormat.format(obj));

		if (!type.getName().startsWith("java.")
				&& !type.getName().startsWith("javax.")) {
			JsonValue child, result = new JsonValue();
			for (Field info : type.getDeclaredFields()) {
				String generic = info.toGenericString();
				if (generic.indexOf(" static ") != -1
						|| generic.indexOf(" final ") != -1)
					continue;
				String name = info.getName();
				try {
					child = ParseValue(info.get(obj));
					if (child != null)
						result.setMember(name, child);
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
			}
			return result;
		}

		throw new JsonException("JsonObjParser not support type: "
				+ type.getSimpleName());
	}
}
