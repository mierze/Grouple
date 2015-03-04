package cs460.grouple.grouple;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
import org.json.JSONArray;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

/*
 * EventAddGroupsActivity allows a user to invite groups to a created event.
 */

public class EventAddGroupsActivity extends ActionBarActivity
{
	private BroadcastReceiver broadcastReceiver;
	private SparseArray<String> added = new SparseArray<String>();    //holds list of name of all group rows to be added
	private ArrayList<Group> allGroups = new ArrayList<Group>();   //holds list of all current groups
	private User user;
	private String email = null;
	private String e_id = null;
	private Global GLOBAL;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_addgroups);
		load();	
	}
	
	private void load()
	{
		GLOBAL = ((Global) getApplicationContext());
		//grab the email of current users from our global class
		user = GLOBAL.getCurrentUser();
		email = user.getEmail();
		
		Bundle extras = getIntent().getExtras();
		//grab the e_id from extras
		e_id = extras.getString("EID");
		
		//load our list of current groups.  key is group id -> value is group name
		allGroups = user.getGroups();
		if (allGroups != null)
			System.out.println("AT THIS TIME ALL GROUPS IS THIS BIG: " + allGroups.size());
		else
			System.out.println("IT IS NULL AT THIS TIME ALLGROUPS");
		populateGroupCreate();
		initActionBar();
		initKillswitchListener();
	}
	
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}
	
	private void populateGroupCreate()
	{
		// begin building the interface
		LayoutInflater inflater = getLayoutInflater();
		LinearLayout membersToAdd = (LinearLayout) findViewById(R.id.linearLayoutNested_eventAddGroups);
		
		if(allGroups.size() == 0)
		{
			View row = inflater.inflate(
					R.layout.list_row_eventinvitegroup, null);

			((Button) row.findViewById(R.id.groupNameButtonNoAccess))
					.setText("You don't have any groups to add yet!");
			row.findViewById(R.id.groupNameButtonNoAccess)
					.setVisibility(1);
			membersToAdd.addView(row);
		}
		
		
		//setup for each group
		for(int i=0; i<allGroups.size(); i++)
		{
			GridLayout rowView;
			rowView = (GridLayout) inflater.inflate(
					R.layout.list_row_eventinvitegroup, null);

			final Button groupNameButton = (Button) rowView
					.findViewById(R.id.groupNameButtonNoAccess);
			final CheckBox cb = (CheckBox) rowView
					.findViewById(R.id.addToEventBox);
			cb.setId(i);
					
			//listener when clicking checkbox
			cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton view, boolean isChecked)
				{
					String text = groupNameButton.getLayout()
							.getText().toString();
				
					if(cb.isChecked())
					{
						added.put(view.getId(), text);
						System.out.println("Added size: "+added.size());
					}
					else
					{
						added.remove(view.getId());
						System.out.println("Added size: "+added.size());
					}
				}
			});
			
			groupNameButton.setText(allGroups.get(i).getName());
			groupNameButton.setId(i);
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
		actionBarTitle.setText("Add Groups");
	}
	
	//onClick for Confirm add groups button
	public void addGroupsButton(View view)
	{		
		//loop through list of added to add all the groups to the event
		int size = added.size();
		System.out.println("Total count of groups to process: "+size);
		for(int i = 0; i < size; i++) 
		{
			System.out.println("adding group #"+i+"/"+added.size());
			//get the groups's g_id by matching indexes from added list with indexes from allGroupslist.
			int key = added.keyAt(i);
			//grab the gid of group to add
			String groupsgid = String.valueOf(allGroups.get(key).getID());
				
			System.out.println("adding group: "+groupsgid);		
			
			//initiate add of group
			new EventAddGroupTask().execute("http://68.59.162.183/"
					+ "android_connect/add_eventmember.php", groupsgid, email, e_id);
		}		
	}
	
	//aSynch task to add individual group to event.
	private class EventAddGroupTask extends AsyncTask<String,Void,String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			int tmpid = Integer.parseInt(urls[1]);
			System.out.println("tmpid: "+tmpid);
			
			//Cannot currently use because of synch problems, until fixed just calling getMembers myself here so it will WORK
			//Group group = new Group(tmpid);
			//group.fetchMembers();
			//allMembers = group.getUsers();
			
			//pass url off to GLOBAL to do the JSON call.  Code continues at onPostExecute when JSON returns.
			return GLOBAL.readJSONFeed("http://68.59.162.183/android_connect/get_group_members.php?gid="+tmpid, null);
			
		}

		@Override
		protected void onPostExecute(String result)
		{
			Map<String, String> allMembers = new HashMap<String, String>();   //list of all current members
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					//gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("gmembers");
					
					System.out.println("gmembers inside");
					
					//looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						
						//at each iteration set to hashmap friendEmail -> 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						//function adds friend to the friends map
						String memberToAdd = o.getString("email");
						System.out.println("FETCHING GROUP MEMBER" + memberToAdd + " EID is " + e_id);
						new EventAddMemberTask().execute("http://68.59.162.183/"
								+ "android_connect/add_eventmember.php", memberToAdd, email, e_id);
					}
					
					Context context = getApplicationContext();
					String message = "Successfully invited groups!";
					Toast toast = GLOBAL.getToast(context, message);
					toast.show();
					finish();
					
				}
				// group has no members
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("fetchgroupmembers", "failed = 2 return");
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}		
	}
	
	//aSynch task to add individual group to event.
		private class EventAddMemberTask extends AsyncTask<String,Void,String>
		{
			@Override
			protected String doInBackground(String... urls)
			{
	
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("email", urls[1]));
					nameValuePairs.add(new BasicNameValuePair("sender", urls[2]));
					nameValuePairs.add(new BasicNameValuePair("e_id", urls[3]));

					//pass url and nameValuePairs off to GLOBAL to do the JSON call.  Code continues at onPostExecute when JSON returns.
					return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
				
			}

			protected void onPostExecute(String result)
			{
				try
				{
					JSONObject jsonObject = new JSONObject(result);

					// group has been successfully added
					if (jsonObject.getString("success").toString().equals("1"))
					{
						//all working correctly, continue to next user or finish.
					} 
					else if (jsonObject.getString("success").toString().equals("0"))
					{	
						//a particular group was unable to be added to database for some reason...
						//Don't tell the user!
					}
				} catch (Exception e)
				{
					Log.d("readJSONFeed", e.getLocalizedMessage());
				}
			}		
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
