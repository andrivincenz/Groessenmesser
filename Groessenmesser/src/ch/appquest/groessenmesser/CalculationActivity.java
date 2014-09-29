package ch.appquest.groessenmesser;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CalculationActivity extends Activity {
	private double alpha;
	private double beta;
	private EditText txtAlpha;
	private EditText txtBeta;
	private EditText txtDistance;
	private EditText txtHeight;
	private Button btnCalculate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calculation);
		
		// winkel von anderem acitiy auslesen
		double cornerOne = getIntent().getDoubleExtra("alpha", 0);
		double cornerTwo = getIntent().getDoubleExtra("beta", 0);
		
		if (cornerOne >= cornerTwo) { 
			alpha = cornerTwo;
			beta = cornerOne - cornerTwo;
		} else {
			alpha = cornerOne;
			beta = cornerTwo - cornerOne;
		}
		
//probiers nomel. handy istecke nocher de chäfer! drucke
		
		//
		
		// init components
		txtAlpha = (EditText)findViewById(R.id.txtAlpha); // soory mach jetzt halt chli schnell
		txtBeta = (EditText)findViewById(R.id.txtBeta);
		txtDistance = (EditText)findViewById(R.id.txtDistance);
		txtHeight = (EditText)findViewById(R.id.txtHeight);
		btnCalculate = (Button)findViewById(R.id.btnCalculate);
		
		txtAlpha.setText(alpha + "°");
		txtBeta.setText(beta + "°");
		
		btnCalculate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				double distance = Double.parseDouble(txtDistance.getText().toString());
				if (distance != 0) {
					txtHeight.setText(calcHeight(alpha, beta, distance) + " m");
				}
			} // selber mol probiere. wa? i will mol da mit mim natel probiere moment ok
		});
	}
	
	private double calcHeight(double alpha, double beta, double distance) {
		double height = 0;
		
		beta = Math.toRadians(beta - (90 - alpha));
		alpha = Math.toRadians(90 - alpha);

		height = (Math.sin(alpha)) * (distance / Math.sin(alpha)) + (Math.tan(beta * distance));
		
		return Math.round(height * 100f) / 100f;
	}
}
