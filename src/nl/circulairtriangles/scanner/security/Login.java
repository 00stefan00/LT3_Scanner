package nl.circulairtriangles.scanner.security;

import android.content.Context;
import nl.circulairtriangles.scanner.config.Config;
import nl.circulairtriangles.scanner.network.RESTRequest;
import nl.circulairtriangles.scanner.utils.JSONParser;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class Login {

	public static JSONObject login(Context context, String username, String password) {
		RESTRequest restRequest = new RESTRequest(Config.API_URL);

		restRequest.putString(Config.KEY_NAME, Config.USER_NAME);
		restRequest.putString(Config.KEY_METHOD, "login");
		restRequest.putString("username", username);
		restRequest.putString("password", password);

		JSONObject jsonObject = null;
		try {
			Login.createCredentialsFile(context, username, password);
			JSONParser jsonParser = JSONParser.getInstance();
			String response = restRequest.execute().get();
			jsonObject = jsonParser.getObjectFromRequest(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	public static void createCredentialsFile(Context context, String username, String password) throws Exception {
		File file = new File(context.getFilesDir(), Config.LOGIN_CREDENTIAL_FILE);
		new FileOutputStream(file, false).close();
		String write = Cryptor.encrypt(username + ";" + password);
		FileOutputStream outputStream = context.openFileOutput(Config.LOGIN_CREDENTIAL_FILE, Context.MODE_PRIVATE);
		outputStream.write(write.getBytes());
		outputStream.close();
	}
}
