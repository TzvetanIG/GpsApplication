package com.tzv.gpsapp;

import java.io.IOException;

import android.os.AsyncTask;

public class PlaybackGpsDataTask extends AsyncTask<Void, GpsData, Void> {
	private GpsData data;
	private MainActivity activity;

	public PlaybackGpsDataTask(MainActivity activity) {
		super();
		this.activity = activity;
		this.data = activity.getData();
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			long firstTime = 0;
			long sleepTime = 0;
			while (this.data.readFromFile() != null) {
				if (isCancelled()) {
					break;
				}

				if (firstTime > 0) {
					sleepTime = this.data.getTimeInMillis() - firstTime;
				}

				firstTime = this.data.getTimeInMillis();
				publishProgress(this.data);
				Thread.sleep(sleepTime);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			this.data.closeReader();
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(GpsData... data) {
		super.onProgressUpdate(data);
		this.activity.showData();
	}
}
