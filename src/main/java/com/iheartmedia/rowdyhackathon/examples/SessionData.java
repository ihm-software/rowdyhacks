package com.iheartmedia.rowdyhackathon.examples;

public class SessionData {
	public SessionData() {
		sessionId = "";
		profileId = "";
	}

	public SessionData(String profileId, String sessionId ) {
		this.sessionId = sessionId;
		this.profileId = profileId;
	}	
	
    public String sessionId;
    public String profileId;
}
