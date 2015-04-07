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
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/*
 * MessagesActivity has not been implemented yet.
 */
public class RecentMessagesActivity extends BaseActivity
{
	private int IMAGE_INDEX = 0;//holy shit
	private User user; //will be null for now
	private ArrayList<ImageView> images = new ArrayList<ImageView>();
	private ArrayList<Message> recentMessages = new ArrayList<Message>();
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
	
    
	//This is the handler that will manager to process the broadcast intent
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() 
	{
	    @Override
	    public void onReceive(Context context, Intent intent) 
	    {
	        // Extract data included in the Intent
	        String toEmail = intent.getStringExtra("TO");
	        if (user.getEmail().equals(toEmail))
	        {
	        	//do this
	        	System.out.println("READ THIS");
	        	fetchRecentContacts();
	        }
	        /*
	        for (Message m : recentMessages)
	        {
	        	if (m.getSender().equals(fromEmail) || m.getReceiver().equals(fromEmail))
	        	{
	        		//copy message over, add it to top of stack, adjust images accordingly
	        		//for now just change message
	        		//m.setMessage(message)
	        		
	        		//maybe for now just grab all
	        	}
	        }
	        */
	    }
	};
    
	@Override
	protected void onStop()
	{
		super.onStop();
		unregisterReceiver(mMessageReceiver);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent_messages);
		user = GLOBAL.getCurrentUser();
		/* Action bar */
		initActionBar("Messages", true);		
		//new getContactsTask().execute("http://68.59.162.183/android_connect/get_chat_id.php");
        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
	    registerReceiver(mMessageReceiver, new IntentFilter("USER_MESSAGE"));
	}
	
	@Override
    protected void onResume() 
	{
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
			//already here
		}
		return super.onOptionsItemSelected(item);
	}

	public void startMessages(View view)
	{
		Intent intent = new Intent(this, MessagesActivity.class);
		String EMAIL = recentMessages.get(view.getId()).getReceiver().equals(user.getEmail()) ? recentMessages.get(view.getId()).getSender() : recentMessages.get(view.getId()).getReceiver();
		intent.putExtra("EMAIL", EMAIL);
		intent.putExtra("NAME", recentMessages.get(view.getId()).getSenderName());
		startActivity(intent);
	}
		
	//onClick for items to bring
	public void newMessageButton(View view)
	{
		loadDialog.show();
		final String CONTENT = "SELECT_FRIEND";
		Intent intent = new Intent(this, ListActivity.class);
		intent.putExtra("EMAIL", user.getEmail());
		user.fetchFriends();
		intent.putExtra("CONTENT", CONTENT);
		startActivity(intent);
	}
	
	private void populateRecentContacts()
	{
		//populate all of the chats between peers, groups and events that are most active
		//layout to inflate into
		LinearLayout messageLayout = (LinearLayout) findViewById(R.id.contactLayout);
		messageLayout.removeAllViews();
		//layout inflater
		LayoutInflater li = getLayoutInflater();
		TextView messageBody, messageDate, contactName;
		View row;

		if (!recentMessages.isEmpty())
			for (int index = recentMessages.size()-1; index >= 0; index--)
			{
				//loop through messages, maybe a map String String with messagebody, date
				row =  li.inflate(R.layout.list_row_contact, null); //inflate this message row
				row.setId(index);
				messageBody = (TextView) row.findViewById(R.id.messageBody);
				messageDate = (TextView) row.findViewById(R.id.messageDate);
				contactName = (TextView) row.findViewById(R.id.contactName);
				messageDate.setText(recentMessages.get(index).getDateString());
				messageBody.setText(recentMessages.get(index).getMessage());
				//recentMessages.get(index).setImage((ImageView)row.findViewById(R.id.contactImage));
				String otherEmail = recentMessages.get(index).getSender().equals(user.getEmail()) ? recentMessages.get(index).getReceiver() : recentMessages.get(index).getSender();
				//String contactName = recentMessages.get(index).getSender().equals(user.getEmail()) ?  : recentMessages.get(index).getSender();
				new getImageTask().execute("http://68.59.162.183/android_connect/get_profile_image.php", otherEmail);
				contactName.setText(recentMessages.get(index).getSenderName());
				ImageView contactImage = (ImageView) row.findViewById(R.id.contactImage);
				images.add(contactImage);
				System.out.println("READ AT INDEX " +index+"\tis " + recentMessages.get(index).getReadByDateString());
				if (recentMessages.get(index).getReadByDateString().equals("0000-00-00 00:00:00") && recentMessages.get(index).getReceiver().equals(user.getEmail()))
				{
					System.out.println("Should be changing color here");
					contactName.setTextColor(getResources().getColor(R.color.purple));
					contactName.setTypeface(null, Typeface.BOLD);
					row.setBackgroundResource(R.drawable.top_bottom_border_new);
				}
				else
					System.out.println("Should not be changing this message");
				//add row into scrollable layout
				messageLayout.addView(row);
				System.out.println("Done adding row with name: "+contactName.getText().toString());
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
				//json fetch was successful
				if (jsonObject.getString("success").toString().equals("1"))
				{
					ImageView iv = images.get(IMAGE_INDEX);
					String image = jsonObject.getString("image").toString();
					Message m = recentMessages.get(IMAGE_INDEX);
					m.setImage(image);
					iv.setId(recentMessages.size()-1-IMAGE_INDEX);
					if (m.getImage() != null)
						iv.setImageBitmap(m.getImage());
					else
						iv.setImageResource(R.drawable.user_image_default);
					iv.setScaleType(ScaleType.CENTER_CROP);
					IMAGE_INDEX++;
				} 
				else
				{
					// failed
					Log.d("getImageTask", "FAILED");
				}
			} 
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
			//do next thing here
		}
	}

	public void startProfileActivity(View view)
	{
		Intent intent = new Intent(this, ProfileActivity.class);
		String friendEmail;
		int id = view.getId();
		friendEmail = recentMessages.get(id).getReceiver().equals(user.getEmail()) ? recentMessages.get(id).getSender() : recentMessages.get(id).getReceiver();
		User u = new User(friendEmail);
		u.fetchEventsUpcoming();
		u.fetchFriends();
		u.fetchEventsPast();
		u.fetchGroups();
		u.fetchUserInfo();
		if (!GLOBAL.isCurrentUser(friendEmail))
			GLOBAL.setUserBuffer(u);
		else
			GLOBAL.setCurrentUser(u); //reloading user
		intent.putExtra("EMAIL", friendEmail);
		intent.putExtra("CONTENT", "USER");	
		startActivity(intent);
	}

	public int fetchRecentContacts()
	{
		//resetting data, currently pulling everything each time
		recentMessages.clear();
		images.clear();
		IMAGE_INDEX = 0;
		new getRecentContactsTask().execute("http://68.59.162.183/android_connect/testcontacts4.php");
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
						Message newMsg = new Message(o.getString("message"), o.getString("senddate"),o.getString("sender"), o.getString("first") + " " + o.getString("last"), o.getString("receiver"), o.getString("read_date"));
						newMsg.setID(Integer.parseInt(o.getString("id")));
						String otherEmail = newMsg.getSender().equals(user.getEmail()) ? newMsg.getReceiver() : newMsg.getSender();
						boolean contains = false;
						int indexFound = -1;
						for (Message m : recentMessages)
						{
							String otherEmail2 = m.getSender().equals(user.getEmail()) ? m.getReceiver() : m.getSender();
							if (otherEmail2.equals(otherEmail))
							{
								contains = true;
								indexFound = recentMessages.indexOf(m);
							}
						}
						if (!contains)
						{
							//recent messages does not contain a message from this user
							recentMessages.add(newMsg);
						}
						else if (recentMessages.get(indexFound).getDate().compareTo(newMsg.getDate()) < 0)
						{
							recentMessages.set(indexFound, newMsg);
							System.out.println("The old message has been replaced and is now:" + recentMessages.get(indexFound).getMessage());
						}		
						else
						{
							System.out.println("no replace happened.  Message is same as before: " + recentMessages.get(indexFound).getMessage());
						}
					}
					//done fetching, now populate to scrollview
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
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
}
