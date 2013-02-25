package com.foodcircles.android.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.support.v4.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.foodcircles.android.R;
import com.foodcircles.android.dao.FacebookInfo;
import com.foodcircles.android.dao.GroupChatMessage;
import com.foodcircles.android.dao.Me;
import com.foodcircles.android.util.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

//TODO: push!
//TODO: disable for all friends
public class CircleChatFragment extends ListFragment {
	ArrayAdapter<GroupChatMessage> mAdapter;
	List<GroupChatMessage> mMessages;
	FacebookInfo mFacebookInfo;
	ProgressBar mProgressBar;

	Button mSendButton;
	EditText mTextField;

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

		View v = inflater.inflate(R.layout.fragment_circle_chat, group, false);

		mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		mAdapter = new ArrayAdapter<GroupChatMessage>(getActivity(), android.R.layout.simple_list_item_1);

		mSendButton = (Button) v.findViewById(R.id.button_send);
		mSendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AsyncSendMessage().execute(null, null, null);
			}
		});
		mTextField = (EditText) v.findViewById(R.id.edit_text_send_message);
		if (mMessages != null) {
			mAdapter.addAll(mMessages);
			mProgressBar.setVisibility(View.GONE);
		} else {
			new AsyncCircleChat().execute(null, null, null);
		}
		setListAdapter(mAdapter);

		return v;
	}

	private class AsyncCircleChat extends AsyncTask<Void, Void, List<GroupChatMessage>> {

		@Override
		protected List<GroupChatMessage> doInBackground(Void... params) {
			String servlet = getActivity().getString(R.string.servlet_get_circle_chat);
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("circle_id", mCircleId.toString()));
			try {
				String jString = HttpUtil.postForJson(servlet, parameters);
				if (jString.length() <= 0) {
					return null;
				}
				Gson gson = new Gson();
				JsonParser jParser = new JsonParser();
				JsonArray jArray = jParser.parse(jString).getAsJsonArray();

				List<GroupChatMessage> messages = new ArrayList<GroupChatMessage>(jArray.size());
				for (JsonElement e : jArray) {
					GroupChatMessage m = gson.fromJson(e, GroupChatMessage.class);
					messages.add(m);
				}

				return messages;
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
		protected void onPostExecute(List<GroupChatMessage> result) {
			if (result!= null) {
				mAdapter.clear();
				mAdapter.addAll(result);
				mMessages = result;
			}
			mProgressBar.setVisibility(View.GONE);
		}
	}

	private class AsyncSendMessage extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			String servlet = getActivity().getString(R.string.servlet_send_circle_message);
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("circle_id", mCircleId.toString()));
			parameters.add(new BasicNameValuePair("user_id", mFacebookInfo.getID()));
			parameters.add(new BasicNameValuePair("token", mFacebookInfo.getToken()));
			parameters.add(new BasicNameValuePair("time", Long.toString(System.currentTimeMillis())));
			parameters.add(new BasicNameValuePair("text", mTextField.getText().toString()));

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
				Log.d("CircleChatFragment", "message sent successfully");
				GroupChatMessage newMessage = new GroupChatMessage(mTextField.getText().toString(),
						new Date(), Me.get(getActivity()), mCircleId);
				mMessages.add(newMessage);
				mAdapter.clear();
				mAdapter.addAll(mMessages);
			} else {
				Toast.makeText(getActivity(), "Failed to send",Toast.LENGTH_SHORT).show();
			}
		}

	}

}
