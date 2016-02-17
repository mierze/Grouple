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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Brett, Todd, Scott 
 * EventListActivity displays vertical event lists of
 *         different types and performs relevant functions based on the
 *         situation and type
 */
public class EventListActivity extends BaseActivity
{
	/*
	 * All possible content types that list activity supports
	 */
	enum CONTENT_TYPE
	{
		EVENTS_UPCOMING, EVENTS_PENDING, EVENTS_PAST, EVENT_INVITES, EVENTS_DECLINED, GROUP_UPCOMING, GROUP_PAST, GROUP_PENDING;
	}

	// CLASS-WIDE DECLARATIONS
	private User user; // user whose current groups displayed
	private Group group;
	private String EMAIL; 
	// type of content to display in list, passed in from other activities
	private String CONTENT;
	private ListView listView; 
	private String sadGuyText = "";
	private LinearLayout sadGuyLayout;
	private GcmUtility gcmUtil;
	// TESTING
	private ArrayList<Event> events;
	private int e_id;


	// DEFAULT METHODS BELOW
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		// grab xml views
		listView = (ListView) findViewById(R.id.listView);
		sadGuyLayout = (LinearLayout) findViewById(R.id.sadGuyLayout);
		
		// init variables
		Bundle extras = getIntent().getExtras();
		CONTENT = extras.getString("content");
		EMAIL = extras.getString("email");
		String actionBarTitle = "";
		try
		{
			gcmUtil = new GcmUtility(GLOBAL);
		}
		catch (Exception e)
		{

		}

		// get a user
		if (EMAIL != null)
			user = GLOBAL.getUser(EMAIL);
		else
			user = GLOBAL.getCurrentUser();

		//check for group to grab
		if (CONTENT != null)
			if (CONTENT.equals(CONTENT_TYPE.GROUP_PAST.toString())
					|| CONTENT.equals(CONTENT_TYPE.GROUP_PENDING.toString())
					|| CONTENT.equals(CONTENT_TYPE.GROUP_UPCOMING.toString()))
			{
				group = GLOBAL.getGroup(extras.getInt("g_id"));
			}
		
		// setting the actionbar text, sadguy text and init events to display
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
		else if (CONTENT.equals(CONTENT_TYPE.GROUP_PAST.toString()))
		{
			events = group.getEventsPast();
			sadGuyText = "No past events.";
			actionBarTitle = "Past Events";
		}
		else if (CONTENT.equals(CONTENT_TYPE.GROUP_UPCOMING.toString()))
		{
			events = group.getEventsUpcoming();
			sadGuyText = "No upcoming groups.";
			actionBarTitle = "Upcoming Events";
		}
		else if (CONTENT.equals(CONTENT_TYPE.GROUP_PENDING.toString()))
		{
			events = group.getEventsPending();
			sadGuyText = "No pending events";
			actionBarTitle = "Pending Events";
		}
		else
		{
			events = user.getEventsDeclined();
			sadGuyText = "You do not have any events declined.";
			actionBarTitle = user.getFirstName() + "'s Declined Events";
		}
		initActionBar(actionBarTitle, true);
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
			else if (CONTENT.equals(CONTENT_TYPE.GROUP_UPCOMING.toString())
					|| CONTENT.equals(CONTENT_TYPE.GROUP_PAST.toString())
					|| CONTENT.equals(CONTENT_TYPE.GROUP_PENDING.toString()))
			{
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
			listView.setVisibility(View.VISIBLE);
			sadGuyLayout.setVisibility(View.GONE);
			ArrayAdapter<Event> adapter = new EventListAdapter();
			listView.setAdapter(adapter);
		}
		else
		{
			View sadGuyView = inflater.inflate(R.layout.list_item_sadguy, null);
			TextView sadGuyTextView = (TextView) sadGuyView.findViewById(R.id.sadGuyTextView);
			sadGuyTextView.setText(sadGuyText);
			sadGuyLayout.removeAllViews();
			sadGuyLayout.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			sadGuyLayout.addView(sadGuyView);
		}
	}

	//fetching appropriate data to display
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
		else if (CONTENT.equals(CONTENT_TYPE.GROUP_UPCOMING.toString())
				|| CONTENT.equals(CONTENT_TYPE.GROUP_PAST.toString())
				|| CONTENT.equals(CONTENT_TYPE.GROUP_PENDING.toString()))
		{
			group.fetchEvents(this);
		}
	}

	// ONCLICK METHODS BELOW
	public void onClick(View view)
	{
		super.onClick(view);
		View parent = (View) view.getParent();
		int e_id = parent.getId();
		// Set class variable.
		this.e_id = e_id;
		switch (view.getId())
		{
		case R.id.declineButton:
			if (CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()))
			{

				new performActionTask().execute("http://mierze.gear.host/grouple/android_connect/leave_event.php",
						Integer.toString(e_id));
			}
			break;
		case R.id.acceptButton:
			if (CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()))
			{
				new performActionTask().execute("http://mierze.gear.host/grouple/android_connect/accept_event_invite.php",
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

		// check first to see if we're in 'declined' or 'past'
		if (CONTENT.equals(CONTENT_TYPE.EVENTS_DECLINED.toString()))
		{
			// simply hide event from list
			new AlertDialog.Builder(this)
					.setMessage("Are you sure?  This will remove the event from your 'Declined Events' history.")
					.setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int id)
						{
							new performActionTask().execute(
									"http://mierze.gear.host/grouple/android_connect/update_event_member.php",
									Integer.toString(e_id));
						}
					}).setNegativeButton("Cancel", null).show();
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
		{
			// simply hide event from list
			new AlertDialog.Builder(this)
					.setMessage("Are you sure?  This will remove the event from your 'Past Events' history.")
					.setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int id)
						{
							new performActionTask().execute(
									"http://mierze.gear.host/grouple/android_connect/update_event_member.php",
									Integer.toString(e_id));
						}
					}).setNegativeButton("Cancel", null).show();
		}
		else
		{
			// actually leave the event
			new AlertDialog.Builder(this).setMessage("Are you sure you want to leave this event?").setCancelable(true)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int id)
						{
							new performActionTask().execute("http://mierze.gear.host/grouple/android_connect/leave_event.php",
									Integer.toString(e_id));
						}
					}).setNegativeButton("Cancel", null).show();
		}
	}

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
			// TODO: change to e_id

			if (urls[0].equals("http://mierze.gear.host/grouple/android_connect/update_event_member.php"))
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

						if (jsonObject.getString("confirmed").toString().equals("1"))
						{
							String eName = " ";

							for (Event e : events)
							{
								if (e.getID() == e_id)
								{
									eName = e.getName();
								}
							}
							try
							{
								gcmUtil.sendEventApproved(eName, Integer.toString(e_id));
							}
							catch (Exception e)
							{
							}
						}
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
