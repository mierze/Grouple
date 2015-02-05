package cs460.grouple.grouple;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/*
 * EventsActivity has not been implemented yet.
 */
public class EventsActivity extends ActionBarActivity
{
	BroadcastReceiver broadcastReceiver;
	User user;
	Global GLOBAL;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);

		
		load();
		
		
	}

	private void load()
	{
		GLOBAL = (Global) getApplicationContext();
		user = GLOBAL.getCurrentUser();
		setNotifications();
		initActionBar();
		initKillswitchListener();
	}
	
	private void initActionBar()
	{
		/* Action bar */
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);

		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);

		actionbarTitle.setText("Events");
	}
	
	private void setNotifications()
	{
		((Button) findViewById(R.id.eventsUpcomingButtonEA)).setText("Upcoming Events (" + user.getNumEventsUpcoming() + ")");
		((Button) findViewById(R.id.eventsPendingButtonEA)).setText("Pending Events (" + user.getNumEventsPending() + ")");
		
	}
	
	public void onClick(View view)
	{
		Intent intent =  new Intent(this, ListActivity.class);
		switch (view.getId())
		{
		case R.id.eventsPendingButtonEA:
			intent.putExtra("CONTENT", "EVENTS_PENDING");
			break;
		case R.id.eventsUpcomingButtonEA:
			intent.putExtra("CONTENT", "EVENTS_UPCOMING");
			break;
		case R.id.eventCreateButtonEA:
			intent = new Intent(this, EventCreateActivity.class);
			break;
		}
		intent.putExtra("EMAIL", user.getEmail());
		startActivity(intent);
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
	public void onResume()
	{
		super.onResume(); // Always call the superclass method first
		load();
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

	/* Start activity functions for going back and logging out */
	public void startHomeActivity(View view)
	{
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		intent.putExtra("EMAIL", user.getEmail());
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
