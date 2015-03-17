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
	private User user; //will be null for now
	
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private String recipient;
    //Tag to search for when logging info.
    static final String TAG = "GCM";
    //Sender ID is the project number from API console. Needs to be secret.
    String SENDER_ID = "957639483805"; 
    private ArrayList<String> messages = new ArrayList<String>();
    private ArrayList<String> dates = new ArrayList<String>();
    private ArrayList<String> senders = new ArrayList<String>();
    private ArrayList<String> receivers = new ArrayList<String>();
    
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;
    String regid;
    String recipientRegID = "";

	public String getRecipientRegID() {
		return recipientRegID;
	}

	public void setRecipientRegID(String recipientRegID) {
		this.recipientRegID = recipientRegID;
	}

	
	//@Override
	//protected void onNewIntent(Intent intent)
	//{
	//	Bundle extras = intent.getExtras();
	//	recipient = extras.getString("EMAIL"); 
	//}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		GLOBAL = ((Global) getApplicationContext());
		
		Bundle extras = getIntent().getExtras();
		super.onCreate(savedInstanceState);
		
		user = GLOBAL.getCurrentUser();
		setContentView(R.layout.activity_messages);
		/* Action bar */
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		gcm = GoogleCloudMessaging.getInstance(this);
		recipient = extras.getString("EMAIL"); 
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText("Messages");
		initKillswitchListener();
		context = getApplicationContext();
		//onNewIntent(getIntent());
		//Get the recipient 
		new getRegIDTask().execute("http://68.59.162.183/android_connect/get_chat_id.php", recipient);
		fetchMessages(); 
        
	}
	
	@Override
    protected void onResume() {
        super.onResume();
      
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
					System.out.println("WE HAD SUCCESS IN GET MESSAGES!");
					// gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("messages");
					// success so clear previous
					// getUsers().clear();
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						// at each iteration set to hashmap friendEmail ->
						// 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						// function adds friend to the friends map
					
						messages.add(o.getString("message"));
						senders.add(o.getString("sender"));
						receivers.add(o.getString("receiver"));
						dates.add(parseDate(o.getString("send_date")));
						
					}
					populateMessages();
				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("fetchFriends", "failed = 2 return");
					// setNumFriends(0); //PANDA need to set the user class not
					// global
				}
			} catch (Exception e)
			{
				Log.d("fetchFriends", "exception caught");
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {

	    	finish(); //preventing back-loop
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
		
		TextView messageBody, messageDate ;
		View row = null;
		String message = "";
		
		//messages consist of some things (messagebody, date, sender, receiver)
		
		//loop through messages (newest first), maybe a map String String with messagebody, date
		for (String m : messages)
		{
			if (receivers.get(messages.indexOf(m)).equals(user.getEmail()/*our email*/))
				row =  li.inflate(R.layout.message_row, null); //inflate this message row
			else
				row =  li.inflate(R.layout.message_row_out, null); //inflate the sender message row
			
			messageBody = (TextView) row.findViewById(R.id.messageBody);
			messageBody.setText(m);
			messageDate = (TextView) row.findViewById(R.id.messageDate);
			messageDate.setText(dates.get(messages.indexOf(m)));
			
			//set these values to what you want
			
			//add row into scrollable layout
			messageLayout.addView(row);
		}
		
		final ScrollView scrollview = ((ScrollView) findViewById(R.id.messagesScrollView));
		scrollview.post(new Runnable() {

		        @Override
		        public void run() {
		            scrollview.fullScroll(ScrollView.FOCUS_DOWN);
		        }
		    });
			

	}
	
    // Send an upstream message.
    public void onClick(final View view) {

        if (view == findViewById(R.id.sendButton)) 
        {
            //Get message from edit text
            EditText mymessage   = (EditText)findViewById(R.id.messageEditText);
            String msg = mymessage.getText().toString();
            
            //make sure message field is not blank
            if(!(msg.compareTo("") ==0))
            {
            	 //PHP expects msg,sender,receiver.
                //new storeMessageTask().execute("http://68.59.162.183/android_connect/send_message.php",msg,"mierze@gmail.com","tfeipel@gmail.com");
                new storeMessageTask().execute("http://68.59.162.183/android_connect/send_message.php",msg, user.getEmail(),recipient);
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String msg = "";

                        
                        try {
                            Bundle data = new Bundle();
                            //Get message from edit text
                            EditText mymessage   = (EditText)findViewById(R.id.messageEditText);
                            msg = mymessage.getText().toString();
        
                            messages.add(msg);
                            receivers.add(recipient);
                            senders.add(user.getEmail());
                          
                    		SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE h:mma");
                    		
                    		String date = dateFormat.format(new Date());
                    		dates.add(date);
                            data.putString("my_message", msg);
                            data.putString("my_action", "cs460.grouple.grouple.ECHO_NOW");
                            data.putString("sender", user.getEmail());
                            //Clear edit text
                           // mymessage.setText("");
                            //Get friend's regID based off their email address from db
                            //Todd's Reg ID
                            //String recipientRegId = "APA91bFdWkh9GiaNoLJvGyFpSK3HRQy8vtlmh3OPK8FekU4aWEhZn_hwvr7LmYu_s11dQnoPmj6hKuklISIh_A2Dhyjm_cNT-K4kh5-bYhPYpp-QGbqScbwE9YCnWqyXORN2gwY3fNQx-_ex7D6i-ONaT7peHcu3Hlzbc-60amu0pTu8SD9l7xI";
                            //Brett's Reg ID
                            
                            //This is where we put the recipients regID.
                            data.putString("recipient",getRecipientRegID());
                            String id = Integer.toString(msgId.incrementAndGet());
                            gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                            msg = "Sent message";
                        } catch (IOException ex) {
                            msg = "Error :" + ex.getMessage();
                        }
                      
                        return msg;
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                    	
                    	EditText mymessage   = (EditText)findViewById(R.id.messageEditText);
                    	mymessage.setText("");
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
					
				} else
				{
					Context context = getApplicationContext();
					Toast toast = Toast.makeText(context, "Error Getting GCM REGID. Contact Devs", Toast.LENGTH_LONG);
					toast.show();
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
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
					
				} else
				{
					Context context = getApplicationContext();
					Toast toast = Toast.makeText(context, "Error Storing Message. Message not sent. Contact Devs", Toast.LENGTH_LONG);
					toast.show();
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
    
    private String parseDate(String dateString)
	{
		System.out.println("\n\nDATE IS FIRST: " + dateString);
		String date = "";
		SimpleDateFormat raw = new SimpleDateFormat("yyyy-M-d h:mm:ss");
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"EEEE h:mma");
		try
		{
			Date parsedDate = (Date) raw.parse(dateString);
			date = dateFormat.format(parsedDate);
			// date = raw.format(parsedDate);
			System.out.println("\nDATE IN RAW TRANSLATION: "
					+ raw.format(parsedDate));
			System.out.println("\nDATE IN FINAL: "
					+ dateFormat.format(parsedDate) + "\n\n");
		} catch (ParseException ex)
		{
			System.out.println("Exception " + ex);
		}
		return date;
	}
}
