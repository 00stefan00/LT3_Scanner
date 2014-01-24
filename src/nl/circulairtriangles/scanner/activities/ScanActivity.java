package nl.circulairtriangles.scanner.activities;

import nl.circulairtriangles.scanner.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ScanActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		initLayout(R.string.title_activity_scanner, true, false, true, false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
