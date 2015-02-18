package com.example.mobilesensing;

import java.util.List;

import android.hardware.*;

import android.util.Log;

import android.hardware.SensorManager;











public class SensorList {

	
	
	
	private List<Sensor> sensors;
	
	
	public List<Sensor> getSensors(SensorManager mgr){
	
			sensors = mgr.getSensorList(Sensor.TYPE_ALL);    
		    for (Sensor sensor : sensors) {
		        Log.d("Sensors", "" + sensor.getName());
		    }
		   
		    
		    return sensors;
		
	}
	
	

	
	
}
