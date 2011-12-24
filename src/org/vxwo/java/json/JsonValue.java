package org.vxwo.java.json;

import java.util.ArrayList;
import java.util.HashMap;

public class JsonValue {
	JsonType type;
	Object store;

	public JsonValue() {
		type = JsonType.None;
		store = null;
	}

	JsonValue(JsonType type, Object obj) {
		this.type = type;
		this.store = obj;
	}

	public JsonValue(boolean value) {
		this(JsonType.Boolean, value);
	}

	public JsonValue(int value) {
		this(JsonType.Int, value);
	}

	public JsonValue(long value) {
		this(JsonType.Long, value);
	}

	public JsonValue(double value) {
		this(JsonType.Double, value);
	}

	public JsonValue(String value) {
		this(JsonType.String, value);
	}

	public void Clear() {
		type = JsonType.None;
		store = null;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, JsonValue> ensureObject() throws JsonException {
		if (type != JsonType.None && type != JsonType.Object)
			throw new JsonException("JsonValue not a object");

		if (type == JsonType.None) {
			type = JsonType.Object;
			store = new HashMap<String, JsonValue>();
		}
		return (HashMap<String, JsonValue>) store;
	}

	public JsonValue getMember(String name) throws JsonException {
		JsonValue result;
		HashMap<String, JsonValue> obj = ensureObject();
		if (obj.containsKey(name))
			result = obj.get(name);
		else {
			result = new JsonValue();
			obj.put(name, result);
		}
		return result;
	}

	public JsonValue setMember(String name, JsonValue value) throws JsonException {
		HashMap<String, JsonValue> obj = ensureObject();
		obj.put(name, value);
		return this;
	}

	public boolean isMember(String name) throws JsonException {
		return ensureObject().containsKey(name);
	}

	public void removeMember(String name) throws JsonException {
		ensureObject().remove(name);
	}

	@SuppressWarnings("unchecked")
	private ArrayList<JsonValue> ensureArray() throws JsonException {
		if (type != JsonType.None && type != JsonType.Array)
			throw new JsonException("JsonValue not a array");

		if (type == JsonType.None) {
			type = JsonType.Array;
			store = new ArrayList<JsonValue>();
		}
		return (ArrayList<JsonValue>) store;
	}

	public int count() throws Exception {
		if (type != JsonType.Array)
			return 0;
		return ensureArray().size();
	}

	public JsonValue getAt(int index) throws JsonException {
		return ensureArray().get(index);
	}

	public JsonValue append(JsonValue value) throws JsonException {
		ensureArray().add(value);
		return this;
	}

	public boolean isBoolean() {
		return type == JsonType.Boolean;
	}

	public boolean isInt() {
		return type == JsonType.Int;
	}

	public boolean isLong() {
		return type == JsonType.Long;
	}

	public boolean isDouble() {
		return type == JsonType.Double;
	}

	public boolean isString() {
		return type == JsonType.String;
	}

	public boolean asBoolean() {
		return isString() ? Boolean.parseBoolean((String) store)
				: ((Boolean) store).booleanValue();
	}

	public int asInt() {
		return isString() ? Integer.parseInt((String) store)
				: ((Integer) store).intValue();
	}

	public long asLong() {
		return isString() ? Long.parseLong((String) store) : ((Long) store)
				.longValue();
	}

	public double asDouble() {
		return isString() ? Double.parseDouble((String) store)
				: ((Double) store).doubleValue();
	}

	public String asString() {
		return isString() ? (String) store : store.toString();
	}

}
