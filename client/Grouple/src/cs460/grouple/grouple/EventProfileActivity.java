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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Brett, Todd, Scott EventProfileActivity displays the current profile
 *         of an Event
 * 
 */
public class EventProfileActivity extends BaseActivity
{
	private ImageView iv;
	private Event event;
	private User user;
	private Group group;
	private String gIdInvited;
	private LinearLayout eventContainer;
	private Button attendingButton;
	private Button messagesButton;
	private Button joinButton;
	private Button editButton;
	private Button inviteButton;
	private Button checklistButton;
	private TextView aboutTextView;
	private View checklistDialogView;
	private AlertDialog checklistAlertDialog;
	private ArrayList<EventItem> items = new ArrayList<EventItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_profile);
		Bundle extras = getIntent().getExtras();
		
		//init views
		eventContainer = (LinearLayout) findViewById(R.id.eventContainer);
		attendingButton = (Button) findViewById(R.id.attendingButton);
		messagesButton = (Button) findViewById(R.id.messagesButton);
		joinButton = (Button) findViewById(R.id.joinButton);
		editButton = (Button) findViewById(R.id.editButton);
		inviteButton = (Button) findViewById(R.id.inviteButton);
		aboutTextView = (TextView) findViewById(R.id.aboutTextView);
		checklistButton = (Button) findViewById(R.id.checklistButton);
		iv = (ImageView) findViewById(R.id.eventImageView);
		
		gIdInvited = extras.getString("g_id");
		if (gIdInvited != null)
			group = GLOBAL.getGroup(Integer.parseInt(gIdInvited));
		//init variables
		user = GLOBAL.getCurrentUser();
		event = GLOBAL.getEvent(extras.getInt("e_id"));
		items = event.getItems();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, new IntentFilter("event_data"));
		fetchData();
		updateUI();
	}

	@Override
	protected void onPause()
	{
		LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver);
		super.onPause();
	}

	// This listens for pings from the data service to let it know that there
	// are updates
	private BroadcastReceiver dataReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			updateUI();// for event
		}
	};

	private void fetchData()
	{
		event.fetchInfo(this);
		event.fetchParticipants(this);
		event.fetchImage(this);
		event.fetchItems(this);		
		new getRoleTask().execute("http://68.59.162.183/android_connect/check_role_event.php",
				Integer.toString(event.getID()));
		new getUnreadEntityMessagesTask().execute(
				"http://68.59.162.183/android_connect/get_unread_entitymessages.php",
				Integer.toString(event.getID()));
	}


	// task to fetch role in event
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
				// json fetch was successful
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// adding role to users array of roles for future reference
					String role = jsonObject.getString("role").toString();

					user.addToEventRoles(event.getID(), role);
					updateUI(); // for group / event
				}
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

	/* CLASS TO FETCH THE ROLE OF THE USER IN GROUP / EVENT */
	private class getUnreadEntityMessagesTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = "e_id";
			String email = user.getEmail();
			String id = urls[1];
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair(type, id));
			nameValuePairs.add(new BasicNameValuePair("email", email));
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
					// adding role to users array of roles for future reference
					int numUnread = jsonObject.getInt("numUnread");
					if (numUnread > 0)
						messagesButton.setText("Event Messages (" + numUnread + " unread)");
				}
				else
				{
					// failed
					Log.d("getUnreadMessages", "FAILED");
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	private void setButtons()
	{
		messagesButton.setVisibility(View.GONE);
		inviteButton.setVisibility(View.GONE);
		attendingButton.setVisibility(View.GONE);
		editButton.setVisibility(View.GONE);
		joinButton.setVisibility(View.GONE);
		messagesButton.setVisibility(View.GONE);
		
		String eventState = event.getEventState();
		
		//in event
		if (event.inUsers(user.getEmail()))
		{
			
			if (eventState.equals("Ended"))
				attendingButton.setText("Participants (" + event.getNumUsers() + ")");
			else if (eventState.equals("Declined") || eventState.equals("Pending"))
				attendingButton.setText("Confirmed (" + event.getNumUsers() + ")");
			else
				attendingButton.setText("Attending (" + event.getNumUsers() + ")");
			String role = user.getEventRole(event.getID());
			messagesButton.setVisibility(View.VISIBLE);
			attendingButton.setVisibility(View.VISIBLE);
			
			//admin and not ended
			if (role.equals("A") && !eventState.equals("Ended"))
			{
				if (eventState.equals("Declined"))
					editButton.setText("Edit and Repropose Event");
				else
					editButton.setText("Edit Event");
				editButton.setVisibility(View.VISIBLE);
			}
			//admin or promoter
			if ((role.equals("A") || role.equals("P"))
					&& (!eventState.equals("Ended") && !eventState.equals("Declined")))
			{
				inviteButton.setVisibility(View.VISIBLE);
			}
			int numUnclaimed = event.getNumUnclaimedItems();
			if (!event.getItems().isEmpty())
			{
				if (numUnclaimed > 0)
				{
					checklistButton.setText("Item Checklist (" + numUnclaimed + " unclaimed)");
				}
				if (!event.getEventState().equals("Ended") && !event.getEventState().equals("Declined"))
					checklistButton.setVisibility(View.VISIBLE);
			}
		}
		//not in event, it is public and ready to join
		else if (!eventState.equals("Ended") && !eventState.equals("Declined")
				&& event.getPub() == 1)
		{
			joinButton.setVisibility(View.VISIBLE);
		}
		else if (group != null && group.inUsers(user.getEmail()))
		{
			joinButton.setVisibility(View.VISIBLE);
		}

	}

	private void updateItemChecklist()
	{
		for (EventItem item : items)
		{
			String id = Integer.toString(item.getID());
			// grab the email of friend to add
			String email = item.getEmail();
			String name = item.getName();
			String eventID = Integer.toString(event.getID());
			// grab the role of friend to add
			if (email.equals(user.getEmail()) || email.equals(""))
			{
				new updateItemChecklistTask().execute("http://68.59.162.183/android_connect/update_item_checklist.php",
						id, email, name, eventID, "update");
			}
		}
	}

	private class updateItemChecklistTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("id", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("email", urls[2]));
			nameValuePairs.add(new BasicNameValuePair("name", urls[3]));
			nameValuePairs.add(new BasicNameValuePair("e_id", urls[4]));
			nameValuePairs.add(new BasicNameValuePair("type", urls[5]));
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
					fetchData();
				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("readMessage", "failed = 2 return");
				}
			}
			catch (Exception e)
			{
				Log.d("readMessage", "exception caught");
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
	//ONCLICK BUTTONS

	public void inviteButton(View view)
	{
		Intent intent = new Intent(this, EventAddGroupsActivity.class);
		intent.putExtra("e_id", event.getID());
		startActivity(intent);
	}

	public void editButton(View v)
	{
		loadDialog.show();
		Intent intent = new Intent(this, EventEditActivity.class);
		// add reprososed extra if clicking this button when it says
		// "repropose event" instead of "edit event"

		if (!editButton.getText().toString().equals("Edit Event"))
		{
			System.out.println("adding reproposed extra!");
			intent.putExtra("reproposed", 1);
			// finish();
		}
		if (user != null)
			intent.putExtra("email", user.getEmail());
		intent.putExtra("e_id", event.getID());
		startActivity(intent);
	}

	public void attendingButton(View v)
	{
		loadDialog.show();
		Intent intent = new Intent(this, UserListActivity.class);
		intent.putExtra("content", "EVENT_PARTICIPANTS");
		if (user != null)
			intent.putExtra("email", user.getEmail());
		intent.putExtra("e_id", event.getID());
		startActivity(intent);
	}

	public void messagesButton(View v)
	{
		loadDialog.show();
		Intent intent = new Intent(this, EntityMessagesActivity.class);
		intent.putExtra("content", "EVENT");
		if (user != null)
			intent.putExtra("email", user.getEmail());
		intent.putExtra("e_id", event.getID());
		startActivity(intent);
	}

	public void checklistButton(View v)
	{
		// make our builder
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Item Checklist");
		// inflate dialog with dialog layout for the list
		checklistDialogView = inflater.inflate(R.layout.dialog_itemlist, null);
		LinearLayout itemListLayout = (LinearLayout) checklistDialogView.findViewById(R.id.itemListLayout);
		// populate list with items
		if (!items.isEmpty())
		{
			for (final EventItem item : items)
			{
				final int id = item.getID();
				// grab the email of friend to add
				final String itemName = item.getName();
				final String email = item.getEmail();
				// grab the role of friend to add

				System.out.println("itemName found was: " + itemName + "\nemail of item was: " + email);

				View row = inflater.inflate(R.layout.list_row_checklist, null);
				final CheckBox itemCheckBox = (CheckBox) row.findViewById(R.id.itemCheckBox);
				final TextView itemUserNameTextView = (TextView) row.findViewById(R.id.itemUsernameTextView);

				itemCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
				{
					@Override
					public void onCheckedChanged(CompoundButton view, boolean isChecked)
					{
						if (itemCheckBox.isChecked())
						{
							if (itemUserNameTextView.getText().equals(""))
							{
								itemUserNameTextView.setText(user.getEmail());
								item.setEmail(user.getEmail());
							}
						}
						else if (!itemCheckBox.isChecked())
						{
							if (!itemUserNameTextView.getText().equals(user.getEmail()))
							{
								System.out.println("this is not your claim");
								itemCheckBox.setChecked(true);
								Context context = getApplicationContext();
								Toast toast = GLOBAL.getToast(context, "Sorry, someone has already claimed that item.");
								toast.show();
							}
							else
							{
								System.out.println("this is your claim... you change it, if you wish.");
								itemCheckBox.setChecked(false);
								itemUserNameTextView.setText("");
								item.setEmail("");
							}
						}
					}
				});
				if (!email.equals("null") && !email.equals(""))
				{
					System.out.println("activating a item row!");
					itemUserNameTextView.setText(email);
					itemCheckBox.setChecked(true);
				}
				else
				{
					itemCheckBox.setChecked(false);
				}

				TextView itemNameTextView = (TextView) row.findViewById(R.id.itemNameTextView);
				itemNameTextView.setText(itemName);
				itemListLayout.addView(row);
			}
		}
		// for itemNames -> make a new checklist row
		// make this save on x out or back
		builder.setView(checklistDialogView);

		checklistAlertDialog = builder.create();
		checklistAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			@Override
			public void onCancel(DialogInterface dialog)
			{
				updateItemChecklist();
			}
		});
		checklistAlertDialog.show();
		// updateItemChecklist();
	}

	public void joinButton(View v)
	{
		// joining public event, defaulting to a promoter status
		new JoinPublicTask().execute("http://68.59.162.183/android_connect/join_public_event.php", user.getEmail(),
				"P", Integer.toString(event.getID()));
	}

	//updating views
	public void updateUI()
	{
		String title = event.getName();
		initActionBar(title, true);
		
		title = event.getName();

		if (event.getCategory().equals("Social"))
		{
			eventContainer.setBackgroundColor(getResources().getColor(R.color.light_red));
		}
		else if (event.getCategory().equals("Professional"))
		{
			eventContainer.setBackgroundColor(getResources().getColor(R.color.light_blue));
		}
		else if (event.getCategory().equals("Fitness"))
		{
			eventContainer.setBackgroundColor(getResources().getColor(R.color.light_yellow));
		}
		else if (event.getCategory().equals("Nature"))
		{
			eventContainer.setBackgroundColor(getResources().getColor(R.color.light_green));
		}
		else if (event.getCategory().equals("Entertainment"))
		{
			eventContainer.setBackgroundColor(getResources().getColor(R.color.light_purple));
		}


		// iv.setImageBitmap(event.getImage());
		String about = "Category: " + event.getCategory() + "\n" + event.getLocation() + "\n" + event.getStartText();
		if (event.getMaxPart() > 0)
			about += "\n(" + event.getNumUsers() + " confirmed / " + event.getMinPart() + " required)"
					+ "\nMax Participants: " + event.getMaxPart();
		else
			about += "\n(" + event.getNumUsers() + " confirmed / " + event.getMinPart() + " required)";
		aboutTextView.setText(about);
		if (event.getImage() != null)
			iv.setImageBitmap(event.getImage());
		else
			iv.setImageResource(R.drawable.image_default);
		iv.setScaleType(ScaleType.CENTER_CROP);
		setButtons(); // for group / event
	}

	// aSynch task to add individual member to group.
	private class JoinPublicTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			System.out.println("urls are " + urls[1] + " " + urls[2] + " " + urls[3]);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("role", urls[2]));
			nameValuePairs.add(new BasicNameValuePair("e_id", urls[3]));
			// pass url and nameValuePairs off to GLOBAL to do the JSON call.
			// Code continues at onPostExecute when JSON returns.
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				// member has been successfully added
				if (jsonObject.getString("success").toString().equals("1"))
				{
					System.out.println("USER HAS SUCCESSFULLY BEEN ADDED");
					Context context = getApplicationContext();
					Toast toast = GLOBAL.getToast(context, jsonObject.getString("message"));
					toast.show();
					messagesButton.setVisibility(View.GONE);

					fetchData();
					// all working correctly, continue to next user or finish.
					loadDialog.hide();
				}
				else if (jsonObject.getString("success").toString().equals("0"))
				{
					// a particular user was unable to be added to database for
					// some reason...
					// Don't tell the user!
				}
			}
			catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}
		}
	}
}
