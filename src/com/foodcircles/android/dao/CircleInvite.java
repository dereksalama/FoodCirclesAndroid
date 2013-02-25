package com.foodcircles.android.dao;

public class CircleInvite {

	private String circleName;
	private Long keyID;

	private String senderName;

	public CircleInvite() {} //no args for json:w


	public CircleInvite(String circleName, Long keyID, String senderName) {
		this.setCircleName(circleName);
		this.setKeyID(keyID);
		this.setSenderName(senderName);
	}


	public String getCircleName() {
		return circleName;
	}


	public void setCircleName(String circleName) {
		this.circleName = circleName;
	}


	public Long getKeyID() {
		return keyID;
	}


	public void setKeyID(Long keyID) {
		this.keyID = keyID;
	}


	public String getSenderName() {
		return senderName;
	}


	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	@Override
	public String toString() {
		return "Invite to " + circleName + " from " + senderName;
	}

}
