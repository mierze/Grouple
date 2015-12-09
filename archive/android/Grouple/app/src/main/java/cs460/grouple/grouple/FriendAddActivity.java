package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Brett, Todd, Scott
 * FriendAddActivity allows user to add another user as a friend.
 * 
 */
public class FriendAddActivity extends BaseActivity
{
	private User user; //current user
	private GcmUtility gcmUtil;
	private String receiver = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Set the activity layout.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_add);
		GLOBAL = ((Global) getApplicationContext());
		user = GLOBAL.getCurrentUser();
        gcmUtil = new GcmUtility(GLOBAL);
        initActionBar("Add Friend", true);
	}
	
	// Adds a friend.
	public void addFriendButton(View view)
	{
		EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
		if (emailEditText.getText().toString() == null || emailEditText.getText().toString().isEmpty())
		{
			TextView addFriendMessage = (TextView) findViewById(R.id.addFriendMessageTextView);
			addFriendMessage.setText("Please enter a valid email address.");
			addFriendMessage.setTextColor(getResources().getColor(
					R.color.red));
			addFriendMessage.setVisibility(View.VISIBLE);

		}
		else
			new getAddFriendTask().execute("http://mierze.gear.host/grouple/android_connect/add_friend.php");
	}

	// This task sends a friend request to the given user.
	private class getAddFriendTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
			receiver = emailEditText.getText().toString();
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

				EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
				emailEditText.setText("");
				TextView addFriendMessage = (TextView) findViewById(R.id.addFriendMessageTextView);
				addFriendMessage.setText(jsonObject.getString("message"));

				if (jsonObject.getString("success").toString().equals("1"))
				{
					Toast toast = GLOBAL.getToast(FriendAddActivity.this, jsonObject.getString("message"));
					toast.show();
					//Send the push notification.
					gcmUtil.sendNotification(receiver,"FRIEND_REQUEST");
			
				} 
				else if (jsonObject.getString("success").toString().equals("2"))
				{
					addFriendMessage.setTextColor(getResources().getColor(
							R.color.yellow));
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

}
