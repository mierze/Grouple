package cs460.grouple.grouple;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import cs460.grouple.grouple.User.getFriendsTask;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * MessagesActivity has not been implemented yet.
 */
public class MessagesActivity extends ActionBarActivity
{
	private BroadcastReceiver broadcastReceiver;
	private Global GLOBAL;
	private Bundle EXTRAS;
	private Button sendMessageButton;
	private EditText messageEditText;
	private User user; 
	private String recipient;
    private String SENDER_ID = "957639483805"; 
    private ArrayList<Message> messages = new ArrayList<Message>();
	private Dialog loadDialog = null;
    private GoogleCloudMessaging gcm;
    private AtomicInteger msgId = new AtomicInteger();
    private String recipientRegID = "";
    
	//don't think we need these below for our app
  //  public static final String EXTRA_MESSAGE = "message";
   // public static final String PROPERTY_REG_ID = "registration_id";
   // private static final String PROPERTY_APP_VERSION = "appVersion";
    //private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    //private String regid;
    
    //Tag to search for when logging info.
    //static final String TAG = "GCM";
    //Sender ID is the project number from API console. Needs to be secret.


	private String getRecipientRegID() 
	{
		return recipientRegID;
	}

	private void setRecipientRegID(String recipientRegID) 
	{
		this.recipientRegID = recipientRegID;
	}

	//register your activity onResume()
	@Override
	protected void onStop()
	{
		super.onStop();
		loadDialog.hide();
	}
	
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}
	
	//Must unregister onPause()
	@Override
	protected void onPause() {
	    super.onPause();
	    this.unregisterReceiver(mMessageReceiver);
	}

	//This is the handler that will manager to process the broadcast intent
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() 
	{
	    @Override
	    public void onReceive(Context context, Intent intent) 
	    {
	        // Extract data included in the Intent
	        String fromEmail = intent.getStringExtra("FROM");
	        if (fromEmail.equals(recipient))
	        {
	        	fetchMessages(); 
	        }
	    }
	};
	
	private void initActionBar()
	{
		 /* Action bar */
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		String name = EXTRAS.getString("NAME");
		actionbarTitle.setText(name);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);
		GLOBAL = ((Global) getApplicationContext());
		EXTRAS = getIntent().getExtras();
		user = GLOBAL.getCurrentUser();
		//initializing the variables for the send button / message edit text
		sendMessageButton = (Button) findViewById(R.id.sendButton);
		messageEditText = (EditText) findViewById(R.id.messageEditText);
		initActionBar();
		gcm = GoogleCloudMessaging.getInstance(this);
		recipient = EXTRAS.getString("EMAIL"); 
		//load dialog is the icon displayed between switching activities
		loadDialog = GLOBAL.getLoadDialog(new Dialog(this));
        loadDialog.setOwnerActivity(this);   
		initKillswitchListener();
		//Get the recipient 
		new getRegIDTask().execute("http://68.59.162.183/android_connect/get_chat_id.php", recipient);
	}
	
	@Override
    protected void onResume() 
	{
        super.onResume();
	    this.registerReceiver(mMessageReceiver, new IntentFilter("USER_MESSAGE"));
		//new getRegIDTask().execute("http://68.59.162.183/android_connect/get_chat_id.php", recipient);
		fetchMessages(); 
    }
	
    
    /*
	 * 
	 * will be fetching the friends key->val stuff here
	 */
	// Get numFriends, TODO: work on returning the integer
	public int fetchMessages()
	{
		new getMessagesTask().execute("http://68.59.162.183/android_connect/get_messages.php");
		return 1;
	}

	class getMessagesTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("sender", recipient));
			nameValuePairs.add(new BasicNameValuePair("receiver", user.getEmail()));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					JSONArray jsonArray = jsonObject.getJSONArray("messages");
					messages.clear();
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						Message m = new Message(o.getString("message"), o.getString("send_date"), o.getString("sender"),
								"NAME", o.getString("receiver"), null);
						messages.add(m); //adding message to message array
					}
					populateMessages();
				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("fetchMessages", "failed = 2 return");
				}
			} catch (Exception e)
			{
				Log.d("fetchMessages", "exception caught");
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	 /*
	 * 
	 * will be fetching the friends key->val stuff here
	 */
	// Get numFriends, TODO: work on returning the integer
	public int readMessages()
	{
		new readMessagesTask().execute("http://68.59.162.183/android_connect/update_messageread_date.php");
		return 1;
	}

	class readMessagesTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("sender", recipient));
			nameValuePairs.add(new BasicNameValuePair("receiver", user.getEmail()));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					System.out.println("WE HAD SUCCESS IN READ MESSAGES!");
				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("readMessage", "failed = 2 return");
				}
			} catch (Exception e)
			{
				Log.d("readMessage", "exception caught");
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent e)  
	{
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	loadDialog.show();
	    	finish();
	    }
	    return true;
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

	private void populateMessages()
	{
		//layout to inflate into
		LinearLayout messageLayout = (LinearLayout) findViewById(R.id.messageLayout);
		//clear out any previous views already inflated
		messageLayout.removeAllViews();
		//layout inflater
		LayoutInflater li = getLayoutInflater();
		TextView messageBody, messageDate;
		View row = null;
		String message = "";
		//messages consist of some things (messagebody, date, sender, receiver)
		int index = 0;
		//loop through messages (newest first), maybe a map String String with messagebody, date
		for (Message m : messages)
		{
			if (m.getReceiver().equals(user.getEmail()/*our email*/))
				row =  li.inflate(R.layout.message_row_out, null); //inflate this message row
			else
				row =  li.inflate(R.layout.message_row, null); //inflate the sender message row
			messageBody = (TextView) row.findViewById(R.id.messageBody);
			messageBody.setText(m.getMessage());
			messageDate = (TextView) row.findViewById(R.id.messageDate);
			messageDate.setText(m.getDateString());
			//add row into scrollable layout
			messageLayout.addView(row);
			index++;
		}
		//scrolling to last message
		final ScrollView scrollview = ((ScrollView) findViewById(R.id.messagesScrollView));
		scrollview.post(new Runnable() {

		        @Override
		        public void run() {
		            scrollview.fullScroll(ScrollView.FOCUS_DOWN);
		        }
		    });
			
		readMessages();
	}
	
    // Send an upstream message.
    public void onClick(final View view) {

        if (view == findViewById(R.id.sendButton)) 
        {
        	sendMessageButton.setClickable(false);
            //Get message from edit text
            String message = messageEditText.getText().toString();
            
            //make sure message field is not blank
            if(!(message.compareTo("") ==0))
            {
            	 //PHP expects message,sender,receiver.
                new storeMessageTask().execute("http://68.59.162.183/android_connect/send_message.php",message, user.getEmail(),recipient);
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String message = "";
                        try 
                        {
                            Bundle data = new Bundle();
                            //Get message from edit text
                            message = messageEditText.getText().toString();
    						Message m = new Message(message, new Date().toString(), user.getEmail(),
    								user.getName(), recipient, null);
    			       		SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE h:mma");
    						String dateString = dateFormat.format(new Date());
                    		m.setDateString(dateString);
                            messages.add(m);
                            data.putString("msg", m.getMessage());
                            data.putString("my_action", "cs460.grouple.grouple.ECHO_NOW");
                            data.putString("CONTENT_TYPE", "USER_MESSAGE");
                            data.putString("sender", m.getSender());
                            //This is where we put the recipients regID.
                            data.putString("recipient",getRecipientRegID());
                            //This is where we put our first and last name. That way the recipient knows who sent it.
                            data.putString("first", user.getFirstName());
                            data.putString("last", user.getLastName());
                            String id = Integer.toString(msgId.incrementAndGet());
                            gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                            message = "Sent message";
                        } 
                        catch (IOException ex) 
                        {
        					Toast toast = GLOBAL.getToast(MessagesActivity.this, "Error sending message. Please try again.");
        					toast.show();
        					sendMessageButton.setClickable(true);
                            message = "Error :" + ex.getMessage();
                        }
                        return message;
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                    	messageEditText.setText("");
                    	sendMessageButton.setClickable(true);
                        //add to an array of some sort
                    	//ideal to store if it is being received / sent, date, message body
                    	//assuming to start just 1 person to 1 person manually set up
                    	//repopulate messages for now
                    	//GOAL, oncreate pull all messages from server for you and the user you want to see messages of
                    	//if new messages get beamed in add them to that array with their timestamp and all and repopulate the messages
                    	//also when you send a message, add it to the array and repopulate messages
                    	populateMessages();
                    }
                }.execute(null, null, null);
            }
            else
            {
            	Toast toast = GLOBAL.getToast(this, "Please enter a message!");
            	toast.show();
            }         
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
					finish();
				}
			}
		};
		registerReceiver(broadcastReceiver, intentFilter);
		// End Kill switch listener
	}
  
    //This task gets your friend's regid
    private class getRegIDTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			//The recipient's email is urls[1]
			nameValuePairs.add(new BasicNameValuePair("email", urls[1]));
			return global.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{
					String id = jsonObject.getString("regid").toString();
					setRecipientRegID(id);
				} 
				else
				{
					Toast toast = GLOBAL.getToast(MessagesActivity.this, "Error getting GCM REG_ID.");
					toast.show();
				}
			} catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
    
    //Stores the message in the database.
    private class storeMessageTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			//The msg is urls[1], sender urls[2], receiver urls[3]
			nameValuePairs.add(new BasicNameValuePair("msg", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("sender", urls[2]));
			nameValuePairs.add(new BasicNameValuePair("receiver", urls[3]));
			
			return global.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{
					/*//Message was successfully stored, now notify the user.
					String msg = "";
					try {
                        Bundle data = new Bundle();
                        //Get message from edit text
                        EditText mymessage   = (EditText)findViewById(R.id.messageEditText);
                        msg = mymessage.getText().toString();
                        messages.add(msg);
                        data.putString("my_message", msg);
                        data.putString("my_action", "cs460.grouple.grouple.ECHO_NOW");
                        data.putString("recipient",getRecipientRegID());
                        String id = Integer.toString(msgId.incrementAndGet());
                        gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                        msg = "Sent message";
                    } catch (IOException ex) {
                        msg = "Error :" + ex.getMessage();
                    }*/
					
				} 
				else
				{		
					Toast toast = GLOBAL.getToast(MessagesActivity.this, "Error sending message. Please try again.");
					toast.show();
				}
			} catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
}
