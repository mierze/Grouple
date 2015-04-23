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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/*
 * ListActivity is an activity that displays lists of different types, 
 * and performs relevant functions based on the situation
 */
public class UserListActivity extends BaseActivity
{
	/*
	 * All possible content types that list activity supports
	 */
	enum CONTENT_TYPE
	{
		FRIENDS_CURRENT, FRIEND_REQUESTS, GROUP_MEMBERS, EVENTS_ATTENDING, SELECT_FRIEND;
	}

	// CLASS-WIDE DECLARATIONS
	private User user; // user whose current groups displayed
	private Group group;
	private Event event;
	private Bundle EXTRAS; // extras passed in from the activity that called
							// ListActivity
	private String CONTENT; // type of content to display in list, passed in
							// from other activities
	private ListView listLayout; // layout for list activity (scrollable layout
									// to inflate into)
	private int listItemID;
	private LayoutInflater li;
	private String ROLE = "U";// defaulting to lowest level
	private String PANDABUFFER = ""; // same
	private int bufferID; // same as below: could alternatively have json return
							// the values instead of saving here
	private Button addNew;
	// TESTING
	private ArrayList<User> users;
	private ArrayList<Group> groups;
	private ArrayList<Event> events;

	/* loading in everything needed to generate the list */
	public void load()
	{
		String actionBarTitle = "";
		addNew.setVisibility(View.GONE); // GONE FOR NOW

		// GRABBING A USER
		if (EXTRAS.getString("email") != null)
			if (GLOBAL.isCurrentUser(EXTRAS.getString("email")))
			{
				System.out.println("MAKING USER THE CURRENT USER");
				user = GLOBAL.getCurrentUser();
			}
			else if (GLOBAL.getUserBuffer() != null
					&& GLOBAL.getUserBuffer().getEmail().equals(EXTRAS.getString("email")))
				user = GLOBAL.getUserBuffer();

		// CALL APPROPRIATE METHODS TO POPULATE LIST
		// CONTENT_TYPE -> POPULATEUSERS

		listItemID = R.layout.list_row;
		if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
		{
			addNew.setText("Add New Friend");
			addNew.setVisibility(View.VISIBLE);
			actionBarTitle = user.getFirstName() + "'s Friends";
		}
		else if (CONTENT.equals(CONTENT_TYPE.GROUP_MEMBERS.toString()))
		{
			group = GLOBAL.getGroupBuffer();
			setRole();
			actionBarTitle = "Group Members";
		}
		else if (CONTENT.equals(CONTENT_TYPE.FRIEND_REQUESTS.toString()))
		{
			listItemID = R.layout.list_row_acceptdecline;
			System.out.println("Load 5 now setting action bar title to : " + user.getFirstName());
			actionBarTitle = user.getFirstName() + "'s Friend Requests";
		}
		else if (CONTENT.equals(CONTENT_TYPE.SELECT_FRIEND.toString()))
		{
			listItemID = R.layout.list_row_nobutton;
			System.out.println("Load 5 now setting action bar title to : " + user.getFirstName());
			actionBarTitle = "Message Who?";
		}
		else
		// EVENTS_ATTENDING
		{
			event = GLOBAL.getEventBuffer();
			setRole();
			actionBarTitle = "Attending " + event.getName();
		}
		populateUsers();

		/*
		 * if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()) &&
		 * GLOBAL.isCurrentUser(user.getEmail()) &&
		 * !CONTENT.equals(CONTENT_TYPE.GROUP_MEMBERS.toString())) { name =
		 * u.getName(); row = li.inflate(R.layout.list_row, null); nameTextView
		 * = (TextView) row.findViewById(R.id.nameTextViewLI); Button
		 * removeFriendButton = (Button) row.findViewById(R.id.removeButtonLI);
		 * removeFriendButton.setId(index); } //FOR FRIEND REQUESTS else if
		 * (CONTENT.equals(CONTENT_TYPE.FRIEND_REQUESTS.toString())) { row =
		 * li.inflate(R.layout.list_row_acceptdecline, null); nameTextView =
		 * (TextView) row.findViewById(R.id.emailTextView); name = email; }
		 * //FOR GROUP MEMBERS / CURRENT FRIENDS NON MOD / SELECT FRIEND else {
		 * name = u.getName(); row = li.inflate(R.layout.list_row_nobutton,
		 * null); nameTextView = (TextView)
		 * row.findViewById(R.id.nameTextViewLI); } nameTextView.setText(name);
		 * nameTextView.setId(index); row.setId(index); listLayout.addView(row);
		 * }
		 */

		// calling next functions to execute
		initActionBar(actionBarTitle, true);

	}

	private class UserListAdapter extends ArrayAdapter<User>
	{
		public UserListAdapter()
		{
			super(UserListActivity.this, listItemID, users);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View itemView = convertView;
			if (itemView == null)
				itemView = inflater.inflate(listItemID, parent, false);
			RelativeLayout itemLayout = (RelativeLayout) itemView.findViewById(R.id.listRowLayout);
			// find group to work with
			User u = users.get(position);
			TextView nameView = (TextView) itemView.findViewById(R.id.nameTextViewLI);
			nameView.setText(u.getName());
			itemView.setId(position);
			Button deleteButton;
			// fill the view
			return itemView;
		}
	}

	// populates a list of users
	private void populateUsers()
	{
		String sadGuyText = "";
		

		if (CONTENT.equals(CONTENT_TYPE.GROUP_MEMBERS.toString()))
		{
			sadGuyText = "There are no members in this group.";
			users = group.getUsers();
		}
		else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))// to																	// +
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
			listLayout.setAdapter(adapter);
				
		}
		else
		{
			// The user has no friend's so display the sad guy image / text
			View row = li.inflate(R.layout.list_item_sadguy, null);
			TextView sadGuy = ((TextView) row.findViewById(R.id.sadGuyTextView));
			sadGuy.setText(sadGuyText);
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
		// clearing any previous views populated, for refresh
		listLayout = (ListView) findViewById(R.id.listLayout);
		// INSTANTIATIONS
		EXTRAS = getIntent().getExtras();
		CONTENT = EXTRAS.getString("content");
		li = getLayoutInflater();
		addNew = (Button) findViewById(R.id.addNewButtonLiA);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		load();
	}

	// ONCLICK METHODS BELOW
	public void onClick(View view)
	{
		super.onClick(view);
		View parent = (View) view.getParent();
		switch (view.getId())
		{
		case R.id.declineButton:
			if (CONTENT.equals(CONTENT_TYPE.FRIEND_REQUESTS.toString()))
			{
				PANDABUFFER = users.get(parent.getId()).getEmail();
				new performActionTask().execute("http://68.59.162.183/android_connect/decline_friend_request.php",
						PANDABUFFER);
			}

			break;
		case R.id.acceptButton:
			if (CONTENT.equals(CONTENT_TYPE.FRIEND_REQUESTS.toString()))
			{
				PANDABUFFER = users.get(parent.getId()).getEmail();
				new performActionTask().execute("http://68.59.162.183/android_connect/accept_friend_request.php",
						PANDABUFFER);
			}
			break;
		}
	}

	// Handles removing a friend when the remove friend button is pushed.
	public void removeButton(View view)
	{
		if (CONTENT.equals(CONTENT_TYPE.GROUP_MEMBERS.toString()))
		{
			// nothing for now
		}
		else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
		{
			final int index = view.getId(); // position in friendArray
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
		if (group != null)
		{
			intent.putExtra("g_id", group.getID());
			GLOBAL.setGroupBuffer(group);
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
				|| CONTENT.equals(CONTENT_TYPE.EVENTS_ATTENDING.toString())
				|| CONTENT.equals(CONTENT_TYPE.FRIEND_REQUESTS.toString())
				|| CONTENT.equals(CONTENT_TYPE.SELECT_FRIEND.toString()))
		{
			String friendEmail = users.get(id).getEmail();
			User u = new User(friendEmail);
			if (!CONTENT.equals(CONTENT_TYPE.SELECT_FRIEND.toString()))
			{
				u.fetchEventsUpcoming();
				u.fetchEventsPast();
				u.fetchUserInfo();
				u.fetchBadges();
				u.fetchFriends();
				u.fetchGroups();
				if (!GLOBAL.isCurrentUser(friendEmail))
					GLOBAL.setUserBuffer(u);
				else
					GLOBAL.setCurrentUser(u); // reloading user
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
		// refresh pertinent info
		if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString())
				|| CONTENT.equals(CONTENT_TYPE.FRIEND_REQUESTS.toString()))
		{
			user.fetchFriends();
			// FRIENDS
			if (GLOBAL.isCurrentUser(user.getEmail()))
				user.fetchFriendRequests();
			if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
			{
				// USER PROFILE
				// FRIEND PROFILE
				user.fetchEventsUpcoming();
				user.fetchGroups();
			}
		}
		else if (CONTENT.equals(CONTENT_TYPE.GROUP_MEMBERS.toString()))
		{
			// GROUP PROFILE
			group.fetchMembers();
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENTS_ATTENDING.toString()))
		{
			// EVENT PROFILE
			event.fetchParticipants();
		}
		// SETTING GLOBALS
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
						if (CONTENT.equals(CONTENT_TYPE.EVENTS_ATTENDING.toString()))
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
						user.fetchFriendRequests();
					}
					else if (CONTENT.equals(CONTENT_TYPE.FRIENDS_CURRENT.toString()))
					{
						user.removeUser(PANDABUFFER);
						user.fetchFriends();
					}
					Toast toast = GLOBAL.getToast(context, message);
					toast.show();
					// reset values, looking to move these
					PANDABUFFER = "";
					bufferID = -1;
					// removing all friend requests for refresh
					listLayout.removeAllViews();

					// CALLING CORRESPONDING METHOD TO REPOPULATE
					populateUsers();
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
