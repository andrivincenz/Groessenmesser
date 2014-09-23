package ch.appquest.groessenmesser;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class MainActivity extends Activity implements SurfaceHolder.Callback, Camera.PictureCallback {
	private SurfaceView surfaceView;
	private Camera camera;
	private Camera.PictureCallback cameraCallbackPreview;
	private Camera.ShutterCallback cameraCallbackVerschluss;
	private SurfaceHolder cameraViewHolder;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);

	    surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
	
	    //disable timeout
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		
		cameraViewHolder = surfaceView.getHolder();
		cameraViewHolder.addCallback(this);
		
		cameraViewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		cameraCallbackPreview = new Camera.PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {}
		};
		cameraCallbackVerschluss = new Camera.ShutterCallback() {
			@Override
			public void onShutter() {}
		};
		camera.startPreview();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (camera != null) {
			camera.stopPreview();
			camera.release();
		}
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		camera.stopPreview();
		
		Camera.Parameters params = camera.getParameters();
		List<Camera.Size> sizes = params.getSupportedPreviewSizes();
		Camera.Size size = sizes.get(0);
		
		params.setPreviewSize(size.width, size.height);
		camera.setParameters(params);
		
		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			// TODO: handle exception
		}
		
		camera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {}
}
