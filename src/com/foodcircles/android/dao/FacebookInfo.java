package com.foodcircles.android.dao;

import android.content.Context;
import android.content.SharedPreferences;

public class FacebookInfo {

	private volatile static FacebookInfo instance;

	private SharedPreferences mPrefs;

	private static String PREFS_NAME = "fbinfo";

	private static String USER_ID = "user_id";

	private static String USER_NAME = "user_name";

	private static String TOKEN = "token";


	public static synchronized FacebookInfo get(Context c) {
		if (instance == null) {
			instance = new FacebookInfo(c.getApplicationContext());
		}
		return instance;
	}

	public void setName(String name) {
		set(USER_NAME, name);
	}

	public String getName() {
		return mPrefs.getString(USER_NAME, null);
	}

	public void setID(String id) {
		set(USER_ID, id);
	}

	public String getID() {
		return mPrefs.getString(USER_ID, null);
	}

	public void setToken(String token) {
		set(TOKEN, token);
	}

	public String getToken() {
		return mPrefs.getString(TOKEN, null);
	}

	private FacebookInfo(Context c) {
		mPrefs = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	}

	private void set(String name, String value) {
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(name, value);
		editor.commit();
	}


}
