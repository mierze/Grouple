package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.util.ArrayList;
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
	//private imagething profileImg;?
	//birthday?
	private String location;
	private int age;
	private Map<Integer, String> groups; 
	private ArrayList<String> friendRequests; //friendRequest emails->names
	private Map <Integer, String> groupInvites; //group invite ids
	private Map <Integer, String> eventsPending;
	private Map <Integer, Boolean> eventsAccepted;
	private Map <Integer, String> eventsUpcoming;
	
	
	/*
	 * Constructor for User class
	 */
	public User(String email) 
	{
		setEmail(email);
		System.out.println("Initializing new user.");
	}
	
	//testing
	public void removeGroup(int gid)
	{
		if (groups != null && groups.containsKey(gid))
			groups.remove(gid);
	}
	//testing
	public void removeGroupInvite(int gid)
	{
		if (groupInvites != null && groupInvites.containsKey(gid))
		{
			groupInvites.remove(gid);
		}

	}
	public void removeEventUpcoming(int id)
	{
		if (eventsUpcoming != null && eventsUpcoming.containsKey(id))
		{
			eventsUpcoming.remove(id);
		}
	}
	public void removeEventPending(int id)
	{
		
		if (eventsPending != null && eventsPending.containsKey(id))
		{
			eventsPending.remove(id);
		}
	}
	public void removeFriendRequest(String email)
	{
		if (friendRequests.contains(email))
			friendRequests.remove(email);
	}
	
	
	/*
	 * Setters for user class below
	 */
	public void setLocation(String location)
	{
		this.location = location;
	}
	public void setAge(int age)
	{
		this.age = age;
	}
	public void addToFriendRequests(String email)
	{
		if (friendRequests == null)
		{
			friendRequests = new ArrayList<String>();
		}
		if (!friendRequests.contains(email))
			friendRequests.add(email);
	}
	public void addToGroups(String id, String name)
	{
		if (groups == null)
		{
			groups = new HashMap<Integer, String>();
		}
		int idNum = Integer.parseInt(id);
		groups.put(idNum, name);
	}
	public void addToGroupInvites(String id, String name, String sender)
	{
		if (groupInvites == null)
		{
			groupInvites = new HashMap<Integer, String>();
		}
		int idNum = Integer.parseInt(id);
		
		groupInvites.put(idNum, name);
	}
	public void addToEventsPending(String id, String name, String sender, boolean accepted)
	{
		if (eventsPending == null)
		{
			eventsPending = new HashMap<Integer, String>();
			eventsAccepted = new HashMap<Integer, Boolean>();
		}
		int idNum = Integer.parseInt(id);
		eventsAccepted.put(idNum, accepted);
		eventsPending.put(idNum, name);
	}
	public void addToEventsUpcoming(String id, String name)
	{
		if (eventsUpcoming == null)
		{
			eventsUpcoming = new HashMap<Integer, String>();
		}
		int idNum = Integer.parseInt(id);
		
		eventsUpcoming.put(idNum, name);
	}
	
	/*
	 * Getters for user class below
	 */

	public String getFirstName()
	{
		String [] n = getName().split(" ");
		return n[0];
	}
	public String getLastName()
	{
		String [] n = getName().split(" ");
		return n[1];
	}
	public String getLocation()
	{
		return location;
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
	public int getNumEventsUpcoming()
	{
		if (eventsUpcoming != null)
			return eventsUpcoming.size();
		else
			return 0;
	}
	public ArrayList<String> getFriendRequests()
	{
		return friendRequests;
	}
	public Map<Integer, String> getGroups()
	{
		return groups;
	}
	public Map<Integer, String> getGroupInvites()
	{
		return groupInvites;
	}
	public Map<Integer, String> getEventsPending()
	{
		return eventsPending;
	}
	public Map<Integer, String> getEventsUpcoming()
	{
		return eventsUpcoming;
	}
	public Map<Integer, Boolean> getEventsAccepted()
	{
		return eventsAccepted;
	}
	
	
	
	
	/*
	 * To delete the user out of memory and clear all arrays
	 */
	public int delete()
	{
		//delete code here
		
		return 1; //successful
	}
	
	

	
	/**
	 * 
	 * fetches the user name, bio, and everything
	 * 
	 */
	public int fetchUserInfo()
	{
		
		AsyncTask<String, Void, String> task = new getUserInfoTask()
		.execute("http://68.59.162.183/android_connect/get_profile.php");
  

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
		
		//while (task.getStatus() != Status.FINISHED);
		return 1;
	}
		

	private class getUserInfoTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("email", getEmail()));
			return readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				//getting json object from the result string
				JSONObject jsonObject = new JSONObject(result);
				//gotta make a json array
				//JSONArray jsonArray = jsonObject.getJSONArray("userInfo");
				
				 
				//json fetch was successful
				if (jsonObject.getString("success").toString().equals("1"))
				{
					JSONArray jsonArray = jsonObject.getJSONArray("profile");
					Log.d("getUserInfoOnPost", "success1");

					//at each iteration set to hashmap friendEmail -> 'first last'
					String fName = (String) jsonArray.get(0);
					//set name
					String lName = (String) jsonArray.get(1);
					setName(fName + " " + lName);
					
					//set bio
					String about = (String) jsonArray.get(3);
					setAbout(about);
					
					//set location
					String location = (String) jsonArray.get(4);
					setLocation(location);
					
					//set birthday (not yet implemented)
					//for now do age
					int age = (Integer) jsonArray.get(2);
					Log.d("getUserInfoOnPost", "age: " + age);
					setAge(age);//panda
					//Log.d("getUserInfoOnPost", "after set age");
					//String fName = jsonObject.getString("fName").toString();
					//setBirthday(fName); 
					
					//get that image niggi
					String image = (String) jsonArray.get(5);
					setImage(image);
				
				} 
				//unsuccessful
				else
				{
					// failed
					Log.d("UserFetchInfoOnPost", "FAILED");
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

	/*
	 * 
	 * will be fetching the friends key->val stuff here
	 * 
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
		
		//while (task.getStatus() != Status.FINISHED);
		return 1;
	}

	private class getFriendsTask extends AsyncTask<String, Void, String>
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
					//gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("friends");
					//looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						//at each iteration set to hashmap friendEmail -> 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						//function adds friend to the friends map
						addToUsers(o.getString("email"), o.getString("first") + " " + o.getString("last"));
					}
				}
				
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("fetchFriends", "failed = 2 return");
					//setNumFriends(0); //PANDA need to set the user class not global
				}
			} catch (Exception e)
			{
				Log.d("fetchFriends", "exception caught");
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}	
	
	/*
	 * Should be getting the friendRequest key->vals here
	 */
	public int fetchFriendRequests()
	{
		
		AsyncTask<String, Void, String> task = new getFriendRequestsTask()
		.execute("http://68.59.162.183/android_connect/get_friend_requests.php?receiver="
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
		
		//while (task.getStatus() != Status.FINISHED);

		
		return 1;
	}

	private class getFriendRequestsTask extends AsyncTask<String, Void, String>
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
					//gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("friendRequests");
					
					//looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						//at each iteration set to hashmap friendEmail -> 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						//function adds friend to the friends map=
						Log.d("fetchFriendRequestsPost", "array length: " + jsonArray.length() + ", email: " + o.getString("email"));
						addToFriendRequests(o.getString("email"));
					}
				}
				// user has no friend requests
				if (jsonObject.getString("success").toString().equals("2"))
				{
					//no friend requests
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

	
		//while (task.getStatus() != Status.FINISHED);
		
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

				//need to get gid, gname for each and put them in hashmap
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					//gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("groups");
					
					
					//clearing old groups
					if (groups != null)
					{
						System.out.println("Clearing groups of user.");
						groups.clear();
					}
					
					
					//looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						//at each iteration set to hashmap friendEmail -> 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						//function adds friend to the friends map
						System.out.println("HERE WE ARE ABOUT TO ADD A GROUP TO THE GROUPS TABLE");
						addToGroups(o.getString("gid"), o.getString("gname"));
					}

				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("getGroups", "ERROR WITH JSON");
					//setNumFriends(0); //PANDA need to set the user class not global
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
	 * 
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
		
		//while (task.getStatus() != Status.FINISHED);
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
					//gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("invites");
					
					//looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						//at each iteration set to hashmap friendEmail -> 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						//function adds friend to the friends map
						addToGroupInvites(o.getString("gid"), o.getString("gname"), o.getString("sender"));
					}

				}
				
				// user has no group invites
				if (jsonObject.getString("success").toString().equals("2"))
				{
					//setNumFriends(0); //PANDA need to set the user class not global
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
	 * 
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
		
		//while (task.getStatus() != Status.FINISHED);
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
					//gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("eventsPending");
					
					//looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						//at each iteration set to hashmap friendEmail -> 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						//function adds friend to the friends map
						boolean accepted = false;
						if (o.get("recdate") != null)
							accepted = true;
						System.out.println("Adding to events pending, boolean accepted = " + accepted);
						addToEventsPending(o.getString("eid"), o.getString("name"), o.getString("sender"), accepted);
					}
				}
				
				// user has no group invites
				if (jsonObject.getString("success").toString().equals("2"))
				{
					//no group invites
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
	 * 
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
		
		//while (task.getStatus() != Status.FINISHED);
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
					//gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("eventsUpcoming");
					
					//looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						//at each iteration set to hashmap friendEmail -> 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						//function adds friend to the friends map
						addToEventsUpcoming(o.getString("eid"), o.getString("name"));
					}

				}
				
				// user has no group invites
				if (jsonObject.getString("success").toString().equals("2"))
				{
					//setNumFriends(0); //PANDA need to set the user class not global
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
	
}
