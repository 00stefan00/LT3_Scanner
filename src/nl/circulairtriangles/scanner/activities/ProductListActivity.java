package nl.circulairtriangles.scanner.activities;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nl.circulairtriangles.scanner.R;
import nl.circulairtriangles.scanner.config.Config;
import nl.circulairtriangles.scanner.network.RESTRequest;
import nl.circulairtriangles.scanner.utils.JSONParser;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProductListActivity extends BaseActivity {

	Map<String, LinearLayout> map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		initLayout(R.string.title_activity_main, true, false, true, false);

		map = new HashMap<String, LinearLayout>();
		Bundle b = getIntent().getExtras();
		String jsonstring = b.getString("jsonstring");
		JSONParser parser = JSONParser.getInstance();
		try {
			JSONArray json = parser.getArrayFromRequest(jsonstring);
			createList(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void addProduct(String quantity, String product_name) {
		LinearLayout ver_layout = ((LinearLayout) findViewById(R.id.shopping_list));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		LinearLayout hor_layout = new LinearLayout(this);
		hor_layout.setOrientation(LinearLayout.HORIZONTAL);
		hor_layout.setLayoutParams(params);

		TextView tv = new TextView(this);
		tv.setText(quantity);

		hor_layout.addView(tv);

		tv = new TextView(this);
		tv.setText(" x  :" + product_name);

		hor_layout.addView(tv);

		map.put(product_name, hor_layout);

		ver_layout.addView(hor_layout, params);
	}

	private void createList(JSONArray json) {

		for (int i = 0; i < json.length(); i++) {
			try {

				JSONObject j_obj = json.getJSONObject(i);
				String quantity = j_obj.getString("quantity");
				String product_name = j_obj.getString("product_name");
				
				addProduct(quantity, product_name);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	public void Scan(View v) {
		Intent intent = new Intent(getApplicationContext(), ScanActivity.class);

		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		processScan();
	}

	private void processScan() {
		RESTRequest restRequest = new RESTRequest(Config.API_URL);
		restRequest.putString(Config.KEY_NAME, Config.USER_NAME);
		restRequest.putString(Config.KEY_METHOD, "getProductByCode");
		restRequest.putString("login_token", login_token);
		restRequest.putString("barcode", scancode);
		try {
			String response = restRequest.execute().get();
			JSONParser parser = JSONParser.getInstance();
			JSONObject json = (JSONObject) parser
					.getObjectFromRequest(response);

			if (map.containsKey(json.getString("name"))) {
				LinearLayout layout = map.get(json.getString("name"));

				TextView tv = (TextView) layout.getChildAt(0);
				String text = ""
						+ (Integer.parseInt(tv.getText().toString()) - 1);
				Log.i("DEBUG", text);
				tv.setText(text);
			} else {
				addProduct("-1", json.getString("name"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
