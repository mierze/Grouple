package cs460.grouple.grouple;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * GroupCreateActivity allows a user to create a new group.
 */

public class GroupCreateActivity extends ActionBarActivity
{
	BroadcastReceiver broadcastReceiver;
	private SparseArray<String> added = new SparseArray<String>();    //holds list of name of all friend rows to be added
	private SparseArray<Boolean> role = new SparseArray<Boolean>();   //holds list of role of all friend rows to be added
	private Map<String, String> allFriends = new HashMap<String, String>();   //holds list of all current friends
	User user;
	private String email = null;
	private String g_id = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_create);

		load();	
	}
	
	private void load()
	{
		Global global = ((Global) getApplicationContext());
		//grab the email of current users from our global class
		email = global.getCurrentUser().getEmail();
		
		user = global.loadUser(email);
		
		//load our list of current friends.  key is friend email -> value is full names
		allFriends = user.getUsers();
		
		populateGroupCreate();
		initActionBar();
		initKillswitchListener();
	}
	
	private void populateGroupCreate()
	{
		// begin building the interface
		LayoutInflater inflater = getLayoutInflater();
		LinearLayout membersToAdd = (LinearLayout) findViewById(R.id.linearLayoutNested1);
		
		if(allFriends.size() == 0)
		{
			View row = inflater.inflate(
					R.layout.list_row_invitefriend, null);

			((Button) row.findViewById(R.id.friendNameButtonNoAccess))
					.setText("You don't have any friends to add yet!");
			row.findViewById(R.id.removeFriendButtonNoAccess)
					.setVisibility(1);
			membersToAdd.addView(row);
		}
		
		Iterator iterator = allFriends.entrySet().iterator();
		
		//setup for each friend
		for(int i=0; i<allFriends.size(); i++)
		{
			
			GridLayout rowView;
			rowView = (GridLayout) inflater.inflate(
					R.layout.list_row_invitefriend, null);
			final Button makeAdminButton = (Button) rowView
					.findViewById(R.id.removeFriendButtonNoAccess);

			final Button friendNameButton = (Button) rowView
					.findViewById(R.id.friendNameButtonNoAccess);
			final CheckBox cb = (CheckBox) rowView
					.findViewById(R.id.addToGroupBox);
			makeAdminButton.setId(i);
			cb.setId(makeAdminButton.getId());

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
			
			Entry thisEntry = (Entry) iterator.next();
			friendNameButton.setText(thisEntry.getValue().toString());
			friendNameButton.setId(i);
			rowView.setId(i);
			membersToAdd.addView(rowView);	
		}
	}
	
	private void initActionBar()
	{
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionBarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionBarTitle.setText("Groups");
	}
	
	//onClick for Confirm create group button
	public void createGroupButton(View view)
	{		
		//first check to make sure a group name has been typed by the user
		EditText groupNameEditText = (EditText) findViewById(R.id.groupName);

		String groupname = groupNameEditText.getText().toString();
		
		//if empty group name, display error box
		if(groupname.compareTo("") == 0)
		{
			new AlertDialog.Builder(this)
			.setMessage("Please give your group a name before creating.")
			.setCancelable(true)
			.setNegativeButton("Ok", null).show();
		}
		//otherwise, display confirmation box
		else
		{
			new AlertDialog.Builder(this)
			.setMessage("Are you sure you want to create this group?")
			.setCancelable(true)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int id)
				{
					//initiate creation of group
					new CreateGroupTask().execute("http://68.59.162.183/"
					+ "android_connect/create_group.php");
				}
			}).setNegativeButton("Cancel", null).show();
		}
	}
	
	//aSynch class to create group 
	private class CreateGroupTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			EditText groupNameEditText = (EditText) findViewById(R.id.groupName);
			EditText groupBioEditText = (EditText) findViewById(R.id.groupBio);

			//grab group name and bio from textviews
			String groupname = groupNameEditText.getText().toString();
			String groupbio = groupBioEditText.getText().toString();	
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("g_name", groupname));
			nameValuePairs.add(new BasicNameValuePair("about", groupbio));
			nameValuePairs.add(new BasicNameValuePair("creator", email));

			//pass url and nameValuePairs off to global to do the JSON call.  Code continues at onPostExecute when JSON returns.
			return global.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);

				// group has been successfully created
				if (jsonObject.getString("success").toString().equals("1"))
				{
					//now we can grab the newly created g_id returned from the server
					//Note: g_id is the only unique identifier of a group and therefore must be used for any future calls concerning that group.
					g_id = jsonObject.getString("g_id").toString();
					
					System.out.println("g_id of newly created group is: "+g_id);
					
					//add yourself to the group as Creator
					new AddGroupMembersTask().execute("http://68.59.162.183/"
							+ "android_connect/add_groupmember.php", email, email, "C", g_id);
					
					//now loop through list of added to add all the additional users to the group
					int size = added.size();
					System.out.println("Total count of users to process: "+size);
					for(int i = 0; i < size; i++) 
					{
						System.out.println("adding friend #"+i+"/"+added.size());
						
						//get the user's email by matching indexes from added list with indexes from allFriendslist.
						int key = added.keyAt(i);
						Iterator it1 = allFriends.entrySet().iterator();
						for(int k=0; k<key; k++)
						{
							//skip over iterations until arriving at key
							it1.next();
						}
						Map.Entry pairs = (Map.Entry)it1.next();
						
						//grab the email of friend to add
						String friendsEmail = (String) pairs.getKey();
						
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
								+ "android_connect/add_groupmember.php", friendsEmail, email, friendsRole, g_id);
					}
					
					//display confirmation box
					new AlertDialog.Builder(GroupCreateActivity.this)
					.setMessage("Nice work, you've successfully created a group!")
					.setCancelable(true)
					.setPositiveButton("View your Group Profile", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int id)
						{
							//add code here to take user to newly created group profile page.  (pass g_id as extra so correct group profile can be loaded)
						}
					}).setPositiveButton("Invite more friends", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							//add code here to take user to groupaddmembersactivity page.  (pass g_id as extra so invites can be sent to correct group id)
						}
					}).show();
				
					Global global = (Global)getApplicationContext();
					global.loadUser(global.getCurrentUser().getEmail());
					//remove this activity from back-loop by calling finish().
					finish();
				} 
				//Create group failed for some reasons.
				else if (jsonObject.getString("success").toString().equals("0"))
				{	
					//display error box
					new AlertDialog.Builder(GroupCreateActivity.this)
					.setMessage("Unable to create group! Please choose an option:")
					.setCancelable(true)
					.setPositiveButton("Try Again", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int id)
						{
							//initiate creation of group AGAIN
							new CreateGroupTask().execute("http://68.59.162.183/"
							+ "android_connect/create_group.php");
						}
					}).setNegativeButton("Cancel", null).show();
				}
			} catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}
		}
	}
	
	//aSynch task to add individual member to group.
	private class AddGroupMembersTask extends AsyncTask<String,Void,String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("sender", urls[2]));
			nameValuePairs.add(new BasicNameValuePair("role", urls[3]));
			nameValuePairs.add(new BasicNameValuePair("g_id", urls[4]));

			//pass url and nameValuePairs off to global to do the JSON call.  Code continues at onPostExecute when JSON returns.
			return global.readJSONFeed(urls[0], nameValuePairs);
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
	
	//does something
	public void startParentActivity(View view)
	{
		Bundle extras = getIntent().getExtras();

		String className = extras.getString("ParentClassName");
		Intent newIntent = null;
		try
		{
			newIntent = new Intent(this, Class.forName("cs460.grouple.grouple."
					+ className));
			if (extras.getString("ParentEmail") != null)
			{
				newIntent.putExtra("email", extras.getString("ParentEmail"));
			}
			// newIntent.putExtra("email", extras.getString("email"));
			System.out.println("delete");
			// newIntent.putExtra("ParentEmail", extras.getString("email"));
			newIntent.putExtra("ParentClassName", "GroupCreateActivity");
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		newIntent.putExtra("up", "true");
		startActivity(newIntent);
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
			Global global = ((Global) getApplicationContext());
			Intent login = new Intent(this, LoginActivity.class);
			global.destroySession();
			startActivity(login);
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		if (id == R.id.action_home)
		{
			Intent intent = new Intent(this, HomeActivity.class);
			intent.putExtra("up", "false");
			intent.putExtra("ParentClassName", "GroupCreateActivity");
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
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
					// System.exit(1);
					finish();
				}
			}
		};
		registerReceiver(broadcastReceiver, intentFilter);
		// End Kill switch listener
	}
}
