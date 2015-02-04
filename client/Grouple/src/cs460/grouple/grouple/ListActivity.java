package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
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

/*
 * GroupsCurrentActivity displays a list of all groups a member is a part of.
 */
public class ListActivity extends ActionBarActivity
{

	int clickedRemoveID;
	BroadcastReceiver broadcastReceiver;
	User user; //user whose current groups displayed
	Group group;
	private ArrayList<String> friendsEmailList = new ArrayList<String>();//test

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

	/* loading in everything for current friends */
	public void load()
	{
		Global global = ((Global) getApplicationContext());
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		
		if (global.isCurrentUser(extras.getString("email")))
		{
			user = global.getCurrentUser();
		}
		else if (global.getUserBuffer() != null && global.getUserBuffer().getEmail().equals(extras.getString("email")))
		{
			user = global.getUserBuffer();
		}
		else
		{
			user = global.loadUser(extras.getString("email"));
		}
		
		Button addNew = (Button)findViewById(R.id.addNewButtonLiA);
		
		/*
		 * BASED ON CONTENT TYPE
		 */
		if (extras.getString("content").equals("groupMembers"))
		{
			group = global.loadGroup(extras.getInt("gid"));
			
			populateUsers();
			initActionBar("Group Members");
		}
		else if (extras.getString("content").equals("groupsCurrent"))
		{
			//grabbing the user with the given email in the extras			
			populateEntities();
			initActionBar(user.getFirstName() + "'s Groups");
		}
		else if (extras.getString("content").equals("eventsPending"))
		{
			addNew.setVisibility(View.GONE);
			populateAcceptDecline();
			initActionBar(user.getFirstName() + "'s Pending Events");
		}
		else if (extras.getString("content").equals("eventsUpcoming"))
		{
			addNew.setVisibility(View.GONE);
			populateEntities();
			initActionBar(user.getFirstName() + "'s Upcoming Events");
		}
		else
		{
			addNew.setVisibility(View.GONE);
			initActionBar(user.getFirstName() + "'s Friends");
			populateUsers();
		}
		
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
			Global global = ((Global) getApplicationContext());
			startActivity(login);
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		if (id == R.id.action_home)
		{
			Intent intent = new Intent(this, HomeActivity.class);
			intent.putExtra("ParentClassName", "GroupsCurrentActivity");
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * Uses the mapping of ids and group names to display the users current groups
	 * Groups will be loaded into the Group class when profile is viewed,
	 * 		this could be changed to immediately if we can get the User class communicating with global.
	 * 		same goes for friends, friend requests and group invites
	 */
	private void populateEntities()
	{
		Global global = ((Global) getApplicationContext());
		LayoutInflater li = getLayoutInflater();
		Bundle extras = getIntent().getExtras();
		LinearLayout listLayout = ((LinearLayout)findViewById(R.id.listLayout));
		Map<Integer, String> entities = null;
		String sadGuyText = "";
		if (extras.getString("content").equals("groupsCurrent"))
		{
			//grabbing the users groups
			entities = user.getGroups();
			sadGuyText = "You do not have any groups.";
		}
		else if (extras.getString("content").equals("eventsUpcoming"))
		{
			entities = user.getEventsUpcoming();
			sadGuyText = "You do not have events upcoming.";
		}

		if (entities != null && entities.size() > 0)
		{
			int i = 0;
			
			for (Map.Entry<Integer, String> entry : entities.entrySet()) {
				//Group group = global.loadGroup(id);
				GridLayout rowView;
				
		
					rowView = (GridLayout) li.inflate(
							R.layout.list_row, null);
	
					
				Button leaveGroupButton = (Button)rowView.findViewById(R.id.removeButtonLI);
				//if mod true this, if not someting else
				if (global.isCurrentUser(user.getEmail()))
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
		
			// user has no groups
			// thinking of putting global func: View row = global.getSadGuy("Text to display");

	
			// The user has no groups so display the sad guy image.
			View row = li.inflate(R.layout.listitem_sadguy, null);
			((TextView) row.findViewById(R.id.sadGuyTextView))
				.setText(sadGuyText);

			listLayout.addView(row);
		}	
	}
	
	public void populateUsers()
	{
		LayoutInflater li = getLayoutInflater();
		Global global = ((Global) getApplicationContext());

		Bundle extras = getIntent().getExtras();
		
		Map<String, String> users;
		if  (extras.getString("content").equals("groupMembers"))
		{
			//For now, display the invite members button if you are in group
			//need to check admin level
			users = group.getUsers();	
			if (!users.containsKey(global.getCurrentUser().getEmail()))
			{
				Button addNew = (Button)findViewById(R.id.addNewButtonLiA);
				addNew.setVisibility(View.GONE);
			}
			
		}
		else if (extras.getString("content").equals("friendsCurrent"))//to do make else ifs." +
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
			LinearLayout listLayout = (LinearLayout) findViewById(R.id.listLayout);
			//Bundle extras = intent.getExtras();
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

				 if (user != null && global.isCurrentUser(user.getEmail()) && !extras.getString("content").equals("groupMembers"))
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
			// user has no friends
			LinearLayout friendsCurrentRL = (LinearLayout) findViewById(R.id.listLayout);
	
			View row = li.inflate(R.layout.listitem_sadguy, null);
			// The user has no friend's so display the sad guy image.
			if (extras.getString("content").equals("groupMembers"))
				((TextView) row.findViewById(R.id.sadGuyTextView)).setText("There are no members in this group.");
			else if (extras.getString("content").equals("friendsCurrent"))
				((TextView) row.findViewById(R.id.sadGuyTextView)).setText("You have no friends.");
			
			friendsCurrentRL.addView(row);
		}
	}
	
	//
	private void populateAcceptDecline()
	{
		//Get current layout.
		LinearLayout listLayout = (LinearLayout) findViewById(R.id.listLayout);
		Global global = ((Global) getApplicationContext());
		LayoutInflater li = getLayoutInflater();
		Bundle extras = getIntent().getExtras();
		Map<Integer, String> listItems = null;
		Map<Integer, Boolean> eventsAccepted = null;
		String sadGuyText = "";
		
		/*
		 * Checking which content we need to inflate
		 */
		if (extras.getString("content").equals("eventsPending"))
		{
			listItems = user.getEventsPending();
			eventsAccepted = user.getEventsAccepted();
			sadGuyText = "You do not have any pending events.";
		}
		else if (extras.getString("content").equals("friendRequests"))
		{
			//this
			sadGuyText = "You do not have any friend requests.";
		}
		else if (extras.getString("content").equals("groupInvites"))
		{
			listItems = user.getGroupInvites();
			sadGuyText = "You do not have any group invites.";
		}
		
		
		//array list needs to have group names, maybe the sender names and needs to have group ids	
		if(listItems != null && listItems.size() > 0 )
		{	
			// looping thru the map
			for (Map.Entry<Integer, String> entry : listItems.entrySet())
			{
				GridLayout row;
				TextView rowText;
				if (extras.getString("content").equals("eventsPending") && eventsAccepted.get(entry.getKey()))
				{
					row = (GridLayout) li.inflate(R.layout.list_row_nobutton, null);
					rowText = (TextView)row.findViewById(R.id.nameButtonLI);
				}
				else
				{
					row = (GridLayout) li.inflate(R.layout.list_row_acceptdecline, null);
					rowText = (TextView)row.findViewById(R.id.emailTextViewFRLI);
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
		LinearLayout groupInvites = (LinearLayout)findViewById(R.id.listLayout);
		switch (view.getId())
		{
		case R.id.declineButton:

			View parent = (View) view.getParent();
			//bufferID = parent.getId();
			//new getDeclineGroupTask().execute("http://68.59.162.183/android_connect/leave_group.php?email="+user.getEmail()+"&gid="+parent.getId());
		
			break;
		case R.id.acceptButton:
			
			View parent2 = (View) view.getParent();
			//bufferID = parent2.getId();
			//new getAcceptGroupTask().execute("http://68.59.162.183/android_connect/accept_group_invite.php",user.getEmail(),Integer.toString(parent2.getId()));

			
			//populateGroupInvites();
			break;
		}
	}

		// Handles removing a friend when the remove friend button is pushed.
		public void removeButton(View view)
		{
			Global global = ((Global) getApplicationContext());
			Bundle extras = getIntent().getExtras();
			
			if (extras.getString("content").equals("groupMembers"))
			{
				//nothing for now
			}
			else if (extras.getString("content").equals("groupsCurrent"))
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
								user.removeGroup(clickedRemoveID);
								clickedRemoveID = -1; //reset
							}
						}).setNegativeButton("Cancel", null).show();
			}
			else if (extras.getString("content").equals("friendsCurrent"))
			{
				final int index = view.getId(); //position in friendArray
				final String friendEmail = friendsEmailList.get(index); //friend to remove
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
								user.removeUser(friendEmail);
								// removing all of the views
								LinearLayout friendsCurrentLayout = (LinearLayout) findViewById(R.id.listLayout);
								friendsCurrentLayout.removeAllViews();
								// calling getFriends to repopulate view
								populateUsers();
							}
						}).setNegativeButton("Cancel", null).show();
			}
			if (user != null)
			{
				global.loadUser(user.getEmail());
				
			}
			if (group != null)
			{
				global.loadGroup(group.getID());
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
				Global global = ((Global) getApplicationContext());
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("sender", urls[1]));
				nameValuePairs.add(new BasicNameValuePair("receiver", urls[2]));
				return global.readJSONFeed(urls[0], nameValuePairs);
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
			Intent invite = new Intent(this, InviteActivity.class);
			System.out.println("Starting invite activity with user : " + user.getEmail());
			invite.putExtra("email", user.getEmail());
			invite.putExtra("gid", group.getID());
			startActivity(invite);
			
		}

		
		private class leaveGroupTask extends AsyncTask<String, Void, String>
		{
			@Override
			protected String doInBackground(String... urls)
			{
				Global global = ((Global) getApplicationContext());
				return global.readJSONFeed(urls[0], null);
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
						LinearLayout groupsLayout = (LinearLayout) findViewById(R.id.listLayout);
						groupsLayout.removeAllViews();
						
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
			Global global = ((Global) getApplicationContext());
			Intent intent = new Intent(this, ProfileActivity.class);
			Bundle extras = getIntent().getExtras();
			if (extras.getString("content").equals("groupsCurrent"))
			{
				
				System.out.println("Loading group gid: " + view.getId());
				intent.putExtra("gid", view.getId());
				intent.putExtra("email", user.getEmail());
				intent.putExtra("content", "group");
				Group g = global.loadGroup(view.getId());
			
				if (g != null)
					global.setGroupBuffer(g);
				
			}
			else if (extras.getString("content").equals("eventsPending"))
			{
				System.out.println("Loading event, eid: " + view.getId());
				intent.putExtra("eid", view.getId());
				intent.putExtra("email", user.getEmail());
				intent.putExtra("content", "event");
				Event e = global.loadEvent(view.getId());
			
				if (e != null)
					global.setEventBuffer(e);
			}
			else
			{
				// need to get access to this friends email
				// launches friendProfileActivity and loads content based on that email
				int id = view.getId();
				// got the id, now we need to grab the users email and somehow pass it
				// to the activity
	
				String friendEmail = friendsEmailList.get(id);
				
				
				User u = global.loadUser(friendEmail);
			
				if (u != null)
					global.setUserBuffer(u);
				intent.putExtra("email", friendEmail);
				intent.putExtra("content", "user");
				intent.putExtra("mod", "false");
				
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
