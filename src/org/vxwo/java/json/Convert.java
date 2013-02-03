package org.vxwo.java.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

class Convert {

	public final static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	static char toChar(Object obj) {
		return (char) toInt(obj);
	}

	static byte toByte(Object obj) {
		return (byte) toInt(obj);
	}

	static int toInt(Object obj) {
		if (obj instanceof Integer)
			return ((Integer) obj).intValue();
		if (obj instanceof Long)
			return ((Long) obj).intValue();
		if (obj instanceof Double)
			return ((Double) obj).intValue();
		if (obj instanceof String)
			return (int) Double.parseDouble((String) obj);
		return 0;
	}

	static long toLong(Object obj) {
		if (obj instanceof Integer)
			return ((Integer) obj).longValue();
		if (obj instanceof Long)
			return ((Long) obj).longValue();
		if (obj instanceof Double)
			return ((Double) obj).longValue();
		if (obj instanceof String)
			return (long) Double.parseDouble((String) obj);
		return 0;
	}

	static float toFloat(Object obj) {
		return (float) toDouble(obj);
	}

	static double toDouble(Object obj) {
		if (obj instanceof Integer)
			return ((Integer) obj).doubleValue();
		if (obj instanceof Long)
			return ((Long) obj).doubleValue();
		if (obj instanceof Double)
			return ((Double) obj).doubleValue();
		if (obj instanceof String)
			return Double.parseDouble((String) obj);
		return 0.0;
	}

	static String toString(Object obj) {
		if (obj instanceof String)
			return (String) obj;
		return String.valueOf(obj);
	}

	static Date toDate(Object obj) {
		try {
			return dateFormat.parse(obj.toString());
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	private static Object toBaseObject(Class type, Object obj) {
		if (obj == null)
			return null;

		if (type == char.class)
			return toChar(obj);
		else if (type == byte.class)
			return toByte(obj);
		else if (type == int.class)
			return toInt(obj);
		else if (type == float.class)
			return toFloat(obj);
		else if (type == double.class)
			return toDouble(obj);
		else if (type == String.class)
			return toString(obj);
		else if (type == Date.class)
			return toDate(obj);

		String typeName = type.getName();
		if (!typeName.startsWith("java.") && !typeName.startsWith("javax."))
			return toObject(type, obj);
		else
			return null;
	}

	@SuppressWarnings("rawtypes")
	static Object toObject(Class type, Object obj) {
		if (obj == null)
			return null;

		Object target = null;

		if (type.isArray()) {
			if (obj instanceof JsonArray) {
				JsonArray jarray = (JsonArray) obj;
				Class subType = type.getComponentType();
				target = Array.newInstance(subType, jarray.length());
				for (int i = 0; i < jarray.length(); i++)
					Array.set(target, i, toBaseObject(subType, jarray.get(i)));
			}
			return target;
		}

		try {
			target = type.newInstance();
			JsonObject jobject = (JsonObject) obj;
			for (Field info : type.getDeclaredFields()) {
				String generic = info.toGenericString();
				if (generic.indexOf(" static ") != -1
						|| generic.indexOf(" final ") != -1)
					continue;
				String name = info.getName();
				if (jobject.has(name)) {
					try {
						Class subType = info.getType();
						Object subObject = jobject.get(name);
						info.set(target, toBaseObject(subType, subObject));
					} catch (IllegalArgumentException e) {
					} catch (IllegalAccessException e) {
					}
				}
			}
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		return target;
	}
}
