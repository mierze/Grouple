package cs460.grouple.grouple;

import cs460.grouple.grouple.R;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * UserActivity displays the profile page of any user
 */
public class UserProfileActivity extends ActionBarActivity
{
	private ImageView iv;
	BroadcastReceiver broadcastReceiver;
	User user; //user who's profile this is
	
	@Override
	protected void onStart()
	{
		super.onStart();
		setNotifications();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_user_profile);
		//Can we do our user load in the profile to save loading time from earlier or will the sync be off?
		load();

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		setNotifications();
		populateProfile();
	}
	public void initActionBar()
	{

		/* Action bar */
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		//ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);

		actionbarTitle.setText(user.getFullName() + "'s Profile");
	}

	public void load()
	{
		Global global = ((Global) getApplicationContext());
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();

		//grabbing the user with the given email in the extras
		if (!global.isCurrentUser(extras.getString("email")))
		{
			if (global.getUserBuffer() != null)
				user = global.getUserBuffer();
			else
				user = global.loadUser(extras.getString("email"));
			//hiding edit profile button
			Button editProfileButton = (Button)findViewById(R.id.editProfileButton);
			editProfileButton.setVisibility(View.GONE);
		}
		else if (global.isCurrentUser(extras.getString("email")))
		{	
			//preloaded
			user = global.getCurrentUser();
		}


		// the textviews
		populateProfile();

		// initializing the action bar and killswitch listener
		initActionBar();
		initKillswitchListener();
	}

	private void setNotifications()
	{
		((Button) findViewById(R.id.friendsButtonUPA)).setText("Friends\n(" + user.getNumFriends() + ")");
		((Button) findViewById(R.id.groupsButtonUPA)).setText("Groups\n(" + user.getNumGroups() + ")");	
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

		// Set up the image view
		if (iv == null)
		{
			iv = (ImageView) findViewById(R.id.profileImageGPA);
		}
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
			startActivity(login);
			iv = null;
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		if (id == R.id.action_home)
		{
			Intent intent = new Intent(this, HomeActivity.class);
			intent.putExtra("ParentClassName", "UserActivity");
			intent.putExtra("up", "false");
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}


	public void startEditProfileActivity(View view)
	{
		Intent intent = new Intent(this, ProfileEditActivity.class);
		intent.putExtra("email", user.getEmail());
		startActivity(intent);
		iv = null;
	}

	/*
	 * Get profile executes get_profile.php. It uses the current users email
	 * address to retrieve the users name, age, and bio.
	 */
	public void populateProfile()
	{
		if (iv == null)
		{
			iv = (ImageView) findViewById(R.id.profileImageUPA);
		}
		iv.setImageBitmap(user.getImage());

		TextView ageTextView = (TextView) findViewById(R.id.ageTextView);
		TextView locationTextView = (TextView) findViewById(R.id.locationTextView);
		TextView bioTextView = (TextView) findViewById(R.id.bioTextView);
		ageTextView.setText(user.getAge() + "yrs old");
		bioTextView.setText(user.getBio());
		locationTextView.setText(user.getLocation());	
	}

	public void startGroupsCurrentActivity(View view)
	{
		Global global = ((Global) getApplicationContext());
		Intent intent = new Intent(this, ListActivity.class);
		intent.putExtra("ParentClassName", "UserActivity");
		intent.putExtra("content", "groupsCurrent");
		
		intent.putExtra("email", user.getEmail());
		
		int success = user.fetchGroups();
		if (success == 1)
			if (global.isCurrentUser(user.getEmail()))
				global.setCurrentUser(user);
			else
				global.setUserBuffer(user);
		
		startActivity(intent);
		iv = null;
	}

	public void startFriendsCurrentActivity(View view)
	{
		Global global = ((Global) getApplicationContext());
		Intent intent = new Intent(this, ListActivity.class);
		intent.putExtra("content", "friendsCurrent");
		intent.putExtra("email", user.getEmail());
		intent.putExtra("mod", "true");
		int success = user.fetchFriends();
		if (success == 1)
			if (global.isCurrentUser(user.getEmail()))
				global.setCurrentUser(user);
			else
				global.setUserBuffer(user);
		
		startActivity(intent);
		iv = null;
	}

	public void startEventsActivity(View view)
	{
		Intent intent = new Intent(this, EventsActivity.class);
		intent.putExtra("ParentClassName", "UserActivity");
		startActivity(intent);
		iv = null;
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
