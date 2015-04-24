package cs460.grouple.grouple;

import java.util.ArrayList;
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
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.gcm.GoogleCloudMessaging;
/*
 * TODO: needs some thinking and re working before making list
 */
public class RecentMessagesActivity extends BaseActivity
{
	private int IMAGE_INDEX = 0;// holy shit
	private User user; // will be null for now
	private ArrayList<Message> recentMessages = new ArrayList<Message>();
	private GoogleCloudMessaging gcm;
	private LinearLayout listViewLayout;
	private ListView listView;
	private AtomicInteger msgId = new AtomicInteger();

	// This is the handler that will manager to process the broadcast intent
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// Extract data included in the Intent
			String receiver = intent.getStringExtra("receiver");
			if (user.getEmail().equals(receiver))
			{
				// do this
				fetchRecentContacts();
			}
			/*
			 * for (Message m : recentMessages) { if
			 * (m.getSender().equals(fromEmail) ||
			 * m.getReceiver().equals(fromEmail)) { //copy message over, add it
			 * to top of stack, adjust images accordingly //for now just change
			 * message //m.setMessage(message)
			 * 
			 * //maybe for now just grab all } }
			 */
		}
	};

	@Override
	protected void onPause()
	{
		unregisterReceiver(mMessageReceiver);
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent_messages);
		listViewLayout = (LinearLayout) findViewById(R.id.listViewLayout);
		listView = (ListView) findViewById(R.id.listView);
		user = GLOBAL.getCurrentUser();
		/* Action bar */
		initActionBar("Messages", true);
		// new
		// getContactsTask().execute("http://68.59.162.183/android_connect/get_chat_id.php");
		// Check device for Play Services APK. If check succeeds, proceed with
		// GCM registration.
	}

	@Override
	protected void onResume()
	{
		registerReceiver(mMessageReceiver, new IntentFilter("USER_MESSAGE"));
		super.onResume();
		fetchRecentContacts();
		// Check device for Play Services APK.
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == R.id.action_messages)
		{
			return true;
			// already here
		}
		return super.onOptionsItemSelected(item);
	}

	public void startMessages(View view)
	{
		Intent intent = new Intent(this, MessagesActivity.class);
		String EMAIL = recentMessages.get(view.getId()).getReceiver().equals(user.getEmail()) ? recentMessages.get(
				view.getId()).getSender() : recentMessages.get(view.getId()).getReceiver();
		intent.putExtra("email", EMAIL);
		intent.putExtra("name", recentMessages.get(view.getId()).getSenderName());
		startActivity(intent);
	}

	// onClick for items to bring
	public void newMessageButton(View view)
	{
		loadDialog.show();
		final String CONTENT = "SELECT_FRIEND";
		Intent intent = new Intent(this, EventListActivity.class);
		intent.putExtra("email", user.getEmail());
		intent.putExtra("content", CONTENT);
		startActivity(intent);
	}

	private class ContactListAdapter extends ArrayAdapter<Message>
	{
		public ContactListAdapter()
		{
			super(RecentMessagesActivity.this, R.layout.list_row_contact, recentMessages);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			View itemView = convertView;
			if (itemView == null)
				itemView = inflater.inflate(R.layout.list_row_contact, parent, false);
			final Message m = recentMessages.get(position);
			TextView contactName = (TextView) itemView.findViewById(R.id.contactName);
			TextView messageBody = (TextView) itemView.findViewById(R.id.messageBody);
			TextView messageDate = (TextView) itemView.findViewById(R.id.messageDate);
			ImageButton contactImage = (ImageButton) itemView.findViewById(R.id.contactImage);
			/*contactImage.setOnClickListener(new OnClickListener()
			{
				@Override
			 public void onClick(View view)
				{
					startProfileActivity(m);
				}
			});*/
			messageBody.setText(m.getMessage());
			messageDate.setText(m.getDateString());
			itemView.setId(position);
			// recentMessages.get(index).setImage((ImageView)row.findViewById(R.id.contactImage));
			String otherEmail = recentMessages.get(position).getSender().equals(user.getEmail()) ? recentMessages.get(
					position).getReceiver() : recentMessages.get(position).getSender();
			new getImageTask().execute("http://68.59.162.183/android_connect/get_profile_image.php", otherEmail);
			contactName.setText(recentMessages.get(position).getSenderName());

			if (recentMessages.get(position).getReadByDateString().equals("0000-00-00 00:00:00")
					&& recentMessages.get(position).getReceiver().equals(user.getEmail()))
			{
				contactName.setTextColor(getResources().getColor(R.color.purple));
				contactName.setTypeface(null, Typeface.BOLD);
				itemView.setBackgroundResource(R.drawable.top_bottom_border_new);
			}

			return itemView;
		}
	}

	private void populateRecentContacts()
	{


		if (!recentMessages.isEmpty())
		{
			ArrayAdapter<Message> adapter = new ContactListAdapter();
			listView.setAdapter(adapter);
		}
		else
		{
			//display sad guy
		}

	}

	private class getImageTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", urls[1]));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				// json fetch was successful
				if (jsonObject.getString("success").toString().equals("1"))
				{
					
					String image = jsonObject.getString("image").toString();
					if (IMAGE_INDEX < recentMessages.size())
					{
					Message m = recentMessages.get(IMAGE_INDEX);
					m.setImage(image);
					updateView(IMAGE_INDEX, m.getImage());
					//updateView(recentMessages.size() - 1 - IMAGE_INDEX, m.getImage());
					
					IMAGE_INDEX++;
					
					}
				}
				else
				{
					// failed
					Log.d("getImageTask", "FAILED");
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSON, IMAGE TASKdTask", e.getLocalizedMessage());
			}
			// do next thing here
		}
	}

	
	private void updateView(int index, Bitmap image){
	    View v = listView.getChildAt(index - 
	        listView.getFirstVisiblePosition());

	    if(v == null)
	       return;

	    ImageView imageView = (ImageView) v.findViewById(R.id.contactImage);
		if (image != null)
			 imageView.setImageBitmap(image);
		else
			imageView.setImageResource(R.drawable.user_image_default);
	    
	   
	   imageView.setScaleType(ScaleType.CENTER_CROP);
	   imageView.setId(index);
	}
	public void startProfileActivity(Message m)
	{
		Intent intent = new Intent(this, UserProfileActivity.class);
		String friendEmail;
		friendEmail = m.getReceiver().equals(user.getEmail()) ? m.getSender()
				: m.getReceiver();
		intent.putExtra("email", friendEmail);
		startActivity(intent);
	}

	public int fetchRecentContacts()
	{
		// resetting data, currently pulling everything each time
		recentMessages.clear();
		IMAGE_INDEX = 0;
		new getRecentContactsTask().execute("http://68.59.162.183/android_connect/get_recent_contacts.php");
		return 1;
	}

	class getRecentContactsTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
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
					// gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("messages");
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						Message newMsg = new Message(o.getString("message"), o.getString("senddate"),
								o.getString("sender"), o.getString("first") + " " + o.getString("last"),
								o.getString("receiver"), o.getString("read_date"));
						newMsg.setID(Integer.parseInt(o.getString("id")));
						String otherEmail = newMsg.getSender().equals(user.getEmail()) ? newMsg.getReceiver() : newMsg
								.getSender();
						boolean contains = false;
						int indexFound = -1;
						for (Message m : recentMessages)
						{
							String otherEmail2 = m.getSender().equals(user.getEmail()) ? m.getReceiver() : m
									.getSender();
							if (otherEmail2.equals(otherEmail))
							{
								contains = true;
								indexFound = recentMessages.indexOf(m);
							}
						}
						if (!contains)
						{
							// recent messages does not contain a message from
							// this user
							recentMessages.add(newMsg);
						}
						else if (recentMessages.get(indexFound).getDate().compareTo(newMsg.getDate()) < 0)
						{
							recentMessages.set(indexFound, newMsg);
							System.out.println("The old message has been replaced and is now:"
									+ recentMessages.get(indexFound).getMessage());
						}
						else
						{
							System.out.println("no replace happened.  Message is same as before: "
									+ recentMessages.get(indexFound).getMessage());
						}
					}
					// done fetching, now populate to scrollview
					populateRecentContacts();
				}
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("fetchRecentContacts", "failed = 2 return");
				}
			}
			catch (Exception e)
			{
				Log.d("fetchRecentContacts", "exception caught");
				Log.d("ReadJSONFINDCONTACTSFeedTask", e.getLocalizedMessage());
			}
		}
	}
}
