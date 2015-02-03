package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

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
import android.widget.ImageButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*
 * FriendRequestsActivity displays a list of all active friend requests of a user.
 */
public class FriendRequestsActivity extends ActionBarActivity
{
	BroadcastReceiver broadcastReceiver;
	User user; //current user
	String acceptEmail;
	String declineEmail;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		load();

	}

	// Start the action bar.
	public void initActionBar()
	{
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText(user.getFirstName() + "'s Friend Requests");
		ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);

	}

	// Gets the friends requests and displays them to the user
	public void load()
	{
		Global global = ((Global) getApplicationContext());

		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		
	
		//preloaded
		user = global.getCurrentUser();
		
		

		// Php call that gets the users friend requests.
		if (user != null)
			populateFriendRequests();

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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_logout)
		{
			Global global = ((Global) getApplicationContext());
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
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * Using the user's email address, we get the user's current friend
	 * requests. On success we display the user's current friends request, if
	 * there are any.
	 */
	public void populateFriendRequests()
	{
		LinearLayout friendRequestsLayout = (LinearLayout)findViewById(R.id.listLayout);
		LayoutInflater li = getLayoutInflater();
		if (user.getNumFriendRequests() > 0)
		{
			ArrayList<String> friendRequests = user.getFriendRequests();	
			Global global = ((Global) getApplicationContext());

			// looping thru array and inflating listitems to the
			// friend requests list
			for (int i = 0; i < friendRequests.size(); i++)
			{
				GridLayout row = (GridLayout) li.inflate(R.layout.list_row_acceptdecline, null);
				// Setting text of each friend request to the email
				// of the sender
				((TextView) row.findViewById(R.id.emailTextViewFRLI)).setText(friendRequests.get(i));
				friendRequestsLayout.addView(row);
			}
		}
		else
		{
			//no friend requests were found
			GridLayout sadGuy = (GridLayout) li.inflate(R.layout.listitem_sadguy, null);
			sadGuy.findViewById(R.id.sadGuyTextView);
			friendRequestsLayout.addView(sadGuy);
		}
	}

	/*
	 * On click listener that determines if the user declined or accepted the
	 * code.
	 */
	public void onClick(View view)
	{
		Global global = ((Global) getApplicationContext());
		View parent = (View) view.getParent();
		switch (view.getId())
		{
		case R.id.declineButton:

			TextView declineEmailTextView = (TextView) parent
					.findViewById(R.id.emailTextViewFRLI);
			declineEmail = declineEmailTextView.getText().toString(); //PANDA
			new getDeclineFriendTask()
					.execute("http://68.59.162.183/android_connect/decline_friend_request.php");
			break;
		case R.id.acceptButton:
			TextView acceptEmailTextView = (TextView) parent
					.findViewById(R.id.emailTextViewFRLI);
			acceptEmail = acceptEmailTextView.getText().toString();
			new getAcceptFriendTask()
					.execute("http://68.59.162.183/android_connect/accept_friend_request.php");
			break;
		}
	}

	/*
	 * Code for declining a friend request. On success, we remove the friend
	 * request and refresh the friend requests activity.
	 */
	private class getDeclineFriendTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());

			String receiver = user.getEmail();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("sender", declineEmail));
			nameValuePairs.add(new BasicNameValuePair("receiver", receiver));
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
					
					Context context = getApplicationContext();
					Toast toast = Toast.makeText(context, "Friend request declined.", Toast.LENGTH_SHORT);
					toast.show();
					//do something probably
					user.removeFriendRequest(declineEmail);
					global.loadUser(user.getEmail());
					System.out.println("success in decline!");
					declineEmail = null;
					//removing all friend requests for refresh
					LinearLayout friendRequests = (LinearLayout)findViewById(R.id.listLayout);
					friendRequests.removeAllViews();
					
					//repopulate view
					populateFriendRequests();

				} else
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
	    	Intent intent = new Intent(this, FriendsActivity.class);
	    	intent.putExtra("email", user.getEmail());
	    	startActivity(intent);
	    }
	    return true;
	   }
	
	/*
	 * Code for accepting a friend request. On success, we remove the friend
	 * request and refresh the friend requests activity. We also confirm the
	 * friendship in the database.
	 */
	private class getAcceptFriendTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			
			String receiver = user.getEmail();
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("sender", acceptEmail));
			nameValuePairs.add(new BasicNameValuePair("receiver", receiver));
			return global.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			Global global = ((Global) getApplicationContext());
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// successful
					System.out.println("success!");
					
					//removing friend request from memory
					user.removeFriendRequest(acceptEmail);
					global.loadUser(user.getEmail());
					acceptEmail = null; //reset
						
					//removing all friend requests for refresh
					LinearLayout friendRequests = (LinearLayout)findViewById(R.id.listLayout);
					friendRequests.removeAllViews();
					
					Context context = getApplicationContext();
					Toast toast = Toast.makeText(context, "Friend request accepted.", Toast.LENGTH_SHORT);
					toast.show();
					//repopulate view
					populateFriendRequests();
					
					//restarting friend requests activity, could also do view removal
					//startFriendRequestsActivity();

				} else
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

	/*
	 * Start activity functions for refreshing friend requests, going back and
	 * logging out
	 */
	public void startFriendRequestsActivity()
	{
		Intent intent = new Intent(this, FriendRequestsActivity.class);
		intent.putExtra("email", user.getEmail());
		startActivity(intent);
	}

	public void startFriendsActivity(View view)
	{
		Intent intent = new Intent(this, FriendsActivity.class);
		intent.putExtra("email", user.getEmail());
		startActivity(intent);
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
