package nl.circulairtriangles.scanner.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import nl.circulairtriangles.scanner.R;
import nl.circulairtriangles.scanner.security.Login;

import org.json.JSONObject;

public class LoginActivity extends BaseActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initLayout(R.string.login_header, false, false, false, false);
	}

	public void login(View view) {
		EditText username = (EditText) findViewById(R.id.login_username);
		EditText password = (EditText) findViewById(R.id.login_password);
//		JSONObject jsonObject = Login.login(this, username.getText().toString(), password.getText().toString());
		JSONObject jsonObject = Login.login(this, "admin", "admin");
		
		setStuff(jsonObject, view);
		
	}

	private void setStuff(JSONObject jsonObject, View view) {
		try{
			jsonObject = processRequest(jsonObject);
			if(jsonObject != null) {
				login_token = jsonObject.getString("login_token");
				startIntentByButton(view);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}