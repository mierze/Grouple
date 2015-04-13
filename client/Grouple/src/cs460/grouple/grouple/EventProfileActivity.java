package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import cs460.grouple.grouple.User.getFriendsTask;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
	LayoutInflater inflater;
	private Button profileButton1;
	private Button profileButton2;
	private Button profileButton3;
	private Button profileButton6;
	private Button itemListButton;
	private AsyncTask getImageTask;
	private TextView infoTextView;
	private TextView aboutTextView;

	private View itemListDialogView;
	private AlertDialog itemListAlertDialog;
	private GcmUtility gcmUtil;
	private ArrayList<String> itemEmails = new ArrayList<String>();
	private ArrayList<String> itemNames = new ArrayList<String>();

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
		inflater = getLayoutInflater();
		iv = (ImageView) findViewById(R.id.profileImageUPA);
		gcmUtil = new GcmUtility(GLOBAL);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		load();
	}

	public void load()
	{
		EXTRAS = getIntent().getExtras();
		String title = "";
		System.out.println("CONTENT IS SET TO " + CONTENT);

		user = GLOBAL.getCurrentUser();

		event = GLOBAL.getEventBuffer();
		// TODO: testing different colors based on event types to bring some
		// spice and push the color association
		// profileLayout.setBackgroundColor(getResources().getColor(R.color.sports_background_color));
		title = event.getName();

		setRole();

		getImageTask = new getImageTask().execute("http://68.59.162.183/android_connect/get_profile_image.php");
		populateProfile(); // populates a group / user profile

		fetchItemsToBring();
		// initializing the action bar and killswitch listener
		initActionBar(title, true);

	}

	public void loadImage(View view)
	{
		ImageView tempImageView = (ImageView) view;
		AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_image, (ViewGroup) findViewById(R.id.layout_root));
		ImageView image = (ImageView) layout.findViewById(R.id.fullImage);
		image.setImageDrawable(tempImageView.getDrawable());
		imageDialog.setView(layout);
		imageDialog.create();
		imageDialog.show();
	}

	private void fetchItemsToBring()
	{
		new getItemsToBringTask().execute("http://68.59.162.183/android_connect/get_items_tobring.php");

	}

	private class getItemsToBringTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("e_id", Integer.toString(event.getID())));
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
					// gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("itemsToBring");
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						// function adds friend to the friends map
						itemNames.add(o.getString("name"));
						itemEmails.add(o.getString("email"));
					}
				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("fetchFriends", "failed = 2 return");
				}
			}
			catch (Exception e)
			{
				Log.d("fetchFriends", "exception caught");
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
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

	// TASK FOR GRABBING IMAGE OF EVENT/USER/GROUP
	private class getImageTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String id = Integer.toString(event.getID());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("eid", id));
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
					String image = jsonObject.getString("image").toString();

					// event
					event.setImage(image);
					if (event.getImage() != null)
						iv.setImageBitmap(event.getImage());
					else
						iv.setImageResource(R.drawable.event_image_default);
					iv.setScaleType(ScaleType.CENTER_CROP);
					setNotifications(); // for group / event
				}
				else
				{
					// failed
					Log.d("fetchImage", "FAILED");
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
			// do next thing here
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
					Log.d("FETCH ROLE FAILED", "FAILED");
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
			profileButton6.setText("Edit Event");
			profileButton6.setVisibility(View.VISIBLE);
		}

	}

	public void onClick(View view)
	{
		super.onClick(view);
		loadDialog.show();
		boolean noIntent = view.getId() == R.id.backButton ? true : false;

		Intent intent = new Intent(this, ListActivity.class);
		switch (view.getId())
		{
		case R.id.profileButton1:

			// events
			intent.putExtra("CONTENT", "EVENTS_ATTENDING");
			event.fetchParticipants();
			GLOBAL.setEventBuffer(event);

			break;
		case R.id.profileButton2:
			intent = new Intent(this, EntityMessagesActivity.class);
			intent.putExtra("CONTENT", "EVENT");
			intent.putExtra("NAME", event.getName());

			break;
		case R.id.profileButton3:
			// events UPCOMING
			// join the public group
			new JoinPublicTask().execute("http://68.59.162.183/"

			+ "android_connect/join_public_event.php", user.getEmail(), "P", Integer.toString(event.getID()));
			noIntent = true;

			break;
		case R.id.itemListButton:
			itemListDialog();
			noIntent = true;
			break;
		case R.id.profileEditButton:

			intent = new Intent(this, EventEditActivity.class);
			break;
		default:
			break;
		}
		if (user != null)
		{
			if (!GLOBAL.isCurrentUser(user.getEmail()))
				GLOBAL.setUserBuffer(user);
			else
				GLOBAL.setCurrentUser(user);
			intent.putExtra("EMAIL", user.getEmail());
		}

		if (event != null)
			intent.putExtra("EID", Integer.toString(event.getID()));
		if (!noIntent) // TODO, move buttons elsewhere that dont start list
			startActivity(intent);
		else
			loadDialog.hide(); // did not launch intent, cancel load dialog
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();

		// events pending case
		GLOBAL.getCurrentUser().fetchEventsUpcoming();
		// events pending case
		GLOBAL.getCurrentUser().fetchEventsPending();
		// event invites case
		GLOBAL.getCurrentUser().fetchEventInvites();

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
		if (!itemNames.isEmpty())
		{
			for (String itemName : itemNames)
			{
				final int index = itemNames.indexOf(itemName);
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
							itemUserNameTextView.setText(user.getEmail());
						}
						else if (!itemCheckBox.isChecked())
						{
							itemCheckBox.setChecked(true);
						}
					}
				});
				if (!itemEmails.get(index).equals("null") && !itemEmails.get(index).equals(""))
				{
					itemUserNameTextView.setText(itemEmails.get(index));
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
		itemListAlertDialog.show();
	}

	/*
	 * Get profile executes get_profile.php. It uses the current users email
	 * address to retrieve the users name, age, and bio.
	 */
	public void populateProfile()
	{

		aboutTextView.setText(event.getAbout());
		// iv.setImageBitmap(event.getImage());
		String infoText = "Category: " + event.getCategory() + "\n" + event.getLocation() + "\n" + event.getStartText();
		if (event.getMaxPart() > 0)
			infoText += "\n(" + event.getNumUsers() + " confirmed / " + event.getMinPart() + " required)"
					+ "\nMax Participants: " + event.getMaxPart();
		else
			infoText += "\n(" + event.getNumUsers() + " confirmed / " + event.getMinPart() + " required)";
		infoTextView.setText(infoText);

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
					event.fetchParticipants();
					event.addToUsers(user);
					load();
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