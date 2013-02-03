package org.vxwo.java.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Date;

public class JsonSerializer {
	private final static int MAX_DEPTH = 10;

	private boolean serializeNulls = true;
	private boolean indentOutput = false;
	private int currentDepth = 0;
	private StringBuilder output = new StringBuilder();

	JsonSerializer(boolean serializeNulls, boolean indentOutput) {
		this.indentOutput = indentOutput;
		this.serializeNulls = serializeNulls;
	}

	String serialize(Object obj) throws JsonException {
		writeValue(obj);
		return output.toString();
	}

	@SuppressWarnings("rawtypes")
	private void writeValue(Object obj) throws JsonException {
		if (obj == null) {
			output.append("null");
			return;
		}

		Class type = obj.getClass();
		if (type.isArray())
			writeArray(obj);
		else if (obj instanceof Character)
			output.append((int) ((Character) obj).charValue());
		else if (obj instanceof Byte || obj instanceof Integer
				|| obj instanceof Long || obj instanceof Float
				|| obj instanceof Double || obj instanceof Boolean)
			output.append(obj);
		else if (obj instanceof String)
			writeString((String) obj);
		else if (obj instanceof Date)
			writeString(Convert.dateFormat.format((Date) obj));
		else if (obj instanceof JsonObject)
			writeObject((JsonObject) obj);
		else if (obj instanceof JsonArray)
			writeArray((JsonArray) obj);
		else {
			String typeName = type.getName();
			if (!typeName.startsWith("java.") && !typeName.startsWith("javax."))
				writeObject(type, obj);
			else
				throw new JsonException("Serializer: unsupport type: "
						+ typeName);
		}
	}

	@SuppressWarnings("rawtypes")
	private void writeObject(Class type, Object obj) throws JsonException {
		enterDepth('{');
		boolean append = false;
		for (Field info : type.getDeclaredFields()) {
			String generic = info.toGenericString();
			if (generic.indexOf(" static ") != -1
					|| generic.indexOf(" final ") != -1)
				continue;
			String name = info.getName();
			try {
				Object child = info.get(obj);
				if (child != null || serializeNulls) {
					if (append)
						output.append(',');
					writePair(name, child);
					append = true;
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			}
		}
		leaveDepth('}');
	}

	private void writeObject(JsonObject obj) throws JsonException {
		enterDepth('{');
		boolean append = false;
		for (String name : obj.names()) {
			Object child = obj.get(name);
			if (child != null || serializeNulls) {
				if (append)
					output.append(',');
				writePair(name, child);
				append = true;
			}
		}
		leaveDepth('}');
	}

	private void writeArray(Object obj) throws JsonException {
		enterDepth('[');
		boolean append = false;
		int i, length = Array.getLength(obj);
		for (i = 0; i < length; ++i) {
			Object child = Array.get(obj, i);
			if (child != null || serializeNulls) {
				if (append)
					output.append(',');
				writeValue(child);
				append = true;
			}
		}
		leaveDepth(']');
	}

	private void writeArray(JsonArray obj) throws JsonException {
		enterDepth('[');
		boolean append = false;
		int i, length = obj.length();
		for (i = 0; i < length; ++i) {
			Object child = obj.get(i);
			if (child != null || serializeNulls) {
				if (append)
					output.append(',');
				writeValue(child);
				append = true;
			}
		}
		leaveDepth(']');
	}

	private void indent() {
		if (indentOutput) {
			output.append("\r\n");
			for (int i = 0; i < currentDepth; i++)
				output.append("\t");
		}
	}

	private void enterDepth(char seq) throws JsonException {
		indent();
		currentDepth++;
		if (currentDepth > MAX_DEPTH)
			throw new JsonException("Serializer: encountered maximum depth of "
					+ MAX_DEPTH);
		output.append(seq);
	}

	private void leaveDepth(char seq) {
		currentDepth--;
		indent();
		output.append(seq);
	}

	private void writePair(String name, Object value) throws JsonException {
		indent();
		output.append('\"');
		output.append(name);
		output.append("\":");
		writeValue(value);
	}

	private void writeString(String s) {
		output.append('\"');
		int runIndex = -1;
		for (int index = 0; index < s.length(); ++index) {
			char c = s.charAt(index);
			if (c >= ' ' && c < 128 && c != '\"' && c != '\\') {
				if (runIndex == -1) {
					runIndex = index;
				}
				continue;
			}
			if (runIndex != -1) {
				output.append(s, runIndex, index);
				runIndex = -1;
			}
			switch (c) {
			case '\t':
				output.append("\\t");
				break;
			case '\r':
				output.append("\\r");
				break;
			case '\n':
				output.append("\\n");
				break;
			case '"':
			case '\\':
				output.append('\\');
				output.append(c);
				break;
			default:
				output.append("\\u");
				output.append(Integer.toHexString(c));
				break;
			}
		}
		if (runIndex != -1)
			output.append(s, runIndex, s.length());
		output.append('\"');
	}
}
