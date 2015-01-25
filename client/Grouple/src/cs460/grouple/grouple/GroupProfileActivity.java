package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import cs460.grouple.grouple.R;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * GroupProfileActivity displays the profile of a user's group.
 */
public class GroupProfileActivity extends ActionBarActivity
{
	private Group group; //current group
	private final static int CAMERA_DATA = 0;
	private BroadcastReceiver broadcastReceiver;

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_profile);

		//Global global = ((Global) getApplicationContext());
		Bundle extras = getIntent().getExtras();
		//gname = extras.getString("gname");

		//groupProfile = findViewById(R.id.groupProfileContainer);
		load();//groupProfile);
	}

	public void initActionBar()
	{

		/* Action bar */
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText(group.getName());
		System.out.println(group.getName());
		ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);

	}

	public void load()
	{
		Log.d("message", "00000000000000001");

		//membersToAdd = (LinearLayout) findViewById(R.id.linearLayoutNested2);

		Global global = ((Global) getApplicationContext());
		// backstack of intents
		// each class has a stack of intents lifo method used to execute them at
		// start of activity
		// intents need to include everything like ParentClassName, things for
		// current page (email, ...)
		// if check that friends
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		System.out.println("(GroupProfile)Loading group gid: " + extras.getInt("gid"));

		group = global.loadGroup(extras.getInt("gid"));
		
		TextView about = (TextView)findViewById(R.id.bioTextViewGPA);
		about.setText(group.getBio());

		//getGroupContents();
		// startActivity(upIntent);
		initActionBar();
		initKillswitchListener();

	}


	public void getGroupContents()
	{
		
		
		
		// <<<<<<<<<< here >>>>>>>>>>//
		/*GridLayout rowView = (GridLayout) inflater.inflate(
				R.layout.listitem_groupprofile, null);
		Button removeFriendButton = (Button) rowView
				.findViewById(R.id.removeFriendButtonNoAccess);
		Button friendNameButton = (Button) rowView
				.findViewById(R.id.friendNameButtonNoAccess);
		friendNameButton.setText(member);
		if (role.equals("C"))
		{
			removeFriendButton.setText("C");
			removeFriendButton.setTextColor(getResources()
					.getColor(R.color.black));
		} else if (role.equals("A"))
		{
			removeFriendButton.setText("A");
			removeFriendButton.setTextColor(getResources()
					.getColor(R.color.light_green));
		} else
		{
			removeFriendButton.setText("-");
			removeFriendButton.setTextColor(getResources()
					.getColor(R.color.light_blue));
		}
		removeFriendButton.setId(index);
		friendNameButton.setId(index);
		rowView.setId(index);
		membersToAdd.addView(rowView);*/
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation_actions, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Global global = ((Global) getApplicationContext());
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_logout)
		{
			Intent login = new Intent(this, LoginActivity.class);
			startActivity(login);
			//bmp = null;
			//iv = null;
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		if (id == R.id.action_home)
		{
			Intent intent = new Intent(this, HomeActivity.class);
			//intent.putExtra("ParentClassName", "GroupProfileActivity");
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	/* Start activity functions for going back to home and logging out */
	public void startHomeActivity(View view)
	{
		Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra("ParentClassName", "GroupProfileActivity");
		startActivity(intent);
	//	bmp = null;
	//	iv = null;
		finish();
	}

	public void startEditProfileActivity(View view)
	{
		Intent intent = new Intent(this, ProfileEditActivity.class);
		intent.putExtra("ParentClassName", "GroupProfileActivity");
		startActivity(intent);
	//	bmp = null;
	//	iv = null;
	}

	
	public void editGroupProfileButton(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.editGroupProfilePhotoButton:
			Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(i, CAMERA_DATA);
			break;

		}
	}

	public void initKillswitchListener()
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
					Log.d("app666", "we killin the login it");
					// System.exit(1);
					finish();
				}

			}
		};
		registerReceiver(broadcastReceiver, intentFilter);
		// End Kill switch listener
	}
}
