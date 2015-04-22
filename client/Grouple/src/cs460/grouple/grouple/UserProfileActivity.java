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
import android.widget.ImageButton;
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
public class UserProfileActivity extends BaseActivity
{
	private ImageView iv;
	private User user; // user who's profile this is
	private Bundle EXTRAS;
	private LinearLayout profileLayout;
	private View xpBar;
	private View pastEventsBadgesLayout;
	private Button profileButton1;
	private Button profileButton2;
	private Button profileButton3;
	private Button profileButton4;// for user profile, is past events
	private Button profileButton5;
	private Button profileButton6;
	private AsyncTask getImageTask;
	private ProgressBar xpProgressBar;
	private TextView xpTextView;
	private TextView levelTextView;
	private GcmUtility gcmUtil;
	private ArrayList<Badge> badges = new ArrayList<Badge>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);
		profileLayout = (LinearLayout) findViewById(R.id.profileLayout);
		xpBar = findViewById(R.id.xpBar);
		xpProgressBar = (ProgressBar) findViewById(R.id.xpProgressBar);
		levelTextView = (TextView) findViewById(R.id.levelTextView);
		xpTextView = (TextView) findViewById(R.id.xpTextView);
		profileButton1 = (Button) findViewById(R.id.profileButton1);
		profileButton2 = (Button) findViewById(R.id.profileButton2);
		profileButton3 = (Button) findViewById(R.id.profileButton3);
		profileButton4 = (Button) findViewById(R.id.profileButton4);
		profileButton5 = (Button) findViewById(R.id.profileButton5);
		profileButton6 = (Button) findViewById(R.id.profileEditButton);
		pastEventsBadgesLayout = findViewById(R.id.profilePastEventsBadgesLayout);
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
		pastEventsBadgesLayout.setVisibility(View.VISIBLE);
		// grabbing the user with the given email in the EXTRAS
		if (!GLOBAL.isCurrentUser(EXTRAS.getString("email")))
		{
			if (GLOBAL.getUserBuffer() != null)
				user = GLOBAL.getUserBuffer();
		}
		else
		{
			user = GLOBAL.getCurrentUser();
		}
		title = user.getFirstName() + "'s Profile";
		new getUserExperienceTask().execute("http://68.59.162.183/android_connect/get_user_experience.php");
		setNotifications();
		badges = user.getBadges();
		getImageTask = new getImageTask().execute("http://68.59.162.183/android_connect/get_profile_image.php");
		populateProfile(); // populates a group / user profile

		// initializing the action bar and killswitch listener
		initActionBar(title, true);

	}

	// TASK FOR GRABBING IMAGE OF EVENT/USER/GROUP
	private class getImageTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = "email";
			String id = user.getEmail();
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

					user.setImage(image);
					if (user.getImage() != null)
						iv.setImageBitmap(user.getImage());
					else
						iv.setImageResource(R.drawable.user_image_default);

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

	// TASK GOR GETTING USER EXPERIENCE
	private class getUserExperienceTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
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
					int eventsIn = Integer.parseInt(jsonObject.getString("eventsIn").toString());
					int eventsCreated = Integer.parseInt(jsonObject.getString("eventsCreated").toString());
					int userPoints = (eventsCreated * 2) + eventsIn;
					setExperience(userPoints);
				}
				else
				{
					// failed
					Log.d("getUserExperience", "FAILED");
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
			// do next thing here
		}
	}

	private void setExperience(int userPoints)
	{
		int level = 1;
		int pointsToNext = 10;
		int pointsStart = 0;
		int pointsEnd;
		while (userPoints > (pointsStart + pointsToNext))
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
		xpProgressBar.setProgress(userPoints - pointsStart);
		xpTextView.setText(userPoints + " / " + pointsEnd);
	}

	private void setNotifications()
	{
		profileButton2.setVisibility(View.VISIBLE);
		profileButton3.setVisibility(View.VISIBLE);
		profileButton1.setText("Friends\n(" + user.getNumUsers() + ")");
		profileButton2.setText("Groups\n(" + user.getNumGroups() + ")");
		profileButton3.setText("Upcoming Events\n(" + user.getNumEventsUpcoming() + ")");
		profileButton4.setText("Past Events\n(" + user.getNumEventsPast() + ")");
		profileButton5.setText("Badges\n(" + user.getNumBadges() + ")");
		if (!GLOBAL.isCurrentUser(user.getEmail()))
		{
			if (user.inUsers(GLOBAL.getCurrentUser().getEmail()))
				profileButton6.setText("Message " + user.getFirstName());
			else
				profileButton6.setText("Invite " + user.getFirstName() + " to Friends");
		}
		profileButton6.setVisibility(View.VISIBLE);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == R.id.action_profile)
		{
			if (user.getEmail().equals(GLOBAL.getCurrentUser().getEmail()))
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onClick(View view)
	{
		super.onClick(view);
		loadDialog.show();
		boolean noIntent = view.getId() == R.id.backButton ? true : false;

		Intent intent = new Intent(this, EventListActivity.class);
		switch (view.getId())
		{
		case R.id.profileButton1:
			intent = new Intent(this, UserListActivity.class);
			// friends
			intent.putExtra("content", "FRIENDS_CURRENT");
			user.fetchFriends();

			break;
		case R.id.profileButton2:
			intent = new Intent(this, GroupListActivity.class);
			intent.putExtra("content", "GROUPS_CURRENT");
			user.fetchGroups();

			break;
		case R.id.profileButton3:
			user.fetchEventsUpcoming();
			intent.putExtra("content", "EVENTS_UPCOMING");

			break;
		case R.id.profileButton4:
			user.fetchEventsPast();
			intent.putExtra("content", "EVENTS_PAST");
			break;
		case R.id.profileButton5:
			noIntent = true;
			badgesDialog();

			break;
		case R.id.profileEditButton:

			if (!GLOBAL.isCurrentUser(user.getEmail()))
			{
				if (user.inUsers(GLOBAL.getCurrentUser().getEmail()))
				{
					System.out.println("\n\nSEND USER A MESSAGE");
					intent = new Intent(this, MessagesActivity.class);
					// View parent = (View)view.getParent();
					// Button name = (Button)
					// parent.findViewById(R.id.nameTextViewLI);
					intent.putExtra("name", user.getName());
				}
				else
				{
					System.out.println("\n\nINVITE TO FRIENDS");
					new addFriendTask().execute("http://68.59.162.183/android_connect/add_friend.php");
					// call a json task here
					// in onpost toast and change refresh the page with that
					// button gone
					// we need a check for pending requests
					noIntent = true;
				}
			}
			else
			{
				intent = new Intent(this, UserEditActivity.class);
			}
			System.out.println("Setting the intent");

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
			intent.putExtra("email", user.getEmail());
		}
		if (!noIntent) // TODO, move buttons elsewhere that dont start list
			startActivity(intent);
		else
			loadDialog.hide(); // did not launch intent, cancel load dialog
	}

	// This task sends a friend request to the given user.
	private class addFriendTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("sender", GLOBAL.getCurrentUser().getEmail()));
			nameValuePairs.add(new BasicNameValuePair("receiver", user.getEmail()));
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
					Toast toast = GLOBAL.getToast(UserProfileActivity.this,
							"Successfully invited " + user.getFirstName() + " to friends!");
					toast.show();
					gcmUtil.sendNotification(user.getEmail(), "FRIEND_REQUEST");
					profileButton6.setVisibility(View.INVISIBLE);
				}
				else if (jsonObject.getString("success").toString().equals("3"))
				{
					Toast toast = GLOBAL.getToast(UserProfileActivity.this,
							"A friend request with " + user.getFirstName() + " to is already active!");
					toast.show();
					profileButton6.setVisibility(View.INVISIBLE);
				}
				else
				{

				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		// current friends case
		GLOBAL.getCurrentUser().fetchFriends();
		// friend requests case
		GLOBAL.getCurrentUser().fetchFriendRequests();
		// group members case
		if (GLOBAL.getGroupBuffer() != null)
			GLOBAL.getGroupBuffer().fetchMembers();
		// event parts case
		if (GLOBAL.getEventBuffer() != null)
			GLOBAL.getEventBuffer().fetchParticipants();
	}

	private void badgesDialog()
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(user.getFirstName() + "'s Badges");
		LinearLayout row = null;
		ImageButton badgeImageButton;
		TextView badgeTextView;
		View dialogView = inflater.inflate(R.layout.dialog_badges, null);
		LinearLayout layout = (LinearLayout) dialogView.findViewById(R.id.badgesLayout);
		for (Badge b : badges)
		{
	
			final int index = badges.indexOf(b);
			if (index % 3 == 0 || index == 0)
			{
				row = (LinearLayout) inflater.inflate(R.layout.list_row_badges, null);
				layout.addView(row);
			}

			View item = inflater.inflate(R.layout.list_item_badge, null);
			badgeTextView = (TextView)item.findViewById(R.id.badgeNameTextView);
			badgeTextView.setText(b.getName());
			badgeImageButton = (ImageButton) item.findViewById(R.id.badgeImageButton);
			if (b.getLevel() > 0)
				badgeImageButton.setImageDrawable(getResources().getDrawable(R.drawable.badge_nature));
			else
				badgeImageButton.setImageDrawable(getResources().getDrawable(R.drawable.badge_nature_grey));
			badgeImageButton.setId(index);
			badgeImageButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					badgeDialog(badges.get(index));
				}
			});
			row.addView(item);
		}
		dialogBuilder.setView(dialogView);
		AlertDialog badgesDialog = dialogBuilder.create();
		badgesDialog.show();
	}

	/*
	 * Get profile executes get_profile.php. It uses the current users email
	 * address to retrieve the users name, age, and bio.
	 */
	private void populateProfile()
	{
		TextView info = (TextView) findViewById(R.id.profileInfoTextView);
		TextView about = (TextView) findViewById(R.id.profileAboutTextView);

		String infoT = "";
		String location = user.getLocation();
		if (location == null)
			location = "";

		int age = user.getAge();
		if (age == -1)
			infoT = location + "\n";
		else
			infoT = age + " yrs young\n" + location + "\n";
		about.setText(infoT + user.getAbout());
	}


}
