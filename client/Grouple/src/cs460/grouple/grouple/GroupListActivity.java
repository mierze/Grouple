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

/**
 * 
 * @author Brett, Todd, Scott
 * GroupListActivity displays vertical group lists of different types and performs relevant
 * functions based on the situation and type
 */
public class GroupListActivity extends BaseActivity
{ 
	// all possible content types that this list activity supports
	enum CONTENT_TYPE
	{
		GROUPS_CURRENT, GROUP_INVITES;
	}
	
	//groups to display
	private ArrayList<Group> groups;
	// CLASS-WIDE DECLARATIONS
	private User user; // user whose current groups displayed
	private LinearLayout sadGuyLayout;
	// extras passed in from the activity that called ListActivity
	private String EMAIL; 
	// type of content to display in list, passed in from other activities
	private String CONTENT; 
	// layout for list activity (scrollable layout to inflate into)
	private ListView listView;
	//optional button to have at the bottom of the list
	private Button bottomButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		//grabbing xml elements
		listView = (ListView) findViewById(R.id.listView);
		bottomButton = (Button) findViewById(R.id.bottomButton);
		sadGuyLayout = (LinearLayout) findViewById(R.id.sadGuyLayout);
		// init variables
		Bundle extras = getIntent().getExtras();
		CONTENT = extras.getString("content");
		EMAIL = extras.getString("email");
		// grab a user
		if (EMAIL != null)
			user = GLOBAL.getUser(EMAIL);
		else
			user = GLOBAL.getCurrentUser();
	}

	@Override
	protected void onPause()
	{
		LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver);
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, new IntentFilter("user_data"));
		fetchData();
		updateUI();
	}

	// This listens for pings from the data service to let it know that there
	// are updates
	private BroadcastReceiver dataReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// repopulate views
			updateUI();
		}
	};
	
	//fetching data to display
	private void fetchData()
	{
		if (CONTENT.equals(CONTENT_TYPE.GROUP_INVITES.toString()))
			user.fetchGroupInvites(this);
		else
			user.fetchGroups(this);		
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
			listView.setVisibility(View.VISIBLE);
			sadGuyLayout.setVisibility(View.GONE);
			ArrayAdapter<Group> adapter = new GroupListAdapter();
			listView.setAdapter(adapter);
		}
		else
		{
			View sadGuyView = inflater.inflate(R.layout.list_item_sadguy, null);
			TextView sadGuyTextView = (TextView) sadGuyView.findViewById(R.id.sadGuyTextView);
			sadGuyTextView.setText(sadGuyText);
			sadGuyLayout.setVisibility(View.VISIBLE);
			sadGuyLayout.removeAllViews();
			listView.setVisibility(View.GONE);
			sadGuyLayout.addView(sadGuyView);
		}
	}


	// ONCLICK METHODS BELOW
	public void onClick(View view)
	{
		super.onClick(view);
		View parent = (View) view.getParent();
		String id = Integer.toString(parent.getId());
		switch (view.getId())
		{
		case R.id.declineButton:
			if (CONTENT.equals(CONTENT_TYPE.GROUP_INVITES.toString()))
			{
				new performActionTask().execute("http://68.59.162.183/android_connect/leave_group.php",
						user.getEmail(), id);
			}
			break;
		case R.id.acceptButton:
			if (CONTENT.equals(CONTENT_TYPE.GROUP_INVITES.toString()))
			{
				new performActionTask().execute("http://68.59.162.183/android_connect/accept_group_invite.php",
						user.getEmail(), id);
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
			final String g_id = Integer.toString(g.getID());

			new AlertDialog.Builder(this).setMessage("Are you sure you want to leave this group?").setCancelable(true)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int id)
						{
							new performActionTask().execute("http://68.59.162.183/android_connect/leave_group.php",
									user.getEmail(), g_id);
						}
					}).setNegativeButton("Cancel", null).show();
		}
	}

	public void startProfileActivity(View view)
	{
		loadDialog.show();
		int id = view.getId();
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
						// since leave_group and decline group invite call same
						// php
						if (!message.equals("Group invite accepted!"))
							message = "Group invite declined!";
					}
					fetchData();
					Toast toast = GLOBAL.getToast(context, message);
					toast.show();
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
