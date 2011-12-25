package org.vxwo.java.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

class JsonSerializer {
	
	private StringBuilder output = new StringBuilder();
	private boolean serializeNulls = true;
	private boolean indentOutput = false;
	private int MAX_DEPTH = 10;
	private int currentDepth = 0;

	JsonSerializer(boolean serializeNulls, boolean indentOutput) {
		this.indentOutput = indentOutput;
		this.serializeNulls = serializeNulls;
	}

	String toJSON(JsonValue obj) throws JsonException {
		WriteValue(obj);
		return output.toString();
	}

	private void WriteValue(JsonValue obj) throws JsonException {
		switch (obj.type) {
		case None:
		case Null:
			output.append("null");
			break;
		case Boolean:
			output.append(((Boolean) obj.store) ? "true" : "false");
			break;
		case Int:
			output.append((Integer) obj.store);
			break;
		case Long:
			output.append((Long) obj.store);
			break;
		case Double:
			output.append((Double) obj.store);
			break;
		case String:
			WriteString((String) obj.store);
			break;
		case Object:
			WriteObject(obj);
			break;
		case Array:
			WriteArray(obj);
			break;
		}
	}

	@SuppressWarnings("unchecked")
	private void WriteObject(JsonValue obj) throws JsonException {
		Indent();
		currentDepth++;
		if (currentDepth > MAX_DEPTH)
			throw new JsonException("Serializer encountered maximum depth of "
					+ MAX_DEPTH);
		output.append('{');

		boolean append = false;
		Entry<String, JsonValue> kv;
		Iterator<Entry<String, JsonValue>> itr = ((HashMap<String, JsonValue>) obj.store)
				.entrySet().iterator();
		while (itr.hasNext()) {
			kv = itr.next();

			if (append)
				output.append(',');

			if (kv.getValue().type == JsonType.None
					|| (kv.getValue().type == JsonType.Null && serializeNulls == false))
				append = false;
			else {
				WritePair(kv.getKey(), kv.getValue());
				append = true;
			}
		}

		currentDepth--;
		Indent();
		output.append('}');
		currentDepth--;
	}

	@SuppressWarnings("unchecked")
	private void WriteArray(JsonValue obj) throws JsonException {
		Indent();
		currentDepth++;
		if (currentDepth > MAX_DEPTH)
			throw new JsonException("Serializer encountered maximum depth of "
					+ MAX_DEPTH);
		output.append('[');

		boolean append = false;
		JsonValue v;
		ArrayList<JsonValue> list = (ArrayList<JsonValue>) obj.store;
		for (int i = 0; i < list.size(); ++i) {
			v = list.get(i);
			if (append)
				output.append(',');

			if (v.type == JsonType.None
					|| (v.type == JsonType.Null && serializeNulls == false))
				append = false;
			else {
				WriteValue(v);
				append = true;
			}
		}

		currentDepth--;
		Indent();
		output.append(']');
		currentDepth--;
	}

	private void Indent() {
		Indent(false);
	}

	private void Indent(boolean dec) {
		if (indentOutput) {
			output.append("\r\n");
			for (int i = 0; i < currentDepth - (dec ? 1 : 0); i++)
				output.append("\t");
		}
	}

	private void WritePair(String name, JsonValue value) throws JsonException {
		if ((value.type == JsonType.None || value.type == JsonType.Null)
				&& serializeNulls == false)
			return;

		Indent();
		WriteStringFast(name);

		output.append(':');

		WriteValue(value);
	}

	private void WriteStringFast(String s) {
		output.append('\"');
		output.append(s);
		output.append('\"');
	}

	private void WriteString(String s) {
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

		if (runIndex != -1) {
			output.append(s, runIndex, s.length());
		}

		output.append('\"');
	}
}
