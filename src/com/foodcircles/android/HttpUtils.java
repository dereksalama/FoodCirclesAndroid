package com.foodcircles.android;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;

public class HttpUtils {

	private static String DOMAIN = "http://localhost:8888/";
	public static String CHARSET = "UTF-8";


	public static boolean doPostNoResponse(String servletUrl, Stack<Parameter> parameters) throws IOException {
		HttpURLConnection connection = preparePost(servletUrl, Parameter.prepare(parameters));
		connection.connect();
		return connection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED;
	}

	private static HttpURLConnection preparePost(String servletUrl, String query) throws MalformedURLException, IOException {
		String urlString = DOMAIN + servletUrl;
		HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
		connection.setDoOutput(true); //POST

		connection.setRequestProperty("Accept-Charset", CHARSET);
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + CHARSET);

		OutputStream output = null;
		try {
		     output = connection.getOutputStream();
		     output.write(query.getBytes(CHARSET));
		} finally {
		     if (output != null)
		    	 output.close();
		}

		return connection;
	}



}
