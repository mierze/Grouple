package cs460.grouple.grouple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * 
 * @author Brett, Todd, Scott
 * MessagesActivity creates the vertical list of side-by-side messaging and provides messaging functions.
 * 
 */
public class MessagesActivity extends BaseActivity
{
	private Button sendMessageButton;
	private EditText messageEditText;
	private User user;
	private String recipient; //user that this user is talking to
	private String SENDER_ID = "957639483805";
	private LinearLayout listViewLayout;
	private ListView listView;
	private ArrayList<Message> messages = new ArrayList<Message>();
	private GoogleCloudMessaging gcm;
	private AtomicInteger msgId = new AtomicInteger();
	private String recipientRegID = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);
		Bundle extras = getIntent().getExtras();
		user = GLOBAL.getCurrentUser();
		// initializing the variables for the send button / message edit text
		sendMessageButton = (Button) findViewById(R.id.sendButton);
		messageEditText = (EditText) findViewById(R.id.messageEditText);
		listViewLayout = (LinearLayout) findViewById(R.id.listViewLayout);
		listView = (ListView) findViewById(R.id.listView);
		initActionBar(extras.getString("name"), true);
		try
		{
		gcm = GoogleCloudMessaging.getInstance(this);
		}
		catch (Exception e)
		{
			
		}
		recipient = extras.getString("email");
		messages = user.getMessages(recipient);
		// Get the recipient
		new getRegIDTask().execute("http://68.59.162.183/android_connect/get_chat_id.php", recipient);
	}
	
	private void fetchData()
	{
		user.fetchMessages(this, recipient);
	}

	// This listens for pings from the data service to let it know that there
	// are updates
	private BroadcastReceiver dataReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getAction().equals("user_data")) 
			{
	            
				updateUI();
			}
			else if (intent.getAction().equals("USER_MESSAGE")) 
			{
			String receiver = intent.getStringExtra("receiver");
			if (receiver.equals(user.getEmail()))
			{
				fetchData();
				updateUI();
			}
			}
		}
	};

	@Override
	protected void onResume()
	{
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction("user_data");
		filter.addAction("USER_MESSAGE");
		LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, filter);
		fetchData();
	}

	@Override
	protected void onPause()
	{
		LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver);
		super.onPause();
	}
	
	private void readMessages()
	{
		new readMessagesTask().execute("http://68.59.162.183/android_connect/update_messageread_date.php");
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
			}
			catch (Exception e)
			{
				Log.d("readMessage", "exception caught");
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
	private class MessageListAdapter extends ArrayAdapter<Message>
	{
		public MessageListAdapter()
		{
			super(MessagesActivity.this, 0, messages);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View itemView = convertView;
			if (itemView == null)
				itemView = inflater.inflate(getItemViewType(position), parent, false);
			Message m = messages.get(position);
			TextView messageBody = (TextView) itemView.findViewById(R.id.messageBody);
			TextView messageDate = (TextView) itemView.findViewById(R.id.messageDate);
			messageBody.setText(m.getMessage());
			messageDate.setText(m.getDateString());
			itemView.setId(m.getID());
			return itemView;
		}

		@Override
		public int getItemViewType(int position)
		{
			int listItemID = R.layout.list_row;
			Message m = messages.get(position);
			if (m.getSender().equals(user.getEmail()))
				listItemID = R.layout.message_row; 
			else
				listItemID = R.layout.message_row_out;
			return listItemID;
		}
	}

	private void updateUI()
	{
		if (!messages.isEmpty())
		{
			ArrayAdapter<Message> adapter = new MessageListAdapter();
			listView.setAdapter(adapter);
			messageEditText.requestFocus();
			scrollListView(adapter.getCount()-1, listView);
			readMessages();
			//listViewLayout.removeAllViews();
			listView.setVisibility(View.VISIBLE);
		}
		else
		{
			TextView sadGuyTextView;
			View sadGuyView = inflater.inflate(R.layout.list_item_sadguy, null);
			sadGuyTextView = (TextView) sadGuyView.findViewById(R.id.sadGuyTextView);
			sadGuyTextView.setText("No messages to display!");
			listView.setVisibility(View.GONE);
			listViewLayout.addView(sadGuyView);
		}
	}

	// Send an upstream message.
	public void onClick(final View view)
	{
		super.onClick(view);
		if (view == findViewById(R.id.sendButton))
		{
			sendMessageButton.setClickable(false);
			// Get message from edit text
			String message = messageEditText.getText().toString();

			// make sure message field is not blank
			if (!(message.compareTo("") == 0))
			{
				// PHP expects message,sender,receiver.
				new storeMessageTask().execute("http://68.59.162.183/android_connect/send_message.php", message,
						user.getEmail(), recipient);
				new AsyncTask<Void, Void, String>()
				{
					@Override
					protected String doInBackground(Void... params)
					{
						String message = "";
						try
						{
							Bundle data = new Bundle();
							// Get message from edit text
							message = messageEditText.getText().toString();
							Message m = new Message(message, new Date(), user.getEmail(), user.getName(), recipient,
									null);
							//messages.add(m);
							data.putString("msg", m.getMessage());
							data.putString("my_action", "cs460.grouple.grouple.ECHO_NOW");
							data.putString("content", "USER_MESSAGE");
							data.putString("sender", m.getSender());
							data.putString("receiver", m.getReceiver());
							// This is where we put the recipients regID.
							data.putString("recipient", recipientRegID);
							// This is where we put our first and last name.
							// That way the recipient knows who sent it.
							data.putString("first", user.getFirstName());
							data.putString("last", user.getLastName());
							String id = Integer.toString(msgId.incrementAndGet());
							gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
							message = "Sent message";
						}
						catch (IOException ex)
						{
							Toast toast = GLOBAL.getToast(MessagesActivity.this,
									"Error sending message. Please try again.");
							toast.show();
							sendMessageButton.setClickable(true);
							messageEditText.requestFocus();
							message = "Error :" + ex.getMessage();
						}
						return message;
					}

					@Override
					protected void onPostExecute(String msg)
					{
						messageEditText.setText("");
						sendMessageButton.setClickable(true);
						messageEditText.requestFocus();
						fetchData();
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

	// This task gets your friend's regid
	private class getRegIDTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			// The recipient's email is urls[1]
			nameValuePairs.add(new BasicNameValuePair("email", urls[1]));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
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
					recipientRegID = jsonObject.getString("regid").toString();
				}
				else
				{
					Toast toast = GLOBAL.getToast(MessagesActivity.this, "Error getting GCM REG_ID.");
					toast.show();
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	// Stores the message in the database.
	private class storeMessageTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			// The msg is urls[1], sender urls[2], receiver urls[3]
			nameValuePairs.add(new BasicNameValuePair("msg", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("sender", urls[2]));
			nameValuePairs.add(new BasicNameValuePair("receiver", urls[3]));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
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
					fetchData();
				}
				else
				{
					Toast toast = GLOBAL.getToast(MessagesActivity.this, "Error sending message. Please try again.");
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
