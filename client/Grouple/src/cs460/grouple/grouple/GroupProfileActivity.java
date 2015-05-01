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
	private ImageView iv;
	private Button membersButton;
	private Button messagesButton;
	private Button joinButton;
	private Button editButton;
	private Button inviteButton;
	private ProgressBar xpProgressBar;
	private TextView xpTextView;
	private TextView levelTextView;
	private TextView aboutTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_profile);
		
		//init views
		xpProgressBar = (ProgressBar) findViewById(R.id.xpProgressBar);
		levelTextView = (TextView) findViewById(R.id.levelTextView);
		xpTextView = (TextView) findViewById(R.id.xpTextView);
		membersButton = (Button) findViewById(R.id.membersButton);
		messagesButton = (Button) findViewById(R.id.messagesButton);
		joinButton = (Button) findViewById(R.id.joinButton);
		editButton = (Button) findViewById(R.id.editButton);
		inviteButton = (Button) findViewById(R.id.inviteButton);
		aboutTextView = (TextView) findViewById(R.id.aboutTextView);
		iv = (ImageView) findViewById(R.id.groupImageView);
		
		//init variables
		Bundle extras = getIntent().getExtras();
		user = GLOBAL.getCurrentUser();
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
		new getUnreadEntityMessagesTask().execute(
				"http://68.59.162.183/android_connect/get_unread_entitymessages.php",
				Integer.toString(group.getID()));
		new getRoleTask().execute("http://68.59.162.183/android_connect/check_role_group.php",
				Integer.toString(group.getID()));
	}

	public void experienceDialog(View view)
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Group Statistics");
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_group_statistics, null);
		TextView socialTextView = (TextView) dialogView.findViewById(R.id.socialTextView);
		TextView entertainmentTextView = (TextView) dialogView.findViewById(R.id.entertainmentTextView);
		TextView professionalTextView = (TextView) dialogView.findViewById(R.id.professionalTextView);
		TextView fitnessTextView = (TextView) dialogView.findViewById(R.id.fitnessTextView);
		TextView natureTextView = (TextView) dialogView.findViewById(R.id.natureTextView);

		socialTextView.setText("" + group.getNumSocialEvents());
		professionalTextView.setText("" + group.getNumProfessionalEvents());
		entertainmentTextView.setText("" + group.getNumEntertainmentEvents());
		fitnessTextView.setText("" + group.getNumFitnessEvents());
		natureTextView.setText("" + group.getNumNatureEvents());
		dialogBuilder.setView(dialogView);
		final AlertDialog experienceDialog = dialogBuilder.create();
		experienceDialog.show();
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
						messagesButton.setText("Group Messages (" + numUnread + " unread)");

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
		
		membersButton.setVisibility(View.GONE);
		inviteButton.setVisibility(View.GONE);
		joinButton.setVisibility(View.GONE);
		editButton.setVisibility(View.GONE);
		messagesButton.setVisibility(View.GONE);
		
		//in group
		if (group.inUsers(user.getEmail()))
		{
			membersButton.setText("Members\n(" + group.getNumUsers() + ")");
			membersButton.setVisibility(View.VISIBLE);
			messagesButton.setVisibility(View.VISIBLE);
			String role = user.getGroupRole(group.getID());
			if (role.equals("A"))
			{
				editButton.setVisibility(View.VISIBLE);
			}
			if (role.equals("A") || role.equals("P"))
			{
				inviteButton.setVisibility(View.VISIBLE);
			}
		}
		//not in group
		else
		{
			//public and not in, allow join
			if (group.getPub() == 1)
			{
				joinButton.setVisibility(View.VISIBLE);
			}
		}

	}

	public void membersButton(View view)
	{
		Intent intent = new Intent(this, UserListActivity.class);
		intent.putExtra("content", "GROUP_MEMBERS");
		intent.putExtra("g_id", group.getID());
		intent.putExtra("email", user.getEmail());
		startActivity(intent);
	}
	public void inviteButton(View view)
	{
		Intent intent = new Intent(this, InviteActivity.class);
		intent.putExtra("g_id", group.getID());
		intent.putExtra("email", user.getEmail());
		startActivity(intent);
	}
	public void messagesButton(View view)
	{
		Intent intent = new Intent(this, EntityMessagesActivity.class);
		intent.putExtra("content", "GROUP");
		intent.putExtra("g_id", group.getID());
		intent.putExtra("email", user.getEmail());
		startActivity(intent);
	}
	public void joinButton(View view)
	{
		new JoinPublicTask().execute("http://68.59.162.183/" + "android_connect/join_public_group.php",
				user.getEmail(), "P", Integer.toString(group.getID()));
	}
	public void editButton(View view)
	{
		Intent intent = new Intent(this, GroupEditActivity.class);
		intent.putExtra("g_id", group.getID());
		intent.putExtra("email", user.getEmail());
		startActivity(intent);
	}
	



	/*
	 * Get profile executes get_profile.php. It uses the current users email
	 * address to retrieve the users name, age, and bio.
	 */
	public void updateUI()
	{
		String title = group.getName();
		initActionBar(title, true);
		setButtons();
		// iv.setImageBitmap(group.getImage());
		aboutTextView.setText("Creator: " + group.getEmail() + "\nGroup Since: " + group.getDateCreatedText() + "\n" +group.getAbout());
		if (group.getImage() != null)
			iv.setImageBitmap(group.getImage());
		else
			iv.setImageResource(R.drawable.group_image_default);
		iv.setScaleType(ScaleType.CENTER_CROP);
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
					joinButton.setVisibility(View.GONE);
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
