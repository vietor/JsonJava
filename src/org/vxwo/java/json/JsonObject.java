package org.vxwo.java.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JsonObject {
	private Map<String, Object> objMap = new HashMap<String, Object>();

	Set<String> names() {
		return objMap.keySet();
	}

	Object get(String name) {
		return objMap.get(name);
	}

	void put(String name, Object obj) {
		objMap.put(name, obj);
	}
	
	public boolean has(String name)
	{
		return objMap.containsKey(name);
	}

	public int getInt(String name) {
		return Convert.toInt(objMap.get(name));
	}

	public void putInt(String name, int value) {
		objMap.put(name, value);
	}

	public long getLong(String name) {
		return Convert.toLong(objMap.get(name));
	}

	public void putLong(String name, long value) {
		objMap.put(name, value);
	}

	public double getDouble(String name) {
		return Convert.toDouble(objMap.get(name));
	}

	public void putDouble(String name, double value) {
		objMap.put(name, value);
	}

	public String getString(String name) {
		return Convert.toString(objMap.get(name));
	}

	public void putString(String name, String value) {
		objMap.put(name, value);
	}

	public JsonObject getObject(String name) {
		return (JsonObject) objMap.get(name);
	}

	public void putObject(String name, JsonObject obj) {
		objMap.put(name, obj);
	}

	public JsonArray getArray(String name) {
		return (JsonArray) objMap.get(name);
	}

	public void putArray(String name, JsonArray array) {
		objMap.put(name, array);
	}
}
