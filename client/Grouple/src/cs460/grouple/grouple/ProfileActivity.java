package cs460.grouple.grouple;

import cs460.grouple.grouple.R;
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
	enum CONTENT_TYPE {
		USER, GROUP, EVENT
	}
	
	private ImageView iv;
	BroadcastReceiver broadcastReceiver;
	private User user; //user who's profile this is
	private Group group;
	private Event event;
	private Bundle EXTRAS;
	private String CONTENT; //type of content to display in profile, passed in from other activities
	private static Global GLOBAL;
	
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
		initKillswitchListener();
		setContentView(R.layout.activity_profile);
		//Can we do our user load in the profile to save loading time from earlier or will the sync be off?
		load();

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		load();
		/*
		if (user != null)
			if (GLOBAL.isCurrentUser(user.getEmail()))
				user = GLOBAL.getCurrentUser();                                                                                ,,
			else if (GLOBAL.getUserBuffer() != null)
				user = GLOBAL.getUserBuffer();
		else if (group != null && GLOBAL.getGroupBuffer() != null)
			group = GLOBAL.getGroupBuffer();
		else if (event != null)
			event = GLOBAL.getEventBuffer();
		*/
		//setNotifications();
		//populateProfile();
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
		GLOBAL = ((Global) getApplicationContext());
		EXTRAS = getIntent().getExtras();
		CONTENT = EXTRAS.getString("CONTENT");
		String title = "";

		if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
		{
			group = GLOBAL.getGroupBuffer();
			Button profileButton2 = (Button)findViewById(R.id.profileButton2);
			Button profileButton3 = (Button)findViewById(R.id.profileButton3);
			profileButton2.setVisibility(View.GONE);
			profileButton3.setVisibility(View.GONE);
			title = group.getName();
		}
		else if (CONTENT.equals(CONTENT_TYPE.USER.toString()))
		{
			//grabbing the user with the given email in the EXTRAS
			if (!GLOBAL.isCurrentUser(EXTRAS.getString("EMAIL")))
			{
				if (GLOBAL.getUserBuffer() != null)
					user = GLOBAL.getUserBuffer();
				//hiding edit profile button
				Button editProfileButton = (Button)findViewById(R.id.profileEditButton);
				editProfileButton.setVisibility(View.GONE);
			}
			else if (GLOBAL.isCurrentUser(EXTRAS.getString("EMAIL")))
			{	
				//preloaded
				user = GLOBAL.getCurrentUser();
			}
	
			title = user.getFirstName() + "'s Profile";
			
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENT.toString()))
		{
			event = GLOBAL.getEventBuffer();

			//Button profileButton2 = (Button)findViewById(R.id.profileButton2);
			Button profileButton3 = (Button)findViewById(R.id.profileButton3);
			//profileButton2.setVisibility(View.GONE);
			profileButton3.setVisibility(View.GONE);
			System.out.println("EVENT NAME: " + event.getName());
			title = event.getName();
		}

		setNotifications();
		populateProfile(); //populates a group / user profile
		
		// initializing the action bar and killswitch listener
		initActionBar(title);
		
	}

	private void setNotifications()
	{
		if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
		{
			((Button) findViewById(R.id.profileButton1)).setText("Members\n(" + group.getNumUsers() + ")");
		}
		else if (CONTENT.equals(CONTENT_TYPE.USER.toString()))
		{
			System.out.println("PANCAKES");
			System.out.println(user.getAge() + user.getName());
			
			((Button) findViewById(R.id.profileButton1)).setText("Friends\n(" + user.getNumUsers() + ")");
			((Button) findViewById(R.id.profileButton2)).setText("Groups\n(" + user.getNumGroups() + ")");	
			((Button) findViewById(R.id.profileButton3)).setText("Events\n(" + user.getNumEventsUpcoming() + ")");	
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENT.toString()))
		{
			//for event later
			((Button) findViewById(R.id.profileButton1)).setText("Attending (" + event.getNumUsers() + ")");
			((Button) findViewById(R.id.profileButton2)).setText("Invite Groups");
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
			//Get rid of sharepreferences for token login
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = preferences.edit();
			editor.remove("session_email");
			editor.remove("session_token");
			editor.commit();
			
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
		Intent intent = new Intent(this, ListActivity.class);
		switch (view.getId())
		{
		case R.id.profileButton1:
			if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
			{
				//members
				intent.putExtra("CONTENT", "GROUPS_MEMBERS");
				System.out.println("Loading a group with id: " + group.getID());
				group.fetchMembers();
				GLOBAL.setGroupBuffer(group);
				intent.putExtra("GID", group.getID());
			}
			else if (CONTENT.equals(CONTENT_TYPE.USER.toString()))
			{
				//friends
				intent.putExtra("CONTENT", "FRIENDS_CURRENT");
				user.fetchFriends();
			}
			else
			{
				//events
				intent.putExtra("CONTENT", "EVENTS_ATTENDING");
				event.fetchParticipants();
				GLOBAL.setEventBuffer(event);
			}
			break;
		case R.id.profileButton2:
			//groups
			if (CONTENT.equals(CONTENT_TYPE.USER.toString()))
			{
				intent.putExtra("CONTENT", "GROUPS_CURRENT");
				user.fetchGroups();
			}
			else if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
			{
				//invite members, only for the current user, this isn't even real right now?
				intent = new Intent(this, InviteActivity.class);
				//user.fetchGroups();
				GLOBAL.getCurrentUser().fetchFriends();
				group.fetchMembers();
			}
			else
			{
				intent = new Intent(this, EventAddGroupsActivity.class);
				GLOBAL.getCurrentUser().fetchGroups();
			}
			break;
		case R.id.profileButton3:
			//events UPCOMING
			user.fetchEventsUpcoming();
			intent.putExtra("CONTENT", "EVENTS_UPCOMING");
			break;
		case R.id.profileEditButton:
			if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
				intent = new Intent(this, GroupEditActivity.class);
			else if (CONTENT.equals(CONTENT_TYPE.USER.toString()))
				intent = new Intent(this, ProfileEditActivity.class);
			else
				intent = new Intent(this, EventEditActivity.class);
			break;
		default:
				break;
		}
		if (user != null)
		{
			if (!GLOBAL.isCurrentUser(user.getEmail()))
				GLOBAL.setUserBuffer(user);
			else
				GLOBAL.setCurrentUser(user);
			intent.putExtra("EMAIL", user.getEmail());
		}
		if (group != null)
		{
			intent.putExtra("GID", Integer.toString(group.getID()));
			GLOBAL.setGroupBuffer(group);
		}
		if (event != null)
			intent.putExtra("EID", Integer.toString(event.getID()));
		iv = null;
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent e)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	//do nothing
	    	if (CONTENT.equals(CONTENT_TYPE.USER.toString()))
	    	{
	    		//current friends case
	    		GLOBAL.getCurrentUser().fetchFriends();
	    		//friend requests case
	    		GLOBAL.getCurrentUser().fetchFriendRequests();
	    		//group members case
	    		if (GLOBAL.getGroupBuffer() != null)
	    			GLOBAL.getGroupBuffer().fetchMembers();
	    		//event parts case
	    		if (GLOBAL.getEventBuffer() != null)
	    			GLOBAL.getEventBuffer().fetchParticipants();
	    	}
	    	else if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
	    	{
	    		//current groups case
	    		GLOBAL.getCurrentUser().fetchGroups();
	    		//group invites case
	    		GLOBAL.getCurrentUser().fetchGroupInvites();
	    	}
	    	else if (CONTENT.equals(CONTENT_TYPE.EVENT.toString()))
	    	{
	    		//events pending case
	    		GLOBAL.getCurrentUser().fetchEventsUpcoming();
	    		//events pending case
	    		GLOBAL.getCurrentUser().fetchEventsPending();
	    		//event invites case
	    		GLOBAL.getCurrentUser().fetchEventsInvites();
	    	}
	    	finish();
	    }
	    return true;
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
		
		TextView aboutTitle = (TextView) findViewById(R.id.aboutTitlePA);
		TextView info = (TextView) findViewById(R.id.profileInfoTextView);
		TextView about = (TextView) findViewById(R.id.profileAboutTextView);
		if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
		{
			aboutTitle.setText("About Group:");
			iv.setImageBitmap(group.getImage());
			info.setText("Extra group info");
			about.setText(group.getAbout());
		}
		else if (CONTENT.equals(CONTENT_TYPE.USER.toString()))
		{
			String infoT = "";
			String location = user.getLocation();
			if (location == null)
				location = "";

			int age = user.getAge();
			if (age == 0)
				infoT = location;
			else
				infoT = age + " yrs young\n" + location;
			iv.setImageBitmap(user.getImage());
			info.setText(infoT);
			about.setText(user.getAbout());
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENT.toString()))
		{
			aboutTitle.setText("About Event:");
			about.setText(event.getAbout());
			iv.setImageBitmap(event.getImage());
			String infoText = "Category: " + event.getCategory() + "\n" + event.getLocation() + "\n" + event.getStartDate();
			if (event.getMaxPart() > 0)
				infoText += "\n" + event.getNumUsers() + " attending / " + event.getMinPart() + " required" + "\nMax Participants: " + event.getMaxPart();
			else
				infoText += "\n" + event.getNumUsers() + " attending / " + event.getMinPart() + " required";
			info.setText(infoText);
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
