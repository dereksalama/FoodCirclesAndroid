package com.foodcircles.android;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class HttpUtil {
	private static String DOMAIN = "http://10.0.2.2:8888/";

	public static boolean postNoResponse(String servlet, List<BasicNameValuePair> params) throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(DOMAIN + servlet);
		post.setEntity(new UrlEncodedFormEntity(params));

		HttpResponse response = client.execute(post);

		return response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED;
	}


}
