package ch.appquest.groessenmesser;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CalculationActivity extends Activity {
	private static final int SCAN_QR_CODE_REQUEST_CODE = 0;
	
	private double alpha;
	private double beta;
	private double distance;
	private EditText txtAlpha;
	private EditText txtBeta;
	private EditText txtDistance;
	private EditText txtHeight;
	private Button btnCalculate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calculation);
		
		float alphaRound;
		float betaRound;
		
		// get angels from activity
		double angelTop = getIntent().getDoubleExtra("alpha", 0);
		double angelBottom = getIntent().getDoubleExtra("beta", 0);
		
		if (angelTop >= angelBottom) { 
			alpha = angelBottom;
			beta = angelTop - angelBottom;
		} else {
			alpha = angelTop;
			beta = angelBottom - angelTop;
		}
		
		// init components
		txtAlpha = (EditText)findViewById(R.id.txtAlpha);
		txtBeta = (EditText)findViewById(R.id.txtBeta);
		txtDistance = (EditText)findViewById(R.id.txtDistance);
		txtHeight = (EditText)findViewById(R.id.txtHeight);
		btnCalculate = (Button)findViewById(R.id.btnCalculate);
		
		initListener();
		
		// round angels and put in edittext
		alphaRound = Math.round(alpha * 100f) / 100f;
		betaRound = Math.round(beta * 100f) / 100f;
		txtAlpha.setText(alphaRound + "°");
		txtBeta.setText(betaRound + "°");

	}

	/**
	 * initialise listeners
	 */
	private void initListener() {
		// calulcagte listener
		btnCalculate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// set the distance
				double distance = Double.parseDouble(txtDistance.getText().toString());
				double height = calcHeight(alpha, beta, distance);
				if (height != 0) {
					txtHeight.setText(height + "");
				}
			}
		});
	}
	
	/**
	 * calucalte hight
	 * @param alpha: angel top
	 * @param beta:  angel bottom
	 * @param distance: distance between 
	 * @return height from object
	 * @see <img src="http://appquest.hsr.ch/2014/wp-content/uploads/gr%C3%B6ssenmesser.png" />
	 */
	private double calcHeight(double alpha, double beta, double distance) {
		double height = 0;
		
		beta = Math.toRadians(beta - (90 - alpha));
		alpha = Math.toRadians(90 - alpha);

		height = (Math.sin(alpha)) * (distance / Math.sin(alpha)) + (Math.tan(beta * distance));
		height = Math.round(height * 100) / 100;
		return height;
	}
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem menuItem = menu.add("Log");
		menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
	 
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				startActivityForResult(intent, SCAN_QR_CODE_REQUEST_CODE);
				return false;
			}
		});
	 
		return super.onCreateOptionsMenu(menu);
	}
	 
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == SCAN_QR_CODE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String logMsg = intent.getStringExtra("SCAN_RESULT");
				log(logMsg);
			}
		}
	}
	
	private void log(String qrCode) {
		Intent intent = new Intent("ch.appquest.intent.LOG");
	 
		if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
			Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
			return;
		}
	 
		intent.putExtra("ch.appquest.taskname", "Grössen Messer");
		CharSequence calculatedObjectHeight = txtHeight.getText();
		intent.putExtra("ch.appquest.logmessage", qrCode + ": " + calculatedObjectHeight);
	 
		startActivity(intent);
	}
}
