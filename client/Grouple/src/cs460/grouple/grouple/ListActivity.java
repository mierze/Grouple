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
	    EVENTS_UPCOMING, EVENTS_PENDING, EVENTS_PAST;    
	}
	
	
	//CLASS-WIDE DECLARATIONS
	int clickedRemoveID;
	BroadcastReceiver broadcastReceiver;
	private User user; //user whose current groups displayed
	private Group group;
	private static Bundle EXTRAS; //extras passed in from the activity that called ListActivity
	private static String CONTENT; //type of content to display in list, passed in from other activities
	private static LinearLayout listLayout; //layout for list activity (scrollable layout to inflate into)
	static Global GLOBAL;// = 
	private static LayoutInflater li;
	private ArrayList<String> friendsEmailList = new ArrayList<String>();//test
	private String PANDABUFFER = "";

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
		System.out.println("Load 1");
	
		//INSTANTIATIONS
		GLOBAL = ((Global) getApplicationContext());
		EXTRAS =  getIntent().getExtras();
	
		
		CONTENT = EXTRAS.getString("CONTENT");
		listLayout = ((LinearLayout)findViewById(R.id.listLayout));
		li = getLayoutInflater();
		
		String actionBarTitle = "";
		Button addNew = (Button)findViewById(R.id.addNewButtonLiA);
		
		System.out.println("Load 2");
		//GRABBING A USER
		if (GLOBAL.isCurrentUser(EXTRAS.getString("EMAIL")))
		{
			System.out.println("Load 3 loading currentuser: " + EXTRAS.getString("EMAIL"));
			user = GLOBAL.getCurrentUser();
		}
		else if (GLOBAL.getUserBuffer() != null && GLOBAL.getUserBuffer().getEmail().equals(EXTRAS.getString("EMAIL")))
		{
			System.out.println("Load 3 loading user buffer: " + EXTRAS.getString("EMAIL"));
			user = GLOBAL.getUserBuffer();
		}
		else
		{
			System.out.println("Load 3 loading user: " + EXTRAS.getString("EMAIL"));
			user = GLOBAL.loadUser(EXTRAS.getString("EMAIL"));
			System.out.println(CONTENT_TYPE.FRIENDS_REQUESTS + " " + CONTENT + " comp: " + CONTENT_TYPE.FRIENDS_REQUESTS.toString().equals(CONTENT));
		}

		System.out.println("Load 4, at this time CONTENT is " + CONTENT);
		//CALL APPROPRIATE METHODS
		if (CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()) || CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
		{
			if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
				actionBarTitle = user.getFirstName() + "'s Friends";
			else
			{
				group = GLOBAL.loadGroup(EXTRAS.getInt("GID"));
				actionBarTitle="Group Members";
			}
			populateUsers();
		}
		else 
		{
			System.out.println("Load 4.5 now in else no button stuff : " + user.getFirstName());
			if (addNew != null)addNew.setVisibility(View.GONE);
			if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()))
			{
				if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
					{
					actionBarTitle = user.getFirstName() + "'s Groups";
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
				if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()))
				{
					actionBarTitle = user.getFirstName() + "'s Pending Events";
		
				}
				else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
				{
					System.out.println("Load 5 now setting action bar title to : " + user.getFirstName());
					actionBarTitle = user.getFirstName() + "'s Friend Requests";
			
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
		
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
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

	/*
	 * Uses the mapping of ids and group names to display the users current groups
	 * Groups will be loaded into the Group class when profile is viewed,
	 * 		this could be changed to immediately if we can get the User class communicating with GLOBAL.
	 * 		same goes for friends, friend requests and group invites
	 */
	private void populateEntities()
	{
		Map<Integer, String> entities = null;
		String sadGuyText = "";
		if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
		{
			//grabbing the users groups
			entities = user.getGroups();
			sadGuyText = "You do not have any groups.";
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()))
		{
			entities = user.getEventsUpcoming();
			sadGuyText = "You do not have events upcoming.";
		}

		if (entities != null && entities.size() > 0)
		{
			int i = 0;
			
			for (Map.Entry<Integer, String> entry : entities.entrySet()) {
				//Group group = GLOBAL.loadGroup(id);
				GridLayout rowView;
				
		
					rowView = (GridLayout) li.inflate(
							R.layout.list_row, null);
	
					
				Button leaveGroupButton = (Button)rowView.findViewById(R.id.removeButtonLI);
				//if mod true this, if not someting else
				if (GLOBAL.isCurrentUser(user.getEmail()))
					leaveGroupButton.setId(entry.getKey());
				else
					leaveGroupButton.setVisibility(View.GONE);
				
				// Grab the buttons and set their IDs. Their IDs
				// will fall inline with the array 'groupsNameList'.
				Button groupNameButton = (Button) rowView
						.findViewById(R.id.nameButtonLI);
				
				groupNameButton.setText(entry.getValue());
				
				//setting ids to the id of the group for button functionality
				groupNameButton.setId(entry.getKey());
				rowView.setId(entry.getKey());
				
				//adding row to view
				listLayout.addView(rowView);

				//incrementing index
				i++;
			}
		}
		else
		{
			// The user has no groups so display the sad guy image.
			View row = li.inflate(R.layout.listitem_sadguy, null);
			((TextView) row.findViewById(R.id.sadGuyTextView))
				.setText(sadGuyText);

			listLayout.addView(row);
		}	
	}
	
	public void populateUsers()
	{	
		Map<String, String> users;
		if  (CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()))
		{
			//For now, display the invite members button if you are in group
			//need to check admin level
			users = group.getUsers();	
			if (!users.containsKey(GLOBAL.getCurrentUser().getEmail()))
			{
				Button addNew = (Button)findViewById(R.id.addNewButtonLiA);
				addNew.setVisibility(View.GONE);
			}
			
		}
		else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))//to do make else ifs." +
		{
			users = user.getUsers();
			Button addNew = (Button)findViewById(R.id.addNewButtonLiA);
			addNew.setVisibility(View.GONE);
		}
		else
		{
			users = user.getUsers();
		}
		if (users != null && !users.isEmpty())
		{
			//Bundle EXTRAS = intent.getExtras();
			// looping thru json and adding to an array
			Iterator it = users.entrySet().iterator();
			int index = 0;
			while (it.hasNext()) {
				 Map.Entry pair = (Map.Entry)it.next();
				 String email = (String) pair.getKey();
				 String fullName = (String) pair.getValue();

				 GridLayout rowView;
				 friendsEmailList.add(index, email);
				 /*
				 * If you are the mod, add the friend button and the
				 * remove button. If you aren't the mod, then add
				 * the friend of a friend button without the remove
				 * button. In this instance, mod means whether or
				 * not these or your friends. You don't want the
				 * option to delete a friend's friend.
				 */

				 if (user != null && GLOBAL.isCurrentUser(user.getEmail()) && !CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()))
				 {
					 rowView = (GridLayout) li.inflate(
							 R.layout.list_row, null);
					 Button removeFriendButton = (Button) rowView
							.findViewById(R.id.removeButtonLI);
					 removeFriendButton.setId(index);
				 } 
				 else
				 {
					rowView = (GridLayout) li.inflate(
							R.layout.list_row_nobutton, null);
				 }
				 // Add the information to the friendnamebutton and
				 // add it to the next row.
				 Button friendNameButton = (Button) rowView
						 .findViewById(R.id.nameButtonLI);

				 friendNameButton.setText(fullName);
				 /*
				 * Setting the ID to i makes it so we can use i to
				 * figure out the friend's email. Important for
				 * finding a friend's profile.
				 */
				 friendNameButton.setId(index);
				 rowView.setId(index);
				 listLayout.addView(rowView);
				 index++;
			 }
		}
		else
		{		
	
			View row = li.inflate(R.layout.listitem_sadguy, null);
			// The user has no friend's so display the sad guy image.
			if (CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()))
				((TextView) row.findViewById(R.id.sadGuyTextView)).setText("There are no members in this group.");
			else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
				((TextView) row.findViewById(R.id.sadGuyTextView)).setText("You have no friends.");
			
			listLayout.addView(row);
		}
	}
	
	//
	private void populateAcceptDecline()
	{
		Map<Integer, String> listItems = null;
		ArrayList<String> friendRequests = null;
		Map<Integer, Boolean> eventsAccepted = null;
		String sadGuyText = "";
		
		/*
		 * Checking which CONTENT we need to inflate
		 */
		if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()))
		{
			listItems = user.getEventsPending();
			eventsAccepted = user.getEventsAccepted();
			sadGuyText = "You do not have any pending events.";
		}
		else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
		{
			System.out.println("GRABBING FRIEND REQUESTS");
			friendRequests = user.getFriendRequests();
			sadGuyText = "You do not have any friend requests.";
		}
		else if (CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()))
		{
			listItems = user.getGroupInvites();
			sadGuyText = "You do not have any group invites.";
		}
		
		
		//FOR FRIEND REQUESTS
		if (CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()) && friendRequests != null && friendRequests.size() > 0)
		{
			// looping thru array and inflating listitems to the
						// friend requests list
				for (int i = 0; i < friendRequests.size(); i++)
				{
					GridLayout row = (GridLayout) li.inflate(R.layout.list_row_acceptdecline, null);
					// Setting text of each friend request to the email
					// of the sender
					if (friendRequests.get(i) != null)
						((TextView) row.findViewById(R.id.nameButtonLI)).setText(friendRequests.get(i));
					listLayout.addView(row);
				}
			
		}
		//FOR EVENTS PENDING
		//array list needs to have group names, maybe the sender names and needs to have group ids	
		else if(CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()) && listItems != null && listItems.size() > 0 )
		{	
			// looping thru the map
			for (Map.Entry<Integer, String> entry : listItems.entrySet())
			{
				GridLayout row;
				TextView rowText;
				if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()) && eventsAccepted.get(entry.getKey()))
				{
					//REDUNDANT PANDA TODO
					row = (GridLayout) li.inflate(R.layout.list_row_nobutton, null);
					rowText = (TextView)row.findViewById(R.id.nameButtonLI);
				}
				else
				{
					row = (GridLayout) li.inflate(R.layout.list_row_acceptdecline, null);
					rowText = (TextView)row.findViewById(R.id.nameButtonLI);
				}
				row.setId(entry.getKey());
				rowText.setId(entry.getKey());
				rowText.setText(entry.getValue());
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
			
	
	public void onClick(View view)
	{
		View parent = (View)view.getParent();
		TextView nameText = (TextView) parent
				.findViewById(R.id.nameButtonLI);
		switch (view.getId())
		{
		case R.id.declineButton:
			if (CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()))
			{
			//bufferID = parent.getId();
			//new getDeclineGroupTask().execute("http://68.59.162.183/android_connect/leave_group.php?email="+user.getEmail()+"&gid="+parent.getId());
			}
			else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
			{
				PANDABUFFER = nameText.getText().toString(); //PANDA
				new getAcceptDeclineTask()
						.execute("http://68.59.162.183/android_connect/decline_friend_request.php", PANDABUFFER);
			}
			break;
		case R.id.acceptButton:
			if (CONTENT.equals(CONTENT_TYPE.GROUPS_INVITES.toString()))
			{
			View parent2 = (View) view.getParent();
			//bufferID = parent2.getId();
			//new getAcceptGroupTask().execute("http://68.59.162.183/android_connect/accept_group_invite.php",user.getEmail(),Integer.toString(parent2.getId()));

			
			//populateGroupInvites();
			}
			else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_REQUESTS.toString()))
			{
				PANDABUFFER = nameText.getText().toString();
				new getAcceptDeclineTask()
						.execute("http://68.59.162.183/android_connect/accept_friend_request.php", PANDABUFFER);
			}
			break;
		}
	}


	
	
	/*
	 * Code for accepting a friend request. On success, we remove the friend
	 * request and refresh the friend requests activity. We also confirm the
	 * friendship in the database.
	 */
	private class getAcceptDeclineTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String receiver = user.getEmail();
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			//possibly check that all gorup invtes / events follow suit
			nameValuePairs.add(new BasicNameValuePair("sender", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("receiver", receiver));
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
					System.out.println("success!");
					//removing all friend requests for refresh
					listLayout.removeAllViews();
					
					Context context = getApplicationContext();
					Toast toast = Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT);
					toast.show();
					//repopulate view
					GLOBAL.loadUser(user.getEmail());
					user.removeFriendRequest(PANDABUFFER);
					PANDABUFFER = "";

					//user.removeFriendRequest(email)
					populateAcceptDecline();

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
				clickedRemoveID = view.getId();

				new AlertDialog.Builder(this)
						.setMessage("Are you sure you want to leave this group?")
						.setCancelable(true)
						.setPositiveButton("Yes", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int id)
							{
								System.out.println("Leave grp email / id: " + user.getEmail() + " " + clickedRemoveID);
								new leaveGroupTask()
										.execute(
												"http://68.59.162.183/android_connect/leave_group.php?email=" + user.getEmail() + "&gid=" + clickedRemoveID);
								//user.removeGroup(clickedRemoveID);
								clickedRemoveID = -1; //reset
							}
						}).setNegativeButton("Cancel", null).show();
			}
			else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
			{
				final int index = view.getId(); //position in friendArray
				final String friendEmail = friendsEmailList.get(index); //friend to remove
				PANDABUFFER = friendEmail;//PANDA
				final String email = user.getEmail(); //user email

				new AlertDialog.Builder(this)
						.setMessage("Are you sure you want to remove that friend?")
						.setCancelable(true)
						.setPositiveButton("Yes", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int id)
							{

								new deleteFriendTask()
										.execute(
												"http://68.59.162.183/android_connect/delete_friend.php",
												email, friendEmail);
								
							}
						}).setNegativeButton("Cancel", null).show();
			}
			if (user != null)
			{
				GLOBAL.loadUser(user.getEmail());
				
			}
			if (group != null)
			{
				GLOBAL.loadGroup(group.getID());
			}
		}

		/*
		 * Code for deleting a friend.
		 */
		private class deleteFriendTask extends AsyncTask<String, Void, String>
		{
			@Override
			protected String doInBackground(String... urls)
			{
				// urls 1, 2 are the emails
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("sender", urls[1]));
				nameValuePairs.add(new BasicNameValuePair("receiver", urls[2]));
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
						// success: friend has been deleted
						GLOBAL.loadUser(user.getEmail());
						// removing all of the views
						listLayout.removeAllViews();
						user.removeUser(PANDABUFFER);
						// calling getFriends to repopulate view
						populateUsers();
						Log.d("dbmsg", jsonObject.getString("message"));
					} else if (jsonObject.getString("success").toString()
							.equals("2"))
					{
						// friend was not found in database
						Log.d("dbmsg", jsonObject.getString("message"));
					} else
					{
						// sql error
						Log.d("dbmsg", jsonObject.getString("message"));
					}

				} catch (Exception e)
				{
					Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
				}
			}
		}

		public void startInviteActivity(View view)
		{
			//FOR NOW
			
			Intent intent = null;
			if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
			{
				intent = new Intent(this, FriendAddActivity.class);
			}
			else if (CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()))
			{
				intent = new Intent(this, InviteActivity.class);

				intent.putExtra("GID", group.getID());
			}
			intent.putExtra("EMAIL", user.getEmail());
			
			startActivity(intent);
			
		}

		
		private class leaveGroupTask extends AsyncTask<String, Void, String>
		{
			@Override
			protected String doInBackground(String... urls)
			{
				
				return GLOBAL.readJSONFeed(urls[0], null);
			}

			@Override
			protected void onPostExecute(String result)
			{
				try
				{
					JSONObject jsonObject = new JSONObject(result);

					if (jsonObject.getString("success").toString().equals("1"))
					{
						// success: group has been deleted

						// removing all of the views
						listLayout.removeAllViews();
						
						// Refresh the page to show the removal of the group.
						populateEntities();
						
						Log.d("dbmsg", jsonObject.getString("message"));
					} 
					else if (jsonObject.getString("success").toString().equals("2"))
					{
						// group was not found in database. Need to throw message

						// alerting the user.
						Log.d("dbmsg", jsonObject.getString("message"));
					} 
					else
					{
						// sql error
						Log.d("dbmsg", jsonObject.getString("message"));
					}

				} catch (Exception e)
				{
					Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
				}
			}
		}

		
		// When you click on a friend, this loads up the friend's profile.
		public void startProfileActivity(View view)
				throws InterruptedException
		{
			
			Intent intent = new Intent(this, ProfileActivity.class);
			if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
			{
				System.out.println("Loading group gid: " + view.getId());
				intent.putExtra("GID", view.getId());
				intent.putExtra("EMAIL", user.getEmail());
				intent.putExtra("CONTENT", "GROUP");
				Group g = GLOBAL.loadGroup(view.getId());
			
				if (g != null)
					GLOBAL.setGroupBuffer(g);
				
			}
			else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
			{
				System.out.println("Loading event, eid: " + view.getId());
				intent.putExtra("EID", view.getId());
				intent.putExtra("EMAIL", user.getEmail());
				intent.putExtra("CONTENT", "EVENT");
				Event e = GLOBAL.loadEvent(view.getId());
			
				if (e != null)
					GLOBAL.setEventBuffer(e);
			}
			else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()) || CONTENT.equals(CONTENT_TYPE.GROUPS_MEMBERS.toString()))
			{
				// need to get access to this friends email
				// launches friendProfileActivity and loads CONTENT based on that email
				int id = view.getId();
				// got the id, now we need to grab the users email and somehow pass it
				// to the activity
	
				String friendEmail = friendsEmailList.get(id);

				GLOBAL.loadUser(friendEmail); //reloading user
	
				intent.putExtra("EMAIL", friendEmail);
				intent.putExtra("CONTENT", "USER");	
			}

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
