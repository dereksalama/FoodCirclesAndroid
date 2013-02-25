package com.foodcircles.android.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.foodcircles.android.R;
import com.foodcircles.android.dao.Circle;
import com.foodcircles.android.dao.FacebookInfo;
import com.foodcircles.android.dao.User;
import com.foodcircles.android.util.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class CircleMembersFragment extends ListFragment {

	ArrayAdapter<User> mAdapter;
	Circle mCircle;
	FacebookInfo mFacebookInfo;
	ProgressBar mProgressBar;

	Long mCircleId;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCircleId = getArguments().getLong("circle_id");
		mFacebookInfo = FacebookInfo.get(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle state) {
		if (group == null) {
			return null;
		}

		View v = inflater.inflate(R.layout.fagment_circle_members, group, false);

		mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		mAdapter = new ArrayAdapter<User>(getActivity(), android.R.layout.simple_list_item_1);
		if (mCircle != null && mCircle.getUsers() != null) {
			mAdapter.addAll(mCircle.getUsers());
			mProgressBar.setVisibility(View.GONE);
		} else {
			new AsyncCircleMembers().execute(null, null, null);
		}
		setListAdapter(mAdapter);

		return super.onCreateView(inflater, group, state);
	}

	private class AsyncCircleMembers extends AsyncTask<Void, Void, Circle> {

		@Override
		protected Circle doInBackground(Void... params) {
			String servlet = getActivity().getString(R.string.servlet_get_circle_members);
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("user_id", mFacebookInfo.getID()));
			parameters.add(new BasicNameValuePair("token", mFacebookInfo.getToken()));
			parameters.add(new BasicNameValuePair("circle_id", mCircleId.toString()));
			try {
				String jString = HttpUtil.postForJson(servlet, parameters);
				if (jString.length() <= 0) {
					return null;
				}
				Gson gson = new Gson();
				JsonParser jParser = new JsonParser();
				JsonElement e = jParser.parse(jString);

				Circle c = gson.fromJson(e, Circle.class);
				return c;
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
		protected void onPostExecute(Circle result) {
			if (result!= null) {
				mAdapter.clear();
				mAdapter.addAll(result.getUsers());
				mCircle = result;
			}

		}
	}

}
