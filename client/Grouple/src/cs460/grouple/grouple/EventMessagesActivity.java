package cs460.grouple.grouple;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class EventMessagesActivity extends ActionBarActivity
{
	
	private BroadcastReceiver broadcastReceiver;
	private Global GLOBAL;
	private User user; //will be null for now
	private Event event;
	
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private String EID;
    //Tag to search for when logging info.
    static final String TAG = "GCM";
    //Sender ID is the project number from API console. Needs to be secret.
    String SENDER_ID = "957639483805"; 
    private ArrayList<String> messages = new ArrayList<String>();
    private ArrayList<String> dates = new ArrayList<String>();
    private ArrayList<String> senders = new ArrayList<String>();
    
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;
    String regid;
    String recipientRegID = "";


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		GLOBAL = ((Global) getApplicationContext());
		
		Bundle extras = getIntent().getExtras();
		user = GLOBAL.getCurrentUser();
		event = GLOBAL.getEventBuffer();
		setContentView(R.layout.activity_messages);
		/* Action bar */
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		gcm = GoogleCloudMessaging.getInstance(this);
		EID = extras.getString("EID"); 
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		String name = extras.getString("NAME");
		//String first = name.split(" ")[0];
		actionbarTitle.setText(name);
	//	initKillswitchListener();
		context = getApplicationContext();
		//onNewIntent(getIntent());
		//Get the recipient 
		//new getRegIDTask().execute("http://68.59.162.183/android_connect/get_chat_id.php", recipient);
		fetchMessages(); 
		setContentView(R.layout.activity_messages);
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
		for (String m : messages)
		{
			
			//if (senders.get(index).equals(user.getEmail()/*our email*/))
			//	row =  li.inflate(R.layout.message_row_out, null); //inflate this message row
			//else
				
			if (senders.get(index).equals(user.getEmail()))
				row =  li.inflate(R.layout.message_row_out, null); //inflate the sender message row
			else
				row =  li.inflate(R.layout.message_row, null); //inflate the sender message row
			
			messageBody = (TextView) row.findViewById(R.id.messageBody);
			messageBody.setText(senders.get(index).split("@")[0] + ":\t" + m);
			messageDate = (TextView) row.findViewById(R.id.messageDate);
			messageDate.setText(dates.get(index));
			
			//set these values to what you want
			
			//add row into scrollable layout
			messageLayout.addView(row);
			index++;
		}
		
		final ScrollView scrollview = ((ScrollView) findViewById(R.id.messagesScrollView));
		scrollview.post(new Runnable() {

		        @Override
		        public void run() {
		            scrollview.fullScroll(ScrollView.FOCUS_DOWN);
		        }
		    });
			

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {

	    	finish(); //preventing back-loop
	    }
	    return true;
	   }
	
	 
    //Stores the message in the database.
    private class storeMessageTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			//The msg is urls[1], sender urls[2], receiver urls[3]
			nameValuePairs.add(new BasicNameValuePair("msg", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("sender", urls[2]));
			nameValuePairs.add(new BasicNameValuePair("e_id", urls[3]));
			
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				System.out.println("CURRENTLY ABOUT TO GET SUCCESS STRING IT IS ");
				System.out.print(jsonObject.getString("success") + "\n\n\n");
				if (jsonObject.getString("success").toString().equals("1"))
				{
					/*//Message was successfully stored, now notify the user.
					String msg = "";
					try {
                        Bundle data = new Bundle();
                        //Get message from edit text
                        
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
					EditText mymessage   = (EditText)findViewById(R.id.messageEditText);
                  mymessage.setText("");
					fetchMessages();
					
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
	
	@Override
    protected void onResume() {
        super.onResume();
	 //   context .registerReceiver(mMessageReceiver, new IntentFilter("NEW_MESSAGE"));
		//new getRegIDTask().execute("http://68.59.162.183/android_connect/get_chat_id.php", recipient);
		fetchMessages(); 
    }
	// Send an upstream message.
    public void onClick(final View view) {

        if (view == findViewById(R.id.sendButton)) 
        {
            //Get message from edit text
            EditText mymessage   = (EditText)findViewById(R.id.messageEditText);
            String msg = mymessage.getText().toString();
            System.out.println("MESSAGE: " + msg);
            //make sure message field is not blank
            if(!(msg.compareTo("") ==0))
            {
            	 //PHP expects msg,sender,receiver.
                //new storeMessageTask().execute("http://68.59.162.183/android_connect/send_message.php",msg,"mierze@gmail.com","tfeipel@gmail.com");
            	System.out.println("http://68.59.162.183/android_connect/send_event_message.php" + msg + " " + user.getEmail() + "," + EID);
                new storeMessageTask().execute("http://68.59.162.183/android_connect/send_event_message.php",msg, user.getEmail(),EID);
               /* new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String msg = "";

                        
                        try {
                            Bundle data = new Bundle();
                            //Get message from edit text
                            EditText mymessage   = (EditText)findViewById(R.id.messageEditText);
                            msg = mymessage.getText().toString();
        
                            messages.add(msg);
                           // receivers.add(recipient);
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
                          //  data.putString("recipient",getRecipientRegID());
                            //This is where we put our first and last name. That way the recipient knows who sent it.
                            data.putString("first", user.getFirstName());
                            data.putString("last", user.getLastName());
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
                }.execute(null, null, null); */
            }
          
          
            
        }
    }

	
    /*
	 * 
	 * will be fetching the friends key->val stuff here
	 */
	// Get numFriends, TODO: work on returning the integer
	public int fetchMessages()
	{
		new getMessagesTask().execute("http://68.59.162.183/android_connect/get_event_messages.php");
		
		return 1;
	}

	class getMessagesTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			System.out.println("GRABBING MESSAGES ID = " + EID);
			nameValuePairs.add(new BasicNameValuePair("e_id", EID));
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
					messages.clear();
					senders.clear();
					dates.clear();
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
						//receivers.add(o.getString("receiver"));
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
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
