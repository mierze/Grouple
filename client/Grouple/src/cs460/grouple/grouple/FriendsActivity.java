package cs460.grouple.grouple;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/*
 * FriendsActivity displays displays the friend navigation page for the user.
 */
public class FriendsActivity extends BaseActivity
{
	private User user; // the current user

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		user.fetchEventInvites();
		user.fetchFriendRequests();
		user.fetchGroupInvites();
		GLOBAL.setCurrentUser(user);
	}

	public void load()
	{
		user = GLOBAL.getCurrentUser();
		setNotifications();
		initActionBar("Friends", true);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		load();
	}

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

	private void setNotifications()
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
		Intent intent = new Intent(this, ListActivity.class);
		intent.putExtra("email", user.getEmail());
		intent.putExtra("content", CONTENT);
		startActivity(intent);
	}

	public void startFriendRequestsActivity(View view)
	{
		loadDialog.show();
		final String CONTENT = "FRIEND_REQUESTS";
		Intent intent = new Intent(this, ListActivity.class);
		intent.putExtra("content", CONTENT);
		intent.putExtra("email", user.getEmail());
		startActivity(intent);
	}
}
