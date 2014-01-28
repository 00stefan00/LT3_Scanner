package nl.circulairtriangles.scanner.activities;

import nl.circulairtriangles.scanner.R;
import nl.circulairtriangles.scanner.utils.IntentIntegrator;
import nl.circulairtriangles.scanner.utils.IntentResult;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
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
		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_LONG;
		if (scanResult != null) {

			CharSequence text = scanResult.getContents();

			scancode = text.toString();
		} else {
			Toast.makeText(context, context.getString(R.string.scan_error),
					duration).show();
		}
		this.finish();
	}
}
