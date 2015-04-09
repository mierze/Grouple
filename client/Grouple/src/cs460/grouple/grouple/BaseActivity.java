package cs460.grouple.grouple;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class BaseActivity extends ActionBarActivity implements OnClickListener
{
	protected Global GLOBAL;
	protected Dialog loadDialog;
	private BroadcastReceiver broadcastReceiver;
	
	@Override
	public void onBackPressed() 
	{
		finish();
		return;
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		loadDialog.hide();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_base);
		GLOBAL = ((Global) getApplicationContext());
		loadDialog = GLOBAL.getLoadDialog(new Dialog(this));
		loadDialog.setOwnerActivity(this);
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
			user.fetchUserInfo();
			user.fetchEventsUpcoming();
			user.fetchFriends();
			user.fetchGroups();
			intent = new Intent(this, UserProfileActivity.class);
			break;
		case R.id.action_messages:
			intent = new Intent(this, RecentMessagesActivity.class);
			break;
		case R.id.action_friends:
			intent = new Intent(this, FriendsActivity.class);
			user.fetchFriendRequests();
			user.fetchFriends();
			break;
		case R.id.action_groups:
			user.fetchGroupInvites();
			user.fetchGroups();
			intent = new Intent(this, GroupsActivity.class);
			break;
		case R.id.action_events:
			user.fetchEventInvites();
			user.fetchEventsPending();
			user.fetchEventsUpcoming();
			user.fetchEventsPast();
			intent = new Intent(this, EventsActivity.class);
			break;
		case R.id.action_settings:
			intent = new Intent(this, SettingsActivity.class);
			break;
		case R.id.action_logout:
			GLOBAL.destroySession();
			intent = new Intent(this, LoginActivity.class);
			Intent CLOSE_ALL = new Intent("CLOSE_ALL");
			sendBroadcast(CLOSE_ALL);
			break;
		}
		if (intent != null)
		{
			GLOBAL.setCurrentUser(user);
			intent.putExtra("EMAIL", user.getEmail());
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
}
