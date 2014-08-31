package com.github.elrimo.indoorlns;

import java.io.IOException;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.savagelook.android.UrlJsonAsyncTask;
public class RegisterActivity extends ActionBarActivity {
	
	private final static String REGISTER_API_ENDPOINT_URL = "http://192.168.1.4:81/localisation/rest/addUser";
	private SharedPreferences mPreferences;
	private String email;
	private String nom;
	private String prenom;
	private String password;
	private String passwordConfirmation;
	private String role="user";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
	}

	public void registerNewAccount(View button) {
		EditText userEmailField = (EditText) findViewById(R.id.userEmail);
		email = userEmailField.getText().toString();
		EditText userNameField = (EditText) findViewById(R.id.userNom);
		nom = userNameField.getText().toString();
		EditText userPrenomField = (EditText) findViewById(R.id.userPrenom);
		prenom = userPrenomField.getText().toString();
		EditText userPasswordField = (EditText) findViewById(R.id.userPassword);
		password = userPasswordField.getText().toString();
		EditText userPasswordConfirmationField = (EditText) findViewById(R.id.userPasswordConfirmation);
		passwordConfirmation = userPasswordConfirmationField.getText().toString();

		// Reset errors.
		userEmailField.setError(null);
		userNameField.setError(null);

		userPrenomField.setError(null);
		userPasswordField.setError(null);
		userPasswordConfirmationField.setError(null);
		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(nom)) {
			userNameField.setError("This field is required");
			focusView = userNameField;
			cancel = true;
		} 
		
		if (TextUtils.isEmpty(prenom)) {
			userPrenomField.setError("This field is required");
			focusView = userPrenomField;
			cancel = true;
		} 
		
		
		// Check for a valid password.
				if (TextUtils.isEmpty(password)) {
					userPasswordField.setError(getString(R.string.error_field_required));
					focusView = userPasswordField;
					cancel = true;
				} else if (password.length() < 4) {
					userPasswordField.setError(getString(R.string.error_invalid_password));
					focusView = userPasswordField;
					cancel = true;
				}else {
					if (! password.equals( passwordConfirmation)) {
						// password doesn't match confirmation
						Toast.makeText(
								this,
								"Your password doesn't match confirmation, check again",
								Toast.LENGTH_LONG).show();
						userPasswordConfirmationField.setError("Your password doesn't match confirmation, check again");
						focusView = userPasswordConfirmationField;
						cancel = true;
						return;
					}

		// Check for a valid email address.
				if (TextUtils.isEmpty(email)) {
					userEmailField.setError(getString(R.string.error_field_required));
					focusView = userEmailField;
					cancel = true;
				} else if (!email.contains("@")) {
					userEmailField.setError(getString(R.string.error_invalid_email));
					focusView = userEmailField;
					cancel = true;
				} 

				
		
		
				if (cancel) {
					// There was an error; don't attempt login and focus the first
					// form field with an error.
					focusView.requestFocus();
				} else{
				// everything is ok!
				RegisterTask registerTask = new RegisterTask(RegisterActivity.this);
				registerTask.setMessageLoading("Registering new account...");
				registerTask.execute(REGISTER_API_ENDPOINT_URL);
			}
		}
	}

	private class RegisterTask extends UrlJsonAsyncTask {
		public RegisterTask(Context context) {
			super(context);
		}

		@Override
		protected JSONObject doInBackground(String... urls) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(urls[0]);
			//JSONObject holder = new JSONObject();
			JSONObject userObj = new JSONObject();
			String response = null;
			JSONObject json = new JSONObject();

			try {
				try {
					json.put("success", false);
					json.put("info", "Something went wrong. Retry!");
					userObj.put("email",  email);
					userObj.put("nom",  nom);
					userObj.put("prenom",  prenom);
					userObj.put("password",  password);
					userObj.put("role", role);
					//holder.put("user", userObj);
					StringEntity se = new StringEntity(userObj.toString());
					post.setEntity(se);
					post.setHeader("Accept", "application/json");
					post.setHeader("Content-Type", "application/json");

					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					response = client.execute(post, responseHandler);
					json = new JSONObject(response);

					
					
				} catch (HttpResponseException e) {
					e.printStackTrace();
					Log.e("ClientProtocol", "" + e);
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("IO", "" + e);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("JSON", "" + e);
			}

			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				if (json.getBoolean("success")) {
					SharedPreferences.Editor editor = mPreferences.edit();
					editor.putString("AuthToken", json.getString("data"));
					editor.commit();
					Intent intent = new Intent(RegisterActivity.this,
							LoginActivity.class);
					startActivityForResult(intent, 0);
									}
				Toast.makeText(context, json.getString("info"),
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
						.show();
			} finally {
				super.onPostExecute(json);
			}
		}
	}

	
}
