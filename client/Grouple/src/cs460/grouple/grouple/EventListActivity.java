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
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * ListActivity is an activity that displays lists of different types, 
 * and performs relevant functions based on the situation
 */
public class EventListActivity extends BaseActivity
{
	/*
	 * All possible content types that list activity supports
	 */
	enum CONTENT_TYPE
	{
		EVENTS_UPCOMING, EVENTS_PENDING, EVENTS_PAST, EVENT_INVITES, EVENTS_DECLINED;
	}

	// CLASS-WIDE DECLARATIONS
	private User user; // user whose current groups displayed
	private Event event;
	private String EMAIL; // extras passed in from the activity that called
							// ListActivity
	private String CONTENT; // type of content to display in list, passed in
							// from other activities
	private LinearLayout listViewLayout; // layout for list activity (scrollable
											// layout
	private ListView listView; // to inflate into)
	private String ROLE = "U";// defaulting to lowest level
	private Button addNew;
	private String sadGuyText = "";
	// TESTING
	private ArrayList<User> users;
	private ArrayList<Event> events;

	private class EventListAdapter extends ArrayAdapter<Event>
	{
		String startDate = "";
		String newStartDate = "";

		public EventListAdapter()
		{
			super(EventListActivity.this, 0, events);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View itemView = convertView;
			if (itemView == null)
				itemView = inflater.inflate(getItemViewType(position), parent, false);
			final Event e = events.get(position);
			// newStartDate = e.getStartTextNoTime();
			TextView dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);
			LinearLayout dateLayout = (LinearLayout) itemView.findViewById(R.id.dateLayout);
			newStartDate = e.getStartTextNoTime();
			if (dateLayout != null)
			{
				if (position == 0)
				{
					startDate = newStartDate;
					dateTextView.setText(e.getStartTextListDisplay());
				}
				else if ((CONTENT.equals(CONTENT_TYPE.EVENTS_DECLINED.toString()) || CONTENT
						.equals(CONTENT_TYPE.EVENTS_PAST.toString())) && startDate.compareTo(newStartDate) > 0)
				{

					startDate = newStartDate;
					dateTextView.setText(e.getStartTextListDisplay());

				}
				else if (!(CONTENT.equals(CONTENT_TYPE.EVENTS_DECLINED.toString()) || CONTENT
						.equals(CONTENT_TYPE.EVENTS_PAST.toString())) && startDate.compareTo(newStartDate) < 0)
				{

					startDate = newStartDate;
					dateTextView.setText(e.getStartTextListDisplay());

				}
				else
				{
					dateLayout.setVisibility(View.GONE);
				}

			}
			Button removeButton = (Button) itemView.findViewById(R.id.removeButton);
			if (removeButton != null)
			{
				removeButton.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						removeButton(e);
					}
				});
			}

			TextView nameView = (TextView) itemView.findViewById(R.id.nameTextView);
			nameView.setText(e.getName());
			itemView.setId(e.getID());
			return itemView;
		}

		@Override
		public int getItemViewType(int position)
		{
			int listItemID = R.layout.list_row_event;

			if (CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()))
			{
				listItemID = R.layout.list_row_acceptdecline;
			}
			else if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString())
					|| CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
			{
				if (!GLOBAL.isCurrentUser(user.getEmail()))
					listItemID = R.layout.list_row_nobutton;
			}
			return listItemID;
		}
	}

	// populates a list of events
	private void updateUI()
	{


		// looping thru and populating list of events
		if (events != null && !events.isEmpty())
		{
			ArrayAdapter<Event> adapter = new EventListAdapter();
			listView.setAdapter(adapter);
		}
		else
		{
			// no event invites were found, show sadguy image / text
			View row = inflater.inflate(R.layout.list_item_sadguy, null);
			TextView sadTextView = (TextView) row.findViewById(R.id.sadGuyTextView);
			// Set the sad guy text.
			sadTextView.setText(sadGuyText);
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
		listView = (ListView) findViewById(R.id.listView);
		// INSTANTIATIONS
		Bundle extras = getIntent().getExtras();
		CONTENT = extras.getString("content");
		EMAIL = extras.getString("email");
		listViewLayout = (LinearLayout) findViewById(R.id.listViewLayout);
		addNew = (Button) findViewById(R.id.addNewButtonLiA);
		String actionBarTitle = "";
		addNew.setVisibility(View.GONE); // GONE FOR NOW

		// GRABBING A USER
		if (EMAIL != null)
			user = GLOBAL.getUser(EMAIL);
		else
			user = GLOBAL.getCurrentUser();
		// CALL APPROPRIATE METHODS TO POPULATE LIST
		// CONTENT_TYPE -> POPULATEGROUPS
		// setting the bottom button gone
		if (addNew != null)
			addNew.setVisibility(View.GONE);
		// setting the actionbar text
		if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()))
		{
			events = user.getEventsPending();
			sadGuyText = "You do not have any pending events.";
			actionBarTitle = user.getFirstName() + "'s Pending Events";
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()))
		{
			events = user.getEventInvites();
			sadGuyText = "You do not have any event invites.";
			actionBarTitle = user.getFirstName() + "'s Event Invites";
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()))
		{
			events = user.getEventsUpcoming();
			sadGuyText = "You do not have any upcoming events.";
			actionBarTitle = user.getFirstName() + "'s Upcoming Events";
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
		{
			events = user.getEventsPast();
			sadGuyText = "You do not have any past events.";
			actionBarTitle = user.getFirstName() + "'s Past Events";
		}			
		else
		{
			events = user.getEventsDeclined();
			sadGuyText = "You do not have any events declined.";
			actionBarTitle = user.getFirstName() + "'s Declined Events";
		}
		
		// calling next functions to execute
		initActionBar(actionBarTitle, true);
		

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
		fetchData();
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

	private void fetchData()
	{
		if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()))
		{
			user.fetchEventsUpcoming(this);
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()))
		{
			user.fetchEventsPending(this);
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
		{
			user.fetchEventsPast(this);
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()))
		{
			user.fetchEventInvites(this);
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENTS_DECLINED.toString()))
		{
			user.fetchEventsDeclined(this);
		}

	}

	// ONCLICK METHODS BELOW
	public void onClick(View view)
	{
		super.onClick(view);
		View parent = (View) view.getParent();
		int e_id = parent.getId();
		switch (view.getId())
		{
		case R.id.declineButton:
			if (CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()))
			{

				new performActionTask().execute("http://68.59.162.183/android_connect/leave_event.php",
						Integer.toString(e_id));
			}
			break;
		case R.id.acceptButton:
			if (CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()))
			{
				new performActionTask().execute("http://68.59.162.183/android_connect/accept_event_invite.php",
						Integer.toString(e_id));
			}
			break;
		}
	}

	// Handles removing a friend when the remove friend button is pushed.
	public void removeButton(Event e)
	{
		// Get the id.
		final int e_id = e.getID();
		
		//check first to see if we're in 'declined' or 'past'
		if(CONTENT.equals(CONTENT_TYPE.EVENTS_DECLINED.toString()))
		{
			//simply hide event from list
			new AlertDialog.Builder(this).setMessage("Are you sure?  This will remove the event from your 'Declined Events' history.").setCancelable(true)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int id)
				{
					new performActionTask().execute("http://68.59.162.183/android_connect/update_event_member.php",
							Integer.toString(e_id));
				}
			}).setNegativeButton("Cancel", null).show();
		}
		else if(CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
		{
			//simply hide event from list
			new AlertDialog.Builder(this).setMessage("Are you sure?  This will remove the event from your 'Past Events' history.").setCancelable(true)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int id)
				{
					new performActionTask().execute("http://68.59.162.183/android_connect/update_event_member.php",
							Integer.toString(e_id));
				}
			}).setNegativeButton("Cancel", null).show();
		}
		else
		{
			//actually leave the event
			new AlertDialog.Builder(this).setMessage("Are you sure you want to leave this event?").setCancelable(true)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int id)
				{
					new performActionTask().execute("http://68.59.162.183/android_connect/leave_event.php",
							Integer.toString(e_id));
				}
			}).setNegativeButton("Cancel", null).show();
		}
	}

	// Starts a USER/GROUP/EVENT profile
	public void startProfileActivity(View view)
	{
		//loadDialog.show();
		int id = view.getId();
		Intent intent = new Intent(this, EventProfileActivity.class);

		intent.putExtra("e_id", id);
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

			// events pending past, upcoming or invites, all need email and
			// eid
			nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
			//TODO: change to e_id

			if(urls[0].equals("http://68.59.162.183/android_connect/update_event_member.php"))
			{
				nameValuePairs.add(new BasicNameValuePair("remove", "no"));
				nameValuePairs.add(new BasicNameValuePair("hidden", "1"));
				nameValuePairs.add(new BasicNameValuePair("e_id", urls[1]));	
			}
			else
			{
				System.out.println("we werent in update_event_member.php");
				nameValuePairs.add(new BasicNameValuePair("eid", urls[1]));
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

					if (CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()))
					{
						// since leave_event and decline event call same php
						if (!message.equals("Event invite accepted!"))
							message = "Event invite declined!";
					}
					if (CONTENT.equals(CONTENT_TYPE.EVENTS_DECLINED.toString()))
					{
						message = "Event cleared from Declined history!";
					}
					if (CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
					{
						message = "Event cleared from Past history!";
					}
					Toast toast = GLOBAL.getToast(context, message);
					toast.show();
					// reset values, looking to move these
					fetchData();

	
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
