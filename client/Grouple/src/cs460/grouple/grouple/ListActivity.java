package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import cs460.grouple.grouple.ProfileActivity.CONTENT_TYPE;
import cs460.grouple.grouple.User.getUserInfoTask;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


/*
 * ListActivity is an activity that displays lists of different types, 
 * and performs relevant functions based on the situation
 */
public class ListActivity extends ActionBarActivity
{
	/*
	 * All possible content types that list activity supports
	 */
	enum CONTENT_TYPE 
	{
		FRIENDS_CURRENT, FRIENDS_REQUESTS, GROUPS_MEMBERS, EVENTS_ATTENDING,
		GROUPS_CURRENT, GROUPS_INVITES, 
	    EVENTS_UPCOMING, EVENTS_PENDING, EVENTS_PAST, EVENTS_INVITES;    
	}	
	
	//CLASS-WIDE DECLARATIONS
	private BroadcastReceiver broadcastReceiver;
	private User user; //user whose current groups displayed
	private Group group;
	private Event event;
	private Bundle EXTRAS; //extras passed in from the activity that called ListActivity
	private String CONTENT; //type of content to display in list, passed in from other activities
	private LinearLayout listLayout; //layout for list activity (scrollable layout to inflate into)
	private Global GLOBAL;// = thinking making few static and do some null checks and regrabs
	private LayoutInflater li;
	private String ROLE = "M";//defaulting to lowest level
	private String PANDABUFFER = ""; //same
	private int bufferID; //same as below: could alternatively have json return the values instead of saving here	
	private Button addNew;
	//TESTING
	private ArrayList<User> users;
	private ArrayList<Group> groups;
	private ArrayList<Event> events;
	private Dialog loadDialog =  null;

	/* loading actionbar */
	public void initActionBar(String actionBarTitle)
	{
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText(actionBarTitle);
	}

	/* loading in everything needed to generate the list */
	public void load()
	{
		//INSTANTIATIONS
		GLOBAL = ((Global) getApplicationContext());
		EXTRAS =  getIntent().getExtras();
		CONTENT = EXTRAS.getString("CONTENT");
		listLayout = ((LinearLayout)findViewById(R.id.listLayout));
		li = getLayoutInflater();	
		String actionBarTitle = "";
		addNew = (Button)findViewById(R.id.addNewButtonLiA);
		addNew.setVisibility(View.GONE); //GONE FOR NOW
		
		
		//LOAD DIALOG INITIALIZING
		if ((loadDialog == null) || (!loadDialog.isShowing())) 
		{
	        loadDialog= new Dialog(this);
	        loadDialog.getWindow().getCurrentFocus();
	        loadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	        View v = li.inflate(R.layout.load, null);
	        ImageView loadImage = (ImageView) v.findViewById(R.id.loadIconImageView);
	        loadImage.startAnimation( 
	        	    AnimationUtils.loadAnimation(this, R.anim.rotate));
	        loadDialog.setContentView(R.layout.load);
	        loadDialog.setCancelable(false);
	        loadDialog.setOwnerActivity(this);
	        loadDialog.getWindow().setDimAmount(0.7f);
	      //  WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();  
	       // lp.dimAmount=0.0f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
	       // dialog.getWindow().setAttributes(lp);
		}
			
		//GRABBING A USER
		if (EXTRAS.getString("EMAIL") != null)
			if (GLOBAL.isCurrentUser(EXTRAS.getString("EMAIL")))
				user = GLOBAL.getCurrentUser();
			else if (GLOBAL.getUserBuffer() != null && GLOBAL.getUserBuffer().getEmail().equals(EXTRAS.getString("EMAIL")))
				user = GLOBAL.getUserBuffer();

		//CALL APPROPRIATE METHODS
		//CONTENT_TYPE -> POPULATEUSERS
		if (CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()) || CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()) 
				||  CONTENT.equals(CONTENT_TYPE.EVENTS_ATTENDING.toString()) || CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
		{
			if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
			{
				addNew.setText("Add New Friend");
				addNew.setVisibility(View.VISIBLE);
				View svLayout = findViewById(R.id.scrollViewLayout);
			    if (svLayout.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
			        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) svLayout.getLayoutParams();
			        p.setMargins(0, 0, 0, 60);
			        svLayout.requestLayout();
			    }
				actionBarTitle = user.getFirstName() + "'s Friends";
			}
			else if (CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()))
			{
				group = GLOBAL.getGroupBuffer();
				setRole();
				actionBarTitle="Group Members";
			}
			else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
			{
		
				System.out.println("Load 5 now setting action bar title to : " + user.getFirstName());
				actionBarTitle = user.getFirstName() + "'s Friend Requests";
			}
			else //EVENTS_ATTENDING
			{
				event = GLOBAL.getEventBuffer();
				setRole();
				actionBarTitle = "Attending " + event.getName();
			}
			populateUsers();
		}
		//CONTENT_TYPE -> POPULATEGROUPS
		else if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()) || CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()))
		{
			if (CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()))
				actionBarTitle = user.getFirstName() + "'s Group Invites";
	
			if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
			{
				actionBarTitle = user.getFirstName() + "'s Groups";
			}
			populateGroups();
		//CONTENT_TYPE -> POPULATEEVENTS
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_INVITES.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
		{
			System.out.println("in the if to call the pop");
			if (addNew != null)
				addNew.setVisibility(View.GONE);//DOI?
			if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()))
				actionBarTitle = user.getFirstName() + "'s Pending Events";
			else if (CONTENT.equals(CONTENT_TYPE.EVENTS_INVITES.toString()))
				actionBarTitle = user.getFirstName() + "'s Event Invites";
			else if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()))
				actionBarTitle = user.getFirstName() + "'s Upcoming Events";
			else
				actionBarTitle = user.getFirstName() + "'s Past Events";
			populateEvents();
		}
		//calling next functions to execute
		initActionBar(actionBarTitle);
		initKillswitchListener();
	}
	
	//populates a list of groups
	private void populateGroups()
	{
		String sadGuyText = "";
		String nameText = "";
		Button nameButton;
		View row = null;
		int index;
		int id;		
		
		if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
		{
			groups = user.getGroups();
			sadGuyText = "You are not in any groups.";
		}
		else if (CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()))
		{
			groups = user.getGroupInvites();
			sadGuyText = "You do not have any group invites.";
		}

		if (groups != null && !groups.isEmpty())
		{
			for (Group g : groups)
			{
				index = groups.indexOf(g);
				id = groups.get(index).getID();
				nameText = groups.get(index).getName();
				//
				if  (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
				{
					if (GLOBAL.isCurrentUser(user.getEmail()))
					{
						row = (GridLayout) li.inflate(R.layout.list_row, null);
						Button leaveGroupButton = (Button)row.findViewById(R.id.removeButtonLI);
						leaveGroupButton.setId(g.getID());
					}
					else
						row = (GridLayout) li.inflate(R.layout.list_row_nobutton, null);
					
					nameButton =  (Button)row.findViewById(R.id.nameButtonLI);
				}
				else //GROUP INVITES
				{
					row = (GridLayout) li.inflate(R.layout.list_row_acceptdecline, null);
					nameButton =  (Button)row.findViewById(R.id.nameButtonAD);
				}
				row.setId(id);
				nameButton.setId(id);
				nameButton.setText(nameText);
				listLayout.addView(row);
			}		
		}
		else
		{
			// The user has no groups so display the sad guy
			row = li.inflate(R.layout.listitem_sadguy, null);
			((TextView) row.findViewById(R.id.sadGuyTextView)).setText(sadGuyText);
			listLayout.addView(row);
		}	
	}
	
	//populates a list of users
	private void populateUsers()
	{	
		String sadGuyText = "";
		View row = null;
		int index;
		String email = "";
		String nameText = "";
		Button nameButton = null;

		if  (CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()))
		{
			sadGuyText = "There are no members in this group.";
			users = group.getUsers();	
		}
		else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))//to do make else ifs." +
		{
			sadGuyText = "You have no friends.";
			users = user.getUsers();
			Button addNew = (Button)findViewById(R.id.addNewButtonLiA);
			if (GLOBAL.isCurrentUser(user.getEmail()))
				addNew.setText("Add New Friend");
			else
				addNew.setVisibility(View.GONE);
		}
		else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
		{
			System.out.println("GRABBING FRIEND REQUESTS");
			users = user.getFriendRequests();
			sadGuyText = "You do not have any friend requests.";
		}
		else 
		{
			users = event.getUsers();
			sadGuyText = "No one is attending event.";
		}
	
		if (users != null && !users.isEmpty())
		{
			for (User u : users)
			{
				index = users.indexOf(u);
				email = u.getEmail();
				System.out.println("IN USERS POP : emaiL: " + email);

				if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()) && GLOBAL.isCurrentUser(user.getEmail()) && !CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()))
				{
					nameText = u.getName();
					row = (GridLayout) li.inflate(R.layout.list_row, null);
					nameButton = (Button) row.findViewById(R.id.nameButtonLI);
					Button removeFriendButton = (Button) row.findViewById(R.id.removeButtonLI);
					removeFriendButton.setId(index);	 
				} 
				//FOR FRIEND REQUESTS
				else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
				{	
					System.out.println("SHOULD BE IN THIS NOW");
					row = (GridLayout) li.inflate(R.layout.list_row_acceptdecline, null);
					nameButton = (Button) row.findViewById(R.id.nameButtonAD);
					nameText = email;
				}
				//FOR GROUP MEMBERS / CURRENT FRIENDS NON MOD
				else
				{
					nameText = u.getName();
					row = (GridLayout) li.inflate(R.layout.list_row_nobutton, null);
					nameButton = (Button) row.findViewById(R.id.nameButtonLI);
				}
				nameButton.setText(nameText);
				nameButton.setId(index);
				row.setId(index);
				listLayout.addView(row);
			}		
		}
		else
		{		
			row = li.inflate(R.layout.listitem_sadguy, null);
			TextView sadGuy = ((TextView) row.findViewById(R.id.sadGuyTextView));
			// The user has no friend's so display the sad guy image.
			sadGuy.setText(sadGuyText);
			listLayout.addView(row);
		}

	}
	
	//populates a list of events
	private void populateEvents()
	{
		View row = null;
		String sadGuyText = "";
		Button nameButton = null;
		Button removeEventButton = null;

		int id;
		int index;
		String nameText = "";
		/*
		 * Checking which CONTENT we need to inflate
		 */
		if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()))
		{
			events = user.getEventsPending();
			sadGuyText = "You do not have any pending events.";
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()))
		{	
			events = user.getEventsUpcoming();
			sadGuyText = "You do not have any upcoming events.";
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENTS_INVITES.toString()))
		{
			events = user.getEventsInvites();
			sadGuyText = "You do not have any event invites.";
		}
		else
		{
			events = user.getEventsPast();
			System.out.println("GRABBING EVENTS PAST");
			System.out.println("Size: " + user.getNumEventsPast());
			sadGuyText = "You do not have any past events.";
		}
		
		if (events != null && !events.isEmpty())
		{
			for (Event e : events) 
			{
				id = e.getID();
				index = events.indexOf(e);
				//Group group = GLOBAL.loadGroup(id);
				if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
				{
					System.out.println("IN FIRST EVENT IF");
					if (GLOBAL.isCurrentUser(user.getEmail()))
					{
						row = (GridLayout) li.inflate(R.layout.list_row, null);	
						removeEventButton = (Button) row.findViewById(R.id.removeButtonLI);
						removeEventButton.setId(id);		
					}
					else //user does not have ability to remove events
						row = (GridLayout) li.inflate(R.layout.list_row_nobutton, null);
					
					nameButton =  (Button)row.findViewById(R.id.nameButtonLI);

					if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
						nameButton.setText(e.getName());//future get date too?
					else
						nameButton.setText(e.getName() + "\n(" + e.getNumUsers() + " confirmed / " + e.getMinPart() + " required)");
				}
				else
				{
					row = (GridLayout) li.inflate(R.layout.list_row_acceptdecline, null);
					nameButton =  (Button)row.findViewById(R.id.nameButtonAD);
					nameButton.setText(e.getName() + "\n(" + e.getNumUsers() + " confirmed / " + e.getMinPart() + " required)");
				}

				//setting ids to the id of the group for button functionality
				nameButton.setId(id);
				row.setId(id);
	
				//adding row to view
				listLayout.addView(row);
			}
		}
		else
		{
			//no group requests were found
			GridLayout sadGuy = (GridLayout) li.inflate(R.layout.listitem_sadguy, null);
			TextView sadTextView = (TextView) sadGuy.findViewById(R.id.sadGuyTextView);
			//Set the sad guy text.
			sadTextView.setText(sadGuyText);
			listLayout.addView(sadGuy);
		}	
	}
			
	/* based on content type, gets the corresponding role */
	private void setRole()
	{
		ArrayList<User> users = new ArrayList<User>();
		if (CONTENT.equals(CONTENT_TYPE.EVENTS_ATTENDING.toString()))
		{
			users = event.getUsers();
			addNew.setText("Invite Groups");//TODO: Mod checks
		}
		else
		{
			users = group.getUsers();
			addNew.setText("Invite Friends");
		}
		
		//checking if user is in group/event
		boolean inEntity = false;
		for (User u : users)
			if (u.getEmail().equals(user.getEmail()))
				inEntity = true;
		
		if (inEntity)
		{
			System.out.println("NOW IN SET ROLE");
			if (CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()))
				new getRoleTask().execute("http://68.59.162.183/android_connect/check_role_group.php", Integer.toString(group.getID()));
			else
				new getRoleTask().execute("http://68.59.162.183/android_connect/check_role_event.php", Integer.toString(event.getID()));
		}
	}

	
	/* Default methods */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		load();
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		listLayout = ((LinearLayout)findViewById(R.id.listLayout));
		listLayout.removeAllViews();
		load();
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

	/*
	 * onClick methods
	 */
	public void onClick(View view)
	{
		GridLayout parent = (GridLayout)view.getParent();
		Button nameText = (Button) parent
				.findViewById(R.id.nameButtonAD);
		switch (view.getId())
		{
		case R.id.declineButton:
			if (CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()))
			{
				bufferID = parent.getId();
				new performActionTask().execute("http://68.59.162.183/android_connect/leave_group.php", user.getEmail(), Integer.toString(parent.getId()));
			}
			else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
			{
				PANDABUFFER = users.get(parent.getId()).getEmail();
				new performActionTask().execute("http://68.59.162.183/android_connect/decline_friend_request.php", PANDABUFFER);
			}
			else if (CONTENT.equals(CONTENT_TYPE.EVENTS_INVITES.toString()))
			{
				bufferID = parent.getId(); //PANDA
				new performActionTask().execute("http://68.59.162.183/android_connect/leave_event.php", Integer.toString(bufferID));
			}
			break;
		case R.id.acceptButton:
			if (CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()))
			{
				bufferID = parent.getId();
				new performActionTask().execute("http://68.59.162.183/android_connect/accept_group_invite.php",user.getEmail(),Integer.toString(bufferID));
			}
			else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
			{
			
				PANDABUFFER = users.get(parent.getId()).getEmail();
				new performActionTask().execute("http://68.59.162.183/android_connect/accept_friend_request.php", PANDABUFFER);
			}
			else if (CONTENT.equals(CONTENT_TYPE.EVENTS_INVITES.toString()))
			{
				bufferID = parent.getId();
				System.out.println("Accepting event invite, eid: " + bufferID);
				new performActionTask().execute("http://68.59.162.183/android_connect/accept_event_invite.php", Integer.toString(bufferID));
			}
			break;
		}
	}
	// Handles removing a friend when the remove friend button is pushed.
	public void removeButton(View view)
	{			
		if (CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()))
		{
			//nothing for now
		}
		else if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
		{
			//Get the id.
			bufferID = view.getId();

			new AlertDialog.Builder(this)
					.setMessage("Are you sure you want to leave this group?")
					.setCancelable(true)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int id)
						{
							new performActionTask().execute("http://68.59.162.183/android_connect/leave_group.php", user.getEmail(), Integer.toString(bufferID));
						}
					}).setNegativeButton("Cancel", null).show();
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
		{	
			//Get the id.
			bufferID = view.getId();
			System.out.println("ID IS SET TO" + bufferID);
			new AlertDialog.Builder(this)
					.setMessage("Are you sure you want to leave this event?")
					.setCancelable(true)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int id)
						{
							new performActionTask().execute("http://68.59.162.183/android_connect/leave_event.php", Integer.toString(bufferID));
						}
					}).setNegativeButton("Cancel", null).show();
		}
		else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
		{
			final int index = view.getId(); //position in friendArray
			final String friendEmail = users.get(index).getEmail(); //friend to remove
			PANDABUFFER = friendEmail;//PANDA TODO

			new AlertDialog.Builder(this)
				.setMessage("Are you sure you want to remove that friend?")
				.setCancelable(true)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int id)
					{
						new performActionTask().execute("http://68.59.162.183/android_connect/delete_friend.php", friendEmail);	
					}
				}).setNegativeButton("Cancel", null).show();
		}
	}
	//Starts an activity to add new friends/group members/invite groups to events
	public void startInviteActivity(View view)
	{
		loadDialog.show();
		Intent intent = null;
		if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
		{
			intent = new Intent(this, FriendAddActivity.class);
		}
		else if (CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()))
		{
			intent = new Intent(this, InviteActivity.class);
			group.fetchMembers();
			GLOBAL.getCurrentUser().fetchFriends();
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENTS_ATTENDING.toString()))
		{
			intent = new Intent(this, EventAddGroupsActivity.class);
			intent.putExtra("EID", Integer.toString(event.getID()));
			System.out.println("Setting EID to " + event.getID());
			GLOBAL.getCurrentUser().fetchGroups();
		}
		if (user != null)
			intent.putExtra("EMAIL", user.getEmail());
		if (event != null)
			intent.putExtra("EID", event.getID());
		if (group != null){
			intent.putExtra("GID", group.getID());
			GLOBAL.setGroupBuffer(group);
		}
		startActivity(intent);	
	}
	//Starts a USER/GROUP/EVENT profile
	public void startProfileActivity(View view)
			throws InterruptedException
	{
		loadDialog.show();
		Thread.sleep(2000);//sleeping to test look of load icon animation
		System.out.println("Just started dialog!");
		int id = view.getId();		
		Intent intent = new Intent(this, ProfileActivity.class);
		if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()) || CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()) )
		{
			System.out.println("Loading group gid: " + id);
			intent.putExtra("GID", id);
			intent.putExtra("EMAIL", user.getEmail());
			intent.putExtra("CONTENT", "GROUP");
			Group g = new Group(id);
			g.fetchGroupInfo();
			g.fetchMembers();
			GLOBAL.setGroupBuffer(g);
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_INVITES.toString()))
		{
			System.out.println("Loading event, eid: " + id);
			intent.putExtra("EID", id);
			intent.putExtra("EMAIL", user.getEmail());
			intent.putExtra("CONTENT", "EVENT");
			Event e = new Event(id);
			e.fetchEventInfo();
			e.fetchParticipants();
			GLOBAL.setEventBuffer(e);
		}
		else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()) || CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_ATTENDING.toString()) || CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
		{
			String friendEmail;
			if (CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
			{
				friendEmail = users.get(id).getEmail();
				User u = new User(friendEmail);
				u.fetchEventsUpcoming();
				u.fetchFriends();
				u.fetchGroups();
				u.fetchUserInfo();
				if (!GLOBAL.isCurrentUser(friendEmail))
					GLOBAL.setUserBuffer(u);
				else
					GLOBAL.setCurrentUser(u); //reloading user
			}
			else
			{
				friendEmail = users.get(id).getEmail();
				users.get(id).fetchUserInfo();
				users.get(id).fetchGroups();
				users.get(id).fetchEventsUpcoming();
				users.get(id).fetchFriends();
				if (!GLOBAL.isCurrentUser(friendEmail))
					GLOBAL.setUserBuffer(users.get(id));
				else
					GLOBAL.setCurrentUser(users.get(id)); //reloading user
			}
			intent.putExtra("EMAIL", friendEmail);
			intent.putExtra("CONTENT", "USER");	
		}
		startActivity(intent);	
	}
	
	protected void onStop() 
	{ 
		super.onStop();
		 
		loadDialog.hide();
	}
	//Overrides the default system back button
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent e)  
	{
		loadDialog.show();
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	//refresh pertinent info
	    	if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()) || CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
	    	{
	    		user.fetchFriends();
	    		//FRIENDS
	    		if (GLOBAL.isCurrentUser(user.getEmail()))
	    			user.fetchFriendRequests();
	    		if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
				{
	    			//USER PROFILE
		    		//FRIEND PROFILE
	    			user.fetchEventsUpcoming();
	    			user.fetchGroups();
				}
	    	}
	    	else if (CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()) || CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
	    	{
	    		//GROUPS
	    		user.fetchGroupInvites();
	    		user.fetchGroups();
	    		if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
	    		{
	    			user.fetchFriends();
	    			user.fetchEventsUpcoming();
	        		//USER PROFILE
		    		//FRIEND PROFILE
	    		}
	    	}
	    	else if (CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()))
	    	{
	    		//GROUP PROFILE
	    		group.fetchMembers();
	    	}
	    	else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_INVITES.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()))
	    	{
	    		//EVENTS
	    		user.fetchEventsUpcoming();
	    		if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()))
	    		{
	    			//profile case
	        		user.fetchFriends();
	        		user.fetchGroups();
	    		}
	    		if (GLOBAL.isCurrentUser(user.getEmail()))
	    		{
		    		user.fetchEventsInvites();
		    		user.fetchEventsPending();
	    		}
	    	}
	    	else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
	    	{
	    		//nothing yet
	    	}
	    	else if (CONTENT.equals(CONTENT_TYPE.EVENTS_ATTENDING.toString()))
	    	{
	    		//EVENT PROFILE
	    		event.fetchParticipants();
	    	}	    	
	    	//SETTING GLOBALS
	    	if (user != null)
	    		if (GLOBAL.isCurrentUser(user.getEmail()))
	    		{
	    			GLOBAL.setCurrentUser(user);
	    		}
	    		else
	    			GLOBAL.setUserBuffer(user);
	    	if (group != null)
	    		GLOBAL.setGroupBuffer(group);
	    	if (event != null)
	    		GLOBAL.setEventBuffer(event);
	    	finish();
	    }
	    return true;
	}
	
	
	

	/* Gets the role of the current user in a group / event */
	private class getRoleTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = (CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString())) ? "gid" : "eid";
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
					if (!ROLE.equals("M"))
						if (CONTENT.equals(CONTENT_TYPE.EVENTS_ATTENDING.toString()))
						{
							if (!event.getEventState().equals("Ended"))
							{
								addNew.setVisibility(View.VISIBLE);
								//TODO PANDA refactor this out, if it works
								View svLayout = findViewById(R.id.scrollViewLayout);
							    if (svLayout.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
							        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) svLayout.getLayoutParams();
							        p.setMargins(0, 0, 0, 100);
							        svLayout.requestLayout();
							    }
							}
						}
						else
						{
							addNew.setVisibility(View.VISIBLE);
							View svLayout = findViewById(R.id.scrollViewLayout);
						    if (svLayout.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
						        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) svLayout.getLayoutParams();
						        p.setMargins(0, 0, 0, 100);
						        svLayout.requestLayout();
						    }
						}
				//	setControls(); //for group / event
				
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
	private class performActionTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			System.out.println("IN ACCEPT DECLINE NOW");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			// Add your data
			
			if (CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()) || CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
			{
				nameValuePairs.add(new BasicNameValuePair("email", urls[1]));
				nameValuePairs.add(new BasicNameValuePair("gid",urls[2]));
			}
			else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()) || CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
			{
				System.out.println("SENDER: " + urls[1]);
				//System.out.println()
				//possibly check that all gorup invtes / events follow suit
				nameValuePairs.add(new BasicNameValuePair("sender", urls[1]));
				nameValuePairs.add(new BasicNameValuePair("receiver", user.getEmail()));
			}
			else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_INVITES.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()))
			{
				//possibly check that all gorup invtes / events follow suit
				nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
				nameValuePairs.add(new BasicNameValuePair("eid", urls[1]));
			}

			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// successful
					System.out.println("success in acceptdecline on post!");
					
		
					//removing all friend requests for refresh
					listLayout.removeAllViews();
					String message = jsonObject.getString("message");
					Context context = getApplicationContext();
					if (CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()))
					{
						//user.fetchGroupInvites();
						user.removeGroupInvite(bufferID);
						if (!message.equals("Group invite accepted!"))
						{
							message = "Group invite declined!";
						}
					}
					else if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
					{
						//user.fetchGroups();
						user.removeGroup(bufferID);
					}
					else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
					{
						user.removeFriendRequest(PANDABUFFER);
						user.fetchFriendRequests();
					}
					else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
					{
						user.removeUser(PANDABUFFER);
						user.fetchFriends();
					
					}
					else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()))
					{
						user.removeEventPending(bufferID);
						user.fetchEventsPending();
					}
					else if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()))
					{
						user.removeEventUpcoming(bufferID);
						user.fetchEventsUpcoming();
					}
					else if (CONTENT.equals(CONTENT_TYPE.EVENTS_INVITES.toString()))
					{
						user.removeEventInvite(bufferID);
						user.fetchEventsInvites();
			
						System.out.println("THE MESSAGE IS" + message);
						if (!message.equals("Event invite accepted!"))
							message = "Event invite declined!";
					}
					System.out.println("NOW IN ACCEPT DECLINE ONPOST3");
					
					/*if (GLOBAL.isCurrentUser(user.getEmail()))
						GLOBAL.setCurrentUser(user);
					else
						GLOBAL.setUserBuffer(user);
					 */
					Toast toast = GLOBAL.getToast(context, message);
					
					toast.show();
					System.out.println("NOW IN ACCEPT DECLINE ONPOST4");
					
					PANDABUFFER = "";
					bufferID = -1;

					///refreshing views
					if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()) || CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
						populateUsers();
					else if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()) || CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()))
						populateGroups();
					else 
						populateEvents();
				} 
				else
				{
					// failed
					System.out.println("fail!");
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	
	//to kill application
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
