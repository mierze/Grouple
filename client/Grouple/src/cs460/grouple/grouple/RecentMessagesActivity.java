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


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import cs460.grouple.grouple.MessagesActivity.getMessagesTask;
import cs460.grouple.grouple.ProfileActivity.CONTENT_TYPE;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

/*
 * MessagesActivity has not been implemented yet.
 */
public class RecentMessagesActivity extends ActionBarActivity
{
	private BroadcastReceiver broadcastReceiver;
	private Global GLOBAL;
	private int IMAGE_INDEX = 0;//holy shit
	private User user; //will be null for now
	private ArrayList<ImageView> images = new ArrayList<ImageView>();
	private Dialog loadDialog = null;
	private ArrayList<Message> recentMessages = new ArrayList<Message>();
    private static final String EXTRA_MESSAGE = "message";
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "GCM";
    //Sender ID is the project number from API console. Needs to be secret.
    private String SENDER_ID = "957639483805";

    
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;

	@Override
	protected void onStop()
	{
		super.onStop();
		loadDialog.hide();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);
		GLOBAL = ((Global) getApplicationContext());
		user = GLOBAL.getCurrentUser();
		/* Action bar */
		initActionBar();		
		initKillswitchListener();
		context = getApplicationContext();
		loadDialog = GLOBAL.getLoadDialog(new Dialog(this));
        loadDialog.setOwnerActivity(this);
		//new getContactsTask().execute("http://68.59.162.183/android_connect/get_chat_id.php");
        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.

	}
	
	private void initActionBar()
	{
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		populateRecentContacts();
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText("Messages");
	}
	
	@Override
    protected void onResume() 
	{
        super.onResume();
        fetchRecentContacts();
        // Check device for Play Services APK.
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  
	{
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	loadDialog.show();
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
				row =  li.inflate(R.layout.contact_row, null); //inflate this message row
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
			} catch (Exception e)
			{
				Log.d("fetchRecentContacts", "exception caught");
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
}
