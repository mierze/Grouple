package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*
 * EventAddGroupsActivity allows a user to invite groups to a created event.
 */
public class EventAddGroupsActivity extends BaseActivity
{
	private SparseArray<String> added = new SparseArray<String>();    //holds list of name of all group rows to be added
	private ArrayList<Group> allGroups = new ArrayList<Group>();   //holds list of all current groups
	private User user;
	private String email;
	private String ID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_addgroups);
		load();	
	}
	
	private void load()
	{
		//grab the email of current users from our global class
		user = GLOBAL.getCurrentUser();
		Bundle extras = getIntent().getExtras();
		//grab the e_id from extras
		ID = Integer.toString(extras.getInt("EID"));
		//load our list of current groups.  key is group id -> value is group name
		allGroups = user.getGroups();
		if (allGroups != null)
			System.out.println("AT THIS TIME ALL GROUPS IS THIS BIG: " + allGroups.size());
		else
			System.out.println("IT IS NULL AT THIS TIME ALLGROUPS");
		populateGroupCreate();
		initActionBar("Invite Groups to Event");
	}
	
	private void populateGroupCreate()
	{
		View row;
		// begin building the interface
		LayoutInflater li = getLayoutInflater();
		LinearLayout membersToAdd = (LinearLayout) findViewById(R.id.linearLayoutNested_eventAddGroups);
		
		if(allGroups.isEmpty())
		{
			// user has no friends
			// The user has no friend's so display the sad guy image.
			row = li.inflate(R.layout.listitem_sadguy, null);
			((TextView) row.findViewById(R.id.sadGuyTextView))
				.setText("You don't have any groups to add yet!");
			membersToAdd.addView(row);
		}
		
		//setup for each group
		for(int i=0; i<allGroups.size(); i++)
		{
			row = li.inflate(
					R.layout.list_row_eventinvitegroup, null);

			final TextView groupNameTextView = (TextView) row
					.findViewById(R.id.groupNameTextView);
			final CheckBox cb = (CheckBox) row
					.findViewById(R.id.addToEventBox);
			final String name = groupNameTextView.getText().toString();
			cb.setId(i);
			row.setId(i);
			groupNameTextView.setId(i);
			//listener when clicking makeAdmin button
			row.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View view) 
				{
					if(cb.isChecked())
					{
						cb.setChecked(false);
						added.remove(view.getId());
						System.out.println("Added size: "+added.size());
					}
					else
					{
						cb.setChecked(true);
						added.put(view.getId(), name);
						System.out.println("Added size: "+added.size());
					}
				}
			});		
			groupNameTextView.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View view) 
				{
					if(cb.isChecked())
					{
						cb.setChecked(false);
						added.remove(view.getId());
						System.out.println("Added size: "+added.size());
					}
					else
					{
						cb.setChecked(true);
						added.put(view.getId(), name);
						System.out.println("Added size: "+added.size());
					}
				}
			});		
			//listener when clicking checkbox
			cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton view, boolean isChecked)
				{
					if(cb.isChecked())
					{
						added.put(view.getId(), name);
						System.out.println("Added size: "+added.size());
					}
					else
					{
						added.remove(view.getId());
						System.out.println("Added size: "+added.size());
					}
				}
			});
			groupNameTextView.setText(allGroups.get(i).getName());
			membersToAdd.addView(row);	
		}
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
			String ID = String.valueOf(allGroups.get(key).getID());
				
			System.out.println("adding group: "+ID);		
			
			//initiate add of group
			new EventAddGroupTask().execute("http://68.59.162.183/"
					+ "android_connect/add_eventmember.php", ID, email, ID);
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
			//pass url off to GLOBAL to do the JSON call.  Code continues at onPostExecute when JSON returns.
			return GLOBAL.readJSONFeed("http://68.59.162.183/android_connect/get_group_members.php?gid="+tmpid, null);
		}

		@Override
		protected void onPostExecute(String result)
		{
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
						JSONObject o = (JSONObject) jsonArray.get(i);
						String memberToAdd = o.getString("email");
						System.out.println("FETCHING GROUP MEMBER" + memberToAdd + " EID is " + ID);
						new EventAddMemberTask().execute("http://68.59.162.183/"
								+ "android_connect/add_eventmember.php", memberToAdd, email, ID);
					}
					//making a success toast
					String message = "Successfully invited groups!";
					Toast toast = GLOBAL.getToast(EventAddGroupsActivity.this, message);
					toast.show();
					finish();		
				}
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("fetchgroupmembers", "failed = 2 return");
				}
			} 
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
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
			} 
			catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}
		}		
	}
}
