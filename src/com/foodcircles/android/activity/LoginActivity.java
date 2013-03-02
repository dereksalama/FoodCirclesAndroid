package com.foodcircles.android.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.foodcircles.android.R;
import com.foodcircles.android.dao.FacebookInfo;
import com.foodcircles.android.dao.Me;
import com.foodcircles.android.dao.User;
import com.foodcircles.android.util.HttpUtil;
import com.foodcircles.android.util.TokenUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class LoginActivity extends Activity {

	EditText mPassField;
	FacebookInfo mFacebookInfo;
	TextView mMessageText;
	Button mLogin;
	ProgressBar mProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		boolean newAccount = getIntent().getBooleanExtra("new", true);

		mProgress = (ProgressBar) findViewById(R.id.login_loading);
		mProgress.setVisibility(View.GONE);

		mFacebookInfo = FacebookInfo.get(this);
		String name = mFacebookInfo.getName();
		mMessageText = (TextView) findViewById(R.id.login_message);


		if (newAccount) {
			mMessageText.setText("Create a FC account:");
		} else {
			mMessageText.setText("Hello, " + name);
		}

		mPassField = (EditText) findViewById(R.id.password);

		mLogin = (Button) findViewById(R.id.login_button);

		mLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String pass = mPassField.getText().toString();
				String token = TokenUtil.hash(pass);
				mFacebookInfo.setToken(token);
				mProgress.setVisibility(View.VISIBLE);
				new AsyncLogin().execute(null, null, null);
			}
		});


	}

	private void openCircles() {
		Intent i = new Intent(this, CirclesActivity.class);
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}
	private class AsyncLogin extends AsyncTask<Void,Void,User> {

		@Override
		protected User doInBackground(Void... user) {
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("user_id", mFacebookInfo.getID()));
			parameters.add(new BasicNameValuePair("name", mFacebookInfo.getName()));
			parameters.add(new BasicNameValuePair("token", mFacebookInfo.getToken()));

			String servlet = LoginActivity.this.getString(R.string.servlet_create_user);

			try {
				String jString = HttpUtil.postForJson(servlet, parameters);
				if (jString.length() <= 0) {
					return null;
				}
				Gson gson = new Gson();
				JsonParser jParser = new JsonParser();
				JsonElement e = jParser.parse(jString);

				User u = gson.fromJson(e, User.class);
				return u;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(User result) {
			if (result != null) {
				Log.d("LoginActivity", "FC login success");
				Me.set(result);
				openCircles();
			} else {
				Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
			}
		}

	}

}
