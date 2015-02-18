package com.example.mobilesensing;

public class AccessPoint {
	
	private String bssid;
	private float level;
	
	public AccessPoint(String _bssid, float _level){
		bssid = _bssid;
		level = _level;
	}
	
	public String getBssid(){
		return bssid;
	}
	
	public float getLevel(){
		return level;
	}
	
	public String toString(){
		return "AccessPoint [bssid=" + bssid + ", signalstrength=" + level + "]";
	}

}
