package nl.circulairtriangles.scanner.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import nl.circulairtriangles.scanner.R;
import nl.circulairtriangles.scanner.animations.CollapseAnimation;
import nl.circulairtriangles.scanner.animations.ExpandAnimation;
import nl.circulairtriangles.scanner.config.Config;
import nl.circulairtriangles.scanner.security.Cryptor;

import org.json.JSONObject;

import java.io.*;


public abstract class BaseActivity extends Activity {

	protected TextView txtHeading;
	protected Button buttonBack;
	private Button buttonMenu;
	protected Button buttonOk;
	protected Button buttonHome;

	private LinearLayout MenuList;
	private int screenHeight;
	private boolean isExpanded = false;

	protected static final String activityPackage = "nl.circulairtriangles.scanner.activities";

	protected static String login_token = "";
	protected static Boolean super_user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		isLoggedIn();
		setContentView(R.layout.footer);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		isLoggedIn();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
//		isLoggedIn();
	}

	protected void isLoggedIn() {
		if(login_token.equals("") && !(this instanceof LoginActivity)) {
			Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * Creates the menu
	 * 
	 * @param resId
	 * @param homeButton
	 * @param backButton
	 * @param menuButton
	 * @param okButton
	 */
	protected void initLayout(int resId, boolean homeButton,
			boolean backButton, boolean menuButton, boolean okButton) {

		if (txtHeading == null)
			txtHeading = (TextView) findViewById(R.id.header_text);
		if (txtHeading != null)
			txtHeading.setText(resId);

		buttonHome = (Button) findViewById(R.id.header_button_home);
		buttonBack = (Button) findViewById(R.id.footer_button_back);
		buttonMenu = (Button) findViewById(R.id.footer_button_menu);
		buttonOk = (Button) findViewById(R.id.footer_button_ok);
		MenuList = (LinearLayout) findViewById(R.id.linearLayout3);

		MenuList.bringToFront();

		buttonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goBack();
			}
		});

		buttonMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				handleMenu();
			}
		});

		buttonHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BaseActivity.this,
						MainActivity.class);
				startActivityForResult(intent, 1);
				finish();
			}
		});

		this.buttonHome.setVisibility(homeButton ? View.VISIBLE
				: View.INVISIBLE);
		this.buttonBack.setVisibility(backButton ? View.VISIBLE
				: View.INVISIBLE);
		this.buttonMenu.setVisibility(menuButton ? View.VISIBLE
				: View.INVISIBLE);
		this.buttonOk.setVisibility(okButton ? View.VISIBLE : View.INVISIBLE);
	}

	/**
	 * Simulates the go back method on a phone
	 */
	protected void goBack() {
		super.onBackPressed();
	}

	/**
	 * Handles the quick menu. Here the extend and collapse animation classes
	 * are used to animate the menu.
	 * 
	 */
	private void handleMenu() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenHeight = metrics.heightPixels;

		Drawable arrowUp = getBaseContext().getResources().getDrawable(
				R.drawable.arrow_up);
		Drawable arrowDown = getBaseContext().getResources().getDrawable(
				R.drawable.arrow_down);

		if (isExpanded) {
			buttonMenu.setText(getResources().getString(
					R.string.footer_button_menu_open));
			isExpanded = false;
			buttonMenu.setCompoundDrawablesWithIntrinsicBounds(null, null,
					arrowUp, null);
			MenuList.startAnimation(new CollapseAnimation(MenuList, 0,
					(int) (screenHeight * 0.20), 10));
		} else {
			buttonMenu.setText(getResources().getString(
					R.string.footer_button_menu_close));
			isExpanded = true;
			buttonMenu.setCompoundDrawablesWithIntrinsicBounds(null, null,
					arrowDown, null);
			MenuList.startAnimation(new ExpandAnimation(MenuList, 0,
					(int) (screenHeight * 0.20), 10));
		}

	}

	/**
	 * 
	 * @param view
	 */
	public void startIntentByButton(View view) {
		Button button = (Button) view;
		if (!button.getTag().equals("")) {
			try {
				Intent intent = new Intent(getApplicationContext(),
						Class.forName(BaseActivity.activityPackage + "."
								+ button.getTag().toString()));
				startActivityForResult(intent, 1);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			this.startActivity(intent);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

	protected JSONObject processRequest(JSONObject jsonObject) throws Exception {
		if(jsonObject == null) {
			Toast.makeText(getApplicationContext(), R.string.error_something_wrong, Toast.LENGTH_SHORT);
		}
		else if(jsonObject.get("error").toString().equals("true")) {
			Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT);
		} else {
			return jsonObject;
		}
		return null;
	}

	public String getUsername() {
		String username = "";
		try {
			String content = getFileContent();
			username = content.split(";")[0];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return username;
	}

	protected String getPassword() {
		String password = "";
		try {
			String content = getFileContent();
			password = content.split(";")[1];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return password;
	}

	protected String getFileContent() throws Exception {
		File file = new File(getFilesDir(), Config.LOGIN_CREDENTIAL_FILE);
		String content = "";
		if(file.exists()) {
			InputStream inputStream = new FileInputStream(file);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String receiveString;
			StringBuilder stringBuilder = new StringBuilder();
			while ((receiveString = bufferedReader.readLine()) != null) {
				stringBuilder.append(receiveString);
			}
			content = Cryptor.decrypt(stringBuilder.toString());
			bufferedReader.close();
			inputStream.close();
		}
		return content;
	}
}
