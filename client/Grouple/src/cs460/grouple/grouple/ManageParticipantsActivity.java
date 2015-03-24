package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;


import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ManageParticipantsActivity extends ActionBarActivity {

	private User user;
	//private ArrayList<User> users;
	private Event event;
	private BroadcastReceiver broadcastReceiver;
	private SparseArray<String> toUpdate = new SparseArray<String>();    //holds list of name of all friend rows to be added
	private SparseArray<String> toUpdateRole = new SparseArray<String>();   //holds list of role of all friend rows to be added
	private SparseArray<String> toRemove = new SparseArray<String>();    //holds list of name of all friend rows to be added
	private Dialog loadDialog = null;
	private ArrayList<User> members = new ArrayList<User>();   //holds list of all current friends
	private static Global GLOBAL;
	private static String CONTENT; //type of content to display
	private static Bundle EXTRAS; //type of content to display
	private ArrayList<String> roles = new ArrayList<String>();
	
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_members);
		
		load();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation_actions, menu);
		return true;
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		loadDialog.hide();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_logout)
		{
			GLOBAL.destroySession();
			Intent login = new Intent(this, LoginActivity.class);
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
	
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	
	
	////////////////////////////////////////////////////////////////////////////////////////
	private void load()
	{
		GLOBAL = (Global)getApplicationContext();
		EXTRAS = getIntent().getExtras();
		CONTENT = EXTRAS.getString("CONTENT");
		//should always be current user
		user = GLOBAL.getCurrentUser();
		event = GLOBAL.getEventBuffer();
		loadDialog = GLOBAL.getLoadDialog(new Dialog(this));
        loadDialog.setOwnerActivity(this);
		setRoles();
		//populateManageMembers();
		
		initActionBar();
		initKillswitchListener();
	}
	
	private void initActionBar()
	{
		
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);

		actionbarTitle.setText("Manage Event Participants"); //PANDA		
	}
	
	
	
	private void initKillswitchListener()
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
	
	private void setRoles()
	{
		members = event.getUsers();

		for (User u : members)
			new getRoleTask().execute("http://68.59.162.183/android_connect/check_role_event.php", Integer.toString(members.indexOf(u)), Integer.toString(event.getID()));
		
	}
		

	private class getRoleTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = "eid";
			String email = members.get(Integer.parseInt(urls[1])).getEmail();
			String id = urls[2];
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair(type, id));
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
					
					String ROLE = jsonObject.getString("role").toString();
					System.out.println("ROLE IS BEING SET TO " + ROLE);
					roles.add(ROLE);
					if (roles.size() == members.size()) //done with all
						populateManageMembers();
					//setNotifications(); //for group / event
				
				} 
				//unsuccessful
				else
				{
					// failed
					Log.d("FETCH ROLE FAILED", "FAILED");
				}
			} 
			catch (Exception e)
			{
				Log.d("atherjsoninuserpost", "here");
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
			//do next thing here
		}
	}
	private void populateManageMembers()
	{
		//could use content here
		
		LinearLayout pickFriendsLayout = (LinearLayout) findViewById(R.id.manageMembersLayout);
		LayoutInflater li = getLayoutInflater();


		if (members != null && !members.isEmpty())
		{
			// looping thru json and adding to an array
			int index = 0;
			//setup for each friend
			for(User user : members)
			{
				index = members.indexOf(user);
				//TODO: add all to aded
				final LayoutInflater inflater = getLayoutInflater();
				final View view = inflater.inflate(R.layout.list_row_invitefriend, null);
				final RelativeLayout row = (RelativeLayout) view.findViewById(R.id.friendManageLayout);
				final Button makeAdminButton = (Button) view.findViewById(R.id.removeFriendButtonNoAccess);
				final TextView friendNameButton = (TextView) view.findViewById(R.id.friendNameTextView);
				final String friendName = friendNameButton.getText().toString();
				final CheckBox cb = (CheckBox) view.findViewById(R.id.addToGroupBox);
				row.setId(index);
				makeAdminButton.setId(index);
				cb.setId(index);
	
				makeAdminButton.setText(roles.get(index));
				if (roles.get(index).equals("A"))
					makeAdminButton.setTextColor(getResources().getColor(R.color.light_green));
				else if (roles.get(index).equals("U"))
					makeAdminButton.setTextColor(getResources().getColor(R.color.orange));
				else if (roles.get(index).equals("P"))
					makeAdminButton.setTextColor(getResources().getColor(R.color.purple));
					
				
				row.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View view) 
					{
						if (makeAdminButton.getText().toString().equals("P")) 
						{
							makeAdminButton.setText("A");
							toUpdateRole.put(view.getId(), "A");
							makeAdminButton.setTextColor(getResources().getColor(
									R.color.light_green));
							cb.setChecked(false);
						} 
						else if (makeAdminButton.getText().toString().equals("A")) 
						{
							makeAdminButton.setText("U");
							toUpdateRole.put(view.getId(), "U");
							makeAdminButton.setTextColor(getResources().getColor(
									R.color.orange));
							cb.setChecked(false);
						} 
						else if (makeAdminButton.getText().toString().equals("U")) 
						{
							makeAdminButton.setText("P");
							toUpdateRole.put(view.getId(), "P");
							makeAdminButton.setTextColor(getResources().getColor(
									R.color.purple));
							cb.setChecked(false);
						}
						else
						{
							makeAdminButton.setText("U");
							toUpdateRole.put(view.getId(), "U");
							makeAdminButton.setTextColor(getResources().getColor(
									R.color.orange));
							cb.setChecked(false);
						}
					}	
				});

				//listener when clicking makeAdmin button
				makeAdminButton.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View view) 
					{
						if (makeAdminButton.getText().toString().equals("U")) 
						{
							makeAdminButton.setText("P");
							toUpdateRole.put(view.getId(), "P");
							makeAdminButton.setTextColor(getResources().getColor(
									R.color.purple));
						} 
						else if (makeAdminButton.getText().toString().equals("P")) 
						{
							makeAdminButton.setText("A");
							toUpdateRole.put(view.getId(), "A");
							makeAdminButton.setTextColor(getResources().getColor(
									R.color.light_green));
						}
						else if (makeAdminButton.getText().toString().equals("A")) 
						{
							makeAdminButton.setText("U");
							toUpdateRole.put(view.getId(), "U");
							makeAdminButton.setTextColor(getResources().getColor(
									R.color.orange));
						}
						else
						{
							makeAdminButton.setText("U");
							toUpdateRole.put(view.getId(), "U");
							cb.setChecked(false);
							makeAdminButton.setTextColor(getResources().getColor(
									R.color.orange));
						}
						System.out.println("Setting role for user: " + toUpdate.get(view.getId()) + " to: " + toUpdateRole.get(view.getId()));
					}
				});
						
				//listener when clicking checkbox
				cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
				{
					@Override
					public void onCheckedChanged(CompoundButton view, boolean isChecked)
					{
						
						if (cb.isChecked())
						{
							//REMOVING THOSE CHECKED OFF FOR DELETION
							toUpdateRole.put(view.getId(), "-");
							makeAdminButton.setText("-");
							makeAdminButton.setTextColor(getResources().getColor(
									R.color.black));
							
							System.out.println("REMOVING A USER");
							System.out.println("Role size: "+toUpdateRole.size());	
						}
						else if (!cb.isChecked())
						{
							toUpdateRole.put(view.getId(), "U");
							//REMOVING THOSE CHECKED OFF FOR DELETION
							makeAdminButton.setText("U");
							makeAdminButton.setTextColor(getResources().getColor(
									R.color.orange));
							System.out.println("Added size: "+toUpdate.size());
							System.out.println("Role size: "+toUpdateRole.size());	
						}
					}
				});
				
				friendNameButton.setText(user.getName());
				friendNameButton.setId(index);
				toUpdate.put(index, user.getEmail());
				toUpdateRole.put(index, roles.get(index));
				row.setId(index);
				pickFriendsLayout.addView(row);	
				index++;
			}
		}
		else
		{		
			// user has no friends
			// The user has no friend's so display the sad guy image.
			View row = li.inflate(R.layout.listitem_sadguy, null);
			((TextView) row.findViewById(R.id.sadGuyTextView))
				.setText("No participants to manage.");
			pickFriendsLayout.addView(row);
		}
	}
	
	public void confirmButton(View view)
	{
		int e_id = event.getID();
		//now loop through list of added to add all the additional users to the group
		int size = toUpdate.size();
		System.out.println("Total count of users to process: "+size);
		for (int i = 0 ; i < size ; i++)
		{
			System.out.println("adding friend #"+i+"/"+toUpdate.size());
			
			//get the user's email by matching indexes from added list with indexes from allFriendslist.
			int key = toUpdate.keyAt(i);
			
			
			//grab the email of friend to add
			String friendsEmail = toUpdate.valueAt(key);
		
			//grab the role of friend to add
			String friendsRole = toUpdateRole.valueAt(key);

			
			if (friendsRole.equals("-"))
			{
			System.out.println("removing mg member: "+friendsEmail);
			new UpdateGroupMembersTask().execute("http://68.59.162.183/"
					+ "android_connect/update_event_member.php", friendsEmail, "yes", "M", Integer.toString(e_id));
			}
			else
			{
			System.out.println("adding member: "+friendsEmail+", role: "+friendsRole);
			//initiate add of user
			new UpdateGroupMembersTask().execute("http://68.59.162.183/android_connect/update_event_member.php", friendsEmail, "no", friendsRole, Integer.toString(e_id));
			}
		}

			
		
		
		//group.fetchGroupInfo();
		event.fetchParticipants();
		//user.fetchGroupInvites();
		//user.fetchGroups();
		//GLOBAL.setCurrentUser(user);
		//GLOBAL.setGroupBuffer(group);
		
		Context context = getApplicationContext();
		Toast toast = GLOBAL.getToast(context, "Event participants have been updated.");
		toast.show();
		
		//remove this activity from back-loop by calling finish().
		finish();
	}
	
	//aSynch task to add individual member to group.
	private class UpdateGroupMembersTask extends AsyncTask<String,Void,String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("remove", urls[2]));
			nameValuePairs.add(new BasicNameValuePair("role", urls[3]));
			nameValuePairs.add(new BasicNameValuePair("e_id", urls[4]));

			//pass url and nameValuePairs off to GLOBAL to do the JSON call.  Code continues at onPostExecute when JSON returns.
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}


		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);

				// member has been successfully added
				if (jsonObject.getString("success").toString().equals("1"))
				{
					System.out.println("USER HAS SUCCESSFULLY BEEN UPDATED");
					//all working correctly, continue to next user or finish.
					
				} 
				else if (jsonObject.getString("success").toString().equals("0"))
				{	
					//a particular user was unable to be added to database for some reason...
					//Don't tell the user!
				}
			} catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}
		}		
	}
		
}
