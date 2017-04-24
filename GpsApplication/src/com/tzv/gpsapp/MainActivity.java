package com.tzv.gpsapp;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tzv.gpsapp.R;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends FragmentActivity   implements OnClickListener, OnMapReadyCallback {
	private static final int MIN_UPDATE_TIME = 2000;
	private static final float MIN_UPDATE_DISTANCE_IN_METER= 1f;
	
	private Button recordingBtn;
	private Button playbackBtn;
	private TextView statusView;
	
	private boolean isRecord = false;
	private boolean isPlayback = false;
	private String status;
	
	private AppLocationListener locationListener;
	private LocationManager locationManager;
	private GpsData data;
	
	private PlaybackGpsDataTask playbackDataTask;
	
	
	public GpsData getData() {
		return this.data;
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.home_activity);
        
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        
        this.data = new GpsData(this);
        this.locationListener = new AppLocationListener(this, data);
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        this.recordingBtn = (Button) findViewById(R.id.record_btn);
        this.recordingBtn.setOnClickListener(this);
        
        this.playbackBtn = (Button) findViewById(R.id.playback_btn);
        this.playbackBtn.setOnClickListener(this);
        
        this.statusView = (TextView) findViewById(R.id.status);
    }

	@Override
	public void onClick(View v) {
		if(v.getId() == this.recordingBtn.getId() && !this.isPlayback) {
			if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				showErrorMesssage(R.string.error_message);
	        	return;
	        }
			
			this.isRecord = !this.isRecord;
			this.changeStatusString(this.isRecord, R.string.recording);
			this.changeButtonText(this.recordingBtn, this.isRecord, R.string.record_btn_stop, R.string.record_btn_start);
			this.recordGpsData();
		} else if(v.getId() == this.playbackBtn.getId() && !this.isRecord) {
			if(!this.data.isFileDataExists()) {
				showErrorMesssage(R.string.error_message);
	        	return;
	        }
			
			this.isPlayback = !this.isPlayback;
			this.changeStatusString(this.isPlayback, R.string.playback);
			this.changeButtonText(this.playbackBtn, 
					this.isPlayback, R.string.playback_btn_stop, R.string.playback_btn_start);
			this.playbackGpsData();
		}
		
		this.statusView.setText(getString(R.string.status).toString()+ " " + this.status);
	}
	

	@Override
	public void onMapReady(GoogleMap map) {
		map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
	}
	
	public void clearAllTextViews() {
		this.clearTextView(R.id.longitude, R.string.longitude);
		this.clearTextView(R.id.latitude, R.string.latitude);
		this.clearTextView(R.id.heading, R.string.heading);
		this.clearTextView(R.id.speed, R.string.speed);
	}
	
	public void showData() {
		this.showLocationProperty(R.id.longitude, R.string.longitude, this.data.getLongitute(), 7, "");
		this.showLocationProperty(R.id.latitude, R.string.latitude, this.data.getLatitude(), 7, "");
		this.showLocationProperty(R.id.heading, R.string.heading, this.data.getHeading(), 0, getString(R.string.degrees));
		this.showLocationProperty(R.id.speed, R.string.speed, this.data.getSpeed(), 0, getString(R.string.speed_units_m_s));
	}
	
	private void showLocationProperty(int viewId, int stringId, double value, int decimalPlaces, String units) {
		TextView textView = (TextView) this.findViewById(viewId);
		String text = this.getString(stringId) + " " + String.format("%."+ decimalPlaces +"f", value) + " " + units;
		textView.setText(text);
	}
	
	private void clearTextView(int viewId, int stringId) {
		TextView textView = (TextView) this.findViewById(viewId);
		String text = this.getString(stringId);
		textView.setText(text);
	}
	
	private void changeButtonText(Button button, boolean status, int trueValueResId, int falseValueResId) {
		int resurseId = status ? trueValueResId : falseValueResId;
		String buttonText = getString(resurseId).toString();
		Log.d("buttonText", buttonText);
		button.setText(buttonText);
	}
	
	private void changeStatusString(boolean isActive, int statusStringResId) {
		this.status = isActive ? getString(statusStringResId).toString() : "";
	}
	
	private void showErrorMesssage(int messageId) {
    	Toast.makeText(getApplicationContext(), 
				getString(messageId).toString(), Toast.LENGTH_SHORT).show();
	}
	
	private void playbackGpsData() {
		
		if(this.playbackDataTask == null) {
			this.playbackDataTask = new PlaybackGpsDataTask(this);
		}
		
		if(this.isPlayback) {
			this.playbackDataTask.execute();
		} else {
			this.playbackDataTask.cancel(true);
			this.playbackDataTask = null;
			this.clearAllTextViews();
		}
	}
	
	private void recordGpsData() {
		if(this.isRecord) {
			this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
					MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE_IN_METER, this.locationListener);
		} else {
			this.locationManager.removeUpdates(this.locationListener);
			this.data.closeWriter();
			this.clearAllTextViews();
		}
	}

}
