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
import android.support.v4.content.LocalBroadcastManager;
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
	private ArrayList<Contact> contacts = new ArrayList<Contact>();
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
		//	if (user.getEmail().equals(receiver))
		//	{
				// do this
				updateUI();
			//}
			/*
			 * for (Message m : contacts) { if
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
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
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
		contacts = user.getContacts();
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
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("user_data"));
		super.onResume();
		fetchData();
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
		String EMAIL = contacts.get(view.getId()).getReceiver().equals(user.getEmail()) ? contacts.get(
				view.getId()).getSender() : contacts.get(view.getId()).getReceiver();
		intent.putExtra("email", EMAIL);
		intent.putExtra("name", contacts.get(view.getId()).getSenderName());
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

	private class ContactListAdapter extends ArrayAdapter<Contact>
	{
		public ContactListAdapter()
		{
			super(RecentMessagesActivity.this, R.layout.list_row_contact, contacts);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			View itemView = convertView;
			if (itemView == null)
				itemView = inflater.inflate(R.layout.list_row_contact, parent, false);
			final Contact c = contacts.get(position);
			TextView contactName = (TextView) itemView.findViewById(R.id.contactName);
			TextView messageBody = (TextView) itemView.findViewById(R.id.messageBody);
			TextView messageDate = (TextView) itemView.findViewById(R.id.messageDate);
			ImageButton contactImage = (ImageButton) itemView.findViewById(R.id.contactImage);
		    
			if (user.getImage() != null)
				 contactImage.setImageBitmap(user.getImage());
			else
				contactImage.setImageResource(R.drawable.user_image_default);
		    
			messageBody.setText(c.getMessage());
			messageDate.setText(c.getDateString());
			itemView.setId(position);
			// contacts.get(index).setImage((ImageView)row.findViewById(R.id.contactImage));
			final String otherEmail = contacts.get(position).getSender().equals(user.getEmail()) ? contacts.get(
					position).getReceiver() : contacts.get(position).getSender();
					contactImage.setOnClickListener(new OnClickListener()
					{
						@Override
					 public void onClick(View view)
						{
							startProfileActivity(otherEmail);
						}
					});
					contactName.setText(contacts.get(position).getSenderName());

			if (contacts.get(position).getReadByDateString().equals("0000-00-00 00:00:00")
					&& contacts.get(position).getReceiver().equals(user.getEmail()))
			{
				contactName.setTextColor(getResources().getColor(R.color.purple));
				contactName.setTypeface(null, Typeface.BOLD);
				itemView.setBackgroundResource(R.drawable.top_bottom_border_new);
			}

			return itemView;
		}
	}

	private void updateUI()
	{
		if (!contacts.isEmpty())
		{
			ArrayAdapter<Contact> adapter = new ContactListAdapter();
			listView.setAdapter(adapter);	
		}
		else
		{
			//display sad guy
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
	public void startProfileActivity(String email)
	{
		Intent intent = new Intent(this, UserProfileActivity.class);
		intent.putExtra("email", email);
		startActivity(intent);
	}

	public void fetchData()
	{
		// resetting data, currently pulling everything each time
		user.fetchContacts(this);
	}


	
}
