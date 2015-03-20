package cs460.grouple.grouple;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * FriendsActivity displays displays the friend navigation page for the user.
 */
public class FriendsActivity extends ActionBarActivity
{
	private BroadcastReceiver broadcastReceiver;
	private User user; //the current user
	private Global GLOBAL;// = 
	private Dialog loadDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		
		load();

		initKillswitchListener();
	}

	protected void onStop() 
	{ 
		super.onStop();
		 
		loadDialog.hide();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		loadDialog.show();
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	loadDialog.show();
	    	user.fetchEventsInvites();
	    	user.fetchFriendRequests();
	    	user.fetchGroupInvites();
	    	GLOBAL.setCurrentUser(user);
	    	finish();
	    }
	    return true;
	   }
	
	public void initActionBar()
	{
		// Actionbar settings
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText("Friends");
		//ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);	
	}

	public void load()
	{
		GLOBAL = ((Global) getApplicationContext());
		
		//grabbing extras from intent
		Intent intent = getIntent();
		Bundle extras = intent.getExtras(); 
		
		//grabbing the user with the given email in the extras
	
		user = GLOBAL.getCurrentUser();////loadUser(extras.getString("email"));

		loadDialog = GLOBAL.getLoadDialog(new Dialog(this));
        loadDialog.setOwnerActivity(this);
		setNotifications();
		initActionBar();
		initKillswitchListener();
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	@Override
	public void onResume()
	{
		super.onResume(); // Always call the superclass method first
		System.out.println("In Friends onResume()");
		load();

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
			intent.putExtra("EMAIL", user.getEmail());
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	
	private void setNotifications()
	{
		//setting notifications for the current view
		Button currentFriendsButton = (Button) findViewById(R.id.currentFriendsButtonFA);
		currentFriendsButton.setText("My Friends (" + user.getNumUsers() + ")");
		Button friendRequestsButton = (Button) findViewById(R.id.friendRequestsButtonFA);
		friendRequestsButton.setText("Friend Requests (" + user.getNumFriendRequests() + ")"); 
	}
	/*
	 * Start activity functions for friends sub activities, going back and
	 * logging out
	 */
	public void startFriendAddActivity(View view)
	{
		loadDialog.show();
		Intent intent = new Intent(this, FriendAddActivity.class);
		intent.putExtra("EMAIL", user.getEmail());
		startActivity(intent);
	}

	public void startFriendsCurrentActivity(View view)
	{
		loadDialog.show();
		final String CONTENT = "FRIENDS_CURRENT";
		Intent intent = new Intent(this, ListActivity.class);
		intent.putExtra("EMAIL", user.getEmail());
		intent.putExtra("CONTENT", CONTENT);
		startActivity(intent);
	}

	public void startFriendRequestsActivity(View view)
	{
		loadDialog.show();
		final String CONTENT = "FRIENDS_REQUESTS";
		Intent intent = new Intent(this, ListActivity.class);
		intent.putExtra("CONTENT", CONTENT);
		intent.putExtra("EMAIL", user.getEmail());
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
