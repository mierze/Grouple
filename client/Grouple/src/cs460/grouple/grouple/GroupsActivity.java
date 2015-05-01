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
 * GroupsActivity is the main menu for the Groups navigation, allowing the user to create a group or view groups.
 */
public class GroupsActivity extends BaseActivity
{
	private User user;
	private Button groupInvitesButton;
	private Button groupsCurrentButton;
	//TODO: grab all ui elements here

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups);
		user = GLOBAL.getCurrentUser();// loadUser(global.getCurrentUser().getEmail());
		initActionBar("Groups", true);
		groupInvitesButton = (Button) findViewById(R.id.groupInvitesButton);
		groupsCurrentButton = (Button) findViewById(R.id.groupsCurrentButton);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, new IntentFilter("user_data"));
		fetchData();
		updateUI();
	}

	@Override
	protected void onPause()
	{
		LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver);
		super.onPause();
	}

	// This listens for pings from the data service to let it know that there
	// are updates
	private BroadcastReceiver dataReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// repopulate views
			updateUI();
		}
	};

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
	}


	private void fetchData()
	{
		user.fetchGroupInvites(this);
		user.fetchGroups(this);
	}

	private void updateUI()
	{
		// Groups activity
		groupInvitesButton.setText("Group Invites (" + user.getNumGroupInvites()
					+ ")");
		groupsCurrentButton.setText("My Groups (" + user.getNumGroups() + ")");
		
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
	// TODO : MAKE THESE ONCLICK
	public void startGroupCreateActivity(View view)
	{
		loadDialog.show();
		Intent intent = new Intent(this, GroupCreateActivity.class);
		intent.putExtra("email", user.getEmail());
		startActivity(intent);
	}

	public void startGroupInvitesActivity(View view)
	{
		loadDialog.show();
		final String CONTENT = "GROUP_INVITES";
		Intent intent = new Intent(this, GroupListActivity.class);
		intent.putExtra("email", user.getEmail());
		intent.putExtra("content", CONTENT);
		startActivity(intent);
	}

	public void startGroupsCurrentActivity(View view)
	{
		loadDialog.show();
		final String CONTENT = "GROUPS_CURRENT";
		Intent intent = new Intent(this, GroupListActivity.class);
		intent.putExtra("content", CONTENT);
		intent.putExtra("email", user.getEmail());// specifies which
		startActivity(intent);
	}
}
