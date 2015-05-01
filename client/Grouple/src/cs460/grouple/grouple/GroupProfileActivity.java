package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//TODO: add a button for inviting friends
/**
 * 
 * @author Brett, Todd, Scott GroupProfileActivity displays the current profile
 *         of a Group
 */
public class GroupProfileActivity extends BaseActivity
{

	private Group group;
	private User user;
	private View xpBar;
	private ImageView iv;
	private Button profileButton1;
	private Button profileButton2;
	private Button profileButton3;
	private Button profileButton6;
	private Button inviteFriendsButton;
	private ProgressBar xpProgressBar;
	private TextView xpTextView;
	private TextView levelTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_profile);

		xpBar = findViewById(R.id.xpBar);
		xpProgressBar = (ProgressBar) findViewById(R.id.xpProgressBar);
		levelTextView = (TextView) findViewById(R.id.levelTextView);
		xpTextView = (TextView) findViewById(R.id.xpTextView);
		profileButton1 = (Button) findViewById(R.id.profileButton1);
		profileButton2 = (Button) findViewById(R.id.profileButton2);
		profileButton3 = (Button) findViewById(R.id.profileButton3);
		profileButton6 = (Button) findViewById(R.id.profileEditButton);
		inviteFriendsButton = (Button) findViewById(R.id.inviteFriendsButton);
		iv = (ImageView) findViewById(R.id.profileImageUPA);
		Bundle extras = getIntent().getExtras();
		String title = "";
		user = GLOBAL.getCurrentUser();
		xpBar.setVisibility(View.VISIBLE);
		group = GLOBAL.getGroup(extras.getInt("g_id"));
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, new IntentFilter("group_data"));

		// getGroupExperience();
		fetchData();
		updateUI(); // populates a group / user profile
		// initializing the action bar and killswitch listener
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
			updateUI();// for group / event
		}
	};

	private void fetchData()
	{
		group.fetchInfo(this);
		group.fetchMembers(this);
		group.fetchImage(this);
		group.fetchExperience(this);
	}

	private void setRole()
	{
		int pub;
		String pro2Text;
		ArrayList<User> users = new ArrayList<User>();

		pub = group.getPub();
		users = group.getUsers();
		pro2Text = "Join Group";

		// checking if user is in group/event
		boolean inEntity = false;
		for (User u : users)
			if (u.getEmail().equals(user.getEmail()))
				inEntity = true;

		if (!inEntity) // user not in group, check if public so they can join
		{
			if (pub == 1)
			{

				profileButton3.setVisibility(View.VISIBLE);
				profileButton3.setText(pro2Text);

			}
			setButtons();// call here since not checking role first
		}
		else
		// user is in group, check role
		{
			new getRoleTask().execute("http://68.59.162.183/android_connect/check_role_group.php",
					Integer.toString(group.getID()));
		}
	}

	private void setExperience()
	{
		int groupExperience = group.getExperience();
		int level = 1;
		int pointsToNext = 10;
		int pointsStart = 0;
		int pointsEnd;
		while (groupExperience > (pointsStart + pointsToNext))
		{
			// means user is greater than this level threshold
			// so increase level
			level++;
			// make a new start
			pointsStart += pointsToNext;
			// recalc pointsToNext
			pointsToNext *= 2;
		}
		pointsEnd = pointsStart + pointsToNext;
		// each level pointsToNext *= level
		levelTextView.setText("Level " + level);
		xpProgressBar.setMax(pointsToNext);
		xpProgressBar.setProgress(groupExperience - pointsStart);
		xpTextView.setText(groupExperience + " / " + pointsEnd);
	}

	/* CLASS TO FETCH THE ROLE OF THE USER IN GROUP / EVENT */
	private class getRoleTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = "gid";
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

					user.addToGroupRoles(group.getID(), role);

					setButtons(); // for group / event
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
			String type = "g_id";
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
						profileButton2.setText("Group Messages (" + numUnread + " unread)");

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
		if (group.inUsers(user.getEmail()))
		{
			profileButton2.setVisibility(View.VISIBLE);
			profileButton2.setText("Group Messages");
			new getUnreadEntityMessagesTask().execute(
					"http://68.59.162.183/android_connect/get_unread_entitymessages.php",
					Integer.toString(group.getID()));
			String role = user.getGroupRole(group.getID());
			if (role.equals("A"))
			{
				profileButton6.setText("Edit Group");
				profileButton6.setVisibility(View.VISIBLE);
			}
			if (role.equals("A") || role.equals("P"))
			{
				inviteFriendsButton.setVisibility(View.VISIBLE);
			}
		}
		profileButton1.setText("Members\n(" + group.getNumUsers() + ")");

	}

	public void inviteFriendsButton(View view)
	{
		Intent intent = new Intent(this, InviteActivity.class);
		intent.putExtra("g_id", group.getID());
		startActivity(intent);
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
			// members
			intent.putExtra("content", "GROUP_MEMBERS");
			System.out.println("Loading a group with id: " + group.getID());
			break;
		case R.id.profileButton2:
			intent = new Intent(this, EntityMessagesActivity.class);
			intent.putExtra("content", "GROUP");
			break;
		case R.id.profileButton3:
			// events UPCOMING
			// join the public group

			new JoinPublicTask().execute("http://68.59.162.183/" + "android_connect/join_public_group.php",
					user.getEmail(), "P", Integer.toString(group.getID()));
			System.out.println("NOW ADDING TO  GROUP");
			noIntent = true;

			break;
		case R.id.profileEditButton:

			intent = new Intent(this, GroupEditActivity.class);

			break;
		default:
			break;
		}
		if (user != null)
		{

			intent.putExtra("email", user.getEmail());
		}
		if (group != null)
		{
			intent.putExtra("g_id", group.getID());
		}

		if (!noIntent) // TODO, move buttons elsewhere that dont start list
			startActivity(intent);
		else
			loadDialog.hide(); // did not launch intent, cancel load dialog
	}

	/*
	 * Get profile executes get_profile.php. It uses the current users email
	 * address to retrieve the users name, age, and bio.
	 */
	public void updateUI()
	{
		String title = group.getName();
		initActionBar(title, true);
		TextView aboutTitle = (TextView) findViewById(R.id.aboutTitlePA);
		TextView info = (TextView) findViewById(R.id.profileInfoTextView);
		TextView about = (TextView) findViewById(R.id.profileAboutTextView);
		setButtons();
		aboutTitle.setText("About Group:");
		// iv.setImageBitmap(group.getImage());
		info.setText("Creator: " + group.getEmail() + "\nCreated: " + group.getDateCreatedText());
		about.setText(group.getAbout());
		if (group.getImage() != null)
			iv.setImageBitmap(group.getImage());
		else
			iv.setImageResource(R.drawable.group_image_default);

		iv.setScaleType(ScaleType.CENTER_CROP);
		setRole();
		setExperience();
		initXpBar();

	}

	private void initXpBar()
	{
		int groupXp = group.getExperience();
		int level = 1;
		int pointsToNext = 10;
		int pointsStart = 0;
		int pointsEnd;
		while (groupXp > (pointsStart + pointsToNext))
		{
			// means user is greater than this level threshold
			// so increase level
			level++;
			// make a new start
			pointsStart += pointsToNext;
			// recalc pointsToNext
			pointsToNext *= 2;
		}
		pointsEnd = pointsStart + pointsToNext;
		// each level pointsToNext *= level
		levelTextView.setText("Level " + level);
		xpProgressBar.setMax(pointsToNext);
		xpProgressBar.setProgress(groupXp - pointsStart);
		xpTextView.setText(groupXp + " / " + pointsEnd);
	}

	// aSynch task to add individual member to group.
	private class JoinPublicTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = "g_id";
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
					Context context = getApplicationContext();
					Toast toast = GLOBAL.getToast(context, jsonObject.getString("message"));
					toast.show();
					profileButton3.setVisibility(View.GONE);
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
