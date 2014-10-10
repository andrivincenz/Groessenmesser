package ch.appquest.groessenmesser;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;


public class CameraActivity extends Activity implements SurfaceHolder.Callback, SensorEventListener {
	// camera
	private Camera					camera;
	private SurfaceHolder			cameraViewHolder;

	// caluclation
	private double 					alpha = 0;
	private double 					beta = 0;
	
	// sensor
	private SensorManager			mSensorManager; // managet alle sensoren besser für ihn
	private Sensor					mMagnetField; // magnet sensor
	private Sensor					mAcceleration; // beschleunigungs sensor oder lage sensor. kp
	private final float[] 			magneticFieldData = new float[3];
	private final float[] 			accelerationData = new float[3];
	
	@Override
	public void onCreate(Bundle savedInstanceState) { // wenn activity erstellt wird. also genau 1 mal
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// initialise sensors
		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		mMagnetField = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).get(0);
		mAcceleration = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
		
		if (mMagnetField == null) {
			Toast.makeText(getApplicationContext(), "Magnetsensor nicht vorhanden", Toast.LENGTH_LONG).show();
			finish();
		}
		
		if (mAcceleration == null) {
			Toast.makeText(getApplicationContext(), "Lagesensor nicht vorhanden", Toast.LENGTH_LONG).show();
			finish();
		}
		
		// Timeout disable
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// unregister sensors
		mSensorManager.unregisterListener(this);
		
		// camera release
		if (camera != null) {
			camera.stopPreview();
			camera.release();
		}
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		
		// register sensors
		if (mMagnetField != null) {
			mSensorManager.registerListener(this, mMagnetField, SensorManager.SENSOR_DELAY_NORMAL);
		}
		
		if (mAcceleration != null){
			mSensorManager.registerListener(this, mAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
		}
		
		// reference
		SurfaceView cameraView = (SurfaceView) findViewById(R.id.surfaceView);
		cameraViewHolder = cameraView.getHolder();
		cameraViewHolder.addCallback(this);		
		
		// reset buffer
		alpha = 0;
		beta = 0;
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();
	}
		
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {	// wenn kamera bild ändert
		camera.stopPreview();
		
		Camera.Parameters params = camera.getParameters();
		
		List<Camera.Size> sizes = params.getSupportedPreviewSizes();
		List<String> focusModes = params.getSupportedFocusModes();
		
		Camera.Size cs = sizes.get(0);
		params.setPreviewSize(cs.width, cs.height);

		if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
			params.setFlashMode(Camera.Parameters.FOCUS_MODE_AUTO);
		}
		
		camera.setParameters(params);
		
		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			Log.d("APPQUEST", "APPQUEST: " + e.getMessage());
		}
		
		camera.startPreview();
	}

	public void surfaceDestroyed(SurfaceHolder holder) { }
		
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { }
 
	@Override
	public void onSensorChanged(SensorEvent event) {
		// copy sensor date in buffer
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			System.arraycopy(event.values, 0, accelerationData, 0, 3);
		}
 
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			System.arraycopy(event.values, 0, magneticFieldData, 0, 3);
		}
	}
 
	private double getCurrentRotationValue() {
		float[] rotationMatrix = new float[16];
		
		if (SensorManager.getRotationMatrix(rotationMatrix, null, accelerationData, magneticFieldData)) {
 
			float[] orientation = new float[4];
			SensorManager.getOrientation(rotationMatrix, orientation);
 
			double neigung = Math.toDegrees(orientation[2]);
 
			return Math.abs(neigung);
		}
 
		return 0;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// check if hand up
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (alpha == 0){
				alpha = getCurrentRotationValue();
				Toast.makeText(getApplicationContext(), "alpha: " + alpha, Toast.LENGTH_LONG).show();
			} else {
				beta = getCurrentRotationValue();
				Toast.makeText(getApplicationContext(), "beta: " + beta, Toast.LENGTH_LONG).show();
				
				Intent calcIntent = new Intent(this, CalculationActivity.class);
				calcIntent.putExtra("alpha", alpha);
				calcIntent.putExtra("beta", beta);
				startActivity(calcIntent);
			}
		}
		return super.onTouchEvent(event);
	}
}
