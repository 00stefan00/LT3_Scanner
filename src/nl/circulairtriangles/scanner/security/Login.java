package nl.circulairtriangles.scanner.security;

import android.content.Context;
import nl.circulairtriangles.scanner.config.Config;
import nl.circulairtriangles.scanner.network.RESTRequest;
import nl.circulairtriangles.scanner.sqlite.SettingsDatabaseHandler;
import nl.circulairtriangles.scanner.utils.JSONParser;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

/**
 * User: johan
 * Date: 11/12/13
 * Time: 12:28 PM
 */
public class Login {

	public static void createPinEntry(Context context, int pin, String username, String password) {
		SettingsDatabaseHandler handler = new SettingsDatabaseHandler(context);
		handler.executeQuery("INSERT INTO users VALUES ('" + username + "','" + password + "','" + pin + "');");
	}

	public static JSONObject loginUsingPin(Context context, String pin) {
		SettingsDatabaseHandler handler = new SettingsDatabaseHandler(context);
		List<HashMap<String, String>> credentials = handler.findOneByPin(pin);
		if(credentials ==null) return null;
		HashMap<String, String> data = credentials.get(0);
		return login(context, data.get("username"), data.get("password"));
	}

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
