package cs460.grouple.grouple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class EntityMessagesActivity extends BaseActivity
{ 
	private User user; //will be null for now
	private Group group;
	private Event event;
	private String NAME;
	private Button sendMessageButton;
	private LinearLayout messageLayout;
	private LayoutInflater inflater;
	private EditText messageEditText;
    private ArrayList<String> regIDList = new ArrayList<String>();
    private Bundle EXTRAS;
    private String ID;
    private String CONTENT_TYPE;
    private String SENDER_ID = "957639483805"; 
    private ArrayList<Message> messages = new ArrayList<Message>();  
    private GoogleCloudMessaging gcm;
    private AtomicInteger msgId = new AtomicInteger();
    
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	    unregisterReceiver(mMessageReceiver);
	}
	
	//This is the handler that will manager to process the broadcast intent
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() 
	{
	    @Override
	    public void onReceive(Context context, Intent intent) 
	    {
	        // Extract data included in the Intent
	    	String type = intent.getStringExtra("type");
	        String id = intent.getStringExtra("id");
	        if (type.equals("GROUP_MESSAGE") && CONTENT_TYPE.equals("GROUP"))
	        {
		        if (id.equals(ID))
		        {
		            //messages.clear(); //TODO: smartly add to this
		        	fetchMessages(); 
		        }
	        }
	        else if (type.equals("EVENT_MESSAGE") && CONTENT_TYPE.equals("EVENT"))
	        {
	        	  if (id.equals(ID))
			        {
			           // messages.clear(); //TODO: smartly add to this
			        	fetchMessages(); 
			        }
	        }
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);
		registerReceiver(mMessageReceiver, new IntentFilter("ENTITY_MESSAGE"));
		EXTRAS = getIntent().getExtras();
		user = GLOBAL.getCurrentUser();
    	messageEditText = (EditText)findViewById(R.id.messageEditText);
    	sendMessageButton = (Button)findViewById(R.id.sendButton);
    	messageLayout = (LinearLayout) findViewById(R.id.messageLayout);
    	inflater = getLayoutInflater();
		initActionBar(EXTRAS.getString("name"), true);
		gcm = GoogleCloudMessaging.getInstance(this);
		CONTENT_TYPE = EXTRAS.getString("content");
		if (CONTENT_TYPE.equals("GROUP"))
		{
			group = GLOBAL.getGroupBuffer();
			NAME = group.getName();
			ID = EXTRAS.getString("g_id"); 
		}
		else
		{
			event = GLOBAL.getEventBuffer();
			NAME = event.getName();
			ID = EXTRAS.getString("e_id");
		}
		//Get the recipient 
		//new getRegIDTask().execute("http://68.59.162.183/android_connect/get_chat_id.php", recipient);
		new getRegIDsTask().execute("http://68.59.162.183/android_connect/get_chat_ids_by_gid.php",ID ,user.getEmail());
	}

	private void populateMessages()
	{
		//clear out any previous views already inflated
		messageLayout.removeAllViews();
		//layout inflater
		TextView messageBody, messageDate;
		Button contactName;
		View row = null;
		
		int index = 0;
		for (Message m : messages)
		{
			if (m.getSender().equals(user.getEmail()))
				row =  inflater.inflate(R.layout.message_row_entity, null); //inflate the sender message row
			else
				row =  inflater.inflate(R.layout.message_row_entity_out, null); //inflate the sender message row
			contactName = (Button) row.findViewById(R.id.contactNameButton);
			contactName.setText(m.getSenderName());
			contactName.setId(index);
			messageBody = (TextView) row.findViewById(R.id.messageBody);
			messageBody.setText(m.getMessage());
			messageDate = (TextView) row.findViewById(R.id.messageDate);
			messageDate.setText(m.getDateString());
			row.setId(index);
			//add row into scrollable layout
			messageLayout.addView(row);
			index++;
		}
		//scrolling down to the bottom
		final ScrollView scrollview = ((ScrollView) findViewById(R.id.messagesScrollView));
		scrollview.post(new Runnable() {
		        @Override
		        public void run() {
		            scrollview.fullScroll(ScrollView.FOCUS_DOWN);
		        }
		    });
		messageEditText.requestFocus();
		readMessages();
	}
	
	private int readMessages()
	{
		new readMessagesTask().execute("http://68.59.162.183/android_connect/update_entitymessage_lastread.php");
		return 1;
	}

	private class readMessagesTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = CONTENT_TYPE.equals("GROUP") ? "g_id" : "e_id";
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair(type, ID));
			nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
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
			} 
			catch (Exception e)
			{
				Log.d("readMessage", "exception caught");
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
	@Override
	public void onBackPressed() 
	{
    	super.onBackPressed();
    	if (CONTENT_TYPE.equals("group"))
    	{
    		group.fetchGroupInfo();
    		group.fetchMembers();
    		GLOBAL.setGroupBuffer(group);
    	}
    	else
    	{
    		event.fetchEventInfo();
    		event.fetchParticipants();
    		GLOBAL.setEventBuffer(event);
    	}
    	//finish();
	    return;
	}
	
    //Stores the message in the database.
    private class storeMessageTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = CONTENT_TYPE.equals("GROUP") ? "g_id" : "e_id";
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			//The msg is urls[1], sender urls[2], receiver urls[3]
			nameValuePairs.add(new BasicNameValuePair("msg", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("sender", urls[2]));
			nameValuePairs.add(new BasicNameValuePair(type, urls[3]));	
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
					System.out.println("Successfully stored message");
				} 
				else
				{
					Toast toast = GLOBAL.getToast(EntityMessagesActivity.this, "Error sending message. Please try again");
					toast.show();
				}
			} catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
	@Override
    protected void onResume() 
	{
        super.onResume();
		//new getRegIDTask().execute("http://68.59.162.183/android_connect/get_chat_id.php", recipient);
		fetchMessages(); 
    }
	
	//Starts a USER/GROUP/EVENT profile
	public void startProfile(View view)
	{
		loadDialog.show();
		int id = view.getId();		
		Intent intent = new Intent(this, UserProfileActivity.class);
		String friendEmail = messages.get(id).getSender();
		User u = new User(friendEmail);
		u.fetchEventsUpcoming();
		u.fetchFriends();
		u.fetchGroups();
		u.fetchUserInfo();
		if (!GLOBAL.isCurrentUser(friendEmail))
			GLOBAL.setUserBuffer(u);
		else
			GLOBAL.setCurrentUser(u); //reloading user
		intent.putExtra("email", friendEmail);
		startActivity(intent);	
	}
	
	// Send an upstream message.
    public void onClick(final View view) {
        if (view == findViewById(R.id.sendButton)) 
        {
            //Get message from edit text
            String msg = messageEditText.getText().toString();
            //make sure message field is not blank
            if(!(msg.compareTo("") ==0))
            {
            	sendMessageButton.setClickable(false);
               if (CONTENT_TYPE.equals("GROUP"))
            	   new storeMessageTask().execute("http://68.59.162.183/android_connect/send_group_message.php",msg, user.getEmail(), ID);
               else
            	   new storeMessageTask().execute("http://68.59.162.183/android_connect/send_event_message.php",msg, user.getEmail(), ID);
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String msg = "";
                        try 
                        {
                            Bundle data = new Bundle();
                            //Get message from edit text
                            msg = messageEditText.getText().toString();
                            //print message to our screen
                            Message m = new Message(msg, new Date(), user.getEmail(),
    								user.getName(), ID, null);
                            messages.add(m);
                            data.putString("msg", msg);
                            data.putString("my_action", "cs460.grouple.grouple.ECHO_NOW");
                            data.putString("content", CONTENT_TYPE + "_MESSAGE");
                            data.putString("receiver", ID);//may want to just use receiver for all instead of ID
                            data.putString("sender", user.getEmail());
                            data.putString("id", ID);
                            data.putString("name", NAME);
                            data.putString("first", user.getFirstName());
                            data.putString("last", user.getLastName());
                            
                            String id = Integer.toString(msgId.incrementAndGet());
                            
                            //Send the message to all the group members.
                            for(int i = 0; i < regIDList.size(); i++)
                            {
                                data.putString("recipient",regIDList.get(i));                           
                                gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                            }
                        } 
                        catch (IOException ex) 
                        {
                            msg = "Error :" + ex.getMessage();
                            sendMessageButton.setClickable(true);
                            messageEditText.requestFocus();
        					Toast toast = GLOBAL.getToast(EntityMessagesActivity.this, "Error sending message. Please try again");
        					toast.show();
                        }
                        return msg;
                    }
                    
                    @Override
                    protected void onPostExecute(String msg) 
                    {
                    	messageEditText.setText("");
                    	sendMessageButton.setClickable(true);
                    	messageEditText.requestFocus();
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

    //grabs from the database group or event messages, respectively
	public int fetchMessages()
	{
		if (CONTENT_TYPE.equals("GROUP"))
			new getMessagesTask().execute("http://68.59.162.183/android_connect/get_group_messages.php");
		else
			new getMessagesTask().execute("http://68.59.162.183/android_connect/get_event_messages.php");
		
		return 1;
	}

	class getMessagesTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = CONTENT_TYPE.equals("GROUP") ? "g_id" : "e_id";
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			System.out.println("GRABBING MESSAGES ID = " + ID);
			nameValuePairs.add(new BasicNameValuePair(type, ID));
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
					// gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("messages");
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						Message m = new Message(o.getString("message"), o.getString("send_date"), o.getString("sender"),
								o.getString("first") + " " + o.getString("last"), ID, null);
						messages.add(m);
					}
					populateMessages();
				}
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("fetchMessages", "failed = 2 return");
				}
				if (jsonObject.getString("success").toString().equals("0"))
				{
					View row = inflater.inflate(R.layout.list_item_sadguy, null);
					TextView sadGuyTextView = (TextView) row.findViewById(R.id.sadGuyTextView);
					sadGuyTextView.setText("No messages to display!");
					messageLayout.addView(row);
				}
			} 
			catch (Exception e)
			{
				Log.d("fetchMessages", "exception caught");
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
	//This task gets your singular friend's regid
    private class getRegIDsTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = CONTENT_TYPE.equals("GROUP") ? "g_id" : "e_id";
			Global global = ((Global) getApplicationContext());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			//The recipient's email is urls[1]
			nameValuePairs.add(new BasicNameValuePair(type, urls[1]));
			nameValuePairs.add(new BasicNameValuePair("email", urls[2]));

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
					JSONArray jsonArray = jsonObject.getJSONArray("chat_ids");
					for (int i = 0; i < jsonArray.length(); i++)
					{
						//get the ith friend's chat id.
						JSONObject o = (JSONObject) jsonArray.get(i);
						regIDList.add(o.getString("chat_id"));			
					}
				}
				else
				{
					Toast toast = GLOBAL.getToast(EntityMessagesActivity.this, "Error getting regID list.");
					toast.show();
				}
			} 
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
}
