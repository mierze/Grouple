package cs460.grouple.grouple;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/*
 * EventsActivity has not been implemented yet.
 */
public class EventsActivity extends BaseActivity
{
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);
	}

	private void load()
	{
		user = GLOBAL.getCurrentUser();
		setNotifications();
		initActionBar("Events", true);
	}
	
	private void setNotifications()
	{
		((Button) findViewById(R.id.eventsUpcomingButtonEA)).setText("Upcoming Events (" + user.getNumEventsUpcoming() + ")");
		((Button) findViewById(R.id.eventsPendingButtonEA)).setText("Pending Events (" + user.getNumEventsPending() + ")");
		((Button) findViewById(R.id.eventInvitesButtonEA)).setText("Event Invites (" + user.getNumEventsInvites() + ")");
		((Button) findViewById(R.id.eventsPastButtonEA)).setText("Past Events (" + user.getNumEventsPast() + ")");		
		((Button) findViewById(R.id.eventsDeclinedButtonEA)).setText("Declined Events (" + user.getNumEventsDeclined() + ")");		
	}
	
	public void onClick(View view)
	{
		loadDialog.show();
		Intent intent =  new Intent(this, ListActivity.class);
		switch (view.getId())
		{
		case R.id.eventsPendingButtonEA:
			user.fetchEventsPending();
			intent.putExtra("content", "EVENTS_PENDING");
			break;
		case R.id.eventsUpcomingButtonEA:
			user.fetchEventsUpcoming();
			intent.putExtra("content", "EVENTS_UPCOMING");
			break;
		case R.id.eventCreateButtonEA:
			intent = new Intent(this, EventCreateActivity.class);
			user.fetchGroups();
			break;
		case R.id.eventInvitesButtonEA:
			user.fetchEventInvites();
			intent.putExtra("content", "EVENT_INVITES");
			break;
		case R.id.eventsPastButtonEA:
			user.fetchEventsPast();
			intent.putExtra("content", "EVENTS_PAST");
			break;
		case R.id.eventsDeclinedButtonEA:
			user.fetchEventsDeclined();
			intent.putExtra("content", "EVENTS_DECLINED");
			break;
		}
		GLOBAL.setCurrentUser(user);
		intent.putExtra("email", user.getEmail());
		startActivity(intent);
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
		
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
		user.fetchEventInvites();
    	user.fetchFriendRequests();
    	user.fetchGroupInvites();
    	GLOBAL.setCurrentUser(user);
	}
}
