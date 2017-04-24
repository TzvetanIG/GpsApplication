package com.tzv.gpsapp;

import java.io.IOException;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class AppLocationListener implements LocationListener {
	private MainActivity activity;
	private GpsData data;
	
	public AppLocationListener(MainActivity activity, GpsData data) {
		this.activity = activity;
		this.data = data;
	}
	
	@Override
	public void onLocationChanged(Location location) {		
		this.data.LoadData(location);
		this.activity.showData();
		try {
			this.data.writeToFile(location);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
}
