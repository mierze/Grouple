package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/*
 * Global stores user values needed for notifications.
 */
public class Global extends Application
{
	private User currentUser; //contains the current user, is updated on every pertinent activity call
	private Group groupBuffer;
	private User userBuffer;
	private Event eventBuffer;
	
	/*
	 * Adds a user to the users arraylist
	 */
	public void setCurrentUser(User u)
	{
		currentUser = u;
	}
	
	public User getCurrentUser()
	{
		return currentUser;
	}
	public void setGroupBuffer(Group g)
	{
		groupBuffer = g;
	}
	public Group getGroupBuffer()
	{
		return groupBuffer;
	}
	public void setUserBuffer(User u)
	{
		userBuffer = u;
	}
	public void setEventBuffer(Event e)
	{
		eventBuffer = e;
	}
	public User getUserBuffer()
	{
		return userBuffer;
	}
	public Event getEventBuffer()
	{
		return eventBuffer;
	}
	public boolean isCurrentUser(String email)
	{
		if (getCurrentUser().getEmail().equals(email))
			return true;
		else 
			return false;
	}
	
	public int destroySession()
	{
		currentUser = null;
		groupBuffer = null;
		userBuffer = null;
		return 1;
	}
	//using the email of user, load them in
	public User loadUser(String email) 
	{	
		//instantiate a new user
		User user = new User(email);
		int success = 0;
	
		/**
		 * Below is just updating / loading all of the user info, friends, groups, pertinent to all loaded users
		 */
		//json call using email to fetch users fName, lName, bio, location, birthday, profileImage
		success = user.fetchUserInfo();
		//was successful in fetching user info
		if (success == 1)
			Log.d("loadUser", "success after fetchUserInfo()");

	
		//json call to populate users friendKeys / friendNames
		success = user.fetchFriends();
		//was successful in fetching friends
		if (success == 1)
			Log.d("loadUser","success after fetchFriends()");

		success = user.fetchGroups();
		//was successful in fetching groups
		if (success == 1)
			Log.d("loadUser","success after fetchGroups()");
	
		success = user.fetchEventsUpcoming();
		//was successful in fetching group invites
		if (success == 1)
			Log.d("loadUser","success after fetchEventsUpcoming()");
		
		//check that currentUser has been initialized
		//little redundancy from earlier if statement, when current user was not null
		//could for sure tweak up
		if (currentUser == null || isCurrentUser(user.getEmail()))
		{
			//if null, then this is our current user
			//getting requests / invites since wasn't triggered above
			//json call to populate users friendRequestKeys / names
			success = user.fetchFriendRequests();
			//was successful in fetching groups
			if (success == 1)
				Log.d("loadUser","success after fetchFriendRequests()");
			
			//fetchGroupInvites
			success = user.fetchGroupInvites();
		
			success = user.fetchEventsPending();
			//was successful in fetching group invites
			if (success == 1)
				Log.d("loadUser","success after fetchEventsPending()");

			System.out.println("Setting current user for first time");
			
		}	

		//saving
		if (getCurrentUser() == null || isCurrentUser(user.getEmail()))
		{
			//this
			setCurrentUser(user);
		}
		else
		{
			//set user buffer
			setUserBuffer(user);
		}
		
		return user; //return the user
	}
	
	//using the id of group, load them up into our array of groups
	public Group loadGroup(int id)
	{	
		Group group = new Group(id);
		
		
		/**
		 * Below is loading all group information / members...
		 */
		
	
		//json call using email to fetch users fName, lName, bio, location, birthday, profileImage
		int success = group.fetchGroupInfo();
		//was successful in fetching user info
		if (success == 1)
			Log.d("loadGroup", "success after fetchGroupInfo()");

			//json call to populate users friendKeys / friendNames
		success = group.fetchMembers();
		
		//was successful in fetching user info
		if (success == 1)
			Log.d("loadGroup", "success after fetchMembers()");
		

		setGroupBuffer(group);
		
		return group;
	}
		

	//using the id of group, load them up into our array of groups
	public Event loadEvent(int id)
	{	
		Event event; //declare group variable
		
		//instantiate a new group
		event = new Event(id);
		
		
		/**
		 * Below is loading all group information / members...
		 */
		
		//json call using email to fetch users fName, lName, bio, location, birthday, profileImage
		int success = event.fetchEventInfo();
		//was successful in fetching user info
		if (success == 1)
			Log.d("loadGroup", "success after fetchGroupInfo()");

			//json call to populate users friendKeys / friendNames
		success = event.fetchParticipants();
		
		//was successful in fetching user info
		if (success == 1)
			Log.d("loadGroup", "success after fetchMembers()");
		

	
		setEventBuffer(event);
		
		return event;
	}
	
	//may be outdated, can either update notifications here or in each activity itself
	public int setNotifications(View view, User user)
	{
		// todo: If I can pass an email in here and skip setting current user
		int numFriendRequests = user.getNumFriendRequests();
		int numFriends = user.getNumUsers();
		int numGroupInvites = user.getNumGroupInvites();
		int numGroups = user.getNumGroups();

		// Home Activity
		if (numFriendRequests > 0
				&& view.findViewById(R.id.friendsButtonHA) != null)
		{
			if (numFriendRequests == 1)
			{
				((Button) view.findViewById(R.id.friendsButtonHA))
						.setText("Friends \n(" + numFriendRequests
								+ " request)");
			} else
			{
				((Button) view.findViewById(R.id.friendsButtonHA))
						.setText("Friends \n(" + numFriendRequests
								+ " requests)");
			}
		} else if (numFriendRequests == 0
				&& view.findViewById(R.id.friendsButtonHA) != null)
		{
			((Button) view.findViewById(R.id.friendsButtonHA))
					.setText("Friends");
		}
		if (numGroupInvites > 0
				&& view.findViewById(R.id.groupsButtonHA) != null)
		{
			if (numFriendRequests == 1)
			{
				((Button) view.findViewById(R.id.groupsButtonHA))
						.setText("Groups \n(" + numGroupInvites + " invites)");
			} else
			{
				((Button) view.findViewById(R.id.groupsButtonHA))
						.setText("Groups \n(" + numGroupInvites + " invites)");
			}
		} else if (numFriendRequests == 0
				&& view.findViewById(R.id.groupsButtonHA) != null)
		{
			((Button) view.findViewById(R.id.groupsButtonHA)).setText("Groups");
		}

		// Friends Activity
		if (view.findViewById(R.id.friendRequestsButtonFA) != null
				&& view.findViewById(R.id.currentFriendsButtonFA) != null)
		{
			Button friendRequestsButton = (Button) view
					.findViewById(R.id.friendRequestsButtonFA);
			friendRequestsButton.setText("Friend Requests ("
					+ numFriendRequests + ")");
			Button currentFriendsButton = (Button) view
					.findViewById(R.id.currentFriendsButtonFA);
			currentFriendsButton
					.setText("My Friends (" + user.getNumUsers() + ")"); //PANDA
		}

		// User Profile Buttons
		if ((view.findViewById(R.id.profileButton1) != null)
				&& (view.findViewById(R.id.profileButton2) != null)
				&& (view.findViewById(R.id.profileButton3) != null))
		{
			((Button) view.findViewById(R.id.profileButton1))
					.setText("Friends\n(" + numFriends + ")");
			((Button) view.findViewById(R.id.profileButton2))
					.setText("Groups\n(" + numGroups + ")");
			// set numfriends, numgroups, and numevents
		}


		// Groups activity
		if (view.findViewById(R.id.pendingGroupsButton) != null)
		{
			System.out.println("Pending groups setting text to what it is");
			((Button) view.findViewById(R.id.pendingGroupsButton))
					.setText("Group Invites (" + numGroupInvites + ")");
		}
		if (view.findViewById(R.id.yourGroupsButton) != null)
		{
			((Button) view.findViewById(R.id.yourGroupsButton))
					.setText("My Groups (" + numGroups + ")");
		}
		return 1; // successful
		// else do nothing, keep that invisible
	}

	

	//probably not going to use this as much, 
	//maybe none if groups / users both have their own and everything goes through those
	//wouldn't let me use global from them, could change that too i suppose, maybe
	public String readJSONFeed(String URL, List<NameValuePair> nameValuePairs)
	{
		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();

		if (nameValuePairs == null)
		{
			HttpGet httpGet = new HttpGet(URL);

			try
			{
				HttpResponse response = httpClient.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200)
				{
					HttpEntity entity = response.getEntity();
					InputStream inputStream = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(inputStream));
					String line;
					while ((line = reader.readLine()) != null)
					{
						System.out.println("New line: " + line);
						stringBuilder.append(line);
					}
					inputStream.close();
				} else
				{
					Log.d("JSON", "Failed to download file");
				}
			} catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}

		}
		else
		{
			HttpPost httpPost = new HttpPost(URL);
			try
			{
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpClient.execute(httpPost);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200)
				{
					HttpEntity entity = response.getEntity();
					InputStream inputStream = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(inputStream));
					String line;
					while ((line = reader.readLine()) != null)
					{
						stringBuilder.append(line);
					}
					inputStream.close();
				} 
				else
				{
					Log.d("JSON", "Failed to download file");
				}
			} 
			catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}
		}
		return stringBuilder.toString();
	}//end readJSONFeed
}//end Global class
