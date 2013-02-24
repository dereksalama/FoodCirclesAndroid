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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.foodcircles.android.R;
import com.foodcircles.android.dao.FacebookInfo;
import com.foodcircles.android.util.HttpUtil;
import com.foodcircles.android.util.TokenUtil;

public class LoginActivity extends Activity {

	ProgressBar mProgressBar;
	String userID;
	FacebookInfo mInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		mProgressBar.setVisibility(View.INVISIBLE);
		Button login = (Button) findViewById(R.id.login_button);
		mInfo = FacebookInfo.get(this);
		userID = mInfo.getID();
		//TODO: update if different?
		if (userID == null) {
			login.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mProgressBar.setActivated(true);
					mProgressBar.setVisibility(View.VISIBLE);
					Session.openActiveSession(LoginActivity.this, true, new Session.StatusCallback() {

						@Override
						public void call(Session session, SessionState state, Exception exception) {
							if (session.isOpened()) {
								mInfo.setToken(TokenUtil.hash(session.getAccessToken()));
								Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

									@Override
									public void onCompleted(GraphUser user, Response response) {
										if (user != null) {
											Log.i("LoginActivity", "User: " + user.getName() + " logged in to facebook");
											mInfo.setID(user.getId());
											new AsyncLogin().execute(user);
										}
									}
								});
							}
						}
					});
				}
			});
		} else {
			openCircles();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  //TODO: cache that token!
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	private void openCircles() {
		Intent i = new Intent(this, CirclesActivity.class);
		startActivity(i);
	}

	private class AsyncLogin extends AsyncTask<GraphUser,Void,Boolean> {

		@Override
		protected Boolean doInBackground(GraphUser... user) {
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("user_id", user[0].getId()));
			parameters.add(new BasicNameValuePair("name", user[0].getName()));
			parameters.add(new BasicNameValuePair("token", TokenUtil.hash(mInfo.getToken())));

			String servlet = LoginActivity.this.getString(R.string.servlet_create_user);
			try {
				return HttpUtil.postNoResponse(servlet, parameters);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Log.d("LoginActivity", "FC login success");
				openCircles();
			} else {
				Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
			}
		}

	}

}
