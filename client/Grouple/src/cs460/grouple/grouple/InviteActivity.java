package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Brett, Todd, Scott InviteActivity invites a list of users to a
 *         created group.
 */
public class InviteActivity extends BaseActivity
{

	private User user;
	private ArrayList<User> users;
	private Group group;
	private String userRole;
	private SparseArray<String> role = new SparseArray<String>();
	private GcmUtility gcmUtil;
	private String groupName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite);
		
		Bundle extras = getIntent().getExtras();
		// should always be current user
		user = GLOBAL.getCurrentUser();
		groupName = extras.getString("group_name");
		group = GLOBAL.getGroup(extras.getInt("g_id"));
		userRole = user.getGroupRole(group.getID());
		try
		{
			gcmUtil = new GcmUtility(GLOBAL);
		}
		catch (Exception e)
		{
		}
		updateUI();
	}


	private void updateUI()
	{
		initActionBar("Invite to " + group.getName(), true);
		// could use content here
		View view;
		LinearLayout pickFriendsLayout = (LinearLayout) findViewById(R.id.pickFriendsLayout);
		ArrayList<User> members;
		members = group.getUsers();
		ArrayList<User> friends = user.getUsers();
		users = new ArrayList<User>();
		System.out.println("friends size: " + friends.size() + ", members size: " + members.size());
		for (User friend : friends)
		{
			boolean inGroup = false;
			for (User member : members)
			{
				if (member.getEmail().equals(friend.getEmail()))
				{
					inGroup = true;
				}
			}
			if (!inGroup)
				users.add(friend);
		}

		if (users != null && users.size() != 0)
		{
			// looping thru json and adding to an array
			int index = 0;
			// setup for each friend
			for (User user : users)
			{
				view = inflater.inflate(R.layout.list_row_invitefriend, null);
				final RelativeLayout row = (RelativeLayout) view.findViewById(R.id.friendManageLayout);
				final Button makeAdminButton = (Button) row.findViewById(R.id.removeFriendButtonNoAccess);
				final TextView friendName = (TextView) row.findViewById(R.id.friendNameTextView);
				final CheckBox cb = (CheckBox) row.findViewById(R.id.addToGroupBox);
				makeAdminButton.setId(index);
				cb.setId(index);
				friendName.setId(index);
				row.setId(index);

				// TODO:
				// if user is promoter, don't allow inviting people as anything
				// above user level
				// should save that mapping in the user
				// onClickListeners
				row.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						if (makeAdminButton.getText().toString().equals("-"))
						{
							makeAdminButton.setText("U");
							role.put(view.getId(), "U");
							cb.setChecked(true);

							makeAdminButton.setTextColor(getResources().getColor(R.color.yellow));
						}
						else if (makeAdminButton.getText().toString().equals("U") && userRole.equals("A"))
						{
							makeAdminButton.setText("P");
							role.put(view.getId(), "P");
							cb.setChecked(true);
							makeAdminButton.setTextColor(getResources().getColor(R.color.purple));
						}
						else if (makeAdminButton.getText().toString().equals("P"))
						{
							makeAdminButton.setText("A");
							role.put(view.getId(), "A");
							cb.setChecked(true);
							makeAdminButton.setTextColor(getResources().getColor(R.color.green));
						}
						else if (makeAdminButton.getText().toString().equals("A")
								|| makeAdminButton.getText().toString().equals("U"))
						{
							makeAdminButton.setText("-");
							cb.setChecked(false);
							role.remove(view.getId());
							makeAdminButton.setTextColor(getResources().getColor(R.color.black));
						}
					}
				});
				makeAdminButton.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						if (makeAdminButton.getText().toString().equals("-"))
						{
							makeAdminButton.setText("U");
							role.put(view.getId(), "U");
							cb.setChecked(true);

							makeAdminButton.setTextColor(getResources().getColor(R.color.yellow));
						}
						else if (makeAdminButton.getText().toString().equals("U") && userRole.equals("A"))
						{
							makeAdminButton.setText("P");
							role.put(view.getId(), "P");
							cb.setChecked(true);
							makeAdminButton.setTextColor(getResources().getColor(R.color.purple));
						}
						else if (makeAdminButton.getText().toString().equals("P"))
						{
							makeAdminButton.setText("A");
							role.put(view.getId(), "A");
							cb.setChecked(true);

							makeAdminButton.setTextColor(getResources().getColor(R.color.green));
						}
						else if (makeAdminButton.getText().toString().equals("A")
								|| makeAdminButton.getText().toString().equals("U"))
						{
							makeAdminButton.setText("-");
							cb.setChecked(false);
							role.remove(view.getId());
							makeAdminButton.setTextColor(getResources().getColor(R.color.black));
						}
					}
				});
				cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
				{
					@Override
					public void onCheckedChanged(CompoundButton view, boolean isChecked)
					{

						if (cb.isChecked())
						{
							role.put(view.getId(), "U");
							makeAdminButton.setText("U");
							makeAdminButton.setTextColor(getResources().getColor(R.color.yellow));
						}
						else
						{
							role.remove(view.getId());
							makeAdminButton.setText("-");
							makeAdminButton.setTextColor(getResources().getColor(R.color.black));
						}
					}
				});
				friendName.setText(user.getName());
				pickFriendsLayout.addView(row);
				index++;
			}
		}
		else
		{
			// no friends that are not already in group
			view = inflater.inflate(R.layout.list_item_sadguy, null);
			((TextView) view.findViewById(R.id.sadGuyTextView))
					.setText("All of your friends are already in this group.");
			pickFriendsLayout.addView(view);
		}
	}

	public void confirmButton(View view)
	{
		// now loop through list of added to add all the additional users to the
		// group
		int size = role.size();
		System.out.println("Total count of users to process: " + size);
		ArrayList<String> friendsEmailList = new ArrayList<String>();
		for (int i = 0; i < size; i++)
		{
			// get the user's email by matching indexes from added list with
			// indexes from allFriendslist.
			int key = role.keyAt(i);
			// grab the email of friend to add
			String friendsEmail = users.get(key).getEmail();
			// grab the role of friend to add
			String friendsRole = role.get(key);
			if (!(friendsRole.equals("-")))
			{
				try
				{
					new AddGroupMembersTask().execute("http://68.59.162.183/" + "android_connect/add_groupmember.php",
							friendsEmail, user.getEmail(), friendsRole, Integer.toString(group.getID())).get();
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (ExecutionException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Send Push Notification TODO: move this to onpost somehow
				// gcmUtil.sendGroupNotification(friendsEmail,group.getName(),Integer.toString(group.getID()),"GROUP_INVITE");
				friendsEmailList.add(friendsEmail);
			}

		}
		// Send Group Notifications. Send list of friends email address, group
		// id, and notificatoin type.
		try
		{
			gcmUtil.sendGroupNotification(friendsEmailList, groupName, Integer.toString(group.getID()), "GROUP_INVITE");
			GLOBAL.getToast(this, "Friends have been invited!").show();
		}
		catch (Exception e)
		{
			GLOBAL.getToast(this, "No friends have been invited.").show();	
		}

		// end activity, go back to previous activity
		finish();
	}

	// aSynch task to add individual member to group.
	private class AddGroupMembersTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("sender", urls[2]));
			nameValuePairs.add(new BasicNameValuePair("role", urls[3]));
			nameValuePairs.add(new BasicNameValuePair("g_id", urls[4]));
			// pass url and nameValuePairs off to GLOBAL to do the JSON call.
			// Code continues at onPostExecute when JSON returns.
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
					// all working correctly, continue to next user or finish.
				}
				else if (jsonObject.getString("success").toString().equals("0"))
				{
					// a particular user was unable to be added to database for
					// some reason...
					// Don't tell the user!
				}
			}
			catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}
		}
	}
}
