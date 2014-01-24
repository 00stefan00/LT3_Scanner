package nl.circulairtriangles.scanner;

import nl.circulairtriangles.controller.LoginActivityController;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends BaseActivity
{
	/** The ID for recognizing a login event. */
	protected static final String LOGIN_EVENT_ID = "loginEvent";
	
	/** Stores the user information during the request. */
	//protected User user;
	
	/** Elements. */
	protected EditText userNameEditText;
	protected EditText passwordEditText;

	protected Button loginButtonButton;
	
	protected ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		loginButtonButton = (Button) findViewById(R.id.loginButtonButton);
		LoginActivityController l = new LoginActivityController(this);
		loginButtonButton.setOnClickListener(l);
	}
}
