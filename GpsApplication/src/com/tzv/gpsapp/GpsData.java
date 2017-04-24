package com.tzv.gpsapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.location.Location;

public class GpsData {
	private static final String DATA_FILE_NAME = "GPS_data.log";
	File file;
	BufferedWriter writer;
	BufferedReader reader;

	private double longitute;
	private double latitude;
	private double heading;
	private double speed;
	private long timeInMillis;

	public GpsData(Activity activity) {
		this.file = new File(activity.getCacheDir(), DATA_FILE_NAME);
	}

	public double getLongitute() {
		return this.longitute;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public double getHeading() {
		return this.heading;
	}

	public double getSpeed() {
		return this.speed;
	}
	
	public long getTimeInMillis() {
		return this.timeInMillis;
	}

	public void writeToFile(Location location) throws IOException {
		if (this.writer == null) {
			FileWriter fileWriter = new FileWriter(this.file, false);
			this.writer = new BufferedWriter(fileWriter);
		}

		String data = location.getLongitude() + ";" + location.getLatitude()
				+ ";" + location.getBearing() + ";" + location.getSpeed() + ";"
				+ System.currentTimeMillis();

		this.timeInMillis = System.currentTimeMillis();
		writer.append(data);
		writer.newLine();
	}

	public void closeWriter() {
		try {
			if (this.writer != null) {
				this.writer.close();
				this.writer = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public GpsData readFromFile() throws IOException {
		if (this.reader == null) {
			FileReader fileReader = new FileReader(this.file);
			this.reader = new BufferedReader(fileReader);
		}

		String line = this.reader.readLine();
		if(line != null) {
			String[] results = line.split(";");
			this.longitute = Double.valueOf(results[0]);
			this.latitude = Double.valueOf(results[1]);
			this.heading = Double.valueOf(results[2]);
			this.speed = Double.valueOf(results[3]);
			this.timeInMillis = Long.valueOf(results[4]);
			return this;
		}
		
		return null;
	}

	public void closeReader() {
		try {
			if (this.reader != null) {
				this.reader.close();
				this.reader = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isFileDataExists() {
		return this.file.exists();
	}
	
	public void LoadData(Location location) {
		this.latitude = location.getLatitude();
		this.longitute  = location.getLongitude();
		this.heading = location.getBearing();
		this.speed = location.getSpeed();
	}
}
