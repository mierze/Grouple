package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import cs460.grouple.grouple.User.getUserInfoTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.widget.Toast;

/*
 * UserActivity displays the profile page of any user
 */
public class ProfileActivity extends ActionBarActivity
{
	enum CONTENT_TYPE {
		USER, GROUP, EVENT
	}
	private ImageView iv;
	private BroadcastReceiver broadcastReceiver;
	private User user; //user who's profile this is
	private Group group;
	private Event event;
	private Bundle EXTRAS;
	private String ROLE = "M";//defaulting to lowest level
	private String CONTENT; //type of content to display in profile, passed in from other activities
	private Global GLOBAL;
	
	
	private Button profileButton1;
	private Button profileButton2;
	private Button profileButton3;
	private Button editProfileButton;
	@Override
	protected void onStart()
	{
		super.onStart();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		initKillswitchListener();
		load();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		load();

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
		System.out.println("CONTENT IS SET TO " + CONTENT);
		profileButton1 = (Button)findViewById(R.id.profileButton1);
		profileButton2 = (Button)findViewById(R.id.profileButton2);
		profileButton3 = (Button)findViewById(R.id.profileButton3);
		editProfileButton = (Button)findViewById(R.id.profileEditButton);
		profileButton2.setVisibility(View.GONE);
		profileButton3.setVisibility(View.GONE);
		editProfileButton.setVisibility(View.GONE);
		if (CONTENT.equals(CONTENT_TYPE.USER.toString()))
		{
			System.out.println("NOW IN USER");
			//grabbing the user with the given email in the EXTRAS
			if (!GLOBAL.isCurrentUser(EXTRAS.getString("EMAIL")))
			{
				if (GLOBAL.getUserBuffer() != null)
					user = GLOBAL.getUserBuffer();
			}
			else
				user = GLOBAL.getCurrentUser();
			title = user.getFirstName() + "'s Profile";
			setNotifications();
		}
		else 
		{
			user = GLOBAL.getCurrentUser();
			if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
			{
				group = GLOBAL.getGroupBuffer();
				title = group.getName();
			}
			else
			{
				event = GLOBAL.getEventBuffer();
				title = event.getName();
			}
			setRole();
		}

		populateProfile(); //populates a group / user profile
		
		// initializing the action bar and killswitch listener
		initActionBar(title);
		
	}

	private void setRole()
	{
		int pub;
		String pro2Text;
		ArrayList<User> users = new ArrayList<User>();
		if (CONTENT.equals(CONTENT_TYPE.EVENT.toString()))
		{
			pub = event.getPub();
			users = event.getUsers();
			pro2Text = "Join Event";
		}
		else
		{
			pub = group.getPub();
			users = group.getUsers();
			pro2Text = "Join Group";
		}
		
		//checking if user is in group/event
		boolean inEntity = false;
		for (User u : users)
			if (u.getEmail().equals(user.getEmail()))
				inEntity = true;
		
		if (!inEntity) //user not in group, check if public so they can join
		{
			if (pub == 1)
			{
				profileButton2.setVisibility(View.VISIBLE);
				profileButton2.setText(pro2Text);
			}
			setNotifications();//call here since not checking role first
		}
		else //user is in group, check role
		{
			if (CONTENT.equals(CONTENT_TYPE.EVENT.toString()))
				new getRoleTask().execute("http://68.59.162.183/android_connect/check_role_event.php", Integer.toString(event.getID()));
			else
				new getRoleTask().execute("http://68.59.162.183/android_connect/check_role_group.php", Integer.toString(group.getID()));
		}
	}
		

	private class getRoleTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = (CONTENT.equals(CONTENT_TYPE.EVENT.toString())) ? "eid" : "gid";
			String email = user.getEmail();
			String id = urls[1];
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair(type, id));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				 
				//json fetch was successful
				if (jsonObject.getString("success").toString().equals("1"))
				{
					
					ROLE = jsonObject.getString("role").toString();
					System.out.println("ROLE IS BEING SET TO " + ROLE);
					setNotifications(); //for group / event
				
				} 
				//unsuccessful
				else
				{
					// failed
					Log.d("FETCH ROLE FAILED", "FAILED");
				}
			} 
			catch (Exception e)
			{
				Log.d("atherjsoninuserpost", "here");
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
			//do next thing here
		}
	}
	
	private void setNotifications()
	{
		System.out.println("NOW IN SET NOTIFICATIONS");
		if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
		{
			profileButton1.setText("Members\n(" + group.getNumUsers() + ")");
			if (ROLE.equals("C"))
				editProfileButton.setVisibility(View.VISIBLE);
			
		
		}
		else if (CONTENT.equals(CONTENT_TYPE.USER.toString()))
		{
			profileButton2.setVisibility(View.VISIBLE);
			profileButton3.setVisibility(View.VISIBLE);
			profileButton1.setText("Friends\n(" + user.getNumUsers() + ")");
			profileButton2.setText("Groups\n(" + user.getNumGroups() + ")");	
			profileButton3.setText("Events\n(" + user.getNumEventsUpcoming() + ")");	
			if (GLOBAL.isCurrentUser(user.getEmail()))
				editProfileButton.setVisibility(View.VISIBLE);
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENT.toString()))
		{
			profileButton1.setText("Attending (" + event.getNumUsers() + ")");	
			if (ROLE.equals("C"))
				editProfileButton.setVisibility(View.VISIBLE);
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
			iv = (ImageView) findViewById(R.id.profileImageUPA);
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
			GLOBAL.destroySession();
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
		boolean noIntent = false;
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
				//join the group
				new JoinPublicTask().execute("http://68.59.162.183/"
						+ "android_connect/join_public_group.php", user.getEmail(), "M", Integer.toString(group.getID()));
					System.out.println("NOW ADDING TO  GROUP");	
					noIntent = true;
			}
			else
			{
				System.out.println("ABOUT TO START JOIN EVENT");
				new JoinPublicTask().execute("http://68.59.162.183/"
						+ "android_connect/join_public_event.php", user.getEmail(), "M", Integer.toString(event.getID()));
					noIntent = true;
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
		if (!noIntent) //TODO, move buttons elsewhere that dont start list
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
	
	//aSynch task to add individual member to group.
	private class JoinPublicTask extends AsyncTask<String,Void,String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = (CONTENT.equals(CONTENT_TYPE.EVENT.toString())) ? "e_id" : "g_id";
			System.out.println("ABOUT TO START JOIN EVENT now type is " + type);
			System.out.println("urls are " + urls[1] + " " + urls[2] + " " + urls[3]);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("role", urls[2]));
			nameValuePairs.add(new BasicNameValuePair(type, urls[3]));

			//pass url and nameValuePairs off to GLOBAL to do the JSON call.  Code continues at onPostExecute when JSON returns.
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}


		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);

				// member has been successfully added
				if (jsonObject.getString("success").toString().equals("1"))
				{
					System.out.println("USER HAS SUCCESSFULLY BEEN ADDED");
					Context context = getApplicationContext();
					
					Toast toast = GLOBAL.getToast(context, jsonObject.getString("message"));
					toast.show();
					profileButton2.setVisibility(View.GONE);
					if (CONTENT.equals(CONTENT_TYPE.EVENT.toString()))
					{
						event.fetchParticipants();
						event.addToUsers(user);
					}
					else
					{
						group.fetchMembers();
						group.addToUsers(user);
					}
					load();
					//all working correctly, continue to next user or finish.
					
				} 
				else if (jsonObject.getString("success").toString().equals("0"))
				{	
					//a particular user was unable to be added to database for some reason...
					//Don't tell the user!
				}
			} catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}
		}	
	}
}
