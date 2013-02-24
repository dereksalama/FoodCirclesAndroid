package com.foodcircles.android.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.foodcircles.android.R;
import com.foodcircles.android.dao.Circle;
import com.foodcircles.android.dao.FacebookInfo;
import com.foodcircles.android.util.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class CirclesActivity extends ListActivity {

	ProgressBar mProgressBar;
	List<Circle> mCircles;
	FacebookInfo mFacebookInfo;
	ArrayAdapter<Circle> mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_circles);
		mFacebookInfo = FacebookInfo.get(this);
		mProgressBar = (ProgressBar) findViewById(R.id.circles_loading);
		mAdapter = new ArrayAdapter<Circle>(this, android.R.layout.simple_list_item_1);
		setListAdapter(mAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mCircles == null) {
			mProgressBar.setVisibility(View.VISIBLE);
			new AsyncCircles().execute(null, null, null);
		} else {
			mAdapter.addAll(mCircles);
			mProgressBar.setVisibility(View.GONE);
		}
	}

	//TODO: add refresh button?
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_circles, menu);
		return true;
	}

	private class AsyncCircles extends AsyncTask<Void, Void, List<Circle>> {

		@Override
		protected List<Circle> doInBackground(Void... params) {
			String servlet = CirclesActivity.this.getString(R.string.servlet_get_circles);
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("user_id", mFacebookInfo.getID()));
			parameters.add(new BasicNameValuePair("token", mFacebookInfo.getToken()));
			try {
				String jString = HttpUtil.postForJson(servlet, parameters);
				if (jString.length() <= 0) {
					return null;
				}
				Gson gson = new Gson();
				JsonParser jParser = new JsonParser();
				JsonArray jArray = jParser.parse(jString).getAsJsonArray();

				List<Circle> circles = new ArrayList<Circle>();
				for (JsonElement e : jArray) {
					Circle circle = gson.fromJson(e, Circle.class);
					circles.add(circle);
				}

				return circles;
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
		protected void onPostExecute(List<Circle> result) {
			if (result == null) {
				Toast.makeText(CirclesActivity.this, "Error fetching circles", Toast.LENGTH_SHORT).show();
			} else {
				mCircles = result;
				mAdapter.addAll(mCircles);
			}
			mProgressBar.setVisibility(View.GONE);
		}

	}

}
