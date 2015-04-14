package cs460.grouple.grouple;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/*
 * GroupsActivity displays displays the group navigation page for the user.
 */
public class GroupsActivity extends BaseActivity
{
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		load();
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
		user = GLOBAL.getCurrentUser();//loadUser(global.getCurrentUser().getEmail());
		setNotifications();
		initActionBar("Groups", true);
	}

	private void setNotifications()
	{
		// Groups activity
		if (findViewById(R.id.pendingGroupsButton) != null)
		{
			System.out.println("Pending groups setting text to what it is");
			((Button) findViewById(R.id.pendingGroupsButton))
					.setText("Group Invites (" + user.getNumGroupInvites() + ")");
		}
		if (findViewById(R.id.yourGroupsButton) != null)
		{
			((Button) findViewById(R.id.yourGroupsButton))
					.setText("My Groups (" + user.getNumGroups() + ")");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_groups)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/* Start activity methods for group sub-activities */
	//TODO : MAKE THESE ONCLICK
	public void startGroupCreateActivity(View view)
	{
		loadDialog.show();
		user.fetchFriends();
		GLOBAL.setCurrentUser(user);
		Intent intent = new Intent(this, GroupCreateActivity.class);
		intent.putExtra("email", user.getEmail());
		startActivity(intent);
	}

	public void startGroupInvitesActivity(View view)
	{
		loadDialog.show();
		final String CONTENT = "GROUPS_INVITES";
		user.fetchGroupInvites();
		GLOBAL.setCurrentUser(user);//update
		Intent intent = new Intent(this, ListActivity.class);
		intent.putExtra("email", user.getEmail());
		intent.putExtra("content", CONTENT);
		startActivity(intent);
	}

	public void startGroupsCurrentActivity(View view)
	{
		loadDialog.show();
		final String CONTENT = "GROUPS_CURRENT";
		user.fetchGroups();
		GLOBAL.setCurrentUser(user);//update
		Intent intent = new Intent(this, ListActivity.class);
		intent.putExtra("content", CONTENT);
		intent.putExtra("email", user.getEmail());// specifies which
		startActivity(intent);
	}
}
