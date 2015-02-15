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
import android.widget.TextView;
import android.widget.Toast;

public class ManageMembersActivity extends ActionBarActivity {

	private User user;
	//private ArrayList<User> users;
	private Group group;
	private BroadcastReceiver broadcastReceiver;
	private SparseArray<String> added = new SparseArray<String>();    //holds list of name of all friend rows to be added
	private SparseArray<Boolean> role = new SparseArray<Boolean>();   //holds list of role of all friend rows to be added
	private ArrayList<User> members = new ArrayList<User>();   //holds list of all current friends
	private static Global GLOBAL;
	private static String CONTENT; //type of content to display
	private static Bundle EXTRAS; //type of content to display
	ArrayList<String> roles = new ArrayList<String>();
	
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
		group = GLOBAL.getGroupBuffer();
		
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

		actionbarTitle.setText("Manage Group Members"); //PANDA		
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
		members = group.getUsers();

		for (User u : members)
			new getRoleTask().execute("http://68.59.162.183/android_connect/check_role_group.php", Integer.toString(members.indexOf(u)), Integer.toString(group.getID()));
		
	}
		

	private class getRoleTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = "gid";
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
				
				GridLayout rowView;
				rowView = (GridLayout) li.inflate(R.layout.list_row_invitefriend, null);
				final Button makeAdminButton = (Button) rowView.findViewById(R.id.removeFriendButtonNoAccess);

				final Button friendNameButton = (Button) rowView.findViewById(R.id.friendNameButtonNoAccess);
				final CheckBox cb = (CheckBox) rowView.findViewById(R.id.addToGroupBox);
				makeAdminButton.setId(index);
				cb.setId(makeAdminButton.getId());
				
				if (roles.get(index).equals("A"))
					makeAdminButton.setText("A");
				else if (roles.get(index).equals("M"))
					makeAdminButton.setText("M");
				else
					makeAdminButton.setText("C");

				//listener when clicking makeAdmin button
				makeAdminButton.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View view) 
					{
						if (makeAdminButton.getText().toString().equals("-")) 
						{
							makeAdminButton.setText("A");
							if (cb.isChecked()) 
							{
								role.put(view.getId(), true);
							}

							makeAdminButton.setTextColor(getResources().getColor(
									R.color.light_green));
						} 
						else 
						{
							makeAdminButton.setText("-");
							if (cb.isChecked()) 
							{
								role.put(view.getId(), false);
							}

							makeAdminButton.setTextColor(getResources().getColor(
									R.color.orange));
						}
					}
				});
						
				//listener when clicking checkbox
				cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
				{
					@Override
					public void onCheckedChanged(CompoundButton view, boolean isChecked)
					{
						String text = friendNameButton.getLayout()
								.getText().toString();
					
						if(makeAdminButton.getText().toString().equals("A") && cb.isChecked())
						{
							added.put(view.getId(), text);
							role.put(view.getId(), true);
							
							System.out.println("Added size: "+added.size());
							System.out.println("Role size: "+role.size());
						}
						else if(makeAdminButton.getText().toString().equals("-") && cb.isChecked())
						{
							added.put(view.getId(), text);
							role.put(view.getId(), false);
							
							System.out.println("Added size: "+added.size());
							System.out.println("Role size: "+role.size());
						}
						else
						{
							added.remove(view.getId());
							role.remove(view.getId());
							
							System.out.println("Added size: "+added.size());
							System.out.println("Role size: "+role.size());
						}
					}
				});
				
				friendNameButton.setText(user.getName());
				friendNameButton.setId(index);
				rowView.setId(index);
				pickFriendsLayout.addView(rowView);	
				index++;
			}
		}
		else
		{		
			// user has no friends
			// The user has no friend's so display the sad guy image.
			View row = li.inflate(R.layout.listitem_sadguy, null);
			((TextView) row.findViewById(R.id.sadGuyTextView))
				.setText("All of your friends are already in this group.");
			pickFriendsLayout.addView(row);
		}
	}
	
	public void confirmButton(View view)
	{
		
		int g_id = group.getID();
		//now loop through list of added to add all the additional users to the group
		int size = added.size();
		System.out.println("Total count of users to process: "+size);
		for(int i = 0; i < size; i++) 
		{
			System.out.println("adding friend #"+i+"/"+added.size());
			
			//get the user's email by matching indexes from added list with indexes from allFriendslist.
			int key = added.keyAt(i);
			
			
			//grab the email of friend to add
			String friendsEmail = members.get(key).getEmail();
			
			//grab the role of friend to add
			boolean tmpRole = role.valueAt(i);
			String friendsRole;
			
			if(tmpRole)
			{
				friendsRole = "A";
			}
			else
			{
				friendsRole = "M";
			}
			
			System.out.println("adding member: "+friendsEmail+", role: "+friendsRole);
			
			//initiate add of user
			new AddGroupMembersTask().execute("http://68.59.162.183/"
					+ "android_connect/add_groupmember.php", friendsEmail, user.getEmail(), friendsRole, Integer.toString(g_id));
		}
		
		//GLOBAL.loadUser(user.getEmail());
		group.fetchGroupInfo();
		group.fetchMembers();
		user.fetchGroupInvites();
		user.fetchGroups();
		GLOBAL.setCurrentUser(user);
		GLOBAL.setGroupBuffer(group);
		
		Context context = getApplicationContext();
		Toast toast = Toast.makeText(context, "Friends have been invited.", Toast.LENGTH_SHORT);
		toast.show();
		//remove this activity from back-loop by calling finish().
		finish();
	}
	
	//aSynch task to add individual member to group.
	private class AddGroupMembersTask extends AsyncTask<String,Void,String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("sender", urls[2]));
			nameValuePairs.add(new BasicNameValuePair("role", urls[3]));
			nameValuePairs.add(new BasicNameValuePair("g_id", urls[4]));

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
					System.out.println("USER HAS SUCCESSFULLY BEEN ADDED");
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
