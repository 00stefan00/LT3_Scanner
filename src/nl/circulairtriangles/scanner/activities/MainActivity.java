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
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


@SuppressLint("UseSparseArrays")
public class MainActivity extends BaseActivity {
	
	Map<String, JSONArray> map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initLayout(R.string.title_activity_main, true, false, true, false);
		Button btn = (Button) findViewById(R.id.getProducts);

		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				getProductLists(v);
			}
		});
		
		map = new HashMap<String, JSONArray>();
	}

	public void getProductLists(View v) {
		RESTRequest restRequest = new RESTRequest(Config.API_URL);
		restRequest.putString(Config.KEY_NAME, Config.USER_NAME);
		restRequest.putString(Config.KEY_METHOD, "getShoppingLists");
		restRequest.putString("login_token", login_token);
		try {
			String response = restRequest.execute().get();
			JSONParser parser = JSONParser.getInstance();
			JSONObject json = (JSONObject) parser
					.getObjectFromRequest(response).get("shopping_lists");

			for (int i = 0; i < json.names().length(); i++) {
				JSONArray list = (JSONArray) json.get(json.names().get(i)
						.toString());
				createButtonForList(json.names().get(i).toString(), list, i);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void createButtonForList(String name, JSONArray list, int i) {
		LinearLayout layout = ((LinearLayout) findViewById(R.id.listLayout));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
	            LinearLayout.LayoutParams.MATCH_PARENT,
	            LinearLayout.LayoutParams.WRAP_CONTENT);
		Button btn = new Button(this);
		btn.setId(i);
		try {
			btn.setText(list.getJSONObject(0).getString("list_name"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		layout.addView(btn, params);
		
		map.put(btn.getText().toString(), list);
		
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Button b = (Button) view;
				Intent i = new Intent(MainActivity.this, ProductListActivity.class);
				
				Bundle bundle = new Bundle();
				bundle.putString("jsonstring", map.get(b.getText()).toString());
				i.putExtras(bundle);
				
				startActivity(i);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
