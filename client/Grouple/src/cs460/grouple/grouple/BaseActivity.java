package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import cs460.grouple.grouple.GcmUtility.CONTENT_TYPE;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author Brett, Todd, Scott
 * BaseActivity is a helper class for other Activities that share common functions
 */
public class BaseActivity extends ActionBarActivity implements OnClickListener
{
	protected Global GLOBAL;
	protected Dialog loadDialog;
	private BroadcastReceiver broadcastReceiver;
	protected LayoutInflater inflater;
	
	@Override
	public void onBackPressed() 
	{
		finish();
		return;
	}
	
	protected void scrollListView(final int index, final ListView listView)
	{
		   listView.post(new Runnable() {
		        @Override
		        public void run() {
		            // Select the last row so it will scroll into view...
		           listView.setSelection(index);
		        }
		    });
	}
	@Override
	protected void onStop()
	{
		super.onStop();
		loadDialog.dismiss();
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		loadDialog = GLOBAL.getLoadDialog(new Dialog(this));
		loadDialog.setOwnerActivity(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_base);
		GLOBAL = ((Global) getApplicationContext());
		
		inflater = getLayoutInflater();
		initKillswitchListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation_actions, menu);
		return true;
	}

	protected void initActionBar(String title, boolean back)
	{
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
		if (!back)
			backButton.setVisibility(View.INVISIBLE);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText(title);
	}
	
	// dialog pop up for a single badge with description
	// TODO: add parameters about the badge?
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
			badgeImageView.setImageDrawable(getResources().getDrawable(R.drawable.badge_nature));
		else
			badgeImageView.setImageDrawable(getResources().getDrawable(R.drawable.badge_nature_grey));
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(b.getName() + " (Level " + b.getLevel() + ")");
		dialogBuilder.setView(dialogView);
		AlertDialog badgesDialog = dialogBuilder.create();
		badgesDialog.show();
	}
	
	protected void updateUI(User user)
	{
		if (user.getNumNewBadges() > 0)
		{
			for (Badge b : user.getNewBadges())
				badgeDialog(b);
		}
		user.getNewBadges().clear();
	}
	
	//for images to blow them up throughout app
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		User user = GLOBAL.getCurrentUser();
		Intent intent = null;
		int id = item.getItemId();
		switch (id)
		{
		case R.id.action_home:
			intent = new Intent(this, HomeActivity.class);
			break;
		case R.id.action_profile:
			intent = new Intent(this, UserProfileActivity.class);
			break;
		case R.id.action_messages:
			intent = new Intent(this, ContactsActivity.class);
			break;
		case R.id.action_friends:
			intent = new Intent(this, FriendsActivity.class);
			break;
		case R.id.action_groups:
			intent = new Intent(this, GroupsActivity.class);
			break;
		case R.id.action_events:
			intent = new Intent(this, EventsActivity.class);
			break;
		case R.id.action_settings:
			intent = new Intent(this, SettingsActivity.class);
			break;
		case R.id.action_logout:
			//Remove RegID
			//We can never remove regid from database without replacing it with another.
			//if a user does clear their reg id from db, any other users who are trying to message this user will no longer be able to fetch the user's reg_id to use in GCM calls.
			//It is okay to have multiple emails linked to the same reg_id.  (This will be common for such as a user who has two accounts and switches between them on the same device)
			//However we must not display push notifications if the GCM recipient is not the user who is currently logged in.  We will simply discard those GCMs if they are received.
			
			//new deleteRegIDTask().execute("http://68.59.162.183/android_connect/delete_chat_id.php", user.getEmail());
			GLOBAL.destroySession();
			intent = new Intent(this, LoginActivity.class);
			Intent CLOSE_ALL = new Intent("CLOSE_ALL");
			sendBroadcast(CLOSE_ALL);
			break;
		}
		if (intent != null)
		{
			GLOBAL.setCurrentUser(user);
			if (user != null)
				intent.putExtra("email", user.getEmail());
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy()
	{
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}
	
	private void initKillswitchListener()
	{
		// START KILL SWITCH LISTENER
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("CLOSE_ALL");
		broadcastReceiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				// close activity
				if (intent.getAction().equals("CLOSE_ALL"))
				{
					finish();
				}
			}
		};
		registerReceiver(broadcastReceiver, intentFilter);
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		if (id == R.id.backButton)
		{
			onBackPressed();
		}
		
	}
	
	protected AlertDialog.Builder getImageBuilder(Context c)
	{
		//final Intent i;
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		final CharSequence[] items =
		{ "Take Photo", "Choose from Gallery", "Cancel" };
		builder.setTitle("Choose your profile picture:");
		builder.setItems(items, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int item)
			{
				if (items[item].equals("Take Photo"))
				{
					Intent i = new Intent(
							android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(i, 1);
				} 
				else if (items[item].equals("Choose from Gallery"))
				{
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					startActivityForResult(
							Intent.createChooser(intent, "Select Photo"), 2);
				} 
				else if (items[item].equals("Cancel"))
				{
					dialog.dismiss();
				}
			}
		});
		
		return builder;
	}
	
	//no one should ever be calling this method
	/*
	//This task deletes your stored regid
    private class deleteRegIDTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			//The recipient's email is urls[1]
			nameValuePairs.add(new BasicNameValuePair("email", urls[1]));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{
					//The reg ID was removed successfully.
					
				} 
				else
				{
					//Toast toast = GLOBAL.getToast(MessagesActivity.this, "Error getting GCM REG_ID.");
					//toast.show();
				}
			} catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	*/
}
