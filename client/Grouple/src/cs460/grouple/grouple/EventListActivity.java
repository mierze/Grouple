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
import android.widget.Button;
import android.widget.LinearLayout;
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
	
	//CLASS-WIDE DECLARATIONS
	private User user; //user whose current groups displayed
	private Event event;
	private Bundle EXTRAS; //extras passed in from the activity that called ListActivity
	private String CONTENT; //type of content to display in list, passed in from other activities
	private LinearLayout listLayout; //layout for list activity (scrollable layout to inflate into)
	private LayoutInflater li;
	private String ROLE = "U";//defaulting to lowest level
	private String PANDABUFFER = ""; //same
	private int bufferID; //same as below: could alternatively have json return the values instead of saving here	
	private Button addNew;
	//TESTING
	private ArrayList<User> users;
	private ArrayList<Event> events;

	/* loading in everything needed to generate the list */
	public void load()
	{
		String actionBarTitle = "";
		addNew.setVisibility(View.GONE); //GONE FOR NOW
			
		//GRABBING A USER
		if (EXTRAS.getString("email") != null)
			if (GLOBAL.isCurrentUser(EXTRAS.getString("email")))
			{
				System.out.println("MAKING USER THE CURRENT USER");
				user = GLOBAL.getCurrentUser();
			}
			else if (GLOBAL.getUserBuffer() != null && GLOBAL.getUserBuffer().getEmail().equals(EXTRAS.getString("email")))
				user = GLOBAL.getUserBuffer();

		
		//CLEAR LIST
		listLayout.removeAllViews();
		
		//CALL APPROPRIATE METHODS TO POPULATE LIST
		//CONTENT_TYPE -> POPULATEGROUPS
		if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_DECLINED.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_DECLINED.toString()) || CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
		{
			//setting the bottom button gone
			if (addNew != null)
				addNew.setVisibility(View.GONE);
			//setting the actionbar text
			if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()))
				actionBarTitle = user.getFirstName() + "'s Pending Events";
			else if (CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()))
				actionBarTitle = user.getFirstName() + "'s Event Invites";
			else if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()))
				actionBarTitle = user.getFirstName() + "'s Upcoming Events";
			else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
				actionBarTitle = user.getFirstName() + "'s Past Events";
			else
				actionBarTitle = user.getFirstName() + "'s Declined Events";
			populateEvents();
		}
		//calling next functions to execute
		initActionBar(actionBarTitle, true);

	}
	
	
	//populates a list of events
	private void populateEvents()
	{
		View row;
		String sadGuyText = "";
		TextView nameTextView;
		Button removeEventButton;
		int id;
		int index;
		
		//Checking which CONTENT we need to get
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
		
		
		//looping thru and populating list of events
		if (events != null && !events.isEmpty())
		{
			String startDate = null;
			for (Event e : events) 
			{
				View dateRow = li.inflate(R.layout.list_row_date, null);	
				TextView dateTextView = (TextView) dateRow.findViewById(R.id.dateTextView);	
				String newStartDate = e.getStartTextNoTime();
				if (startDate == null)
				{
					//first event to display, show its time, and set startDate = to it
					startDate = newStartDate;
					dateTextView.setText(e.getStartTextListDisplay());
					listLayout.addView(dateRow);
				}
				else if (startDate.compareTo(newStartDate) < 0)
				{
					//if previous start date less than new start date, display date
					startDate = e.getStartTextNoTime();
					dateTextView.setText(e.getStartTextListDisplay());
					listLayout.addView(dateRow);
				}
				id = e.getID();
				index = events.indexOf(e);
				//Group group = GLOBAL.loadGroup(id);
				if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_DECLINED.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
				{
					System.out.println("IN FIRST EVENT IF");
					if (GLOBAL.isCurrentUser(user.getEmail()))
					{
						row = li.inflate(R.layout.list_row, null);	
						removeEventButton = (Button) row.findViewById(R.id.removeButtonLI);
						removeEventButton.setId(id);		
					}
					else //user does not have ability to remove events
						row = li.inflate(R.layout.list_row_nobutton, null);
					
					nameTextView =  (TextView)row.findViewById(R.id.nameTextViewLI);

					if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_DECLINED.toString()))
						nameTextView.setText(e.getName());//future get date too?
					else
						nameTextView.setText(e.getName() + "\n(" + e.getNumUsers() + " confirmed / " + e.getMinPart() + " required)");
				}
				else
				{
					row = li.inflate(R.layout.list_row_acceptdecline, null);
					nameTextView =  (TextView)row.findViewById(R.id.emailTextView);
					nameTextView.setText(e.getName() + "\n(" + e.getNumUsers() + " confirmed / " + e.getMinPart() + " required)");
				}
				//setting ids to the id of the group for button functionality
				nameTextView.setId(id);
				row.setId(id);
				//adding row to view
				listLayout.addView(row);
			}
		}
		else
		{
			//no event invites were found, show sadguy image / text
			row = li.inflate(R.layout.list_item_sadguy, null);
			TextView sadTextView = (TextView) row.findViewById(R.id.sadGuyTextView);
			//Set the sad guy text.
			sadTextView.setText(sadGuyText);
			listLayout.addView(row);
		}	
	}
			
	// based on content type, gets the corresponding role
	private void setRole()
	{
		//checking if user is in group/event
		boolean inEntity = false;
		for (User u : users)
			if (u.getEmail().equals(user.getEmail()))
				inEntity = true;
		if (inEntity) //user is in the group or event, grab their role
		{
			new getRoleTask().execute("http://68.59.162.183/android_connect/check_role_event.php", Integer.toString(event.getID()));
		}
	}
	
	//DEFAULT METHODS BELOW
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		//clearing any previous views populated, for refresh
		listLayout = ((LinearLayout)findViewById(R.id.listLayout));
		//INSTANTIATIONS
		EXTRAS =  getIntent().getExtras();
		CONTENT = EXTRAS.getString("content");
		listLayout = ((LinearLayout)findViewById(R.id.listLayout));
		li = getLayoutInflater();	
		addNew = (Button)findViewById(R.id.addNewButtonLiA);
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		load();
	}	

	//ONCLICK METHODS BELOW
	public void onClick(View view)
	{
		super.onClick(view);
		View parent = (View)view.getParent();
		switch (view.getId())
		{
		case R.id.declineButton:
			if (CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()))
			{
				bufferID = parent.getId(); //PANDA
				new performActionTask().execute("http://68.59.162.183/android_connect/leave_event.php", Integer.toString(bufferID));
			}
			break;
		case R.id.acceptButton:
			if (CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()))
			{
				bufferID = parent.getId();
				System.out.println("Accepting event invite, eid: " + bufferID);
				new performActionTask().execute("http://68.59.162.183/android_connect/accept_event_invite.php", Integer.toString(bufferID));
			}
			break;
		}
	}
	// Handles removing a friend when the remove friend button is pushed.
	public void removeButton(View view)
	{			
		if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
		{	
			//Get the id.
			bufferID = view.getId();
			System.out.println("ID IS SET TO IN PAST EVENT!! " + bufferID);
			new AlertDialog.Builder(this)
					.setMessage("Are you sure you want to leave this event?")
					.setCancelable(true)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int id)
						{
							new performActionTask().execute("http://68.59.162.183/android_connect/leave_event.php", Integer.toString(bufferID));
						}
					}).setNegativeButton("Cancel", null).show();
		}
	}

	//Starts a USER/GROUP/EVENT profile
	public void startProfileActivity(View view)
	{
		loadDialog.show();
		int id = view.getId();		
		Intent intent = new Intent(this, GroupProfileActivity.class);

		if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_DECLINED.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()) || CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()))
		{
			intent = new Intent(this, EventProfileActivity.class);
			intent.putExtra("e_id", id);
			intent.putExtra("email", user.getEmail());
			Event e = new Event(id);
			e.fetchEventInfo();
			e.fetchParticipants();
			GLOBAL.setEventBuffer(e);
		}
		
		startActivity(intent);	
	}

	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
    	//refresh pertinent info
    	
    	
 
    	if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_DECLINED.toString()))
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
	    		user.fetchEventInvites();
	    		user.fetchEventsPending();
	    		user.fetchEventsDeclined();
    		}
    	}
    	else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
    	{
    		//nothing yet
    		//user.fetchEventsInvites();
    		user.fetchEventsPast();
    	}
    	
    	//SETTING GLOBALS
    	if (user != null)
    		if (GLOBAL.isCurrentUser(user.getEmail()))
    		{
    			GLOBAL.setCurrentUser(user);
    		}
    		else
    			GLOBAL.setUserBuffer(user);
    	
    	if (event != null)
    		GLOBAL.setEventBuffer(event);
	}

	/* Gets the role of the current user in a group / event */
	private class getRoleTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = "eid";
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
				 
				//json fetch was successful
				if (jsonObject.getString("success").toString().equals("1"))
				{
					ROLE = jsonObject.getString("role").toString();
					System.out.println("ROLE IS BEING SET TO " + ROLE);
					if (!ROLE.equals("U"))
							addNew.setVisibility(View.VISIBLE);
				} 
				//unsuccessful
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
			if (CONTENT.equals(CONTENT_TYPE.EVENTS_PENDING.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString())|| CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()) || CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()))
			{
				//events pending past, upcoming or invites, all need email and eid
				nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
				nameValuePairs.add(new BasicNameValuePair("eid", urls[1]));
			}
			//calling readJSONFeed in Global, continues below in onPostExecute
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
						user.fetchEventsPending();
					}
					else if (CONTENT.equals(CONTENT_TYPE.EVENTS_DECLINED.toString()))
					{
						user.removeEventDeclined(bufferID);
						user.fetchEventsDeclined();
					}
					else if (CONTENT.equals(CONTENT_TYPE.EVENTS_UPCOMING.toString()))
					{
						user.removeEventUpcoming(bufferID);
						user.fetchEventsUpcoming();
					}
					else if (CONTENT.equals(CONTENT_TYPE.EVENT_INVITES.toString()))
					{
						user.removeEventInvite(bufferID);
						user.fetchEventInvites();
						//since leave_event and decline event call same php
						if (!message.equals("Event invite accepted!"))
							message = "Event invite declined!";
					}
					else if (CONTENT.equals(CONTENT_TYPE.EVENTS_PAST.toString()))
					{
						user.removeEventPast(bufferID);
						message = "Past event removed!";
						user.fetchEventsPast();
					}
					Toast toast = GLOBAL.getToast(context, message);
					toast.show();
					//reset values, looking to move these 
					PANDABUFFER = "";
					bufferID = -1;
					//removing all friend requests for refresh
					listLayout.removeAllViews();
					
					//CALLING CORRESPONDING METHOD TO REPOPULATE
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
