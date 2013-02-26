package com.foodcircles.android.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.Session;
import com.foodcircles.android.R;
import com.foodcircles.android.dao.FacebookInfo;
import com.foodcircles.android.dao.User;
import com.foodcircles.android.util.HttpUtil;

public class CircleDetail extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	Long mCircleId;

	private FacebookInfo mFacebookInfo;

	private CircleMembersFragment mMembersFragment;
	private CircleChatFragment mChatFragment;
	private static final int REAUTH_ACTIVITY_CODE = 100;
	private static final int PICK_FRIENDS = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_circle_detail);

		mFacebookInfo = FacebookInfo.get(this);
		mCircleId = getIntent().getLongExtra("circle_id", 0);
		mMembersFragment = new CircleMembersFragment();
		Bundle args = new Bundle();
		args.putLong("circle_id", mCircleId);
		mMembersFragment.setArguments(args);

		mChatFragment = new CircleChatFragment();
		mChatFragment.setArguments(args);


		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Show the Up button in the action bar.
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getIntent().getStringExtra("circle_name"));

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

        if (Session.getActiveSession() == null ||
                Session.getActiveSession().isClosed()) {
            Session.openActiveSession(this, true, null);
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_circle_detail, menu);
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
			mMembersFragment.refresh();
			return true;
		case R.id.menu_invite:
			startPickerActivity();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return mMembersFragment;
			case 1:
				return mChatFragment;
			default:
				throw new IllegalStateException("no fragment");
			}
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_circle_members).toUpperCase();
			case 1:
				return getString(R.string.title_circle_messages).toUpperCase();
			}
			return null;
		}
	}

	private void startPickerActivity() {
	     Intent intent = new Intent();
	     if (mMembersFragment.getCircleMembers() != null) {
	    	 //TODO: check if this works
	    	 Bundle args = new Bundle();
	    	 StringBuilder csv = new StringBuilder();
	    	 for (User u : mMembersFragment.getCircleMembers()) {
	    		 csv.append(u.userID);
	    		 csv.append(',');
	    	 }
	    	 args.putString("com.facebook.android.PickerFragment.Selection", csv.toString());
	    	 intent.putExtra("users", args);
	     }
	     intent.setData(PickerActivity.FRIEND_PICKER);
	     intent.setClass(this, PickerActivity.class);
	     startActivityForResult(intent, PICK_FRIENDS);
	 }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == REAUTH_ACTIVITY_CODE) {
	    	Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	    } else if (resultCode == Activity.RESULT_OK && requestCode == PICK_FRIENDS) {
    		ArrayList<String> selectedUserIds = data.getStringArrayListExtra("selected");
	    	if (selectedUserIds != null && selectedUserIds.size() > 0) {
	    		String[] userArray = new String[selectedUserIds.size()];
	    		userArray = selectedUserIds.toArray(userArray);
	    		new AsyncSendInvites().execute(userArray);
	    	}
	    }

	}

	private class AsyncSendInvites extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			String servlet = CircleDetail.this.getString(R.string.servlet_send_invites);
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("sender_id", mFacebookInfo.getID()));
			parameters.add(new BasicNameValuePair("token", mFacebookInfo.getToken()));
			parameters.add(new BasicNameValuePair("circle_id", Long.toString(mCircleId)));
			for (int i = 0; i < params.length; i++) {
				parameters.add(new BasicNameValuePair("receiver_id", params[i]));
			}

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
		    	Toast.makeText(CircleDetail.this, "Invites sent!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(CircleDetail.this, "Failed to send invites", Toast.LENGTH_LONG).show();
			}

		}

	}

}
