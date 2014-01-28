package nl.circulairtriangles.scanner.activities;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import nl.circulairtriangles.scanner.R;
import nl.circulairtriangles.scanner.config.Config;
import nl.circulairtriangles.scanner.network.RESTRequest;
import nl.circulairtriangles.scanner.utils.JSONParser;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

@SuppressLint("UseSparseArrays")
public class SettingsActivity extends BaseActivity {

	EditText change_pin1, change_pin2;
	RadioGroup languageOptions;
	ToggleButton toggleButton1;
	int currentScaleChoice = -1;
	int currentLanguageChoice = -1;
	final static int ENGLISH = 0;
	final static int DUTCH = 1;
	final static int MEDIUMID = 1;

	String[] languages = { "en", "nl" };
	int[] fontScaleOptions = { R.string.small, R.string.medium, R.string.big };
	double[] fontScaleValues = { 0.7, 1, 1.3 };
	HashMap<Integer, Double> fontScaleMap = new HashMap<Integer, Double>();
	String[] fontSpinnerArray = new String[3];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		initLayout(R.string.title_activity_settings, true, true, true, true);
		buttonOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				applySettings(v);
			}
		});

		updateSpinner();
		languageOptions = (RadioGroup) findViewById(R.id.languagePicker);
		change_pin1 = (EditText) findViewById(R.id.change_pin1);
		change_pin2 = (EditText) findViewById(R.id.change_pin2);
		toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);

		toggleButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				processToggle(isChecked);
			}
		});

		if(!super_user){
			toggleButton1.setVisibility(View.INVISIBLE);
		}
		initLanguagePicker();
	}

	/**
	 * Initializes the language picker
	 */
	private void initLanguagePicker() {
		languageOptions = (RadioGroup) findViewById(R.id.languagePicker);
		for (int i = 0; i < languages.length; i++) {
			RadioButton button;
			button = new RadioButton(this);
			button.setText(languages[i]);
			button.setId(i);
			languageOptions.addView(button);
		}

	}

	/**
	 * Applies the given settings
	 * 
	 * @param view
	 */
	public void applySettings(View view) {
		Configuration config = new Configuration();
		Spinner s = (Spinner) findViewById(R.id.font_scale);
		String scale_choice = s.getSelectedItem().toString();
		int choice = s.getSelectedItemPosition();

		if (scale_choice != null) {
			changeScale(config, choice, fontScaleMap.get(choice));
		}

		RadioGroup group = (RadioGroup) findViewById(R.id.languagePicker);
		currentLanguageChoice = group.getCheckedRadioButtonId();

		if (currentLanguageChoice != -1) {
			changeLanguage(config, languages[currentLanguageChoice]);
		}
		updateContent(config);
	}

	protected void processToggle(boolean isChecked) {
		request(isChecked);

	}

	protected JSONObject request(Boolean bool) {
		RESTRequest restRequest = new RESTRequest(Config.API_URL);

		restRequest.putString(Config.KEY_NAME, Config.USER_NAME);
		restRequest.putString(Config.KEY_METHOD, "setSettingTemp");
		restRequest.putString("login_token", login_token);
		restRequest.putString("temp", bool.toString());
		Log.i("DEBUG", ""+restRequest);

		JSONObject jsonObject = null;
		try {
			JSONParser jsonParser = JSONParser.getInstance();
			String response = restRequest.execute().get();
			Log.i("DEBUG", response);
			jsonObject = jsonParser.getObjectFromRequest(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	/**
	 * Changes the language
	 * 
	 * @param config
	 * @param language
	 */
	public void changeLanguage(Configuration config, String language) {
		String languageToLoad = language;
		Locale locale = new Locale(languageToLoad);
		Locale.setDefault(locale);
		config.locale = locale;
		request(locale.toString());
	}

	protected JSONObject request(String language) {
		RESTRequest restRequest = new RESTRequest(Config.API_URL);

		restRequest.putString(Config.KEY_NAME, Config.USER_NAME);
		restRequest.putString(Config.KEY_METHOD, "getBasicNeeds");
		restRequest.putString("login_token", login_token);
		restRequest.putString("language", language);

		JSONObject jsonObject = null;
		try {
			JSONParser jsonParser = JSONParser.getInstance();
			String response = restRequest.execute().get();
			Log.i("DEBUG", response);
			jsonObject = jsonParser.getObjectFromRequest(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	/**
	 * Changes the font size
	 * 
	 * @param config
	 * @param choice
	 * @param scale
	 */
	public void changeScale(Configuration config, int choice, double scale) {
		Log.d("Default font scale", "" + config.fontScale);
		config.fontScale = (float) scale;
		currentScaleChoice = choice;
	}

	/**
	 * Updates the content
	 * 
	 * @param config
	 */
	public void updateContent(Configuration config) {
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
		this.setContentView(R.layout.activity_settings);
		initLayout(R.string.title_activity_settings, true, true, true, true);
		buttonOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				applySettings(v);
			}
		});
		updateSpinner();
		initLanguagePicker();
	}

	/**
	 * Updates the spinner
	 */
	public void updateSpinner() {
		for (int i = 0; i < fontScaleOptions.length; i++) {
			fontSpinnerArray[i] = getResources().getString(fontScaleOptions[i]);
			fontScaleMap.put(i, fontScaleValues[i]);
		}

		Spinner s = (Spinner) findViewById(R.id.font_scale);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item, fontSpinnerArray);
		s.setAdapter(adapter);

		if (currentScaleChoice == -1) {
			s.setSelection(MEDIUMID);
		} else {
			s.setSelection(currentScaleChoice);
		}
	}

	protected void goBack() {
		setContentView(R.layout.activity_main);
	}

}
