package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/*
 * AddFriendActivity allows user to add another user as a friend.
 */
public class FriendAddActivity extends ActionBarActivity
{
	private BroadcastReceiver broadcastReceiver;
	private User user; //current user
	private Global GLOBAL;
	private Dialog loadDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Set the activity layout.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend);
		GLOBAL = ((Global) getApplicationContext());
		user = GLOBAL.getCurrentUser();
		loadDialog = GLOBAL.getLoadDialog(new Dialog(this));
        loadDialog.setOwnerActivity(this);
		initActionBar();
		initKillswitchListener();
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		loadDialog.hide();
	}

	public void initActionBar()
	{
		// Set up the action bar.
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		// Set up the back button listener for the action bar.

		// Set the action bar title.
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText("Add Friend");
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
			// If the user hits the logout button, then clear global and go to
			// the logout screen.
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

	// Adds a friend.
	public void addFriendButton(View view)
	{
		EditText emailEditText = (EditText) findViewById(R.id.emailEditTextAFA);
		if (emailEditText.getText().toString() == null || emailEditText.getText().toString().isEmpty())
		{
			TextView addFriendMessage = (TextView) findViewById(R.id.addFriendMessageTextViewAFA);
			addFriendMessage.setText("Please enter a valid email address.");
			addFriendMessage.setTextColor(getResources().getColor(
					R.color.red));
			addFriendMessage.setVisibility(View.VISIBLE);

		}
		else
			new getAddFriendTask().execute("http://68.59.162.183/android_connect/add_friend.php");
	}

	// This task sends a friend request to the given user.
	private class getAddFriendTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			EditText emailEditText = (EditText) findViewById(R.id.emailEditTextAFA);
			String receiver = emailEditText.getText().toString();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("sender", user.getEmail()));
			nameValuePairs.add(new BasicNameValuePair("receiver", receiver));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				System.out.println(jsonObject.getString("success"));

				EditText emailEditText = (EditText) findViewById(R.id.emailEditTextAFA);
				emailEditText.setText("");
				TextView addFriendMessage = (TextView) findViewById(R.id.addFriendMessageTextViewAFA);
				addFriendMessage.setText(jsonObject.getString("message"));

				if (jsonObject.getString("success").toString().equals("1"))
				{
					Context context = getApplicationContext();
					Toast toast = GLOBAL.getToast(context, jsonObject.getString("message"));
					toast.show();
			
				} 
				else if (jsonObject.getString("success").toString().equals("2"))
				{
					addFriendMessage.setTextColor(getResources().getColor(
							R.color.orange));
					addFriendMessage.setVisibility(0);
				} 
				else
				{
					// user does not exist, self request, or sql error
					System.out.println("fail!");
					addFriendMessage.setTextColor(getResources().getColor(
							R.color.red));
					addFriendMessage.setVisibility(0);
				}
			

			} 
			catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	
	@Override
	public void onBackPressed() 
	{
    	loadDialog.show();
    	user.fetchFriends();
    	user.fetchFriendRequests();
    	GLOBAL.setCurrentUser(user);
    	finish(); //preventing back-loop
	    return;
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
					finish();
				}

			}
		};
		registerReceiver(broadcastReceiver, intentFilter);
		// End Kill switch listener
	}
}
