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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
 * @author Brett, Todd, Scott EntityMessagesActivity
 * 
 */
public class EntityMessagesActivity extends BaseActivity
{
	private User user; // will be null for now
	private Group group;
	private Event event;
	private String NAME;
	private Button sendMessageButton;
	private ListView listView;
	private EditText messageEditText;
	private ArrayList<String> regIDList = new ArrayList<String>();
	private LinearLayout sadGuyLayout;
	private String ID;
	private ArrayAdapter<Message> adapter;
	private String CONTENT;
	private String SENDER_ID = "957639483805";
	private ArrayList<Message> messages = new ArrayList<Message>();
	private GoogleCloudMessaging gcm;
	private AtomicInteger msgId = new AtomicInteger();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);
		Bundle extras = getIntent().getExtras();
		String email = extras.getString("email");
		if (email != null)
			user = GLOBAL.getUser(email);
		else
			user = GLOBAL.getCurrentUser();
		sadGuyLayout = (LinearLayout) findViewById(R.id.sadGuyLayout);
		messageEditText = (EditText) findViewById(R.id.messageEditText);
		sendMessageButton = (Button) findViewById(R.id.sendButton);
		listView = (ListView) findViewById(R.id.listView);

		try
		{
			gcm = GoogleCloudMessaging.getInstance(this);
		}
		catch (Exception e)
		{
		}
		CONTENT = extras.getString("content");
		if (CONTENT.equals("GROUP"))
		{
			ID = Integer.toString(extras.getInt("g_id"));
			group = GLOBAL.getGroup(Integer.parseInt(ID));
			NAME = group.getName();
		}
		else
		{
			ID = Integer.toString(extras.getInt("e_id"));
			event = GLOBAL.getEvent(Integer.parseInt(ID));
			NAME = event.getName();
		}

		initActionBar(NAME, true);
		new getRegIDsTask()
				.execute("http://mierze.gear.host/grouple/android_connect/get_chat_ids_by_gid.php", ID, user.getEmail());
	}

	@Override
	protected void onResume()
	{

		LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, new IntentFilter("ENTITY_MESSAGE"));
		super.onResume();
		fetchData();
	}

	@Override
	protected void onPause()
	{
		LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver);
		super.onPause();
	}

	// This is the handler that will manager to process the broadcast intent
	private BroadcastReceiver dataReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// Extract data included in the Intent
			String type = intent.getStringExtra("type");
			String id = intent.getStringExtra("id");
			if (type.equals("GROUP_MESSAGE") && CONTENT.equals("GROUP"))
			{
				if (id.equals(ID))
				{
					fetchData();
				}
			}
			else if (type.equals("EVENT_MESSAGE") && CONTENT.equals("EVENT"))
			{
				if (id.equals(ID))
				{
					fetchData();
				}
			}
		}
	};

	private class MessageListAdapter extends ArrayAdapter<Message>
	{
		public MessageListAdapter()
		{
			super(EntityMessagesActivity.this, 0, messages);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final Message m = messages.get(position);
			if (convertView == null)
				convertView = inflater.inflate(m.getSender().equals(user.getEmail()) ? R.layout.message_row_entity
						: R.layout.message_row_entity_out, null);

			Button contactName = (Button) convertView.findViewById(R.id.contactNameButton);
			contactName.setText(m.getSenderName());
			contactName.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					startProfile(m);
				}
			});

			TextView messageBody = (TextView) convertView.findViewById(R.id.messageBody);
			TextView messageDate = (TextView) convertView.findViewById(R.id.messageDate);
			messageBody.setText(m.getMessage());
			messageDate.setText(m.getDateString());
			convertView.setId(m.getID());
			return convertView;
		}

		@Override
		public int getItemViewType(int position)
		{
			Message m = messages.get(position);
			return m.getSender().equals(user.getEmail()) ? 0 : 1;
		}

		@Override
		public int getViewTypeCount()
		{
			return 2;
		}
	}

	private void updateUI()
	{
		if (!messages.isEmpty())
		{
			if (adapter == null)
			{
				adapter = new MessageListAdapter();
				listView.setAdapter(adapter);
				messageEditText.requestFocus();
				scrollListView(adapter.getCount() - 1, listView);
				readMessages();
			}
			else
			{
				messageEditText.requestFocus();
				adapter.notifyDataSetChanged();
				sadGuyLayout.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
				scrollListView(adapter.getCount() - 1, listView);
				readMessages();
			}
		}
		else
		{
			View sadGuyView = inflater.inflate(R.layout.list_item_sadguy, null);
			TextView sadGuyTextView = (TextView) sadGuyView.findViewById(R.id.sadGuyTextView);
			sadGuyTextView.setText("No messages to display!");
			listView.setVisibility(View.GONE);
			sadGuyLayout.removeAllViews();
			sadGuyLayout.addView(sadGuyView);
			sadGuyLayout.setVisibility(View.VISIBLE);

		}
	}

	private void readMessages()
	{
		new readMessagesTask().execute("http://mierze.gear.host/grouple/android_connect/update_entitymessage_lastread.php");
	}

	private class readMessagesTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = CONTENT.equals("GROUP") ? "g_id" : "e_id ";
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

	// Stores the message in the database.
	private class storeMessageTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = CONTENT.equals("GROUP") ? "g_id" : "e_id";
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			// The msg is urls[1], sender urls[2], receiver urls[3]
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
					fetchData();
				}
				else
				{
					Toast toast = GLOBAL.getToast(EntityMessagesActivity.this,
							"Error sending message. Please try again");
					toast.show();
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	// starts a a user profile
	public void startProfile(Message m)
	{
		loadDialog.show();
		Intent intent = new Intent(this, UserProfileActivity.class);
		String friendEmail = m.getSender();
		intent.putExtra("email", friendEmail);
		startActivity(intent);
	}

	// Send an upstream message.
	public void onClick(final View view)
	{
		super.onClick(view);
		if (view == findViewById(R.id.sendButton))
		{
			// Get message from edit text
			String msg = messageEditText.getText().toString();
			// make sure message field is not blank
			if (!(msg.compareTo("") == 0))
			{
				sendMessageButton.setClickable(false);
				if (CONTENT.equals("GROUP"))
					new storeMessageTask().execute("http://mierze.gear.host/grouple/android_connect/send_group_message.php", msg,
							user.getEmail(), ID);
				else
					new storeMessageTask().execute("http://mierze.gear.host/grouple/android_connect/send_event_message.php", msg,
							user.getEmail(), ID);
				new AsyncTask<Void, Void, String>()
				{
					@Override
					protected String doInBackground(Void... params)
					{
						String msg = "";
						try
						{
							Bundle data = new Bundle();
							// Get message from edit text
							msg = messageEditText.getText().toString();
							// print message to our screen
							Message m = new Message(msg, new Date(), user.getEmail(), user.getName(), ID, null);
							messages.add(m);
							data.putString("msg", msg);
							data.putString("my_action", "cs460.grouple.grouple.ECHO_NOW");
							data.putString("content", CONTENT + "_MESSAGE");
							data.putString("receiver", ID);// may want to just
															// use receiver for
															// all instead of ID
							data.putString("sender", user.getEmail());
							data.putString("id", ID);
							data.putString("name", NAME);
							data.putString("first", user.getFirstName());
							data.putString("last", user.getLastName());

							String id = Integer.toString(msgId.incrementAndGet());

							// Send the message to all the group members.
							for (int i = 0; i < regIDList.size(); i++)
							{
								data.putString("recipient", regIDList.get(i));
								gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
							}
						}
						catch (IOException ex)
						{
							msg = "Error :" + ex.getMessage();
							sendMessageButton.setClickable(true);
							messageEditText.requestFocus();
							Toast toast = GLOBAL.getToast(EntityMessagesActivity.this,
									"Error sending message. Please try again");
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

	// grabs from the database group or event messages, respectively
	private void fetchData()
	{
		if (CONTENT.equals("GROUP"))
			new getMessagesTask().execute("http://mierze.gear.host/grouple/android_connect/get_group_messages.php");
		else
			new getMessagesTask().execute("http://mierze.gear.host/grouple/android_connect/get_event_messages.php");

	}

	class getMessagesTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = CONTENT.equals("GROUP") ? "g_id" : "e_id";
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair(type, ID));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				messages.clear();
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("messages");
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						Message m = new Message(o.getString("message"), o.getString("send_date"),
								o.getString("sender"), o.getString("first") + " " + o.getString("last"), ID, null);
						messages.add(m);
					}

				
				}
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("fetchMessages", "failed = 2 return");
				}
				if (jsonObject.getString("success").toString().equals("0"))
				{

				}
				updateUI();
			}
			catch (Exception e)
			{
				Log.d("fetchMessages", "exception caught");
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	// This task gets your singular friend's regid
	private class getRegIDsTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = CONTENT.equals("GROUP") ? "g_id" : "e_id";
			Global global = ((Global) getApplicationContext());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			// The recipient's email is urls[1]
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
						// get the ith friend's chat id.
						JSONObject o = (JSONObject) jsonArray.get(i);
						regIDList.add(o.getString("chat_id"));
					}
				}
				else
				{
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
}
