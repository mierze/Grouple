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
public class ProfileActivity extends ActionBarActivity
{
	private ImageView iv;
	BroadcastReceiver broadcastReceiver;
	User user; //user who's profile this is
	Group group;
	Event event;
	
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

		setContentView(R.layout.activity_profile);
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
	
	public void initActionBar(String title)
	{
		
		/* Action bar */
		ActionBar ab = getSupportActionBar();
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		//ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);

		actionbarTitle.setText(title);
	}

	public void load()
	{
		Global global = ((Global) getApplicationContext());
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String title = "";

		if (extras.getString("content").equals("group"))
		{
			group = global.getGroupBuffer();
			Button profileButton2 = (Button)findViewById(R.id.profileButton2);
			Button profileButton3 = (Button)findViewById(R.id.profileButton3);
			profileButton2.setVisibility(View.GONE);
			profileButton3.setVisibility(View.GONE);
			title = group.getName();
		}
		else if (extras.getString("content").equals("user"))
		{
			//grabbing the user with the given email in the extras
			if (!global.isCurrentUser(extras.getString("email")))
			{
				if (global.getUserBuffer() != null)
					user = global.getUserBuffer();
				else
					user = global.loadUser(extras.getString("email"));
				//hiding edit profile button
				Button editProfileButton = (Button)findViewById(R.id.profileEditButton);
				editProfileButton.setVisibility(View.GONE);
			}
			else if (global.isCurrentUser(extras.getString("email")))
			{	
				//preloaded
				user = global.getCurrentUser();
			}
	
			title = user.getFirstName() + "'s Profile";
			
		}
		else if (extras.getString("content").equals("event"))
		{
			event = global.getEventBuffer();
			if (event == null)
			{
				System.out.println("OPUR EVEMNT IS NUL!");
			}
			else
				System.out.println("NTNULL WE ARE GOOD");
			Button profileButton2 = (Button)findViewById(R.id.profileButton2);
			Button profileButton3 = (Button)findViewById(R.id.profileButton3);
			profileButton2.setVisibility(View.GONE);
			profileButton3.setVisibility(View.GONE);
			System.out.println("EVENT NAME: " + event.getName());
			title = event.getName();
		}

		setNotifications();
		populateProfile(); //populates a group / user profile
		
		// initializing the action bar and killswitch listener
		initActionBar(title);
		initKillswitchListener();
	}

	private void setNotifications()
	{
		Bundle extras = getIntent().getExtras();
		
		if (extras.getString("content").equals("group"))
		{
			((Button) findViewById(R.id.profileButton1)).setText("Members\n(" + group.getNumUsers() + ")");
		}
		else if (extras.getString("content").equals("user"))
		{
			((Button) findViewById(R.id.profileButton1)).setText("Friends\n(" + user.getNumUsers() + ")");
			((Button) findViewById(R.id.profileButton2)).setText("Groups\n(" + user.getNumGroups() + ")");	
			((Button) findViewById(R.id.profileButton3)).setText("Events\n(" + user.getNumEventsUpcoming() + ")");	
		}
		else
		{
			//for event later
			((Button) findViewById(R.id.profileButton1)).setText("Attending (" + event.getNumUsers() + ")");
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
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	public void onClick(View view)
	{
		Global global = (Global) getApplicationContext();
		Bundle extras = getIntent().getExtras();
		Intent intent = new Intent(this, ListActivity.class);
		switch (view.getId())
		{
		case R.id.profileButton1:
			if (extras.getString("content").equals("group"))
			{
				//members
				intent.putExtra("content", "groupMembers");
				intent.putExtra("gid", group.getID());
			}
			else if (extras.getString("content").equals("user"))
			{
				//friends
				intent.putExtra("content", "friendsCurrent");
				intent.putExtra("mod", "true");
				int success = user.fetchFriends();
				if (success == 1)
					if (global.isCurrentUser(user.getEmail()))
						global.setCurrentUser(user);
					else
						global.setUserBuffer(user);
			}
			else
			{
				//events
			}
			break;
		case R.id.profileButton2:
			//groups
			intent.putExtra("content", "groupsCurrent");
			int success = user.fetchGroups();
			if (success == 1)
				if (global.isCurrentUser(user.getEmail()))
					global.setCurrentUser(user);
				else
					global.setUserBuffer(user);
			break;
		case R.id.profileButton3:
			//events
			intent = new Intent(this, EventsActivity.class);
			break;
		case R.id.profileEditButton:
			if (extras.getString("content").equals("group"))
			{
				intent = new Intent(this, GroupEditActivity.class);
				intent.putExtra("gid", group.getID());
			}
			else if (extras.getString("content").equals("user"))
			{
				intent = new Intent(this, ProfileEditActivity.class);
			}
			else
			{
				
			}
			break;
		default:
				break;
		}
		intent.putExtra("email", extras.getString("email"));
		iv = null;
		startActivity(intent);
	}

	/*
	 * Get profile executes get_profile.php. It uses the current users email
	 * address to retrieve the users name, age, and bio.
	 */
	public void populateProfile()
	{
		Bundle extras = getIntent().getExtras();
		if (iv == null)
		{
			iv = (ImageView) findViewById(R.id.profileImageUPA);
		}
		
		
		TextView info = (TextView) findViewById(R.id.profileInfoTextView);
		TextView about = (TextView) findViewById(R.id.profileAboutTextView);
		if (extras.getString("content").equals("group"))
		{
			iv.setImageBitmap(group.getImage());
			info.setText("Extra group info");
			about.setText(group.getAbout());
		}
		else if (extras.getString("content").equals("user"))
		{
			iv.setImageBitmap(user.getImage());
			info.setText(user.getAge() + "yrs old" +
					"\n" + user.getLocation());
			about.setText(user.getAbout());
		}
		else
		{
			about.setText(event.getAbout());
			info.setText(event.getCategory() + "\n" +
					event.getLocation());
		}		
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
