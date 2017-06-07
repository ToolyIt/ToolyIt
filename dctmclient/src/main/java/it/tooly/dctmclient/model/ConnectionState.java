package it.tooly.dctmclient.model;

public enum ConnectionState {
	DISCONNECTED("circle_grey"), CONNECTING("circle_blue"), CONNECTED("circle_green"), FAILED("circle_red");
	
	public String imageName;
	
	ConnectionState(String imgName) {
		imageName = imgName;
	}
}
