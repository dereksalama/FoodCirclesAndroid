package com.foodcircles.android.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.foodcircles.android.R;
import com.foodcircles.android.dao.CircleInvite;
import com.foodcircles.android.dao.FacebookInfo;
import com.foodcircles.android.util.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class InvitesActivity extends ListActivity {

	ArrayAdapter<CircleInvite> mAdapter;
	ProgressBar mProgressBar;
	FacebookInfo mFacebookInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invites);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("Circle Invites");

		mAdapter = new ArrayAdapter<CircleInvite>(this, android.R.layout.simple_list_item_1);
		setListAdapter(mAdapter);
		mProgressBar = (ProgressBar) findViewById(R.id.loading_invites);
		mFacebookInfo = FacebookInfo.get(this);

		refresh();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		CircleInvite i = mAdapter.getItem(position);
		//TODO: post
		mAdapter.remove(i);
		Toast.makeText(this, "You joined: " + i.getCircleName(), Toast.LENGTH_SHORT).show();
	}

	private void refresh() {
		new AsyncInvites().execute(null, null, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_invites, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.menu_refresh:
			refresh();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class AsyncInvites extends AsyncTask<Void, Void, List<CircleInvite>> {

		@Override
		protected List<CircleInvite> doInBackground(Void... params) {
			String servlet = InvitesActivity.this.getString(R.string.servlet_get_invites);
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("receiver_id", mFacebookInfo.getID()));
			parameters.add(new BasicNameValuePair("token", mFacebookInfo.getToken()));
			try {
				String jString = HttpUtil.postForJson(servlet, parameters);
				if (jString.length() <= 0) {
					return null;
				}
				Gson gson = new Gson();
				JsonParser jParser = new JsonParser();
				JsonArray jArray = jParser.parse(jString).getAsJsonArray();

				List<CircleInvite> invites = new ArrayList<CircleInvite>(jArray.size());
				for (JsonElement e : jArray) {
					CircleInvite i = gson.fromJson(e, CircleInvite.class);
					invites.add(i);
				}

				return invites;
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
		protected void onPostExecute(List<CircleInvite> result) {
			if (result == null) {
				Toast.makeText(InvitesActivity.this, "Error fetching invites", Toast.LENGTH_SHORT).show();
			} else {
				mAdapter.clear();
				mAdapter.addAll(result);
			}
			mProgressBar.setVisibility(View.GONE);
		}

	}

}
