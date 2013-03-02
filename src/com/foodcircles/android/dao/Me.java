package com.foodcircles.android.dao;

import android.content.Context;
import android.content.SharedPreferences;


public class Me {

	private static User me;

	public static void set(User u) {
		me = u;
	}

	public static User get() {
		return me;
	}

	private static final String tableName = "meinfo";

	private static final String STATUS = "status";

	private SharedPreferences mPrefs;

	public Me(Context c) {
		mPrefs = c.getSharedPreferences(tableName, Context.MODE_PRIVATE);
	}

	//TODO: so hacky
	public void setStatus(int status) {
		mPrefs.edit().putInt(STATUS, status);
		if (me != null) {
			me.status = status;
		}
	}

	public int getStatus() {
		return mPrefs.getInt(STATUS, 2);
	}
}
