package com.foodcircles.android.dao;

import com.google.gson.annotations.SerializedName;




public class User {

	@SerializedName("user_id")
	public String userID;

	public String name;

	public Integer status;

	@SerializedName("time")
	public String desiredTime;
	//having trouble using real date stuff, just use hh:mm time stuff for now

	@SerializedName("location")
	public String desiredLocation;


	public User (){};

	public User(String userID, String name, Integer status) {
		this.userID = userID;
		this.name = name;
		this.status = status;

		desiredTime = null;
		desiredLocation = null;
	}

	public User(String userID, String name, Integer status, String desiredTime, String desiredLocation) {
		this.userID = userID;
		this.name = name;
		this.status = status;

		this.desiredTime = desiredTime;
		this.desiredLocation = desiredLocation;

	}

	public String getEventString() {
		if (desiredLocation == null && desiredLocation == null)
			return null;

		String result = "";
		if (desiredLocation != null)
			result += desiredLocation;
		if (desiredTime != null)
			result += desiredTime;

		return result;
	}


	@Override
	public String toString() {
		return name;
	}


}
