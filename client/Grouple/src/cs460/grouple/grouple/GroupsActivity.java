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

/*
 * GroupsActivity displays displays the group navigation page for the user.
 */
public class GroupsActivity extends BaseActivity
{
	private User user;
	//TODO: grab all ui elements here

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups);
		user = GLOBAL.getCurrentUser();// loadUser(global.getCurrentUser().getEmail());
		initActionBar("Groups", true);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("user_data"));
		fetchData();
		updateUI();
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
		if (findViewById(R.id.pendingGroupsButton) != null)
		{
			System.out.println("Pending groups setting text to what it is");
			((Button) findViewById(R.id.pendingGroupsButton)).setText("Group Invites (" + user.getNumGroupInvites()
					+ ")");
		}
		if (findViewById(R.id.yourGroupsButton) != null)
		{
			((Button) findViewById(R.id.yourGroupsButton)).setText("My Groups (" + user.getNumGroups() + ")");
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
