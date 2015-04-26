package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;
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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*
 * UserActivity displays the profile page of any user
 */
public class EventProfileActivity extends BaseActivity
{
	private ImageView iv;
	private Event event;
	private User user;
	private Bundle EXTRAS;
	private String CONTENT; // type of content to display in profile, passed in
							// from other activities
	private LinearLayout profileLayout;
	private Button profileButton1;
	private Button profileButton2;
	private Button profileButton3;
	private Button profileButton6;
	private Button itemListButton;
	private TextView infoTextView;
	private TextView aboutTextView;
	private View itemListDialogView;
	private AlertDialog itemListAlertDialog;
	private GcmUtility gcmUtil;
	private ArrayList<EventItem> items = new ArrayList<EventItem>();

	@Override
	protected void onResume()
	{
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("event_data"));
		fetchData();
		updateUI();
	}

	@Override
	protected void onPause()
	{
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
		super.onPause();

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

			updateUI();// for group / event
		}
	};

	private void fetchData()
	{
		event.fetchInfo(this);
		event.fetchParticipants(this);
		event.fetchImage(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_profile);
		profileLayout = (LinearLayout) findViewById(R.id.profileLayout);

		profileButton1 = (Button) findViewById(R.id.profileButton1);
		profileButton2 = (Button) findViewById(R.id.profileButton2);
		profileButton3 = (Button) findViewById(R.id.profileButton3);
		profileButton6 = (Button) findViewById(R.id.profileEditButton);
		infoTextView = (TextView) findViewById(R.id.profileInfoTextView);
		aboutTextView = (TextView) findViewById(R.id.profileAboutTextView);
		itemListButton = (Button) findViewById(R.id.itemListButton);
		iv = (ImageView) findViewById(R.id.profileImageUPA);
		gcmUtil = new GcmUtility(GLOBAL);
		EXTRAS = getIntent().getExtras();
		user = GLOBAL.getCurrentUser();
		event = GLOBAL.getEvent(EXTRAS.getInt("e_id"));
		
		
	}

	private void setRole()
	{
		int pub;
		String pro2Text;
		ArrayList<User> users = new ArrayList<User>();
		pub = event.getPub();
		users = event.getUsers();
		pro2Text = "Join Event";
		// checking if user is in group/event
		boolean inEntity = false;
		for (User u : users)
			if (u.getEmail().equals(user.getEmail()))
				inEntity = true;
		if (!inEntity) // user not in group, check if public so they can join
		{
			if (pub == 1)
			{
				if (event.getEventState().equals("Ended"))
				{
				}// do nothing, else it is public and joinable
				else
				{
					profileButton3.setVisibility(View.VISIBLE);
					profileButton3.setText(pro2Text);
				}
			}
			setNotifications();// call here since not checking role first
		}
		else
		// user is in group, check role
		{
			// and check for not past
			new getRoleTask().execute("http://68.59.162.183/android_connect/check_role_event.php",
					Integer.toString(event.getID()));

		}
	}

	/* CLASS TO FETCH THE ROLE OF THE USER IN GROUP / EVENT */
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
					setNotifications(); // for group / event
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
						profileButton2.setText("Event Messages (" + numUnread + " unread)");
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

	private void setNotifications()
	{

		if (event.inUsers(user.getEmail()))
		{
			itemListButton.setVisibility(View.VISIBLE);
			profileButton2.setVisibility(View.VISIBLE);
			profileButton2.setText("Event Messages");
			new getUnreadEntityMessagesTask().execute(
					"http://68.59.162.183/android_connect/get_unread_entitymessages.php",
					Integer.toString(event.getID()));
		}
		profileButton1.setText("Attending (" + event.getNumUsers() + ")");
		if (user.getEventRole(event.getID()) != null && user.getEventRole(event.getID()).equals("A")
				&& !event.getEventState().equals("Ended"))
		{
			if (event.getEventState().equals("Declined"))
				profileButton6.setText("Edit and Re-propose Event");
			else
				profileButton6.setText("Edit Event");
			profileButton6.setVisibility(View.VISIBLE);
		}
	}

	private int updateItemChecklist()
	{
		for (EventItem item : items)
		{
			int id = item.getID();
			// grab the email of friend to add
			String email = item.getEmail();
			// grab the role of friend to add
			if (email.equals(user.getEmail()) || email.equals(""))
			{
				new updateItemChecklistTask().execute("http://68.59.162.183/android_connect/update_item_checklist.php",
						Integer.toString(id), email);
			}
		}
		return 1;
	}

	private class updateItemChecklistTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("id", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("email", urls[2]));
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
					System.out.println("WE HAD SUCCESS IN UPDATING TO BRING!");
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

	public void onClick(View view)
	{
		super.onClick(view);
		loadDialog.show();
		boolean noIntent = view.getId() == R.id.backButton ? true : false;

		Intent intent = new Intent(this, UserListActivity.class);
		switch (view.getId())
		{
		case R.id.profileButton1:

			// events
			intent.putExtra("content", "EVENT_PARTICIPANTS");
			break;
		case R.id.profileButton2:
			intent = new Intent(this, EntityMessagesActivity.class);
			intent.putExtra("content", "EVENT");
			intent.putExtra("name", event.getName());

			break;
		case R.id.profileButton3:
			// joining public event, defaulting to a promoter status
			new JoinPublicTask().execute("http://68.59.162.183/android_connect/join_public_event.php", user.getEmail(),
					"P", Integer.toString(event.getID()));
			noIntent = true;
			break;
		case R.id.itemListButton:
			itemListDialog();
			noIntent = true;
			break;
		case R.id.profileEditButton:
			intent = new Intent(this, EventEditActivity.class);
			// add reprososed extra if clicking this button when it says
			// "repropose event" instead of "edit event"
			if (!profileButton6.getText().toString().equals("Edit Event"))
			{
				System.out.println("adding reproposed extra!");
				intent.putExtra("reproposed", 1);
				finish();
			}
			break;
		default:
			break;
		}
		intent.putExtra("email", user.getEmail());

		intent.putExtra("e_id", event.getID());
		if (!noIntent) // TODO, move buttons elsewhere that dont start list
			startActivity(intent);
		else
			loadDialog.hide(); // did not launch intent, cancel load dialog
	}

	private void itemListDialog()
	{
		// make our builder
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Item Checklist");
		// inflate dialog with dialog layout for the list
		itemListDialogView = inflater.inflate(R.layout.dialog_itemlist, null);
		LinearLayout itemListLayout = (LinearLayout) itemListDialogView.findViewById(R.id.itemListLayout);
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
		builder.setView(itemListDialogView);

		itemListAlertDialog = builder.create();
		itemListAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			@Override
			public void onCancel(DialogInterface dialog)
			{
				updateItemChecklist();
			}
		});
		itemListAlertDialog.show();
		// updateItemChecklist();
	}

	/*
	 * Get profile executes get_profile.php. It uses the current users email
	 * address to retrieve the users name, age, and bio.
	 */
	public void updateUI()
	{
		String title = event.getName();
		// initializing the action bar and killswitch listener
		initActionBar(title, true);
		// TODO: testing different colors based on event types to bring some
		// spice and push the color association
		// profileLayout.setBackgroundColor(getResources().getColor(R.color.sports_background_color));
		title = event.getName();

		if (event.getCategory().equals("Social"))
		{
			profileLayout.setBackgroundColor(getResources().getColor(R.color.light_red));
		}
		else if (event.getCategory().equals("Professional"))
		{
			profileLayout.setBackgroundColor(getResources().getColor(R.color.light_blue));
		}
		else if (event.getCategory().equals("Fitness"))
		{
			profileLayout.setBackgroundColor(getResources().getColor(R.color.light_yellow));
		}
		else if (event.getCategory().equals("Nature"))
		{
			profileLayout.setBackgroundColor(getResources().getColor(R.color.light_green));
		}
		else if (event.getCategory().equals("Entertainment"))
		{
			profileLayout.setBackgroundColor(getResources().getColor(R.color.light_purple));
		}
		setRole();
		items = event.getItems();
		int numUnclaimed = event.getNumUnclaimedItems();
		if (event.getItems().isEmpty())
		{
			itemListButton.setVisibility(View.GONE);
		}
		else if (numUnclaimed > 0)
		{
			itemListButton.setText("Item Checklist (" + numUnclaimed + " unclaimed)");
		}
		else if (numUnclaimed == 0)
		{
			itemListButton.setText("Item Checklist");
		}

		aboutTextView.setText(event.getAbout());
		// iv.setImageBitmap(event.getImage());
		String infoText = "Category: " + event.getCategory() + "\n" + event.getLocation() + "\n" + event.getStartText();
		if (event.getMaxPart() > 0)
			infoText += "\n(" + event.getNumUsers() + " confirmed / " + event.getMinPart() + " required)"
					+ "\nMax Participants: " + event.getMaxPart();
		else
			infoText += "\n(" + event.getNumUsers() + " confirmed / " + event.getMinPart() + " required)";
		infoTextView.setText(infoText);
		if (event.getImage() != null)
			iv.setImageBitmap(event.getImage());
		else
			iv.setImageResource(R.drawable.event_image_default);
		iv.setScaleType(ScaleType.CENTER_CROP);
		setNotifications(); // for group / event
	}

	// aSynch task to add individual member to group.
	private class JoinPublicTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = "e_id";
			System.out.println("urls are " + urls[1] + " " + urls[2] + " " + urls[3]);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("role", urls[2]));
			nameValuePairs.add(new BasicNameValuePair(type, urls[3]));
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
					profileButton2.setVisibility(View.GONE);

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
