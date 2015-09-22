package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Brett, Todd, Scott
 */
public class UserProfileActivity extends BaseActivity
{
	private ImageView iv;
	private User user; // user who's profile this is
	private String EMAIL;
	private Button friendsButton;
	private Button groupsButton;
	private Button eventsUpcomingButton;
	private Button eventsPastButton;// for user profile, is past events
	private Button badgesButton;
	private Button messageButton;
	private Button inviteButton;
	private Button userEditButton;
	private ProgressBar xpProgressBar;
	private TextView xpTextView;
	private TextView aboutTextView;
	private TextView levelTextView;
	private GcmUtility gcmUtil;
	private ArrayList<Badge> badges = new ArrayList<Badge>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);

		// initializing views
		xpProgressBar = (ProgressBar) findViewById(R.id.xpProgressBar);
		levelTextView = (TextView) findViewById(R.id.levelTextView);
		aboutTextView = (TextView) findViewById(R.id.profileAboutTextView);
		xpTextView = (TextView) findViewById(R.id.xpTextView);
		friendsButton = (Button) findViewById(R.id.friendsButton);
		inviteButton = (Button) findViewById(R.id.inviteButton);
		groupsButton = (Button) findViewById(R.id.groupsButton);
		eventsUpcomingButton = (Button) findViewById(R.id.eventsUpcomingButton);
		eventsPastButton = (Button) findViewById(R.id.eventsPastButton);
		messageButton = (Button) findViewById(R.id.messageButton);
		badgesButton = (Button) findViewById(R.id.badgesButton);
		userEditButton = (Button) findViewById(R.id.userEditButton);
		iv = (ImageView) findViewById(R.id.profileImage);

		try
		{
			gcmUtil = new GcmUtility(GLOBAL);
		}
		catch (Exception e)
		{
		}
		Bundle extras = getIntent().getExtras();
		EMAIL = extras.getString("email");
		// grabbing the user with the given email in the EXTRAS
		if (!GLOBAL.isCurrentUser(EMAIL))
			user = GLOBAL.getUser(EMAIL);
		else
		{
			user = GLOBAL.getCurrentUser();
			user.fetchNewBadges(this);
		}

	}

	public void experienceDialog(View view)
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("User Statistics");
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_user_statistics, null);
		TextView socialTextView = (TextView) dialogView.findViewById(R.id.socialTextView);
		TextView entertainmentTextView = (TextView) dialogView.findViewById(R.id.entertainmentTextView);
		TextView professionalTextView = (TextView) dialogView.findViewById(R.id.professionalTextView);
		TextView fitnessTextView = (TextView) dialogView.findViewById(R.id.fitnessTextView);
		TextView natureTextView = (TextView) dialogView.findViewById(R.id.natureTextView);
		TextView socialCreateTextView = (TextView) dialogView.findViewById(R.id.socialCreateTextView);
		TextView entertainmentCreateTextView = (TextView) dialogView.findViewById(R.id.entertainmentCreateTextView);
		TextView professionalCreateTextView = (TextView) dialogView.findViewById(R.id.professionalCreateTextView);
		TextView fitnessCreateTextView = (TextView) dialogView.findViewById(R.id.fitnessCreateTextView);
		TextView natureCreateTextView = (TextView) dialogView.findViewById(R.id.natureCreateTextView);
		TextView itemsBroughtTextView = (TextView) dialogView.findViewById(R.id.itemsBroughtTextView);

		socialTextView.setText("" + user.getNumSocialEvents());
		professionalTextView.setText("" + user.getNumProfessionalEvents());
		entertainmentTextView.setText("" + user.getNumEntertainmentEvents());
		fitnessTextView.setText("" + user.getNumFitnessEvents());
		natureTextView.setText("" + user.getNumNatureEvents());
		socialCreateTextView.setText("" + user.getNumSocialEventsCreated());
		professionalCreateTextView.setText("" + user.getNumProfessionalEventsCreated());
		entertainmentCreateTextView.setText("" + user.getNumEntertainmentEventsCreated());
		fitnessCreateTextView.setText("" + user.getNumFitnessEventsCreated());
		natureCreateTextView.setText("" + user.getNumNatureEventsCreated());
		itemsBroughtTextView.setText("" + user.getNumItemsBrought());
		dialogBuilder.setView(dialogView);
		final AlertDialog experienceDialog = dialogBuilder.create();
		experienceDialog.show();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, new IntentFilter("user_data"));
		fetchData();
		updateUI(); // populates a group / user profile
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
			updateUI();
		}
	};

	/*
	 * fetchData fetches all data needed to be displayed in the UI for user
	 * profile activity
	 */
	private void fetchData()
	{
		user.fetchInfo(this);
		user.fetchGroups(this);
		user.fetchFriends(this);
		user.fetchBadges(this);
		user.fetchEventsUpcoming(this);
		user.fetchEventsPast(this);
		user.fetchExperience(this);
		user.fetchImage(this);
	}

	private void setExperience(int userPoints)
	{
		int level = 1;
		int pointsToNext = 10;
		int pointsStart = 0;
		int pointsEnd;
		while (userPoints >= (pointsStart + pointsToNext))
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

	private void setButtons()
	{
		// clear buttons
		friendsButton.setVisibility(View.GONE);
		groupsButton.setVisibility(View.GONE);
		eventsUpcomingButton.setVisibility(View.GONE);
		eventsPastButton.setVisibility(View.GONE);
		// badgesButton.setVisibility(View.GONE);
		userEditButton.setVisibility(View.GONE);
		inviteButton.setVisibility(View.GONE);
		messageButton.setVisibility(View.GONE);

		if (!GLOBAL.isCurrentUser(user.getEmail()))
		{
			if (user.inUsers(GLOBAL.getCurrentUser().getEmail()))
			{
				friendsButton.setVisibility(View.VISIBLE);
				groupsButton.setVisibility(View.VISIBLE);
				eventsUpcomingButton.setVisibility(View.VISIBLE);
				eventsPastButton.setVisibility(View.VISIBLE);
				// badgesButton.setVisibility(View.GONE);
				messageButton.setText("Message " + user.getFirstName());
				messageButton.setVisibility(View.VISIBLE);
			}
			else
			{
				inviteButton.setText("Invite " + user.getFirstName() + " to Friends");
				inviteButton.setVisibility(View.VISIBLE);
			}
		}
		else
		{
			friendsButton.setVisibility(View.VISIBLE);
			groupsButton.setVisibility(View.VISIBLE);
			eventsUpcomingButton.setVisibility(View.VISIBLE);
			eventsPastButton.setVisibility(View.VISIBLE);
			// badgesButton.setVisibility(View.GONE);
			userEditButton.setVisibility(View.VISIBLE);
		}

		friendsButton.setText("Friends\n(" + user.getNumUsers() + ")");
		groupsButton.setText("Groups\n(" + user.getNumGroups() + ")");
		eventsUpcomingButton.setText("Upcoming Events\n(" + user.getNumEventsUpcoming() + ")");
		eventsPastButton.setText("Past Events\n(" + user.getNumEventsPast() + ")");
		badgesButton.setText("Badges\n(" + user.getNumBadges() + ")");
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

	public void groupsButton(View v)
	{
		loadDialog.show();
		Intent intent = new Intent(this, GroupListActivity.class);
		intent.putExtra("content", "GROUPS_CURRENT");
		startActivity(intent);
		if (user != null)
		{
			intent.putExtra("email", user.getEmail());
		}
		startActivity(intent);
	}

	public void friendsButton(View v)
	{
		loadDialog.show();
		Intent intent = new Intent(this, UserListActivity.class);
		// friends
		intent.putExtra("content", "FRIENDS_CURRENT");
		if (user != null)
		{
			intent.putExtra("email", user.getEmail());
		}
		startActivity(intent);
	}

	public void eventsUpcomingButton(View v)
	{
		loadDialog.show();
		Intent intent = new Intent(this, EventListActivity.class);
		intent.putExtra("content", "EVENTS_UPCOMING");
		if (user != null)
		{
			intent.putExtra("email", user.getEmail());
		}
		startActivity(intent);
	}

	public void eventsPastButton(View v)
	{
		loadDialog.show();
		Intent intent = new Intent(this, EventListActivity.class);
		intent.putExtra("content", "EVENTS_PAST");
		if (user != null)
		{
			intent.putExtra("email", user.getEmail());
		}
		startActivity(intent);
	}

	public void messageButton(View v)
	{
		loadDialog.show();
		Intent intent = new Intent(this, MessagesActivity.class);
		intent.putExtra("name", user.getName());
		if (user != null)
		{
			intent.putExtra("email", user.getEmail());
		}
		startActivity(intent);
	}

	public void inviteButton(View v)
	{
		new addFriendTask().execute("http://mierze.gear.host/grouple/android_connect/add_friend.php");
	}

	public void userEditButton(View v)
	{
		loadDialog.show();
		Intent intent = new Intent(this, UserEditActivity.class);
		if (user != null)
		{
			intent.putExtra("email", user.getEmail());
		}
		startActivity(intent);
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
					try
					{
						gcmUtil.sendNotification(user.getEmail(), "FRIEND_REQUEST");
					}
					catch (Exception e)
					{

					}
					inviteButton.setVisibility(View.GONE);
				}
				else if (jsonObject.getString("success").toString().equals("3"))
				{
					Toast toast = GLOBAL.getToast(UserProfileActivity.this,
							"A friend request with " + user.getFirstName() + " to is already active!");
					toast.show();
					inviteButton.setVisibility(View.GONE);
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

	private class BadgesListAdapter extends ArrayAdapter<Badge>
	{
		public BadgesListAdapter()
		{
			super(UserProfileActivity.this, R.layout.list_item_badge, badges);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			View itemView = convertView;
			if (itemView == null)
				itemView = inflater.inflate(R.layout.list_item_badge, parent, false);
			Badge b = badges.get(position);
			TextView badgeTextView = (TextView) itemView.findViewById(R.id.badgeNameTextView);
			ImageButton badgeImageButton = (ImageButton) itemView.findViewById(R.id.badgeImageButton);
			if (b.getName().equals("Environmentalist"))
			{
				badgeTextView.setText("Tree Hugger");
			}
			else
			{
				badgeTextView.setText(b.getName());
			}
			final String gender = user.getGender();
			badgeImageButton.setImageDrawable(getResources().getDrawable(b.getImageID(gender)));

			// badgeImageButton.setId(position);
			badgeImageButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					badgeDialog(badges.get(position), gender);
				}
			});
			itemView.setId(position);
			return itemView;
		}

	}

	public void badgesButton(View view)
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(user.getFirstName() + "'s Badges");
		View dialogView = inflater.inflate(R.layout.dialog_badges, null);
		GridView gridView = (GridView) dialogView.findViewById(R.id.gridView);

		ArrayAdapter<Badge> adapter = new BadgesListAdapter();
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id)
			{
				badgeDialog(badges.get(position), user.getGender());
			}
		});

		dialogBuilder.setView(dialogView);
		AlertDialog badgesDialog = dialogBuilder.create();
		badgesDialog.show();
	}

	// should be responsible for updating all ui data
	private void updateUI()
	{
		super.updateUI(user);
		String title = user.getFirstName() + "'s Profile";
		String about = "";
		String location = user.getLocation();
		String gender = user.getGender();
		int age = user.getAge();

		initActionBar(title, true);
		setButtons();
		badges = user.getBadges();

		if (age == -1 && !gender.equals(""))
		{
			if (gender.equals("M"))
			{
				about += "Male";
			}
			else
			{
				about += "Female";
			}
		}
		else if (age != -1)
		{

			if (gender.equals(""))
			{
				about += age + " yrs young";
			}
			else if (gender.equals("M"))
			{
				about += age + " yr young male";
			}
			else
			{
				about += age + " yr young female";
			}
		}
		if (!location.equals("") && !about.equals(""))
		{
			about += "\n" + location;
		}
		else if (!location.equals(""))
			about += location;

		aboutTextView.setText(about + "\n" + user.getAbout());

		if (user.getImage() != null)
			iv.setImageBitmap(user.getImage());
		else
			iv.setImageResource(R.drawable.image_default);

		iv.setScaleType(ScaleType.CENTER_CROP);
		setExperience(user.getExperience());
	}

}
