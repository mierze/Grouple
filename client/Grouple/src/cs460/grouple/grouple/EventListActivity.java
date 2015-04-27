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
	private String PANDABUFFER = ""; // same
	private int bufferID; // same as below: could alternatively have json return
							// the values instead of saving here
	private Button addNew;
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
				else if ((CONTENT.equals(CONTENT_TYPE.EVENTS_DECLINED.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
						&& startDate.compareTo(newStartDate) > 0)
				{

					startDate = newStartDate;
					dateTextView.setText(e.getStartTextListDisplay());

				}
				else if (!(CONTENT.equals(CONTENT_TYPE.EVENTS_DECLINED.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
						&& startDate.compareTo(newStartDate) < 0)
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
		/*
		 * 
		 * TextView dateTextView = (TextView)
		 * dateRow.findViewById(R.id.dateTextView); String if (startDate ==
		 * null) { //first event to display, show its time, and set startDate =
		 * to it startDate = newStartDate;
		 * dateTextView.setText(e.getStartTextListDisplay());
		 * listLayout.addView(dateRow); } else if
		 * (startDate.compareTo(newStartDate) < 0) { //if previous start date
		 * less than new start date, display date startDate =
		 * e.getStartTextNoTime();
		 * dateTextView.setText(e.getStartTextListDisplay());
		 * listLayout.addView(dateRow); } id = e.getID(); index =
		 * events.indexOf(e); //Group group = GLOBAL.loadGroup(id); if
		 * (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()) ||
		 * CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()) ||
		 * CONTENT.equals(CONTENT_TYPE.EVENTS_DECLINED.toString()) ||
		 * CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString())) {
		 * System.out.println("IN FIRST EVENT IF"); if
		 * (GLOBAL.isCurrentUser(user.getEmail())) { row =
		 * li.inflate(R.layout.list_row, null); removeEventButton = (Button)
		 * row.findViewById(R.id.removeButtonLI); removeEventButton.setId(id); }
		 * else //user does not have ability to remove events row =
		 * li.inflate(R.layout.list_row_nobutton, null);
		 * 
		 * nameTextView = (TextView)row.findViewById(R.id.nameTextViewLI);
		 * 
		 * if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()) ||
		 * CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()) ||
		 * CONTENT.equals(CONTENT_TYPE.EVENTS_DECLINED.toString()))
		 * nameTextView.setText(e.getName());//future get date too? else
		 * nameTextView.setText(e.getName() + "\n(" + e.getNumUsers() +
		 * " confirmed / " + e.getMinPart() + " required)"); } else { row =
		 * li.inflate(R.layout.list_row_acceptdecline, null); nameTextView =
		 * (TextView)row.findViewById(R.id.emailTextView);
		 * nameTextView.setText(e.getName() + "\n(" + e.getNumUsers() +
		 * " confirmed / " + e.getMinPart() + " required)"); } //setting ids to
		 * the id of the group for button functionality nameTextView.setId(id);
		 * row.setId(id); //adding row to view listLayout.addView(row);
		 */
	}

	// populates a list of events
	private void populateEvents()
	{
		String sadGuyText = "";

		// Checking which CONTENT we need to get
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
		else if (CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()))
		{
			events = user.getEventInvites();
			sadGuyText = "You do not have any event invites.";
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENTS_DECLINED.toString()))
		{
			events = user.getEventsDeclined();
			sadGuyText = "You do not have any events declined.";
		}
		else
		{
			events = user.getEventsPast();
			sadGuyText = "You do not have any past events.";
		}

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
			actionBarTitle = user.getFirstName() + "'s Pending Events";
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()))
		{
			actionBarTitle = user.getFirstName() + "'s Event Invites";
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()))
		{
			actionBarTitle = user.getFirstName() + "'s Upcoming Events";
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
			actionBarTitle = user.getFirstName() + "'s Past Events";
		else
			actionBarTitle = user.getFirstName() + "'s Declined Events";
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
		populateEvents();
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
			populateEvents();
		}
	};

	// ONCLICK METHODS BELOW
	public void onClick(View view)
	{
		super.onClick(view);
		View parent = (View) view.getParent();
		switch (view.getId())
		{
		case R.id.declineButton:
			if (CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()))
			{
				bufferID = parent.getId(); // PANDA
				new performActionTask().execute("http://68.59.162.183/android_connect/leave_event.php",
						Integer.toString(bufferID));
			}
			break;
		case R.id.acceptButton:
			if (CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()))
			{
				bufferID = parent.getId();
				System.out.println("Accepting event invite, eid: " + bufferID);
				new performActionTask().execute("http://68.59.162.183/android_connect/accept_event_invite.php",
						Integer.toString(bufferID));
			}
			break;
		}
	}

	// Handles removing a friend when the remove friend button is pushed.
	public void removeButton(Event e)
	{
		// Get the id.
		bufferID = e.getID();
		new AlertDialog.Builder(this).setMessage("Are you sure you want to leave this event?").setCancelable(true)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int id)
					{
						new performActionTask().execute("http://68.59.162.183/android_connect/leave_event.php",
								Integer.toString(bufferID));
					}
				}).setNegativeButton("Cancel", null).show();

	}

	// Starts a USER/GROUP/EVENT profile
	public void startProfileActivity(View view)
	{
		loadDialog.show();
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
			nameValuePairs.add(new BasicNameValuePair("eid", urls[1]));

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
					if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()))
					{
						user.removeEventPending(bufferID);

					}
					else if (CONTENT.equals(CONTENT_TYPE.EVENTS_DECLINED.toString()))
					{
						user.removeEventDeclined(bufferID);

					}
					else if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()))
					{
						user.removeEventUpcoming(bufferID);

					}
					else if (CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()))
					{
						user.removeEventInvite(bufferID);

						// since leave_event and decline event call same php
						if (!message.equals("Event invite accepted!"))
							message = "Event invite declined!";
					}
					else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
					{
						user.removeEventPast(bufferID);
						message = "Past event removed!";

					}
					Toast toast = GLOBAL.getToast(context, message);
					toast.show();
					// reset values, looking to move these
					PANDABUFFER = "";
					bufferID = -1;

					// CALLING CORRESPONDING METHOD TO REPOPULATE
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
