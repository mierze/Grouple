package cs460.grouple.grouple;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/*
 * EventsActivity has not been implemented yet.
 */
public class EventsActivity extends BaseActivity
{
	private User user;
	private Button eventsUpcomingButton;
	private Button eventsPastButton;
	private Button eventsPendingButton;
	private Button eventCreateButton;
	private Button eventsDeclinedButton;
	private Button eventInvitesButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);
		
		initActionBar("Events", true);
		user = GLOBAL.getCurrentUser();
		//fetchData(); TODO;
		eventsUpcomingButton = (Button) findViewById(R.id.eventsUpcomingButton);
		eventsPendingButton = (Button) findViewById(R.id.eventsPendingButton);
		eventInvitesButton = (Button) findViewById(R.id.eventInvitesButton);
		eventsPastButton = (Button) findViewById(R.id.eventsPastButton);		
		eventsDeclinedButton = (Button) findViewById(R.id.eventsDeclinedButton);
	}
	

	@Override
	protected void onResume()
	{
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("user_data"));
		fetchData();
		updateUI();
	}

	@Override
	protected void onPause()
	{
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
		super.onPause();

	}

	//This listens for pings from the data service to let it know that there are updates
	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// Extract data included in the Intent
			String type = intent.getStringExtra("message");
			//repopulate views
			updateUI();
		}
	};

	private void fetchData()
	{
		//fetch all to updata
		user.fetchEventsDeclined(this);
	}
	private void updateUI()
	{
		eventsUpcomingButton.setText("Upcoming Events (" + user.getNumEventsUpcoming() + ")");
		eventsPendingButton.setText("Pending Events (" + user.getNumEventsPending() + ")");
		eventInvitesButton.setText("Event Invites (" + user.getNumEventsInvites() + ")");
		eventsPastButton.setText("Past Events (" + user.getNumEventsPast() + ")");		
		eventsDeclinedButton.setText("Declined Events (" + user.getNumEventsDeclined() + ")");		
	}
	
	public void onClick(View view)
	{
		loadDialog.show();
		Intent intent =  new Intent(this, EventListActivity.class);
		switch (view.getId())
		{
		case R.id.eventsPendingButton:
			intent.putExtra("content", "EVENTS_PENDING");
			break;
		case R.id.eventsUpcomingButton:
			intent.putExtra("content", "EVENTS_UPCOMING");
			break;
		case R.id.eventCreateButton:
			intent = new Intent(this, EventCreateActivity.class);
			break;
		case R.id.eventInvitesButton:
			intent.putExtra("content", "EVENT_INVITES");
			break;
		case R.id.eventsPastButton:
			intent.putExtra("content", "EVENTS_PAST");
			break;
		case R.id.eventsDeclinedButton:
			intent.putExtra("content", "EVENTS_DECLINED");
			break;
		}
		intent.putExtra("email", user.getEmail());
		startActivity(intent);
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == R.id.action_events)
		{
			//do nothing, already here'
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/* Start activity functions for going back and logging out */
	public void startHomeActivity(View view)
	{
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		intent.putExtra("email", user.getEmail());
	}

}
