package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
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
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONObject;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class User extends Entity
{
	// birthday?
	private String location;
	private int age;
	private String birthday;
	private ArrayList<Group> groups;
	private ArrayList<User> friendRequests; // friendRequest emails->names
	private ArrayList<Group> groupInvites; // group invite ids
	private ArrayList<Event> eventsUpcoming;
	private ArrayList<Event> eventsPast;
	private ArrayList<Event> eventsInvites;
	// TESTING
	private ArrayList<Event> eventsPending;

	/*
	 * Constructor for User class
	 */
	public User(String email)
	{
		setEmail(email);
		System.out.println("Initializing new user.");
	}

	// testing
	public void removeGroup(int id)
	{
		if (groups != null)
			for (Group g : groups)
				if (g.getID() == id)
				{
					System.out.println("WE FOUND A MATCH");
					groups.remove(groups.indexOf(g));
					System.out.println("REMOVE SEEMS SUCCS");
					break;
				}
	}

	// testing
	public void removeGroupInvite(int id)
	{
		if (groupInvites != null)
			for (Group g : groupInvites)
				if (g.getID() == id)
				{
					System.out.println("WE FOUND A MATCH");
					groupInvites.remove(groupInvites.indexOf(g));
					System.out.println("REMOVE SEEMS SUCCS");
					break;
				}
	}

	public void removeEventUpcoming(int id)
	{
		if (eventsUpcoming != null)
			for (Event e : eventsUpcoming)
				if (e.getID() == id)
				{
					System.out.println("WE FOUND A MATCH");
					eventsUpcoming.remove(eventsUpcoming.indexOf(e));
					System.out.println("REMOVE SEEMS SUCCS");
					break;
				}
	}

	public void removeEventPending(int id)
	{
		if (eventsPending != null)
			for (Event e : eventsPending)
				if (e.getID() == id)
				{
					System.out.println("WE FOUND A MATCH");
					eventsPending.remove(eventsPending.indexOf(e));
					System.out.println("REMOVE SEEMS SUCCS");
					break;
				}
	}

	public void removeEventInvite(int id)
	{
		if (eventsInvites != null)
			for (Event e : eventsInvites)
				if (e.getID() == id)
				{
					System.out.println("WE FOUND A MATCH");
					eventsInvites.remove(eventsInvites.indexOf(e));
					System.out.println("REMOVE SEEMS SUCCS");
					break;
				}
	}

	public void removeEventPast(int id)
	{
		if (eventsPast != null)
			for (Event e : eventsPast)
				if (e.getID() == id)
				{
					System.out.println("WE FOUND A MATCH");
					eventsPast.remove(eventsPast.indexOf(e));
					System.out.println("REMOVE SEEMS SUCCS");
					break;
				}
	}

	public void removeFriendRequest(String email)
	{
		if (friendRequests != null)
			for (User u : friendRequests)
				if (u.getEmail().equals(email))
				{
					System.out.println("WE FOUND A MATCH");
					friendRequests.remove(friendRequests.indexOf(u));
					System.out.println("REMOVE SEEMS SUCCS");
					break;
				}
	}

	/*
	 * Setters for user class below
	 */
	public void setLocation(String location)
	{
		this.location = location;
	}

	public void setBirthday(String birthday)
	{
		System.out.println(birthday);
		this.birthday = birthday;
		
		if (!birthday.equals(""))
		{
			SimpleDateFormat raw = new SimpleDateFormat("yyyy-M-d");
			Date birthDate = null;
			try
			{
				birthDate = raw.parse(birthday);
			} catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int ageInt;
			Calendar dob = Calendar.getInstance();
			dob.setTime(birthDate);
			Calendar today = Calendar.getInstance();
			if ((today.get(Calendar.MONTH) < dob.get(Calendar.MONTH))
					|| (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH) && today
							.get(Calendar.DAY_OF_MONTH) <= dob
							.get(Calendar.DAY_OF_MONTH)))
			{
				// year --
				System.out.println("SETTING AGE IN THE IF");
				ageInt = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
			} else
			{
				System.out.println("IN ELSE RIGHT NOW");
				ageInt = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR) - 1;
			}
			this.age = ageInt;
		} 
		else
		{
			System.out.println("SETTING AGE TO -1");
			this.age = -1;
		}
			
	}

	public void addToFriendRequests(User u)
	{
		boolean inFriendRequests = false;
		if (friendRequests == null)
		{
			friendRequests = new ArrayList<User>();
		}
		for (User t : friendRequests)
			if (t.getEmail().equals(u.getEmail()))
				inFriendRequests = true;
		if (!inFriendRequests) // TODO: this?
			friendRequests.add(u);
	}

	public void addToGroups(Group g)
	{
		boolean inGroups = false;
		if (groups == null)
		{
			groups = new ArrayList<Group>();
		}
		for (Group t : groups)
			if (t.getID() == g.getID())
				inGroups = true;
		if (!inGroups)
			groups.add(g);
	}

	public void addToGroupInvites(Group g)
	{
		boolean inGroupInvites = false;
		if (groupInvites == null)
		{
			groupInvites = new ArrayList<Group>();
		}
		for (Group t : groupInvites)
			if (t.getID() == g.getID())
				inGroupInvites = true;
		if (!inGroupInvites)
			groupInvites.add(g);
	}

	public void addToEventsPending(Event e)
	{
		boolean inEventsPending = false;
		if (eventsPending == null)
		{
			eventsPending = new ArrayList<Event>();
		}
		for (Event t : eventsPending)
			if (t.getID() == e.getID())
				inEventsPending = true;
		if (!inEventsPending)
			eventsPending.add(e);
	}

	public void addToEventsInvites(Event e)
	{
		boolean inEventsInvites = false;
		if (eventsInvites == null)
		{
			eventsInvites = new ArrayList<Event>();
		}
		for (Event t : eventsInvites)
			if (t.getID() == e.getID())
				inEventsInvites = true;
		if (!inEventsInvites)
			eventsInvites.add(e);
	}

	public void addToEventsUpcoming(Event e)
	{
		boolean inEventsUpcoming = false;
		if (eventsUpcoming == null)
		{
			eventsUpcoming = new ArrayList<Event>();
		}
		for (Event t : eventsUpcoming)
			if (t.getID() == e.getID())
				inEventsUpcoming = true;
		if (!inEventsUpcoming)
			eventsUpcoming.add(e);
	}

	public void addToEventsPast(Event e)
	{
		boolean inEventsPast = false;
		if (eventsPast == null)
		{
			eventsPast = new ArrayList<Event>();
		}
		for (Event t : eventsPast)
			if (t.getID() == e.getID())
				inEventsPast = true;
		if (!inEventsPast)
			eventsPast.add(e);
	}

	/*
	 * Getters for user class below
	 */
	public String getFirstName()
	{
		String[] n = getName().split(" ");
		return n[0];
	}

	public String getLastName()
	{
		String[] n = getName().split(" ");
		return n[1];
	}

	public String getLocation()
	{
		return location;
	}

	public String getBirthday()
	{
		return birthday;
	}

	public int getAge()
	{
		return age;
	}

	public int getNumGroups()
	{
		if (groups != null)
			return groups.size();
		else
			return 0;
	}

	public int getNumFriendRequests()
	{
		if (friendRequests != null)
			return friendRequests.size();
		else
			return 0;
	}

	public int getNumGroupInvites()
	{
		if (groupInvites != null)
			return groupInvites.size();
		else
			return 0;
	}

	public int getNumEventsPending()
	{
		if (eventsPending != null)
			return eventsPending.size();
		else
			return 0;
	}

	public int getNumEventsPast()
	{
		if (eventsPast != null)
			return eventsPast.size();
		else
			return 0;
	}

	public int getNumEventsInvites()
	{
		if (eventsInvites != null)
			return eventsInvites.size();
		else
			return 0;
	}

	public int getNumEventsUpcoming()
	{
		if (eventsUpcoming != null)
			return eventsUpcoming.size();
		else
			return 0;
	}

	public ArrayList<User> getFriendRequests()
	{
		return friendRequests;
	}

	public ArrayList<Group> getGroups()
	{
		return groups;
	}

	public ArrayList<Group> getGroupInvites()
	{
		return groupInvites;
	}

	public ArrayList<Event> getEventsPending()
	{
		return eventsPending;
	}

	public ArrayList<Event> getEventsUpcoming()
	{
		return eventsUpcoming;
	}

	public ArrayList<Event> getEventsInvites()
	{
		return eventsInvites;
	}

	public ArrayList<Event> getEventsPast()
	{
		return eventsPast;
	}

	/**
	 * 
	 * fetches the user name, bio, and everything
	 * 
	 */
	public int fetchUserInfo()
	{
		AsyncTask<String, Void, String> task = new getUserInfoTask()
				.execute("http://68.59.162.183/android_connect/get_user_info.php");
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// while (task.getStatus() != Status.FINISHED);
		return 1;
	}

	class getUserInfoTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			System.out.println("ABOT TO GET USER INFO for " + getEmail());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", getEmail()));
			return readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				// getting json object from the result string
				JSONObject jsonObject = new JSONObject(result);
				// gotta make a json array
				// JSONArray jsonArray = jsonObject.getJSONArray("userInfo");
				System.out.println("USER INFO on post TRY");
				// json fetch was successful
				if (jsonObject.getString("success").toString().equals("1"))
				{
					System.out.println("in succuess USER INFO");
					JSONArray jsonArray = jsonObject.getJSONArray("userInfo");
					System.out.println("in succuess USER INFO1");
					Log.d("getUserInfoOnPost", "success1");
					// at each iteration set to hashmap friendEmail -> 'first
					// last'
					String fName = (String) jsonArray.get(0);
					// set name
					String lName = (String) jsonArray.get(1);
					setName(fName + " " + lName);
					// it must do this null check for brand new users who have
					// never updated these optional fields, since database
					// defaults them to NULL.
					// set bio
					Object about = jsonArray.get(3);
					if (about.toString().equals("null"))
					{
						setAbout("");
					} else
					{
						setAbout(about.toString());
					}
					// set location
					Object location = jsonArray.get(4);
					if (location.toString().equals("null"))
					{
						setLocation("");
					} else
					{
						setLocation(location.toString());
					}
					// set birthday (not yet implemented)
					// for now do age
					Object age = jsonArray.get(2);
					if (age.toString().equals("null") || age.toString().equals("0000-00-00"))
					{
						setBirthday("");
					} else
					{
						setBirthday(age.toString());// panda
					}
					Log.d("getUserInfoOnPost", "age: " + age);
					// Log.d("getUserInfoOnPost", "after set age");
					// String fName = jsonObject.getString("fName").toString();
					// setBirthday(fName);
					// get that image niggi
					// String image = (String) jsonArray.get(5);
					// setImage(image);
				}
				// unsuccessful
				else
				{
					// failed
					Log.d("UserFetchInfoOnPost", "FAILED");
				}
			} catch (Exception e)
			{
				Log.d("atherjsoninuserpost", "here");
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
			// do next thing here
		}
	}

	/*
	 * 
	 * will be fetching the friends key->val stuff here
	 */
	// Get numFriends, TODO: work on returning the integer
	public int fetchFriends()
	{
		AsyncTask<String, Void, String> task = new getFriendsTask()
				.execute("http://68.59.162.183/android_connect/get_friends.php?email="
						+ getEmail());
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// while (task.getStatus() != Status.FINISHED);
		return 1;
	}

	class getFriendsTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			return readJSONFeed(urls[0], null);
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
					JSONArray jsonArray = jsonObject.getJSONArray("friends");
					// success so clear previous
					// getUsers().clear();
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						// at each iteration set to hashmap friendEmail ->
						// 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						// function adds friend to the friends map
						User u = new User(o.getString("email"));
						u.setName(o.getString("first") + " "
								+ o.getString("last"));
						addToUsers(u);
					}
				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("fetchFriends", "failed = 2 return");
					// setNumFriends(0); //PANDA need to set the user class not
					// global
				}
			} catch (Exception e)
			{
				Log.d("fetchFriends", "exception caught");
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	public void setFriendRequests(ArrayList<User> friendRequests)
	{
		this.friendRequests = friendRequests;
	}

	/*
	 * Should be getting the friendRequest key->vals here
	 */
	public int fetchFriendRequests()
	{
		// friendRequests = null;//reset
		AsyncTask<String, Void, String> task = new getFriendRequestsTask();
		task.execute("http://68.59.162.183/android_connect/get_friend_requests.php?receiver="
				+ getEmail());
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(task.getStatus());
		return 1;
	}

	class getFriendRequestsTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			return readJSONFeed(urls[0], null);
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
					JSONArray jsonArray = jsonObject
							.getJSONArray("friendRequests");
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						// at each iteration set to hashmap friendEmail ->
						// 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						// function adds friend to the friends map=
						Log.d("fetchFriendRequestsPost",
								"array length: " + jsonArray.length()
										+ ", email: " + o.getString("email"));
						addToFriendRequests(new User(o.getString("email")));
					}
				}
				// user has no friend requests
				if (jsonObject.getString("success").toString().equals("2"))
				{
					// no friend requests
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	/*
	 * should be getting the groups key->vals here
	 */
	public int fetchGroups()
	{
		AsyncTask<String, Void, String> task = new getGroupsTask()
				.execute("http://68.59.162.183/android_connect/get_groups.php?email="
						+ getEmail());
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// while (task.getStatus() != Status.FINISHED);
		return 1;
	}

	private class getGroupsTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			return readJSONFeed(urls[0], null);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				// need to get gid, gname for each and put them in hashmap
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("groups");
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						// at each iteration set to hashmap friendEmail ->
						// 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						// function adds friend to the friends map
						System.out
								.println("HERE WE ARE ABOUT TO ADD A GROUP TO THE GROUPS TABLE");
						Group g = new Group(
								Integer.parseInt(o.getString("gid")));
						g.setName(o.getString("gname"));
						addToGroups(g);
					}
				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("getGroups", "ERROR WITH JSON");
					// setNumFriends(0); //PANDA need to set the user class not
					// global
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	/*
	 * 
	 * should be getting the groupInvites key->vals here
	 */
	// Get numFriends, TODO: work on returning the integer
	public int fetchGroupInvites()
	{
		AsyncTask<String, Void, String> task = new getGroupInvitesTask()
				.execute("http://68.59.162.183/android_connect/get_group_invites.php?email="
						+ getEmail());
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// while (task.getStatus() != Status.FINISHED);
		return 1;
	}

	private class getGroupInvitesTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			return readJSONFeed(urls[0], null);
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
					JSONArray jsonArray = jsonObject.getJSONArray("invites");
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						// at each iteration set to hashmap friendEmail ->
						// 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						// function adds friend to the friends map
						Group g = new Group(
								Integer.parseInt(o.getString("gid")));
						g.setName(o.getString("gname"));
						g.setInviter(o.getString("sender"));
						addToGroupInvites(g);
					}
				}
				// user has no group invites
				if (jsonObject.getString("success").toString().equals("2"))
				{
					// setNumFriends(0); //PANDA need to set the user class not
					// global
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	/*
	 * 
	 * should be getting the groupInvites key->vals here
	 */
	// Get numFriends, TODO: work on returning the integer
	public int fetchEventsPending()
	{
		AsyncTask<String, Void, String> task = new getEventsPendingTask()
				.execute("http://68.59.162.183/android_connect/get_events_pending.php?email="
						+ getEmail());
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// while (task.getStatus() != Status.FINISHED);
		return 1;
	}

	private class getEventsPendingTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			return readJSONFeed(urls[0], null);
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
					JSONArray jsonArray = jsonObject
							.getJSONArray("eventsPending");
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						// at each iteration set to hashmap friendEmail ->
						// 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						// function adds friend to the friends map
						Event e = new Event(
								Integer.parseInt(o.getString("eid")));
						e.setName(o.getString("name"));
						e.setInviter(o.getString("sender"));
						e.setMinPart(Integer.parseInt(o.getString("minpart")));
						e.setMaxPart(Integer.parseInt(o.getString("maxpart")));
						e.fetchParticipants();
						addToEventsPending(e);
						// set min max
					}
				}
				// user has no group invites
				if (jsonObject.getString("success").toString().equals("2"))
				{
					// no group invites
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	/*
	 * 
	 * should be getting the groupInvites key->vals here
	 */
	// Get numFriends, TODO: work on returning the integer
	public int fetchEventsPast()
	{
		AsyncTask<String, Void, String> task = new getEventsPastTask()
				.execute("http://68.59.162.183/android_connect/get_events_past.php?email="
						+ getEmail());
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// while (task.getStatus() != Status.FINISHED);
		return 1;
	}

	private class getEventsPastTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			return readJSONFeed(urls[0], null);
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
					JSONArray jsonArray = jsonObject.getJSONArray("eventsPast");
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						// at each iteration set to hashmap friendEmail ->
						// 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						// function adds friend to the friends map
						Event e = new Event(
								Integer.parseInt(o.getString("eid")));
						e.setName(o.getString("name"));
						e.setInviter(o.getString("sender"));
						e.setMinPart(Integer.parseInt(o.getString("minpart")));
						e.setMaxPart(Integer.parseInt(o.getString("maxpart")));
						e.fetchParticipants();
						addToEventsPast(e);
						// set min max
					}
				}
				// user has no group invites
				if (jsonObject.getString("success").toString().equals("2"))
				{
					// no group invites
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	public int fetchEventsInvites()
	{
		AsyncTask<String, Void, String> task = new getEventsInvitesTask()
				.execute("http://68.59.162.183/android_connect/get_events_invites.php?email="
						+ getEmail());
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// while (task.getStatus() != Status.FINISHED);
		return 1;
	}

	private class getEventsInvitesTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			return readJSONFeed(urls[0], null);
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
					JSONArray jsonArray = jsonObject
							.getJSONArray("eventsInvites");
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						// at each iteration set to hashmap friendEmail ->
						// 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						Event e = new Event(
								Integer.parseInt(o.getString("eid")));
						e.setName(o.getString("name"));
						e.setInviter(o.getString("sender"));
						e.setMinPart(Integer.parseInt(o.getString("minpart")));
						e.setMaxPart(Integer.parseInt(o.getString("maxpart")));
						e.fetchParticipants();
						addToEventsInvites(e);
					}
				}
				// user has no group invites
				if (jsonObject.getString("success").toString().equals("2"))
				{
					// no group invites
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	/*
	 * 
	 * should be getting the groupInvites key->vals here
	 */
	// Get numFriends, TODO: work on returning the integer
	public int fetchEventsUpcoming()
	{
		AsyncTask<String, Void, String> task = new getEventsUpcomingTask()
				.execute("http://68.59.162.183/android_connect/get_events_upcoming.php?email="
						+ getEmail());
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// while (task.getStatus() != Status.FINISHED);
		return 1;
	}

	private class getEventsUpcomingTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			return readJSONFeed(urls[0], null);
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
					JSONArray jsonArray = jsonObject
							.getJSONArray("eventsUpcoming");
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						// at each iteration set to hashmap friendEmail ->
						// 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						// function adds friend to the friends map
						Event e = new Event(
								Integer.parseInt(o.getString("eid")));
						e.setName(o.getString("name"));
						addToEventsUpcoming(e);
					}
				}
				// user has no group invites
				if (jsonObject.getString("success").toString().equals("2"))
				{
					// setNumFriends(0); //PANDA need to set the user class not
					// global
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
}
