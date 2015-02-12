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
import cs460.grouple.grouple.User.getUserInfoTask;
import android.app.AlertDialog;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
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
		FRIENDS_CURRENT, FRIENDS_REQUESTS,
		GROUPS_CURRENT, GROUPS_INVITES, GROUPS_MEMBERS,
	    EVENTS_UPCOMING, EVENTS_PENDING, EVENTS_PAST, EVENTS_ATTENDING, EVENTS_INVITES;    
	}
	
	
	//CLASS-WIDE DECLARATIONS
	private BroadcastReceiver broadcastReceiver;
	private User user; //user whose current groups displayed
	private Group group;
	private Event event;
	private Bundle EXTRAS; //extras passed in from the activity that called ListActivity
	private String CONTENT; //type of content to display in list, passed in from other activities
	private LinearLayout listLayout; //layout for list activity (scrollable layout to inflate into)
	private Global GLOBAL;// = 
	private static LayoutInflater li;
	private ArrayList<String> friendsEmailList = new ArrayList<String>();//test
	private String PANDABUFFER = ""; //same
	private int bufferID; //same as below: could alternatively have json return the values instead of saving here
	
	
	//TESTING
	private ArrayList<User> users;
	private ArrayList<Group> groups;
	private ArrayList<Event> events;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		load();
	}

	/* loading actionbar */
	public void initActionBar(String actionBarTitle)
	{
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText(actionBarTitle); //PANDA
		//ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);
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
		Button addNew = (Button)findViewById(R.id.addNewButtonLiA); //TODO: hide / unhide
		
		//for now to test
		//GRABBING A USER
		if (EXTRAS.getString("EMAIL") != null)
			if (GLOBAL.isCurrentUser(EXTRAS.getString("EMAIL")))
			{
				user = GLOBAL.getCurrentUser();
			}
			else if (GLOBAL.getUserBuffer() != null && GLOBAL.getUserBuffer().getEmail().equals(EXTRAS.getString("EMAIL")))
			{
				user = GLOBAL.getUserBuffer();
				System.out.println("Load 4");
			}

		//CALL APPROPRIATE METHODS
		//CONTENT_TYPE -> POPULATEUSERS
		if (CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()) || CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()) 
				||  CONTENT.equals(CONTENT_TYPE.EVENTS_ATTENDING.toString()) || CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
		{
			if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
				actionBarTitle = user.getFirstName() + "'s Friends";
			else if (CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()))
			{
				group = GLOBAL.getGroupBuffer();
				actionBarTitle="Group Members";
			}
			else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
			{
				if (addNew != null)addNew.setVisibility(View.GONE);
				System.out.println("Load 5 now setting action bar title to : " + user.getFirstName());
				actionBarTitle = user.getFirstName() + "'s Friend Requests";
			}
			else 
			{
				event = GLOBAL.getEventBuffer();
				actionBarTitle = "Attending " + event.getName();
			}
			populateUsers();
		}
		//CONTENT_TYPE -> POPULATEGROUPS
		
		
		
		//CONTENT_TYPE -> POPULATEEVENTS
		else 
		{
			System.out.println("Load 4.5 now in else no button stuff : " + user.getFirstName());
			if (addNew != null)addNew.setVisibility(View.GONE);
			if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()))
			{
				if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
				{
					actionBarTitle = user.getFirstName() + "'s Groups";
				}
				else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()))
				{
					actionBarTitle = user.getFirstName() + "'s Pending Events";
				}
				else 
				{
					actionBarTitle = user.getFirstName() + "'s Upcoming Events";
				}
				populateEntities();
			}
			else
			{
				System.out.println("Load 4.5 now in else acceptdecline stuff : " + user.getFirstName());
		
				if (CONTENT.equals(CONTENT_TYPE.EVENTS_INVITES.toString()))
				{
					actionBarTitle = user.getFirstName() + "'s Event Invites";
				}
				
				else if (CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()))
				{
					actionBarTitle = user.getFirstName() + "'s Group Invites";
				}
				System.out.println("Load 6, about to call pop");
				populateAcceptDecline();	
			}
		}
		
		System.out.println("Load 7");
		initActionBar(actionBarTitle);
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

	//SOON TO BE POPULATE GROUPS
	private void populateEntities()
	{
		String sadGuyText = "";
		ArrayList<Event> eventList = null;
		ArrayList<Group> groupList = null;

		
		if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()))
		{
			eventList = user.getEventsPending();
			sadGuyText = "You do not have any pending events.";
		}
		if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()))
		{	
			eventList = user.getEventsUpcoming();
			sadGuyText = "You do not have any upcoming events.";
		}
		else if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
		{
			groupList = user.getGroups();
			sadGuyText = "You are not in any groups.";
		}

		if (eventList != null && eventList.size() > 0)
		{
			for (Event e : eventList) 
			{
				//Group group = GLOBAL.loadGroup(id);
				Button nameButton = null;
				GridLayout row = (GridLayout) li.inflate(R.layout.list_row, null);
				//if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()))
				//{
					row = (GridLayout) li.inflate(R.layout.list_row_nobutton, null);
					nameButton =  (Button)row.findViewById(R.id.nameButtonLI);
				//}
				
				
				// Grab the buttons and set their IDs. Their IDs
				// will fall inline with the array 'groupsNameList'.
				if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()))
				{
					nameButton.setText(e.getName());//future get date too?
				}
				else
				{
					nameButton.setText(e.getName() + "\t(" + e.getNumUsers() + ")/" + e.getMinPart() + " attending");
				}
				
				//setting ids to the id of the group for button functionality
				nameButton.setId(e.getID());
				row.setId(e.getID());
				
				//adding row to view
				listLayout.addView(row);
			}
		}
		else if (groupList != null && groupList.size() > 0)
		{
			for (Group g : groupList)
			{
				//Group group = GLOBAL.loadGroup(id);
				GridLayout rowView = (GridLayout) li.inflate(R.layout.list_row, null);
				Button leaveGroupButton = (Button)rowView.findViewById(R.id.removeButtonLI);
				//if mod true this, if not someting else
				if (GLOBAL.isCurrentUser(user.getEmail()))
					leaveGroupButton.setId(g.getID());
				else
					leaveGroupButton.setVisibility(View.GONE);
				
				// Grab the buttons and set their IDs. Their IDs
				Button nameButton = (Button) rowView.findViewById(R.id.nameButtonLI);
				nameButton.setText(g.getName());
				//setting ids to the id of the group for button functionality
				nameButton.setId(g.getID());
				rowView.setId(g.getID());
				//adding row to view
				listLayout.addView(rowView);
			}
		}
		else
		{
			// The user has no groups so display the sad guy
			View row = li.inflate(R.layout.listitem_sadguy, null);
			((TextView) row.findViewById(R.id.sadGuyTextView)).setText(sadGuyText);
			listLayout.addView(row);
		}	
	}
	
	//METHOD FOR POPULATING A LIST OF USERS
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
			boolean currUserInGroup = false;
			if (users != null)
				for (User u : users)
					if (u.getEmail().equals(GLOBAL.getCurrentUser().getEmail()))
						currUserInGroup = true;
				
			if (!currUserInGroup)
			{
				Button addNew = (Button)findViewById(R.id.addNewButtonLiA);
				addNew.setVisibility(View.GONE);
			}
				
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
	
	//SOON TO BE POPULATE EVENTS
	private void populateAcceptDecline()
	{
		ArrayList<Event> eventList = null;//TODO: make populateEvents, populateUsers, populateGroups
		Map<Integer, String> listItems = null;
		String sadGuyText = "";
		
		/*
		 * Checking which CONTENT we need to inflate
		 */
		if (CONTENT.equals(CONTENT_TYPE.EVENTS_INVITES.toString()))
		{
			eventList = user.getEventsInvites();
			sadGuyText = "You do not have any event invites.";
		}
		else if (CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()))
		{
			listItems = user.getGroupInvites();
			sadGuyText = "You do not have any group invites.";
		}
		if((CONTENT.equals(CONTENT_TYPE.EVENTS_INVITES.toString()) && eventList != null && !eventList.isEmpty()))
		{	
			for (Event e : eventList) 
			{
				//Group group = GLOBAL.loadGroup(id);
				Button nameButton = null;
				GridLayout row = (GridLayout) li.inflate(R.layout.list_row, null);
				//if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()))
				//{
				row = (GridLayout) li.inflate(R.layout.list_row_acceptdecline, null);
				nameButton =  (Button)row.findViewById(R.id.nameButtonAD);
				//}
	
				nameButton.setText(e.getName() + "\t(" + e.getNumUsers() + ")/" + e.getMinPart() + " attending");
				
				
				//setting ids to the id of the group for button functionality
				nameButton.setId(e.getID());
				row.setId(e.getID());
				
				//adding row to view
				listLayout.addView(row);
			}
		}
		else if  (CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()) && listItems != null && !listItems.isEmpty())
		{
			// looping thru the map
			for (Map.Entry<Integer, String> entry : listItems.entrySet())
			{
				GridLayout row;
				Button nameButton;
			
				row = (GridLayout) li.inflate(R.layout.list_row_acceptdecline, null);
				nameButton =  (Button)row.findViewById(R.id.nameButtonAD);
				
				row.setId(entry.getKey());
				nameButton.setId(entry.getKey());
				nameButton.setText(entry.getValue());
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
			
	/*
	 * onClick method for accept and declines
	 */
	public void onClick(View view)
	{
		GridLayout parent = (GridLayout)view.getParent();
		Button nameText = (Button) parent
				.findViewById(R.id.nameButtonAD);
		if (nameText == null) System.out.println("HELL YEAH IT NULL");
		switch (view.getId())
		{
		case R.id.declineButton:
			if (CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()))
			{
				bufferID = parent.getId();
				new getAcceptDeclineTask().execute("http://68.59.162.183/android_connect/leave_group.php", user.getEmail(), Integer.toString(parent.getId()));
			}
			else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
			{
				PANDABUFFER = users.get(parent.getId()).getEmail();
				new getAcceptDeclineTask().execute("http://68.59.162.183/android_connect/decline_friend_request.php", PANDABUFFER);
			}
			else if (CONTENT.equals(CONTENT_TYPE.EVENTS_INVITES.toString()))
			{
				bufferID = parent.getId(); //PANDA
				new getAcceptDeclineTask().execute("http://68.59.162.183/android_connect/leave_event.php", Integer.toString(bufferID));
			}
			break;
		case R.id.acceptButton:
			if (CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()))
			{
				bufferID = parent.getId();
				new getAcceptDeclineTask().execute("http://68.59.162.183/android_connect/accept_group_invite.php",user.getEmail(),Integer.toString(bufferID));
			}
			else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
			{
			
				PANDABUFFER = users.get(parent.getId()).getEmail();
				new getAcceptDeclineTask().execute("http://68.59.162.183/android_connect/accept_friend_request.php", PANDABUFFER);
			}
			else if (CONTENT.equals(CONTENT_TYPE.EVENTS_INVITES.toString()))
			{
				bufferID = parent.getId();
				System.out.println("Accepting event invite, eid: " + bufferID);
				new getAcceptDeclineTask().execute("http://68.59.162.183/android_connect/accept_event_invite.php", Integer.toString(bufferID));
			}
			break;
		}
	}


	private class getAcceptDeclineTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
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
			else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_INVITES.toString()))
			{
				//code for this
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
					System.out.println("ACCEPT DECLINE POST 2");
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
						System.out.println("ACCEPT DECLINE POST 3");
						user.removeFriendRequest(PANDABUFFER);
						System.out.println("ACCEPT DECLINE POST 4");
						//user.fetchFriendRequests();
						System.out.println("ACCEPT DECLINE POST 5");
					}
					else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
					{
						System.out.println("NOW IN ACCEPT DECLINE ONPOST PANDABUFFER IS " + PANDABUFFER);
						//sYSTEM.
						user.removeUser(PANDABUFFER);
						System.out.println("NOW IN ACCEPT DECLINE ONPOST2");
						//user.fetchFriends();
					
					}
					else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()))
					{
						user.removeEventPending(bufferID);
						//user.fetchEventsPending();
				
					}
					else if (CONTENT.equals(CONTENT_TYPE.EVENTS_INVITES.toString()))
					{
						user.removeEventInvite(bufferID);
						//user.fetchEventsInvites();
			
						if (!message.equals("Event invite accepted!"))
							message = "Event invite declined!";
					}
					System.out.println("NOW IN ACCEPT DECLINE ONPOST3");
					
					/*if (GLOBAL.isCurrentUser(user.getEmail()))
						GLOBAL.setCurrentUser(user);
					else
						GLOBAL.setUserBuffer(user);
					 */
					Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
					toast.show();
					System.out.println("NOW IN ACCEPT DECLINE ONPOST4");

					PANDABUFFER = "";
					bufferID = -1;

					///refreshing views
					if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
						populateUsers();
					else if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()))
						populateEntities();
					else 
						populateAcceptDecline();
					System.out.println("NOW IN ACCEPT DECLINE ONPOST5");
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
								new getAcceptDeclineTask().execute("http://68.59.162.183/android_connect/leave_group.php", user.getEmail(), Integer.toString(bufferID));
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

								new getAcceptDeclineTask()
										.execute(
												"http://68.59.162.183/android_connect/delete_friend.php",
												friendEmail);
								
							}
						}).setNegativeButton("Cancel", null).show();
			}
		}

		public void startInviteActivity(View view)
		{
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
			if (user != null)
			{
				intent.putExtra("EMAIL", user.getEmail());
			}
			
			if (group != null){
				intent.putExtra("GID", group.getID());
				GLOBAL.setGroupBuffer(group);
			}
			startActivity(intent);	
		}

		// When you click on a friend, this loads up the friend's profile.
		public void startProfileActivity(View view)
				throws InterruptedException
		{
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
				//TODO use the event object instead eventually
			}
			else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()) || CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_ATTENDING.toString()) || CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
			{
				String friendEmail;
				System.out.println("Loading user profile, id: " + id);
				if (CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
				{
					friendEmail = friendsEmailList.get(id);
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

	
				
				System.out.println("Friend email is " +  friendEmail);
				
				
				intent.putExtra("EMAIL", friendEmail);
				System.out.println("CURRENTLY SETTING CONTENT");
				intent.putExtra("CONTENT", "USER");	
			}
			startActivity(intent);
		}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent e)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	//refresh pertinent info
			/*FRIENDS_CURRENT, FRIENDS_REQUESTS,
			GROUPS_CURRENT, GROUPS_INVITES, GROUPS_MEMBERS,
		    EVENTS_UPCOMING, EVENTS_PENDING, EVENTS_PAST, EVENTS_ATTENDING, EVENTS_INVITES;    */
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
