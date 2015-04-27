package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import cs460.grouple.grouple.EventListActivity.CONTENT_TYPE;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
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
public class GroupListActivity extends BaseActivity
{
	/*
	 * All possible content types that list activity supports
	 */
	enum CONTENT_TYPE
	{
		GROUPS_CURRENT, GROUP_INVITES;
	}

	// LIST-VIEW STUFF
	private ArrayList<Group> groups;

	// CLASS-WIDE DECLARATIONS
	private User user; // user whose current groups displayed
	private Group group;
	private String EMAIL; // extras passed in from the activity that called
							// ListActivity
	private String CONTENT; // type of content to display in list, passed in
							// from other activities
	private ListView listView; // layout for list activity (scrollable layout to
								// inflate into)
	private LinearLayout listViewLayout;
	private String ROLE = "U";// defaulting to lowest level
	private String PANDABUFFER = ""; // same
	private int bufferID; // same as below: could alternatively have json return
							// the values instead of saving here
	private Button addNew;

	// TESTING



	private void fetchData()
	{
		if (user != null)
		{
			user.fetchGroupInvites(this);
			user.fetchGroups(this);
		}
		
	}
	private class GroupListAdapter extends ArrayAdapter<Group>
	{
		public GroupListAdapter()
		{
			super(GroupListActivity.this, 0, groups);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View itemView = convertView;
			if (itemView == null)
				itemView = inflater.inflate(getItemViewType(position), parent, false);
			//RelativeLayout itemLayout = (RelativeLayout) itemView.findViewById(R.id.listRowLayout);
			// find group to work with
			final Group g = groups.get(position);
			TextView nameView = (TextView) itemView.findViewById(R.id.nameTextView);
			nameView.setText(g.getName());
			// nameView.setId(g.getID());
			System.out.println(g.getID());
			itemView.setId(g.getID());
			Button removeButton = (Button) itemView.findViewById(R.id.removeButton);
			if (removeButton != null)
			{
				removeButton.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						removeButton(g);
					}
				});
			}
			// fill the view
			return itemView;
		}

		@Override
		public int getItemViewType(int position)
		{
			int listItemID = R.layout.list_row;
			if (CONTENT.equals(CONTENT_TYPE.GROUP_INVITES.toString()))
			{
				listItemID = R.layout.list_row_acceptdecline;
			}
			else if (!GLOBAL.isCurrentUser(user.getEmail()))
					listItemID = R.layout.list_row_nobutton;
			return listItemID;
		}
	}

	private void registerClickCallback()
	{
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id)
			{
				System.out.println("ADSFASFDA\b\n\n\n\n");
				Group g = groups.get(position);
				// startProfileActivity(g);
			}
		});
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
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("user_data"));
		updateUI();
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


	// populates a list of groups
	// populateListView
	private void updateUI()
	{
		String actionBarTitle = "";
		if (CONTENT.equals(CONTENT_TYPE.GROUP_INVITES.toString()))
			actionBarTitle = user.getFirstName() + "'s Group Invites";
		if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
			actionBarTitle = user.getFirstName() + "'s Groups";
		initActionBar(actionBarTitle, true);
		String sadGuyText = "";
		
		if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
		{
			groups = user.getGroups();
			sadGuyText = "You are not in any groups.";
		}
		else if (CONTENT.equals(CONTENT_TYPE.GROUP_INVITES.toString()))
		{
			groups = user.getGroupInvites();
			sadGuyText = "You do not have any group invites.";
		}

		// looping thru and populating list of groups
		if (groups != null && !groups.isEmpty())
		{
			ArrayAdapter<Group> adapter = new GroupListAdapter();
			listView.setAdapter(adapter);
		}
		else
		{
			// The user has no groups so display the sad guy
			View row = inflater.inflate(R.layout.list_item_sadguy, null);
			((TextView) row.findViewById(R.id.sadGuyTextView)).setText(sadGuyText);
			listView.setVisibility(View.GONE);
			listViewLayout.addView(row);
		}

	}

	// DEFAULT METHODS BELOW
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		//grabbing xml elements
		listView = (ListView) findViewById(R.id.listView);
		listViewLayout = (LinearLayout) findViewById(R.id.listViewLayout);
		addNew = (Button) findViewById(R.id.addNewButtonLiA);
		// INSTANTIATIONS
		Bundle extras = getIntent().getExtras();
		CONTENT = extras.getString("content");
		EMAIL = extras.getString("email");
		String actionBarTitle = "";
		addNew.setVisibility(View.GONE); // GONE FOR NOW

		// GRABBING A USER
		if (EMAIL != null)
		{
			user = GLOBAL.getUser(EMAIL);
		}
		else
		{
			user = GLOBAL.getCurrentUser();
		}
	
		// registerClickCallback();
	}

	// ONCLICK METHODS BELOW
	public void onClick(View view)
	{
		super.onClick(view);
		View parent = (View) view.getParent();
		switch (view.getId())
		{
		case R.id.declineButton:
			if (CONTENT.equals(CONTENT_TYPE.GROUP_INVITES.toString()))
			{
				bufferID = parent.getId();
				new performActionTask().execute("http://68.59.162.183/android_connect/leave_group.php",
						user.getEmail(), Integer.toString(parent.getId()));
			}
			break;
		case R.id.acceptButton:
			if (CONTENT.equals(CONTENT_TYPE.GROUP_INVITES.toString()))
			{
				bufferID = parent.getId();
				new performActionTask().execute("http://68.59.162.183/android_connect/accept_group_invite.php",
						user.getEmail(), Integer.toString(bufferID));
			}
			break;
		}
	}

	// Handles removing a friend when the remove friend button is pushed.
	public void removeButton(Group g)
	{
		if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
		{
			// Get the id.
			bufferID = g.getID();

			new AlertDialog.Builder(this).setMessage("Are you sure you want to leave this group?").setCancelable(true)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int id)
						{
							new performActionTask().execute("http://68.59.162.183/android_connect/leave_group.php",
									user.getEmail(), Integer.toString(bufferID));
						}
					}).setNegativeButton("Cancel", null).show();
		}
	}

	// Starts a USER/GROUP/EVENT profile
	public void startProfileActivity(View view)
	{
		//loadDialog.show();
		int id = view.getId();
		Group g = new Group(id);
		Intent intent = new Intent(this, GroupProfileActivity.class);

		intent.putExtra("g_id", id);
		intent.putExtra("email", user.getEmail());

		startActivity(intent);
	}


	private class performActionTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			// Add your data
			if (CONTENT.equals(CONTENT_TYPE.GROUP_INVITES.toString())
					|| CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
			{
				// group invites or current groups, add email and gid
				nameValuePairs.add(new BasicNameValuePair("email", urls[1]));
				nameValuePairs.add(new BasicNameValuePair("gid", urls[2]));
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
					if (CONTENT.equals(CONTENT_TYPE.GROUP_INVITES.toString()))
					{
						user.removeGroupInvite(bufferID);
						// since leave_group and decline group invite call same
						// php
						if (!message.equals("Group invite accepted!"))
							message = "Group invite declined!";
					}
					else if (CONTENT.equals(CONTENT_TYPE.GROUPS_CURRENT.toString()))
					{
						user.removeGroup(bufferID);
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
