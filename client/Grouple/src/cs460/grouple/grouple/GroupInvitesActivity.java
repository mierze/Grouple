package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

//import cs460.grouple.grouple.FriendRequestsActivity.getAcceptFriendTask;
//import cs460.grouple.grouple.FriendRequestsActivity.getDeclineFriendTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*
 * GroupCreateActivity displays a list of all active group requests of a user.
 */
public class GroupInvitesActivity extends ActionBarActivity
{
	Intent parentIntent;
	Intent upIntent;
	int bufferID;
	BroadcastReceiver broadcastReceiver;
	User user;//our current user
	private String receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_invites);


		load();
	}

	public void initActionBar()
	{

		Global global = ((Global) getApplicationContext());
		/* Action bar */
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);

		// upButton.setOnClickListener
		// global.fetchNumFriends(email)
		//Set actionbar title.
		actionbarTitle.setText(user.getFirstName() + "'s Group Invites");
	}
	//Loads the page. As well as populates the user.
	public void load()
	{
		Global global = ((Global) getApplicationContext());
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();	
		//String className = extras.getString("ParentClassName");
		
		//String email = extras.getString("email");
		try
		{
			user = global.loadUser(global.getCurrentUser().getEmail());
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		populateGroupInvites();

		initActionBar();
		initKillswitchListener();
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
			global.destroySession();
			startActivity(login);
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		if (id == R.id.action_home)
		{
			Intent intent = new Intent(this, HomeActivity.class);
			intent.putExtra("ParentClassName", "GroupInvitesActivity");
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
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

	//
	private void populateGroupInvites()
	{
		//Get current layout.
		LinearLayout groupInvitesLayout = (LinearLayout) findViewById(R.id.groupInvitesLayout);
		//Get Global. We use it for global stuff.
		Global global = ((Global) getApplicationContext());
		LayoutInflater li = getLayoutInflater();
		
		Map<Integer, String> groupInvites = user.getGroupInvites();
		
		//array list needs to have group names, maybe the sender names and needs to have group ids	
		if(groupInvites != null && groupInvites.size() > 0 )
		{
			//Calls user getGroups(). 
			
			// looping thru the map
			for (Map.Entry<Integer, String> entry : groupInvites.entrySet())
			{
				GridLayout row = (GridLayout) li.inflate(R.layout.listitem_group_request, null);
				row.setId(entry.getKey());
				((TextView) row.findViewById(R.id.emailTextViewGRLI)).setText(entry.getValue());
				groupInvitesLayout.addView(row);
			}
		}
		else
		{
			//no group requests were found
			GridLayout sadGuy = (GridLayout) li.inflate(R.layout.listitem_sadguy, null);
			TextView sadTextView = (TextView) sadGuy.findViewById(R.id.sadGuyTextView);
			//Set the sad guy text.
			sadTextView.setText("You do not have any group invites.");
			groupInvitesLayout.addView(sadGuy);
		}
	}
			
	
	public void onClick(View view)
	{
		LinearLayout groupInvites = (LinearLayout)findViewById(R.id.groupInvitesLayout);
		switch (view.getId())
		{
		case R.id.declineGroupRequestButtonGRLI:

			View parent = (View) view.getParent();
			bufferID = parent.getId();
			new getDeclineGroupTask().execute("http://68.59.162.183/android_connect/leave_group.php?email="+user.getEmail()+"&gid="+parent.getId());
		
			break;
		case R.id.acceptGroupRequestButtonGRLI:
			
			View parent2 = (View) view.getParent();
			bufferID = parent2.getId();
			new getAcceptGroupTask().execute("http://68.59.162.183/android_connect/accept_group_invite.php",user.getEmail(),Integer.toString(parent2.getId()));

			
			//populateGroupInvites();
			break;
		}
	}

	// Decline Group Request. Refactor the JSON calls.
	private class getDeclineGroupTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
		
			return global.readJSONFeed(urls[0], null);
		}

		@Override
		protected void onPostExecute(String result)
		{
			LinearLayout groupInvites = (LinearLayout) findViewById(R.id.groupInvitesLayout);
			Global global = ((Global) getApplicationContext());
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{
					user.removeGroupInvite(bufferID);
					global.loadUser(user.getEmail());
					//Global global = ((Global) getApplicationContext());
					Context context = getApplicationContext();
					Toast toast = Toast.makeText(context, "Group invite declined.", Toast.LENGTH_SHORT);
					toast.show();
					groupInvites.removeAllViews();
					populateGroupInvites();
				} 
				else
				{
					// failed
					System.out.println("fail!");
					// TextView addFriendMessage = (TextView)
					// findViewById(R.id.addFriendMessageTextViewAFA);
					// addFriendMessage.setText("User not found.");
					// addFriendMessage.setVisibility(0);
				}
			} 
			catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	// Accept code.
	private class getAcceptGroupTask extends AsyncTask<String, Void, String>
	{
		LinearLayout groupInvites = (LinearLayout) findViewById(R.id.groupInvitesLayout);
		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("email", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("gid",urls[2]));
			return global.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			Global global = ((Global) getApplicationContext());
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{		
					user.removeGroupInvite(bufferID);
					global.loadUser(user.getEmail());
					Context context = getApplicationContext();
					Toast toast = Toast.makeText(context, "Group invite accepted.", Toast.LENGTH_SHORT);
					toast.show();
					groupInvites.removeAllViews();
					populateGroupInvites();
				} 
				else
				{
					// failed
					System.out.println("fail!");
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	Intent intent = new Intent(this, GroupsActivity.class);
	    	intent.putExtra("email", user.getEmail());
	    	startActivity(intent);
	    }
	    return true;
	   }

}

