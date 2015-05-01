package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Brett, Todd, Scott
 * GroupListActivity displays vertical user email lists of different types and performs relevant
 * functions based on the situation and type
 */
public class UserListActivity extends BaseActivity
{
	/*
	 * All possible content types that list activity supports
	 */
	enum CONTENT_TYPE
	{
		FRIENDS_CURRENT, FRIEND_REQUESTS, GROUP_MEMBERS, EVENT_PARTICIPANTS, SELECT_FRIEND;
	}

	// CLASS-WIDE DECLARATIONS
	private User user; // user whose current groups displayed
	private Group group;
	private Event event;
	private String EMAIL; // extras passed in from the activity that called
							// ListActivity
	private String CONTENT; // type of content to display in list, passed in
							// from other activities
	private ListView listView;
	private LinearLayout listViewLayout;
	private String ROLE = "U";// defaulting to lowest level
	private String PANDABUFFER = ""; // same
	private int bufferID; // same as below: could alternatively have json return
							// the values instead of saving here
	private Button addNew;
	// TESTING
	private ArrayList<User> users;

	/* loading in everything needed to generate the list */
	public void load()
	{
		String actionBarTitle = "";
		addNew.setVisibility(View.GONE); // GONE FOR NOW
		// CONTENT_TYPE -> POPULATEUSERS
		if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
		{
			addNew.setText("Add New Friend");
			addNew.setVisibility(View.VISIBLE);
			actionBarTitle = user.getFirstName() + "'s Friends";
		}
		else if (CONTENT.equals(CONTENT_TYPE.GROUP_MEMBERS.toString()))
		{
			setRole();
			actionBarTitle = "Group Members";
		}
		else if (CONTENT.equals(CONTENT_TYPE.FRIEND_REQUESTS.toString()))
			actionBarTitle = user.getFirstName() + "'s Friend Requests";
		else if (CONTENT.equals(CONTENT_TYPE.SELECT_FRIEND.toString()))
			actionBarTitle = "Message Who?";
		else
		// EVENT_PARTICIPANTS
		{
			setRole();
			actionBarTitle = "Attending " + event.getName();
		}
		updateUI();

		// calling next functions to execute
		initActionBar(actionBarTitle, true);

	}

	private class UserListAdapter extends ArrayAdapter<User>
	{

		public UserListAdapter()
		{
			super(UserListActivity.this, 0, users);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			View itemView = convertView;
			if (itemView == null)
				itemView = inflater.inflate(getItemViewType(position), parent, false);
			LinearLayout itemLayout = (LinearLayout) itemView.findViewById(R.id.listRowLayout);
			// find group to work with
			User u = users.get(position);
			TextView nameView = (TextView) itemView.findViewById(R.id.nameTextView);
			
			
			itemView.setId(position);
			//TODO: accept decline button onclicks here?
			Button removeButton = (Button) itemView.findViewById(R.id.removeButton);
			if (removeButton != null)
			{
				removeButton.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						removeButton(position);
					}
				});
			}
			if (CONTENT.equals(CONTENT_TYPE.FRIEND_REQUESTS.toString()))
			{
				nameView.setText(u.getEmail());//TODO: get name?
				Button acceptButton = (Button) itemView.findViewById(R.id.acceptButton);
				Button declineButton = (Button) itemView.findViewById(R.id.declineButton);
				acceptButton.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						acceptButton(users.get(position).getEmail());
					}
				});
				declineButton.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						declineButton(users.get(position).getEmail());
					}
				});
			
			}
			else
			{
				nameView.setText(u.getName());
			}
			// fill the view
			return itemView;
		}

		@Override
		public int getItemViewType(int position)
		{
			int listItemID = R.layout.list_row;

			if (CONTENT.equals(CONTENT_TYPE.FRIEND_REQUESTS.toString()))
			{
				listItemID = R.layout.list_row_acceptdecline;
			}
			else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
			{
				if (!GLOBAL.isCurrentUser(user.getEmail()))
					listItemID = R.layout.list_row_nobutton;
			}
			else if (CONTENT.equals(CONTENT_TYPE.EVENT_PARTICIPANTS.toString())
					|| CONTENT.equals(CONTENT_TYPE.GROUP_MEMBERS.toString()))
				listItemID = R.layout.list_row_nobutton;
			return listItemID;
		}
	}

	// populates a list of users
	private void updateUI()
	{
		super.updateUI(user);
		String sadGuyText = "";

		if (CONTENT.equals(CONTENT_TYPE.GROUP_MEMBERS.toString()))
		{
			sadGuyText = "There are no members in this group.";
			users = group.getUsers();
		}
		else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))// to
																			// //
																			// +
		{
			sadGuyText = "You have no friends.";
			users = user.getUsers();
			Button addNew = (Button) findViewById(R.id.addNewButtonLiA);
			if (GLOBAL.isCurrentUser(user.getEmail()))
				addNew.setText("Add New Friend");
			else
				addNew.setVisibility(View.GONE);
		}
		else if (CONTENT.equals(CONTENT_TYPE.FRIEND_REQUESTS.toString()))
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

		// looping thru and populating list of users
		if (users != null && !users.isEmpty())
		{
			ArrayAdapter<User> adapter = new UserListAdapter();
			listView.setAdapter(adapter);
			/*listView.setOnTouchListener(new OnTouchListener()
			{
				float historicX = Float.NaN, historicY = Float.NaN;
				final int DELTA = 50;

				
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					// TODO Auto-generated method stub
					switch (event.getAction())
					{
					case MotionEvent.ACTION_DOWN:
						historicX = event.getX();
						historicY = event.getY();
						break;

					case MotionEvent.ACTION_UP:
						if (event.getX() - historicX < -DELTA)
						{
							GLOBAL.getToast(UserListActivity.this, "HASJDFHLAHSFDJHAF").show();
							// updateUI();
							return true;
						}
						else if (event.getX() - historicX > DELTA)
						{
							GLOBAL.getToast(UserListActivity.this, "HASJDFHLAHSFDJHAF").show();
							// users.remove(position);
							// updateUI();
							return true;
						}
						break;
					default:
						return false;
					}
					return false;
				}
			});*/
		}
		else
		{
			// The user has no friend's so display the sad guy image / text
			View row = inflater.inflate(R.layout.list_item_sadguy, null);
			TextView sadGuy = ((TextView) row.findViewById(R.id.sadGuyTextView));
			sadGuy.setText(sadGuyText);
			listView.setVisibility(View.GONE);
			listViewLayout.addView(row);
		}
	}

	// based on content type, gets the corresponding role
	private void setRole()
	{
		ArrayList<User> users = new ArrayList<User>();
		if (CONTENT.equals(CONTENT_TYPE.EVENT_PARTICIPANTS.toString()))
		{
			users = event.getUsers();
			addNew.setText("Invite Groups");// TODO: Mod checks
		}
		else
		{
			users = group.getUsers();
			addNew.setText("Invite Friends");
		}

		// checking if user is in group/event
		boolean inEntity = false;
		for (User u : users)
			if (u.getEmail().equals(user.getEmail()))
				inEntity = true;
		if (inEntity) // user is in the group or event, grab their role
		{
			if (CONTENT.equals(CONTENT_TYPE.GROUP_MEMBERS.toString()))
				new getRoleTask().execute("http://68.59.162.183/android_connect/check_role_group.php",
						Integer.toString(group.getID()));
			else
				new getRoleTask().execute("http://68.59.162.183/android_connect/check_role_event.php",
						Integer.toString(event.getID()));
		}
	}

	// DEFAULT METHODS BELOW
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		// grabbing xml elements
		listView = (ListView) findViewById(R.id.listView);
		listViewLayout = (LinearLayout) findViewById(R.id.listViewLayout);
		// INSTANTIATIONS
		Bundle extras = getIntent().getExtras();
		EMAIL = extras.getString("email");
		CONTENT = extras.getString("content");

		if (CONTENT.equals(CONTENT_TYPE.GROUP_MEMBERS.toString()))
		{
			group = GLOBAL.getGroup(extras.getInt("g_id"));
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENT_PARTICIPANTS.toString()))
		{
			event = GLOBAL.getEvent(extras.getInt("e_id"));
		}
		if (EMAIL != null)
			user = GLOBAL.getUser(EMAIL);
		else
		{		
			user = GLOBAL.getCurrentUser();
			user.fetchExperience(this);
		}
		if (user == null)
		{
			user = GLOBAL.getUser(EMAIL);
			GLOBAL.setCurrentUser(user);
			user.fetchExperience(this);
		}
		addNew = (Button) findViewById(R.id.addNewButtonLiA);
	}

	/*
	 * fetchData fetches all data needed to be displayed in the UI for user
	 * profile activity
	 */
	private void fetchData()
	{
		user.fetchFriends(this);
		user.fetchFriendRequests(this);
		user.fetchExperience(this);
		if (group != null)
		{
			group.fetchMembers(this);
		}
		if (event != null)
			event.fetchParticipants(this);

	}

	@Override
	protected void onPause()
	{
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if (CONTENT.equals(CONTENT_TYPE.EVENT_PARTICIPANTS.toString()))
		{
			LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("event_data"));
		}
		else if (CONTENT.equals(CONTENT_TYPE.GROUP_MEMBERS.toString()))
		{
			LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("group_data"));
		}
		else
		{
			LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("user_data"));
		}
		fetchData();
		load();
	}

	// This listens for pings from the data service to let it know that there
	// are updates
	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// Extract data included in the Intent
			String type = intent.getStringExtra("message");
			// repopulate views
			updateUI();
		}
	};

	// ONCLICK METHODS BELOW
	public void acceptButton(String email)
	{

				PANDABUFFER = email;
				new performActionTask().execute("http://68.59.162.183/android_connect/accept_friend_request.php",
						PANDABUFFER);

	}

	public void declineButton(String email)
	{

				PANDABUFFER = email;
				new performActionTask().execute("http://68.59.162.183/android_connect/decline_friend_request.php",
								PANDABUFFER);

	}


	// Handles removing a friend when the remove friend button is pushed.
	public void removeButton(int index)
	{
		if (CONTENT.equals(CONTENT_TYPE.GROUP_MEMBERS.toString()))
		{
			// nothing for now
		}
		else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
		{
			final String friendEmail = users.get(index).getEmail(); // friend to
																	// remove
			PANDABUFFER = friendEmail;// PANDA TODO

			new AlertDialog.Builder(this).setMessage("Are you sure you want to remove that friend?")
					.setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int id)
						{
							new performActionTask().execute("http://68.59.162.183/android_connect/delete_friend.php",
									friendEmail);
						}
					}).setNegativeButton("Cancel", null).show();
		}
	}

	// Starts an activity to add new friends/group members/invite groups to
	// events
	public void bottomButton(View view)
	{
		loadDialog.show();
		Intent intent = null;
		if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
		{
			intent = new Intent(this, FriendAddActivity.class);
		}
		else if (CONTENT.equals(CONTENT_TYPE.GROUP_MEMBERS.toString()))
		{
			intent = new Intent(this, InviteActivity.class);
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENT_PARTICIPANTS.toString()))
		{
			intent = new Intent(this, EventAddGroupsActivity.class);
		}
		if (user != null)
			intent.putExtra("email", user.getEmail());
		if (event != null)
			intent.putExtra("e_id", event.getID());
		if (group != null)
		{
			intent.putExtra("g_id", group.getID());
		}
		startActivity(intent);
	}

	// Starts a USER/GROUP/EVENT profile
	public void startProfileActivity(View view)
	{
		loadDialog.show();
		int id = view.getId();
		Intent intent = new Intent(this, UserProfileActivity.class);

		if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString())
				|| CONTENT.equals(CONTENT_TYPE.GROUP_MEMBERS.toString())
				|| CONTENT.equals(CONTENT_TYPE.EVENT_PARTICIPANTS.toString())
				|| CONTENT.equals(CONTENT_TYPE.FRIEND_REQUESTS.toString())
				|| CONTENT.equals(CONTENT_TYPE.SELECT_FRIEND.toString()))
		{
			String friendEmail = users.get(id).getEmail();
			if (!CONTENT.equals(CONTENT_TYPE.SELECT_FRIEND.toString()))
			{

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

	/* Gets the role of the current user in a group / event */
	private class getRoleTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = (CONTENT.equals(CONTENT_TYPE.GROUP_MEMBERS.toString())) ? "gid" : "eid";
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

				// json fetch was successful
				if (jsonObject.getString("success").toString().equals("1"))
				{
					ROLE = jsonObject.getString("role").toString();
					System.out.println("ROLE IS BEING SET TO " + ROLE);
					if (!ROLE.equals("U"))
						if (CONTENT.equals(CONTENT_TYPE.EVENT_PARTICIPANTS.toString()))
						{
							if (!event.getEventState().equals("Ended"))
								addNew.setVisibility(View.VISIBLE);
						}
						else
							addNew.setVisibility(View.VISIBLE);
				}
				// unsuccessful
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
			if (CONTENT.equals(CONTENT_TYPE.FRIEND_REQUESTS.toString())
					|| CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
			{
				// friend requests or remove friend, both pass sender and
				// receiver
				nameValuePairs.add(new BasicNameValuePair("sender", urls[1]));
				nameValuePairs.add(new BasicNameValuePair("receiver", user.getEmail()));
			}

			// calling readJSONFeed in Global, continues below in onPostExecute
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

					if (CONTENT.equals(CONTENT_TYPE.FRIEND_REQUESTS.toString()))
					{
						user.removeFriendRequest(PANDABUFFER);

					}
					else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
					{
						user.removeUser(PANDABUFFER);

					}
					Toast toast = GLOBAL.getToast(context, message);
					toast.show();
					// reset values, looking to move these
					PANDABUFFER = "";
					bufferID = -1;

					// CALLING CORRESPONDING METHOD TO REPOPULATE
					updateUI();
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
