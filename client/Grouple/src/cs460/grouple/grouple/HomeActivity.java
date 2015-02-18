package cs460.grouple.grouple;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/*
 * HomeActivity displays the primary navigation and welcome screen after logging in.
 */
public class HomeActivity extends ActionBarActivity
{
	User user; //current user
	BroadcastReceiver broadcastReceiver;
	static Global GLOBAL;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		load();
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	//do nothing
	    }
	    return true;
	   }
	
	private void load()
	{
		GLOBAL = ((Global) getApplicationContext());
		
		//grabbing the user with the given email in the extras
		user = GLOBAL.getCurrentUser();
		
		//set notifications
		setNotifications();

		//initializing action bar and killswitch listener
		initActionBar();
		initKillswitchListener();
	}
	
	private void initActionBar()
	{
		// Actionbar settings
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		// ab.setDisplayHomeAsUpEnabled(true);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText("Welcome, " + user.getFirstName() + "!"); //PANDA
	}

	public void setNotifications()
	{
		// Home Activity
		int numFriendRequests = user.getNumFriendRequests();
		if (numFriendRequests > 0)
		{
			if (numFriendRequests == 1)
			{
				((Button) findViewById(R.id.friendsButtonHA)).setText("Friends \n(" + numFriendRequests
								+ " request)");
			} 
			else
			{
				((Button) findViewById(R.id.friendsButtonHA)).setText("Friends \n(" + numFriendRequests
								+ " requests)");
			}
		} 
		else if (numFriendRequests == 0)
		{
			((Button) findViewById(R.id.friendsButtonHA)).setText("Friends");
		}
		int numGroupInvites = user.getNumGroupInvites();
		if (numGroupInvites > 0)
		{
			if (numGroupInvites == 1)
			{
				((Button) findViewById(R.id.groupsButtonHA)).setText("Groups \n(" + numGroupInvites
								+ " invite)");
			} 
			else
			{
				((Button) findViewById(R.id.groupsButtonHA)).setText("Groups \n(" + numGroupInvites
								+ " invites)");
			}
		} 
		else if (numGroupInvites == 0)
		{
			((Button) findViewById(R.id.groupsButtonHA)).setText("Groups");
		}
		
		int numEventsInvites = user.getNumEventsInvites();
		if (numEventsInvites > 0)
		{
			if (numEventsInvites == 1)
				((Button) findViewById(R.id.eventsButtonHA)).setText("Events \n(" + numEventsInvites + " invite)");
			else
				((Button) findViewById(R.id.eventsButtonHA)).setText("Events \n(" + numEventsInvites + " invites)");
		} 
		else if (numEventsInvites == 0)
		{
			((Button) findViewById(R.id.eventsButtonHA)).setText("Events");
		}
	}
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	// This method closes the app when back button is pressed on main page
	@Override
	public void onBackPressed()
	{
		Log.d("backPress", "Back was pressed on home screen.");
		finish();
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
			GLOBAL.destroySession();
			Intent login = new Intent(this, LoginActivity.class);
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

	@Override
	public void onResume()
	{
		super.onResume(); // Always call the superclass method first
		Log.d("onResume()","after superonresume");
		
		load();
		//GLOBAL.setNotifications(home); PANDA
	}


	public void navigate(View view)
	{
		//originally setting intent to null
		Intent intent = null;

		switch (view.getId())
		{
		case R.id.friendsButtonHA:
			intent = new Intent(this, FriendsActivity.class);
			user.fetchFriendRequests();
			user.fetchFriends();
			break;
		case R.id.settingsButtonHA:
			intent = new Intent(this, SettingsActivity.class);
			break;
		case R.id.eventsButtonHA:
			user.fetchEventsInvites();
			user.fetchEventsPending();
			user.fetchEventsUpcoming();
			user.fetchEventsPast();
			intent = new Intent(this, EventsActivity.class);
			break;
		case R.id.messagesButtonHA:
			intent = new Intent(this, MessagesActivity.class);
			break;
		case R.id.groupsButtonHA:
			user.fetchGroupInvites();
			user.fetchGroups();
			intent = new Intent(this, GroupsActivity.class);
			break;
		case R.id.userButtonHA:
			user.fetchUserInfo();
			user.fetchEventsUpcoming();
			user.fetchFriends();
			user.fetchGroups();
			intent = new Intent(this, ProfileActivity.class);
			intent.putExtra("CONTENT", "USER");
			break;
		default: //default just break out
			break;
		}
		
		GLOBAL.setCurrentUser(user);//update
		
		//checking that intent was assigned
		if (intent != null)
		{
			intent.putExtra("EMAIL", user.getEmail());
			startActivity(intent);
		}
		//else do nothing
	}

	/*
	 * Initializing the killswitch listener for shutting down
	 */
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
