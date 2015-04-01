package cs460.grouple.grouple;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/*
 * GroupsActivity displays displays the group navigation page for the user.
 */
public class GroupsActivity extends ActionBarActivity
{
	private BroadcastReceiver broadcastReceiver;
	private User user;
	private Dialog loadDialog = null;
	private Global GLOBAL;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups);
		initKillswitchListener();
		load();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		setContentView(R.layout.activity_groups);
		load();
	}
	
	public void initActionBar()
	{
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText("Groups");
	}

	@Override
	public void onBackPressed() 
	{
    	user.fetchEventsInvites();
    	user.fetchFriendRequests();
    	user.fetchGroupInvites();
    	GLOBAL.setCurrentUser(user);
    	finish(); //preventing back-loop
	    return;
	}
	
	public void load()
	{
		GLOBAL = ((Global) getApplicationContext());
	
		user = GLOBAL.getCurrentUser();//loadUser(global.getCurrentUser().getEmail());
		loadDialog = GLOBAL.getLoadDialog(new Dialog(this));
        loadDialog.setOwnerActivity(this);
		setNotifications();
		initActionBar();
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
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_logout)
		{
			Intent login = new Intent(this, LoginActivity.class);
			GLOBAL.destroySession();
			startActivity(login);
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		if (id == R.id.action_home)
		{
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	protected void onStop() 
	{ 
		super.onStop();
		 
		loadDialog.hide();
	}
	
	/* Start activity methods for group sub-activities */
	//TODO : MAKE THESE ONCLICK
	public void startGroupCreateActivity(View view)
	{
		loadDialog.show();
		user.fetchFriends();
		GLOBAL.setCurrentUser(user);
		Intent intent = new Intent(this, GroupCreateActivity.class);
		intent.putExtra("EMAIL", user.getEmail());
		startActivity(intent);
	}

	public void startGroupInvitesActivity(View view)
	{
		loadDialog.show();
		final String CONTENT = "GROUPS_INVITES";
		user.fetchGroupInvites();
		GLOBAL.setCurrentUser(user);//update
		Intent intent = new Intent(this, ListActivity.class);
		intent.putExtra("EMAIL", user.getEmail());
		intent.putExtra("CONTENT", CONTENT);
		startActivity(intent);
	}

	public void startGroupsCurrentActivity(View view)
	{
		loadDialog.show();
		final String CONTENT = "GROUPS_CURRENT";
		user.fetchGroups();
		GLOBAL.setCurrentUser(user);//update
		Intent intent = new Intent(this, ListActivity.class);
		intent.putExtra("CONTENT", CONTENT);
		intent.putExtra("EMAIL", user.getEmail());// specifies which
		startActivity(intent);
	}

	public void initKillswitchListener()
	{
		// START KILL SWITCH LISTENER
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("CLOSE_ALL");
		broadcastReceiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				// close activity
				if (intent.getAction().equals("CLOSE_ALL"))
				{
					Log.d("app666", "we killin the login it");
					// System.exit(1);
					finish();
				}
			}
		};
		registerReceiver(broadcastReceiver, intentFilter);
		// End Kill switch listener
	}
}
