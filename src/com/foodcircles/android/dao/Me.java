package com.foodcircles.android.dao;

import android.content.Context;

public class Me {

	private static String NAME = "Me";
	private static Integer STATUS = -1;

	public static User get(Context c) {
		User u = new User();
		u.name = NAME;
		u.status = STATUS;
		u.userID = FacebookInfo.get(c).getID();
		return u;
	}

}
