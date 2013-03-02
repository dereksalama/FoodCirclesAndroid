package com.foodcircles.android.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.foodcircles.android.R;
import com.foodcircles.android.activity.NewCircleDialog.NewCircleListener;
import com.foodcircles.android.dao.Circle;
import com.foodcircles.android.dao.FacebookInfo;
import com.foodcircles.android.dao.Me;
import com.foodcircles.android.util.HttpUtil;
import com.foodcircles.android.util.UpdateStatusTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class CirclesActivity extends ListActivity implements NewCircleListener {

	ProgressBar mProgressBar;
	List<Circle> mCircles;
	FacebookInfo mFacebookInfo;
	ArrayAdapter<Circle> mAdapter;
	StatusButtonListener mButtonListener;
	LinearLayout mButtonList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_circles);
		mFacebookInfo = FacebookInfo.get(this);
		mProgressBar = (ProgressBar) findViewById(R.id.circles_loading);
		mAdapter = new ArrayAdapter<Circle>(this, android.R.layout.simple_list_item_1);

		mButtonListener = new StatusButtonListener();
		mButtonList = (LinearLayout) findViewById(R.id.circles_status_selector);
		for (int i = 0; i < mButtonList.getChildCount(); i++) {
			mButtonList.getChildAt(i).setOnClickListener(mButtonListener);
		}
		setListAdapter(mAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Circle c = mAdapter.getItem(position);
		Intent i = new Intent(this, CircleDetail.class);
		i.putExtra("circle_id", c.id);
		i.putExtra("circle_name", c.name);

		startActivity(i);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_view_invites:
			Intent i = new Intent(this, InvitesActivity.class);
			startActivity(i);
			return true;
		case R.id.menu_new_circle:
			NewCircleDialog frag = NewCircleDialog.get(this);
			frag.show(getFragmentManager(), "dialog");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mCircles == null) {
			mProgressBar.setVisibility(View.VISIBLE);
			new AsyncCircles().execute(null, null, null);
		} else {
			mAdapter.clear();
			mAdapter.addAll(mCircles);
			mProgressBar.setVisibility(View.GONE);
		}
		int status = new Me(this).getStatus();
		setSelected(status);
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
				mAdapter.clear();
				mAdapter.addAll(mCircles);
			}
			mProgressBar.setVisibility(View.GONE);
		}

	}

	@Override
	public void onOkClick(String name) {
		if (name != null && name.length() > 0) {
			new AsyncNewCircle(name).execute(null, null, null);
			mProgressBar.setVisibility(View.VISIBLE);
		}
	}


	private class AsyncNewCircle extends AsyncTask<Void, Void, List<Circle>> {

		String newCircle;
		public AsyncNewCircle(String name) {
			super();
			newCircle = name;
		}

		@Override
		protected List<Circle> doInBackground(Void... params) {
			String servlet = CirclesActivity.this.getString(R.string.servlet_make_circle);
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("user_id", mFacebookInfo.getID()));
			parameters.add(new BasicNameValuePair("token", mFacebookInfo.getToken()));
			parameters.add(new BasicNameValuePair("circle_name", newCircle));
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
				Toast.makeText(CirclesActivity.this, "Error creating circle", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(CirclesActivity.this, "New circle:" + newCircle, Toast.LENGTH_SHORT).show();
				mCircles = result;
				mAdapter.clear();
				mAdapter.addAll(mCircles);
			}
			mProgressBar.setVisibility(View.GONE);
		}

	}

	private void setSelected(int selected) {
		for (int i = 0; i < mButtonList.getChildCount(); i++) {
			View child = mButtonList.getChildAt(i);
			child.setSelected(selected == i);
		}
	}


	//TODO: enum this shit
	private class StatusButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Me me = new Me(CirclesActivity.this);
			int status = 2;
			switch(v.getId()) {
			case R.id.status_green:
				status = 0;
				break;
			case R.id.status_yellow:
				status = 1;
				break;
			case R.id.status_red:
				status = 2;
				break;
			case R.id.status_other:
				status = 3;
				break;
			}

			me.setStatus(status);
			setSelected(status);

			new UpdateStatusTask(CirclesActivity.this).execute(null, null, null);

		}



	}


}
