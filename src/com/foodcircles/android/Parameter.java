package com.foodcircles.android;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Stack;

public class Parameter {

	String name;
	String value;
	public Parameter(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String toString() {
		try {
			return name + "=" + URLEncoder.encode(value, HttpUtils.CHARSET);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String prepare(Stack<Parameter> parameters) {
		if (parameters.empty()) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		builder.append(parameters.pop().toString());
		while (!parameters.empty()) {
			builder.append('&');
			builder.append(parameters.pop().toString());
		}

		return builder.toString();
	}

}
