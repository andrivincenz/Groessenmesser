package ch.appquest.groessenmesser;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    
	    Button kamerabutton = (Button) findViewById(R.id.cmdStartKamera);
	    kamerabutton.setOnClickListener((android.view.View.OnClickListener) this);
	
	    // TODO Auto-generated method stub
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, KameraActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}



}
