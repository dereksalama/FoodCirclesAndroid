package com.foodcircles.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.foodcircles.android.R;
import com.foodcircles.android.dao.FacebookInfo;
import com.foodcircles.android.util.TokenUtil;

public class SplashActivity extends Activity {

	ProgressBar mProgressBar;
	FacebookInfo mInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		mProgressBar.setVisibility(View.INVISIBLE);
		mInfo = FacebookInfo.get(this);
		String userID = mInfo.getID();
		if (userID == null) {
			if (mInfo.getToken() != null ) {
				Intent i = new Intent(this, CirclesActivity.class);
				startActivity(i);
			}
			mProgressBar.setActivated(true);
			mProgressBar.setVisibility(View.VISIBLE);
			Session.openActiveSession(SplashActivity.this, true, new Session.StatusCallback() {

				@Override
				public void call(Session session, SessionState state, Exception exception) {
					if (session.isOpened()) {
						mInfo.setToken(TokenUtil.hash(session.getAccessToken()));
						Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

							@Override
							public void onCompleted(GraphUser user, Response response) {
								if (user != null) {
									Log.i("SplashActivity", "User: " + user.getName() + " logged in to facebook");
									mInfo.setID(user.getId());
									mInfo.setName(user.getName());
									launchLogin(true);
									//TODO: check if they already have an account!
								}
							}
						});
					}
				}
			});
		} else {
			launchLogin(false);
		}
	}

	private void launchLogin(boolean isNew) {
			Intent i = new Intent(this, LoginActivity.class);
			i.putExtra("new", isNew);
			startActivity(i);
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
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

}
