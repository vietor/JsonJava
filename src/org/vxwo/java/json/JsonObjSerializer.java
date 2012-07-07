package org.vxwo.java.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;

public class JsonObjSerializer {

	private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);

	JsonObjSerializer() {
	}

	@SuppressWarnings("rawtypes")
	Object ConvertToObject(Class type, JsonValue obj) throws JsonException {
		return WriteObject(type, null, obj);
	}

	@SuppressWarnings("rawtypes")
	private Object WriteObject(Class type, Class declare, JsonValue obj)
			throws JsonException {
		if (obj == null || obj.isNull())
			return null;

		if (type.isArray()) {
			Class etype = type.getComponentType();
			Object array = Array.newInstance(etype, obj.count());
			for (int i = 0; i < obj.count(); i++)
				Array.set(array, i, WriteObject(etype, null, obj.getAt(i)));
			return array;
		}
		if (type.getSimpleName().equalsIgnoreCase("Char"))
			return (char) obj.asInt();
		if (type.getSimpleName().equalsIgnoreCase("Byte"))
			return (byte) obj.asInt();
		if (type.getSimpleName().equalsIgnoreCase("Boolean"))
			return obj.asBoolean();
		if (type.getSimpleName().equalsIgnoreCase("Integer"))
			return obj.asInt();
		if (type.getSimpleName().equalsIgnoreCase("Long"))
			return obj.asLong();
		if (type.getSimpleName().equalsIgnoreCase("Folat"))
			return (float) obj.asDouble();
		if (type.getSimpleName().equalsIgnoreCase("Double"))
			return obj.asDouble();
		if (type.getSimpleName().equalsIgnoreCase("String"))
			return obj.asString();
		if (type.getSimpleName().equalsIgnoreCase("Date"))
			try {
				return dateFormat.parse(obj.asString());
			} catch (ParseException e) {
				return null;
			}

		if (!type.getName().startsWith("java.")
				&& !type.getName().startsWith("javax.")) {
			Object result;
			try {
				result = type.newInstance();
			} catch (Exception e1) {
				return null;
			}
			for (Field info : type.getDeclaredFields()) {
				String generic = info.toGenericString();
				if (generic.indexOf(" static ") != -1
						|| generic.indexOf(" final ") != -1)
					continue;
				String name = info.getName();
				if (obj.isMember(name))
					try {
						info.set(
								result,
								WriteObject(info.getType(),
										info.getDeclaringClass(),
										obj.getMember(name)));
					} catch (IllegalArgumentException e) {
					} catch (IllegalAccessException e) {
					}
			}
			return result;
		}

		throw new JsonException("JsonObjSerializer not support type: "
				+ type.getName());
	}
}
