package ch.appquest.groessenmesser;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;


public class CameraActivity extends Activity implements SurfaceHolder.Callback {
	// camera
	private Camera					camera;
	private SurfaceHolder			cameraViewHolder;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Timeout disable
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// camera release
		if (camera != null) {
			camera.stopPreview();
			camera.release();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// reference
		SurfaceView cameraView = (SurfaceView) findViewById(R.id.surfaceView);
		cameraViewHolder = cameraView.getHolder();
		cameraViewHolder.addCallback(this);		
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();
	}
		
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {	
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
	public boolean onTouchEvent(MotionEvent event) {
		// check if hand up
		if (event.getAction() == MotionEvent.ACTION_UP) {
			Toast.makeText(getApplicationContext(), "Touch event", Toast.LENGTH_LONG).show();
		}
		
		return super.onTouchEvent(event);
	}
}
