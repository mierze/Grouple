package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*
 * ListActivity is an activity that displays lists of different types, 
 * and performs relevant functions based on the situation
 */
public class ListActivity extends BaseActivity
{
	/*
	 * All possible content types that list activity supports
	 */
	enum CONTENT_TYPE 
	{
		FRIENDS_CURRENT, FRIENDS_REQUESTS, GROUPS_MEMBERS, EVENTS_ATTENDING, SELECT_FRIEND,
		GROUPS_CURRENT, GROUPS_INVITES, 
	    EVENTS_UPCOMING, EVENTS_PENDING, EVENTS_PAST, EVENTS_INVITES;    
	}	
	
	//CLASS-WIDE DECLARATIONS
	private User user; //user whose current groups displayed
	private Group group;
	private Event event;
	private Bundle EXTRAS; //extras passed in from the activity that called ListActivity
	private String CONTENT; //type of content to display in list, passed in from other activities
	private LinearLayout listLayout; //layout for list activity (scrollable layout to inflate into)
	private LayoutInflater li;
	private String ROLE = "U";//defaulting to lowest level
	private String PANDABUFFER = ""; //same
	private int bufferID; //same as below: could alternatively have json return the values instead of saving here	
	private Button addNew;
	//TESTING
	private ArrayList<User> users;
	private ArrayList<Group> groups;
	private ArrayList<Event> events;

	/* loading in everything needed to generate the list */
	public void load()
	{
		String actionBarTitle = "";
		addNew.setVisibility(View.GONE); //GONE FOR NOW
			
		//GRABBING A USER
		if (EXTRAS.getString("email") != null)
			if (GLOBAL.isCurrentUser(EXTRAS.getString("email")))
			{
				System.out.println("MAKING USER THE CURRENT USER");
				user = GLOBAL.getCurrentUser();
			}
			else if (GLOBAL.getUserBuffer() != null && GLOBAL.getUserBuffer().getEmail().equals(EXTRAS.getString("email")))
				user = GLOBAL.getUserBuffer();

		
		//CLEAR LIST
		listLayout.removeAllViews();
		
		//CALL APPROPRIATE METHODS TO POPULATE LIST
		//CONTENT_TYPE -> POPULATEUSERS
		if (CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()) || CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()) 
				||  CONTENT.equals(CONTENT_TYPE.EVENTS_ATTENDING.toString()) || CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()) || CONTENT.equals((CONTENT_TYPE.SELECT_FRIEND.toString())))
		{
			if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
			{
				addNew.setText("Add New Friend");
				addNew.setVisibility(View.VISIBLE);
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
			else if (CONTENT.equals(CONTENT_TYPE.SELECT_FRIEND.toString()))
			{
				System.out.println("Load 5 now setting action bar title to : " + user.getFirstName());
				actionBarTitle = "Message Who?";
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
			//setting the bottom button gone
			if (addNew != null)
				addNew.setVisibility(View.GONE);
			//setting the actionbar text
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
		initActionBar(actionBarTitle, true);

	}
	
	//populates a list of groups
	private void populateGroups()
	{
		String sadGuyText = "";
		String name = "";
		TextView nameView;
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

		//looping thru and populating list of groups
		if (groups != null && !groups.isEmpty())
		{
			for (Group g : groups)
			{
				index = groups.indexOf(g);
				id = groups.get(index).getID();
				name = groups.get(index).getName();
				
				//GROUPS CURRENT
				if  (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
				{
					if (GLOBAL.isCurrentUser(user.getEmail()))
					{
						row = li.inflate(R.layout.list_row, null);
						Button leaveGroupButton = (Button)row.findViewById(R.id.removeButtonLI);
						leaveGroupButton.setId(g.getID());
					}
					else
						row = li.inflate(R.layout.list_row_nobutton, null);
					
					nameView =  (TextView)row.findViewById(R.id.nameTextViewLI);
				}
				else //GROUP INVITES
				{
					row = li.inflate(R.layout.list_row_acceptdecline, null);
					nameView =  (TextView)row.findViewById(R.id.emailTextView);
				}
				row.setId(id);
				nameView.setId(id);
				nameView.setText(name);
				listLayout.addView(row);
			}		
		}
		else
		{
			// The user has no groups so display the sad guy
			row = li.inflate(R.layout.list_item_sadguy, null);
			((TextView) row.findViewById(R.id.sadGuyTextView)).setText(sadGuyText);
			listLayout.addView(row);
		}	
	}
	
	//populates a list of users
	private void populateUsers()
	{	
		String sadGuyText = "";
		View row;
		int index;
		String email = "";
		String name = "";
		TextView nameTextView;

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
		else if (CONTENT.equals(CONTENT_TYPE.SELECT_FRIEND.toString()))
		{
			users = user.getUsers();
			sadGuyText = "No friends to message.";
		}
		else 
		{
			users = event.getUsers();
			sadGuyText = "No one is attending event.";
		}
	
		//looping thru and populating list of users
		if (users != null && !users.isEmpty())
		{
			for (User u : users)
			{
				index = users.indexOf(u);
				email = u.getEmail();
				if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()) && GLOBAL.isCurrentUser(user.getEmail()) && !CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()))
				{
					name = u.getName();
					row = li.inflate(R.layout.list_row, null);
					nameTextView = (TextView) row.findViewById(R.id.nameTextViewLI);
					Button removeFriendButton = (Button) row.findViewById(R.id.removeButtonLI);
					removeFriendButton.setId(index);	 
				} 
				//FOR FRIEND REQUESTS
				else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
				{	
					row = li.inflate(R.layout.list_row_acceptdecline, null);
					nameTextView = (TextView) row.findViewById(R.id.emailTextView);
					name = email;
				}
				//FOR GROUP MEMBERS / CURRENT FRIENDS NON MOD / SELECT FRIEND
				else
				{
					name = u.getName();
					row = li.inflate(R.layout.list_row_nobutton, null);
					nameTextView = (TextView) row.findViewById(R.id.nameTextViewLI);
				}
				nameTextView.setText(name);
				nameTextView.setId(index);
				row.setId(index);
				listLayout.addView(row);
			}		
		}
		else
		{		
			// The user has no friend's so display the sad guy image / text
			row = li.inflate(R.layout.list_item_sadguy, null);
			TextView sadGuy = ((TextView) row.findViewById(R.id.sadGuyTextView));
			sadGuy.setText(sadGuyText);
			listLayout.addView(row);
		}

	}
	
	//populates a list of events
	private void populateEvents()
	{
		View row;
		String sadGuyText = "";
		TextView nameTextView;
		Button removeEventButton;
		int id;
		int index;
		
		//Checking which CONTENT we need to get
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
			events = user.getEventInvites();
			sadGuyText = "You do not have any event invites.";
		}
		else
		{
			events = user.getEventsPast();
			sadGuyText = "You do not have any past events.";
		}
		
		
		//looping thru and populating list of events
		if (events != null && !events.isEmpty())
		{
			String startDate = null;
			for (Event e : events) 
			{
				View dateRow = li.inflate(R.layout.list_row_date, null);	
				TextView dateTextView = (TextView) dateRow.findViewById(R.id.dateTextView);	
				String newStartDate = e.getStartTextNoTime();
				if (startDate == null)
				{
					//first event to display, show its time, and set startDate = to it
					startDate = newStartDate;
					dateTextView.setText(e.getStartTextListDisplay());
					listLayout.addView(dateRow);
				}
				else if (startDate.compareTo(newStartDate) < 0)
				{
					//if previous start date less than new start date, display date
					startDate = e.getStartTextNoTime();
					dateTextView.setText(e.getStartTextListDisplay());
					listLayout.addView(dateRow);
				}
				id = e.getID();
				index = events.indexOf(e);
				//Group group = GLOBAL.loadGroup(id);
				if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
				{
					System.out.println("IN FIRST EVENT IF");
					if (GLOBAL.isCurrentUser(user.getEmail()))
					{
						row = li.inflate(R.layout.list_row, null);	
						removeEventButton = (Button) row.findViewById(R.id.removeButtonLI);
						removeEventButton.setId(id);		
					}
					else //user does not have ability to remove events
						row = li.inflate(R.layout.list_row_nobutton, null);
					
					nameTextView =  (TextView)row.findViewById(R.id.nameTextViewLI);

					if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
						nameTextView.setText(e.getName());//future get date too?
					else
						nameTextView.setText(e.getName() + "\n(" + e.getNumUsers() + " confirmed / " + e.getMinPart() + " required)");
				}
				else
				{
					row = li.inflate(R.layout.list_row_acceptdecline, null);
					nameTextView =  (TextView)row.findViewById(R.id.emailTextView);
					nameTextView.setText(e.getName() + "\n(" + e.getNumUsers() + " confirmed / " + e.getMinPart() + " required)");
				}
				//setting ids to the id of the group for button functionality
				nameTextView.setId(id);
				row.setId(id);
				//adding row to view
				listLayout.addView(row);
			}
		}
		else
		{
			//no event invites were found, show sadguy image / text
			row = li.inflate(R.layout.list_item_sadguy, null);
			TextView sadTextView = (TextView) row.findViewById(R.id.sadGuyTextView);
			//Set the sad guy text.
			sadTextView.setText(sadGuyText);
			listLayout.addView(row);
		}	
	}
			
	// based on content type, gets the corresponding role
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
		if (inEntity) //user is in the group or event, grab their role
		{
			if (CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()))
				new getRoleTask().execute("http://68.59.162.183/android_connect/check_role_group.php", Integer.toString(group.getID()));
			else
				new getRoleTask().execute("http://68.59.162.183/android_connect/check_role_event.php", Integer.toString(event.getID()));
		}
	}
	
	//DEFAULT METHODS BELOW
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		//clearing any previous views populated, for refresh
		listLayout = ((LinearLayout)findViewById(R.id.listLayout));
		//INSTANTIATIONS
		EXTRAS =  getIntent().getExtras();
		CONTENT = EXTRAS.getString("content");
		listLayout = ((LinearLayout)findViewById(R.id.listLayout));
		li = getLayoutInflater();	
		addNew = (Button)findViewById(R.id.addNewButtonLiA);
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		load();
	}	

	//ONCLICK METHODS BELOW
	public void onClick(View view)
	{
		super.onClick(view);
		View parent = (View)view.getParent();
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
			System.out.println("ID IS SET TO IN PAST EVENT!! " + bufferID);
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
	public void bottomButton(View view)
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
			GLOBAL.getCurrentUser().fetchGroups();
		}
		if (user != null)
			intent.putExtra("email", user.getEmail());
		if (event != null)
			intent.putExtra("e_id", event.getID());
		if (group != null){
			intent.putExtra("g_id", group.getID());
			GLOBAL.setGroupBuffer(group);
		}
		startActivity(intent);	
	}
	//Starts a USER/GROUP/EVENT profile
	public void startProfileActivity(View view)
	{
		loadDialog.show();
		int id = view.getId();		
		Intent intent = new Intent(this, GroupProfileActivity.class);
		if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()) || CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()) )
		{
			intent.putExtra("g_id", id);
			intent.putExtra("email", user.getEmail());
			Group g = new Group(id);
			g.fetchGroupInfo();
			g.fetchMembers();
			GLOBAL.setGroupBuffer(g);
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_INVITES.toString()))
		{
			intent = new Intent(this, EventProfileActivity.class);
			intent.putExtra("e_id", id);
			intent.putExtra("email", user.getEmail());
			Event e = new Event(id);
			e.fetchEventInfo();
			e.fetchParticipants();
			GLOBAL.setEventBuffer(e);
		}
		else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()) || CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_ATTENDING.toString()) || CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()) || CONTENT.equals(CONTENT_TYPE.SELECT_FRIEND.toString()))
		{
			intent = new Intent(this, UserProfileActivity.class);
			String friendEmail = users.get(id).getEmail();
			User u = new User(friendEmail);
			if (!CONTENT.equals(CONTENT_TYPE.SELECT_FRIEND.toString()))
			{
				u.fetchEventsUpcoming();
				u.fetchEventsPast();
				u.fetchUserInfo();
				u.fetchFriends();
				u.fetchGroups();
				if (!GLOBAL.isCurrentUser(friendEmail))
					GLOBAL.setUserBuffer(u);
				else
					GLOBAL.setCurrentUser(u); //reloading user
			}
			else
			{
				intent = new Intent(this, MessagesActivity.class);
				intent.putExtra("name", users.get(id).getName());
			}
			intent.putExtra("email", friendEmail);
		}		
		startActivity(intent);	
	}

	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
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
	    		user.fetchEventInvites();
	    		user.fetchEventsPending();
    		}
    	}
    	else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
    	{
    		//nothing yet
    		//user.fetchEventsInvites();
    		user.fetchEventsPast();
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
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
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
					if (!ROLE.equals("U"))
						if (CONTENT.equals(CONTENT_TYPE.EVENTS_ATTENDING.toString()))
						{
							if (!event.getEventState().equals("Ended"))
								addNew.setVisibility(View.VISIBLE);
						}
						else
							addNew.setVisibility(View.VISIBLE);
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
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	private class performActionTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			// Add your data
			if (CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()) || CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
			{
				//group invites or current groups, add email and gid
				nameValuePairs.add(new BasicNameValuePair("email", urls[1]));
				nameValuePairs.add(new BasicNameValuePair("gid",urls[2]));
			}
			else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()) || CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
			{
				//friend requests or remove friend, both pass sender and receiver
				nameValuePairs.add(new BasicNameValuePair("sender", urls[1]));
				nameValuePairs.add(new BasicNameValuePair("receiver", user.getEmail()));
			}
			else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString())|| CONTENT.equals(CONTENT_TYPE.EVENTS_INVITES.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()))
			{
				//events pending past, upcoming or invites, all need email and eid
				nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
				nameValuePairs.add(new BasicNameValuePair("eid", urls[1]));
			}
			//calling readJSONFeed in Global, continues below in onPostExecute
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
					String message = jsonObject.getString("message");
					Context context = getApplicationContext();
					if (CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()))
					{
						user.removeGroupInvite(bufferID);
						//since leave_group and decline group invite call same php
						if (!message.equals("Group invite accepted!"))
							message = "Group invite declined!";
					}
					else if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
					{
						user.removeGroup(bufferID);
						user.fetchGroups();
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
						user.fetchEventInvites();
						//since leave_event and decline event call same php
						if (!message.equals("Event invite accepted!"))
							message = "Event invite declined!";
					}
					else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
					{
						user.removeEventPast(bufferID);
						message = "Past event removed!";
						user.fetchEventsPast();
					}
					Toast toast = GLOBAL.getToast(context, message);
					toast.show();
					//reset values, looking to move these 
					PANDABUFFER = "";
					bufferID = -1;
					//removing all friend requests for refresh
					listLayout.removeAllViews();
					
					//CALLING CORRESPONDING METHOD TO REPOPULATE
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
			} 
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
}
