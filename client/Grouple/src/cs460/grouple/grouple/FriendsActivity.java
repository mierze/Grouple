package cs460.grouple.grouple;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
/**
 * 
 * @author Brett, Todd, Scott
 * FriendsActivity is the navigation menu of Friends, allowing user to view or add friends.
 */
public class FriendsActivity extends BaseActivity
{
	private User user; // the current user
	//TODO: grab all ui elements here
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);

		user = GLOBAL.getCurrentUser();
		initActionBar("Friends", true);
	}

	private void load()
	{
		fetchData();
		updateUI();

	}

	private void fetchData()
	{
		
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("user_data"));
		load();
	}

	@Override
	protected void onPause()
	{
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
		super.onPause();

	}

	// This listens for pings from the data service to let it know that there
	// are updates
	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// Extract data included in the Intent
			String type = intent.getStringExtra("message");
			// repopulate views
			updateUI();
		}
	};
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == R.id.action_friends)
		{
			return true;
			//do nothing, already there
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateUI()
	{
		// setting notifications for the current view
		Button currentFriendsButton = (Button) findViewById(R.id.currentFriendsButtonFA);
		currentFriendsButton.setText("My Friends (" + user.getNumUsers() + ")");
		Button friendRequestsButton = (Button) findViewById(R.id.friendRequestsButtonFA);
		friendRequestsButton.setText("Friend Requests ("
				+ user.getNumFriendRequests() + ")");
	}

	/*
	 * Start activity functions for friends sub activities, going back and
	 * logging out
	 */
	public void startFriendAddActivity(View view)
	{
		loadDialog.show();
		Intent intent = new Intent(this, FriendAddActivity.class);
		intent.putExtra("email", user.getEmail());
		startActivity(intent);
	}

	public void startFriendsCurrentActivity(View view)
	{
		loadDialog.show();
		final String CONTENT = "FRIENDS_CURRENT";
		Intent intent = new Intent(this, UserListActivity.class);
		intent.putExtra("email", user.getEmail());
		intent.putExtra("content", CONTENT);
		startActivity(intent);
	}

	public void startFriendRequestsActivity(View view)
	{
		loadDialog.show();
		final String CONTENT = "FRIEND_REQUESTS";
		Intent intent = new Intent(this, UserListActivity.class);
		intent.putExtra("content", CONTENT);
		intent.putExtra("email", user.getEmail());
		startActivity(intent);
	}
}
