package com.foodcircles.android.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;

import com.foodcircles.android.dao.FacebookInfo;
import com.foodcircles.android.dao.Me;
import com.foodcircles.android.dao.User;

public class UpdateStatusTask extends AsyncTask<Void, Void, Boolean> {

	private static final String SERVLET = "updatestatus";

	Context c;

	public UpdateStatusTask(Context c) {
		super();
		this.c = c;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		User me = Me.get();
		if ( me == null) {
			throw new IllegalStateException("User not inited!!!");
		}

		List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
		parameters.add(new BasicNameValuePair("user_id", me.userID));
		parameters.add(new BasicNameValuePair("action", "status_loc_time"));
		parameters.add(new BasicNameValuePair("status", Integer.toString(me.status)));
		if (me.desiredLocation != null) {
			parameters.add(new BasicNameValuePair("location", me.desiredLocation));
		}

		if (me.desiredTime != null) {
			parameters.add(new BasicNameValuePair("time", me.desiredTime));
		}
		parameters.add(new BasicNameValuePair("token", FacebookInfo.get(c).getToken()));
		try {
			return HttpUtil.postNoResponse(SERVLET, parameters);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

}
