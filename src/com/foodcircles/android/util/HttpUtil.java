package com.foodcircles.android.util;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtil {
//	private static String DOMAIN = "http://192.168.1.30:8888/"; //home
//	private static String DOMAIN = "http://10.0.2.2:8888/";
	private static String DOMAIN = "http://10.1.4.56:8888/"; //work
	public static boolean postNoResponse(String servlet, List<BasicNameValuePair> params) throws ClientProtocolException, IOException {

		HttpResponse response = post(servlet, params);

		return response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED;
	}

	public static String postForJson(String servlet, List<BasicNameValuePair> params) throws ClientProtocolException, IOException {

		HttpResponse response = post(servlet, params);

		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED || response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity result = response.getEntity();
			return EntityUtils.toString(result);
		} else {
			Log.d("HttpUtil", "error performing post for json");
			return "";
		}
	}

	public static HttpResponse post(String servlet, List<BasicNameValuePair> params) throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(DOMAIN + servlet);
		Log.d("HttpUtil", params.toString());
		post.setEntity(new UrlEncodedFormEntity(params));

		HttpResponse response = client.execute(post);

		return response;
	}


}
