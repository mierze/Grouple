package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
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
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/*
 * UserActivity displays the profile page of any user
 */
public class EntityProfileActivity extends BaseActivity
{
	private enum CONTENT_TYPE
	{
		GROUP, EVENT
	}

	private ImageView iv;
	private Group group;
	private Event event;
	private User user;
	private Bundle EXTRAS;
	private String CONTENT; // type of content to display in profile, passed in
							// from other activities
	private LinearLayout profileLayout;
	private View xpBar;
	private View pastEventsBadgesLayout;
	private Button profileButton1;
	private Button profileButton2;
	private Button profileButton3;
	private Button profileButton6;
	private AsyncTask getImageTask;
	private ProgressBar xpProgressBar;
	private TextView xpTextView;
	private TextView levelTextView;
	private GcmUtility gcmUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		profileLayout = (LinearLayout) findViewById(R.id.profileLayout);
		xpBar = findViewById(R.id.xpBar);
		xpProgressBar = (ProgressBar) findViewById(R.id.xpProgressBar);
		profileButton1 = (Button) findViewById(R.id.profileButton1);
		profileButton2 = (Button) findViewById(R.id.profileButton2);
		profileButton3 = (Button) findViewById(R.id.profileButton3);
		profileButton6 = (Button) findViewById(R.id.profileEditButton);
		pastEventsBadgesLayout = findViewById(R.id.profilePastEventsBadgesLayout);
		levelTextView = (TextView) findViewById(R.id.levelTextView);
		xpTextView = (TextView) findViewById(R.id.xpTextView);
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
		CONTENT = EXTRAS.getString("CONTENT");
		String title = "";
		System.out.println("CONTENT IS SET TO " + CONTENT);

		user = GLOBAL.getCurrentUser();
		if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
		{
			xpBar.setVisibility(View.VISIBLE);
			group = GLOBAL.getGroupBuffer();
			title = group.getName();
		}
		else
		{
			event = GLOBAL.getEventBuffer();
			// TODO: testing different colors based on event types to bring some
			// spice and push the color association
			profileLayout.setBackgroundColor(getResources().getColor(R.color.sports_background_color));
			title = event.getName();
		}
		setRole();

		getImageTask = new getImageTask().execute("http://68.59.162.183/android_connect/get_profile_image.php");
		populateProfile(); // populates a group / user profile

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

	private void setRole()
	{
		int pub;
		String pro2Text;
		ArrayList<User> users = new ArrayList<User>();
		if (CONTENT.equals(CONTENT_TYPE.EVENT.toString()))
		{
			pub = event.getPub();
			users = event.getUsers();
			pro2Text = "Join Event";
		}
		else
		{
			pub = group.getPub();
			users = group.getUsers();
			pro2Text = "Join Group";
		}

		// checking if user is in group/event
		boolean inEntity = false;
		for (User u : users)
			if (u.getEmail().equals(user.getEmail()))
				inEntity = true;

		if (!inEntity) // user not in group, check if public so they can join
		{
			if (pub == 1)
			{
				if (CONTENT.equals(CONTENT_TYPE.EVENT.toString()) && event.getEventState().equals("Ended"))
				{
				}// do nothing, else it is public and joinable
				else if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
				{
					profileButton3.setVisibility(View.VISIBLE);
					profileButton3.setText(pro2Text);
				}
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
			if (CONTENT.equals(CONTENT_TYPE.EVENT.toString()))
				new getRoleTask().execute("http://68.59.162.183/android_connect/check_role_event.php",
						Integer.toString(event.getID()));
			else
				new getRoleTask().execute("http://68.59.162.183/android_connect/check_role_group.php",
						Integer.toString(group.getID()));
		}
	}

	// TASK FOR GRABBING IMAGE OF EVENT/USER/GROUP
	private class getImageTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type;
			String id;

				type = (CONTENT.equals(CONTENT_TYPE.EVENT.toString())) ? "eid" : "gid";
				id = (CONTENT.equals(CONTENT_TYPE.EVENT.toString())) ? Integer.toString(event.getID()) : Integer
						.toString(group.getID());
	

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
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
					String image = jsonObject.getString("image").toString();
					
					if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
					{
						group.setImage(image);
						if (group.getImage() != null)
							iv.setImageBitmap(group.getImage());
						else
							iv.setImageResource(R.drawable.group_image_default);
					}
					else
					{
						// event
						event.setImage(image);
						if (event.getImage() != null)
							iv.setImageBitmap(event.getImage());
						else
							iv.setImageResource(R.drawable.event_image_default);
					}
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
			String type = (CONTENT.equals(CONTENT_TYPE.EVENT.toString())) ? "eid" : "gid";
			String email = user.getEmail();
			String id = urls[1];
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
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
					if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
						user.addToGroupRoles(group.getID(), role);
					else
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
			String type = (CONTENT.equals(CONTENT_TYPE.EVENT.toString())) ? "e_id" : "g_id";
			String email = user.getEmail();
			String id = urls[1];
			System.out.println("EMAIL, type" + email + " " + type + "id:" + id);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
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
					if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
						profileButton2.setText("Group Messages (" + numUnread + " unread)");
					else
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
		if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
		{
			if (group.inUsers(user.getEmail()))
			{
				profileButton2.setVisibility(View.VISIBLE);
				profileButton2.setText("Group Messages");
				new getUnreadEntityMessagesTask().execute(
						"http://68.59.162.183/android_connect/get_unread_entitymessages.php",
						Integer.toString(group.getID()));
			}
			profileButton1.setText("Members\n(" + group.getNumUsers() + ")");

			if (user.getGroupRole(group.getID()) != null && user.getGroupRole(group.getID()).equals("A"))
			{
				profileButton6.setText("Edit Group");
				profileButton6.setVisibility(View.VISIBLE);
			}
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENT.toString()))
		{
			if (event.inUsers(user.getEmail()))
			{
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
			if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
			{
				// members
				intent.putExtra("CONTENT", "GROUPS_MEMBERS");
				System.out.println("Loading a group with id: " + group.getID());
				group.fetchMembers();
				GLOBAL.setGroupBuffer(group);
				intent.putExtra("GID", group.getID());
			}
			else
			{
				// events
				intent.putExtra("CONTENT", "EVENTS_ATTENDING");
				event.fetchParticipants();
				GLOBAL.setEventBuffer(event);
			}
			break;
		case R.id.profileButton2:
			if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
			{
				intent = new Intent(this, EntityMessagesActivity.class);
				intent.putExtra("CONTENT", "GROUP");
				intent.putExtra("NAME", group.getName());
			}
			else
			{
				intent = new Intent(this, EntityMessagesActivity.class);
				intent.putExtra("CONTENT", "EVENT");
				intent.putExtra("NAME", event.getName());
			}
			break;
		case R.id.profileButton3:
			// events UPCOMING
			// join the public group
			if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
			{
				new JoinPublicTask().execute("http://68.59.162.183/" + "android_connect/join_public_group.php",
						user.getEmail(), "P", Integer.toString(group.getID()));
				System.out.println("NOW ADDING TO  GROUP");
				noIntent = true;

			}
			else if (CONTENT.equals(CONTENT_TYPE.EVENT))// join the public event
			{
				new JoinPublicTask().execute("http://68.59.162.183/"

				+ "android_connect/join_public_event.php", user.getEmail(), "P", Integer.toString(event.getID()));
				noIntent = true;
			}
			break;
		case R.id.profileEditButton:
			if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
				intent = new Intent(this, GroupEditActivity.class);
			else
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
		if (group != null)
		{
			intent.putExtra("GID", Integer.toString(group.getID()));
			GLOBAL.setGroupBuffer(group);
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
		if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
		{
			// current groups case
			GLOBAL.getCurrentUser().fetchGroups();
			// group invites case
			GLOBAL.getCurrentUser().fetchGroupInvites();
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENT.toString()))
		{
			// events pending case
			GLOBAL.getCurrentUser().fetchEventsUpcoming();
			// events pending case
			GLOBAL.getCurrentUser().fetchEventsPending();
			// event invites case
			GLOBAL.getCurrentUser().fetchEventsInvites();
		}
	}

	/*
	 * Get profile executes get_profile.php. It uses the current users email
	 * address to retrieve the users name, age, and bio.
	 */
	public void populateProfile()
	{
		TextView aboutTitle = (TextView) findViewById(R.id.aboutTitlePA);
		TextView info = (TextView) findViewById(R.id.profileInfoTextView);
		TextView about = (TextView) findViewById(R.id.profileAboutTextView);
		if (CONTENT.equals(CONTENT_TYPE.GROUP.toString()))
		{
			aboutTitle.setText("About Group:");
			// iv.setImageBitmap(group.getImage());
			info.setText("Creator: " + group.getEmail() + "\nCreated: " + group.getDateCreatedText());
			about.setText(group.getAbout());
		}
		else if (CONTENT.equals(CONTENT_TYPE.EVENT.toString()))
		{
			aboutTitle.setText("About Event:");
			about.setText(event.getAbout());
			// iv.setImageBitmap(event.getImage());
			String infoText = "Category: " + event.getCategory() + "\n" + event.getLocation() + "\n"
					+ event.getStartText();
			if (event.getMaxPart() > 0)
				infoText += "\n(" + event.getNumUsers() + " confirmed / " + event.getMinPart() + " required)"
						+ "\nMax Participants: " + event.getMaxPart();
			else
				infoText += "\n(" + event.getNumUsers() + " confirmed / " + event.getMinPart() + " required)";
			info.setText(infoText);
		}
	}

	// aSynch task to add individual member to group.
	private class JoinPublicTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = (CONTENT.equals(CONTENT_TYPE.EVENT.toString())) ? "e_id" : "g_id";
			System.out.println("ABOUT TO START JOIN EVENT now type is " + type);
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
					if (CONTENT.equals(CONTENT_TYPE.EVENT.toString()))
					{
						event.fetchParticipants();
						event.addToUsers(user);
					}
					else
					{
						group.fetchMembers();
						group.addToUsers(user);
					}
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
