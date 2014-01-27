package nl.circulairtriangles.scanner.activities;

import nl.circulairtriangles.scanner.R;
import nl.circulairtriangles.scanner.utils.IntentIntegrator;
import nl.circulairtriangles.scanner.utils.IntentResult;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class ScanActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		initLayout(R.string.title_activity_scanner, true, false, true, false);
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		  IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		  if (scanResult != null) {
		    // handle scan result
			  Log.e("<test>", scanResult.toString());
			  Context context = getApplicationContext();
			  CharSequence text = scanResult.getContents();
			  int duration = Toast.LENGTH_LONG;

			  Toast toast = Toast.makeText(context, text, duration);
			  toast.show();
		  }
		  // else continue with any other code you need in the method
		}
}
