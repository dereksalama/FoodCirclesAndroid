package com.foodcircles.android.dao;

import java.util.Date;

public class GroupChatMessage {

	public String text;

	public Date time;

	public User sender;

	public Long circle;

	public GroupChatMessage() {}

	public GroupChatMessage(String text, Date time, User sender, Long circle) {
		this.text = text;
		this.time = time;
		this.sender = sender;
		this.circle = circle;
	}


	@Override
	public String toString() {
		return text;
	}

}
