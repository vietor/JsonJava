package org.vxwo.java.json;

import java.util.ArrayList;

public class JsonArray {
	private ArrayList<Object> objList = new ArrayList<Object>();

	Object get(int index) {
		return objList.get(index);
	}

	void add(Object obj) {
		objList.add(obj);
	}

	public int length() {
		return objList.size();
	}

	public int getInt(int index) {
		return Convert.toInt(objList.get(index));
	}

	public void addInt(int value) {
		objList.add(value);
	}

	public long getLong(int index) {
		return Convert.toLong(objList.get(index));
	}

	public void addLong(long value) {
		objList.add(value);
	}

	public double getDouble(int index) {
		return Convert.toDouble(objList.get(index));
	}

	public void addDouble(double value) {
		objList.add(value);
	}

	public String getString(int index) {
		return Convert.toString(objList.get(index));
	}

	public void addString(String value) {
		objList.add(value);
	}

	public JsonObject getObject(int index) {
		return (JsonObject) objList.get(index);
	}

	public void addObject(JsonObject obj) {
		objList.add(obj);
	}

	public JsonArray getArray(int index) {
		return (JsonArray) objList.get(index);
	}

	public void addArray(JsonArray array) {
		objList.add(array);
	}

}
