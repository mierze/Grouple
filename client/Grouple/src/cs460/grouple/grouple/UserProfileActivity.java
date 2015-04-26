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
import android.support.v4.content.LocalBroadcastManager;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
	private String EMAIL;
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
	private TextView infoTextView;
	private TextView aboutTextView;
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
		infoTextView = (TextView) findViewById(R.id.profileInfoTextView);
		aboutTextView = (TextView) findViewById(R.id.profileAboutTextView);
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
		Bundle extras = getIntent().getExtras();
		EMAIL = extras.getString("email");
		pastEventsBadgesLayout.setVisibility(View.VISIBLE);
		// grabbing the user with the given email in the EXTRAS
		if (!GLOBAL.isCurrentUser(EMAIL))
			user = GLOBAL.getUser(EMAIL);
		else
			user = GLOBAL.getCurrentUser();

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("user_data"));
		fetchData();
		updateUI(); // populates a group / user profile

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
		user.fetchNewBadges(this);
		user.fetchEventsUpcoming(this);
		user.fetchEventsPast(this);
		user.fetchImage(this);
		user.fetchPoints(this);
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

	private void setButtons()
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
		// loadDialog.show();
		boolean noIntent = view.getId() == R.id.backButton ? true : false;

		Intent intent = new Intent(this, EventListActivity.class);
		switch (view.getId())
		{
		case R.id.profileButton1:
			intent = new Intent(this, UserListActivity.class);
			// friends
			intent.putExtra("content", "FRIENDS_CURRENT");

			break;
		case R.id.profileButton2:
			intent = new Intent(this, GroupListActivity.class);
			intent.putExtra("content", "GROUPS_CURRENT");

			break;
		case R.id.profileButton3:

			intent.putExtra("content", "EVENTS_UPCOMING");

			break;
		case R.id.profileButton4:

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
			badgeTextView.setText(b.getName());

			if (b.getLevel() > 0)
			{
				badgeImageButton.setImageDrawable(getBadgeImage(b));
			}
			else
				badgeImageButton.setImageDrawable(getResources().getDrawable(R.drawable.badge_nature_grey));
			// badgeImageButton.setId(position);
			badgeImageButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					badgeDialog(badges.get(position));
				}
			});
			itemView.setId(position);
			return itemView;
		}

	}
	
	protected void badgeDialog(Badge b)
	{
		int level = b.getLevel();
		View dialogView = inflater.inflate(R.layout.dialog_badge, null);
		ImageView badgeImageView = (ImageView) dialogView.findViewById(R.id.badgeImageView);
		TextView badgeTitleTextView = (TextView) dialogView.findViewById(R.id.badgeTitleTextView);

		TextView badgeAboutTextView = (TextView) dialogView.findViewById(R.id.badgeAboutTextView);
		if(level > 0)
			badgeTitleTextView.setText("Congratulations! You just earned a badge!\n");
		else
			badgeTitleTextView.setText("You have not unlocked this badge, keep on working!\n");
		badgeAboutTextView.setText(getResources().getString(b.getAboutID()));
		if (b.getLevel() > 0)
			badgeImageView.setImageDrawable(getBadgeImage(b));
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(b.getName() + " (Level " + b.getLevel() + ")");
		dialogBuilder.setView(dialogView);
		AlertDialog badgesDialog = dialogBuilder.create();
		badgesDialog.show();
	}

	private Drawable getBadgeImage(Badge b)
	{
		Drawable d = null;
		if (b.getName().equals("Outdoorsman"))
		{
			d = getResources().getDrawable(R.drawable.badge_outdoorsman);
		}
		else if (b.getName().equals("Agile"))
		{
			d = getResources().getDrawable(R.drawable.badge_agile);
		}
		else if (b.getName().equals("Gregarious"))
		{
			d = getResources().getDrawable(R.drawable.badge_gregarious);
		}
		else if (b.getName().equals("Amused"))
		{
			d = getResources().getDrawable(R.drawable.badge_amused);
		}
		else if (b.getName().equals("Environmentalist"))
		{
			d = getResources().getDrawable(R.drawable.badge_environmentalist);
		}
		else if (b.getName().equals("Regular"))
		{
			d = getResources().getDrawable(R.drawable.badge_regular);
		}
		else if (b.getName().equals("Diligent"))
		{
			d = getResources().getDrawable(R.drawable.badge_diligent2);
		}
		else if (b.getName().equals("Diversity"))
		{
			d = getResources().getDrawable(R.drawable.badge_diversity);
		}
		else if (b.getName().equals("Productive"))
		{
			d = getResources().getDrawable(R.drawable.badge_professionalcreate);
		}
		else if (b.getName().equals("Health Nut"))
		{
			d = getResources().getDrawable(R.drawable.badge_healthnut);
		}
		else if (b.getName().equals("Congregator"))
		{
			d = getResources().getDrawable(R.drawable.badge_environmentalist);
		}
		else
			d = getResources().getDrawable(R.drawable.badge_regular);
		return d;
	}
	private void badgesDialog()
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

				badgeDialog(badges.get(position));

			}
		});

		dialogBuilder.setView(dialogView);
		AlertDialog badgesDialog = dialogBuilder.create();
		badgesDialog.show();
	}

	// should be responsible for updating all ui data
	private void updateUI()
	{
		String title = user.getFirstName() + "'s Profile";
		String about = "";
		String location = user.getLocation();
		int age = user.getAge();

		initActionBar(title, true);
		setButtons();
		badges = user.getBadges();
		if (location == null)
			location = "";
		if (age == -1)
			about = location + "\n";
		else
			about = age + " yrs young\n" + location + "\n";
		aboutTextView.setText(about + user.getAbout());

		if (user.getImage() != null)
			iv.setImageBitmap(user.getImage());
		else
			iv.setImageResource(R.drawable.user_image_default);

		iv.setScaleType(ScaleType.CENTER_CROP);
		setExperience(user.getPoints());
	}

}
