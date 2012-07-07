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
		if (this.type == JsonType.Array && obj == null)
			this.store = new ArrayList<JsonValue>();
		else
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

	public JsonValue setMember(String name, JsonValue value)
			throws JsonException {
		if (value == null)
			value = new JsonValue(JsonType.Null, null);
		ensureObject().put(name, value);
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

	public int count() throws JsonException {
		if (type != JsonType.Array)
			return 0;
		return ensureArray().size();
	}

	public JsonValue getAt(int index) throws JsonException {
		return ensureArray().get(index);
	}

	public JsonValue append(JsonValue value) throws JsonException {
		if (value != null)
			ensureArray().add(value);
		return this;
	}

	public boolean isNull() {
		return type == JsonType.Null || type == JsonType.None;
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

	public boolean asBoolean() throws JsonException {
		boolean value = false;
		if (isBoolean())
			value = ((Boolean) store).booleanValue();
		else if (isString())
			value = Boolean.parseBoolean((String) store);
		else
			throw new JsonException("JsonValue cannot convert to a bool");
		return value;
	}

	public int asInt() throws JsonException {
		int value = 0;
		if (isInt())
			value = ((Integer) store).intValue();
		else if (isString())
			value = Integer.parseInt((String) store);
		else if (isLong())
			value = ((Long) store).intValue();
		else if (isDouble())
			value = ((Double) store).intValue();
		else
			throw new JsonException("JsonValue cannot convert to a int");
		return value;
	}

	public long asLong() throws JsonException {
		long value = 0;
		if (isLong())
			value = ((Long) store).longValue();
		else if (isString())
			value = Long.parseLong((String) store);
		else if (isInt())
			value = ((Integer) store).longValue();
		else if (isDouble())
			value = ((Double) store).longValue();
		else
			throw new JsonException("JsonValue cannot convert to a long");
		return value;
	}

	public double asDouble() throws JsonException {
		double value = 0;
		if (isDouble())
			value = ((Double) store).doubleValue();
		else if (isString())
			value = Double.parseDouble((String) store);
		else if (isInt())
			value = ((Integer) store).doubleValue();
		else if (isLong())
			value = ((Long) store).doubleValue();
		else
			throw new JsonException("JsonValue cannot convert to a double");
		return value;
	}

	public String asString() {
		return isString() ? (String) store : store.toString();
	}

}
