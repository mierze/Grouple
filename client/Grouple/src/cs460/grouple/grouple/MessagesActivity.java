package cs460.grouple.grouple;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * MessagesActivity has not been implemented yet.
 */
public class MessagesActivity extends ActionBarActivity
{
	private BroadcastReceiver broadcastReceiver;
	private Global GLOBAL;
	private static int alt = 1;
	private User user; //will be null for now
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		GLOBAL = ((Global) getApplicationContext());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);
		/* Action bar */
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);

		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);

		actionbarTitle.setText("Brett Mierzejewski");
		initKillswitchListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.messages, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_logout)
		{
			
			Intent login = new Intent(this, LoginActivity.class);
			GLOBAL.destroySession();
			startActivity(login);
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		if (id == R.id.action_home)
		{
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	//use this method to display the messages between 2 users. 
	//you will still need to get those messages in a mapping or arraylist of some sort
	private void populateMessages()
	{
		//layout to inflate into
		LinearLayout messageLayout = (LinearLayout) findViewById(R.id.messageLayout);
		//layout inflater
		LayoutInflater li = getLayoutInflater();
		TextView messageBody, messageDate ;
		View row = null;
		String message = "";
		
		//messages consist of some things (messagebody, date, sender, receiver)
		
		//loop through messages (newest first), maybe a map String String with messagebody, date
		
			if ("RECEIVER".equals(user.getEmail()/*our email*/))
				row =  li.inflate(R.layout.message_row, null); //inflate this message row
			else
				row =  li.inflate(R.layout.message_row_out, null); //inflate the sender message row
			
			messageBody = (TextView) row.findViewById(R.id.messageBody);
			messageDate = (TextView) row.findViewById(R.id.messageDate);
			
			//set these values to what you want
			
			//add row into scrollable layout
			messageLayout.addView(row);

	}
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.sendButton:
			
			LinearLayout messageLayout = (LinearLayout) findViewById(R.id.messageLayout);
			LayoutInflater li = getLayoutInflater();
			
			View row = null;
			if (alt % 2 == 0)
				row =  li.inflate(R.layout.message_row, null);
			else
				row =  li.inflate(R.layout.message_row_out, null);
			
			alt++;

	
			messageLayout.addView(row);
			//code to call a task to send a message
			break;
		default:
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
