package cs460.grouple.grouple;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

@SuppressLint("SimpleDateFormat")
public class User extends Entity
{
	private String location;
	private int age;
	private String birthday;
	private String birthdayText;
	private ArrayList<Group> groups = new ArrayList<Group>();
	private ArrayList<User> friendRequests = new ArrayList<User>();
	private ArrayList<Group> groupInvites = new ArrayList<Group>();
	private ArrayList<Event> eventsUpcoming = new ArrayList<Event>();
	private ArrayList<Event> eventsPast = new ArrayList<Event>();
	private ArrayList<Event> eventInvites = new ArrayList<Event>();
	private ArrayList<Event> eventsDeclined = new ArrayList<Event>();
	private ArrayList<Event> eventsPending = new ArrayList<Event>();
	private ArrayList<Badge> badges = new ArrayList<Badge>();
	private SparseArray<String> groupRoles = new SparseArray<String>();
	private SparseArray<String> eventRoles = new SparseArray<String>();

	/*
	 * Constructor for User class
	 */
	protected User(String email)
	{
		super();
		setEmail(email);
		initBadges();
		System.out.println("Initializing new user.");
	}

	protected void removeGroup(int id)
	{
		if (groups != null)
			for (Group g : groups)
				if (g.getID() == id)
				{
					groups.remove(groups.indexOf(g));
					break;
				}
	}

	protected void removeGroupInvite(int id)
	{
		if (groupInvites != null)
			for (Group g : groupInvites)
				if (g.getID() == id)
				{
					groupInvites.remove(groupInvites.indexOf(g));
					break;
				}
	}

	protected void removeEventUpcoming(int id)
	{
		if (eventsUpcoming != null)
			for (Event e : eventsUpcoming)
				if (e.getID() == id)
				{
					eventsUpcoming.remove(eventsUpcoming.indexOf(e));
					break;
				}
	}

	protected void removeEventPending(int id)
	{
		if (eventsPending != null)
			for (Event e : eventsPending)
				if (e.getID() == id)
				{
					eventsPending.remove(eventsPending.indexOf(e));
					break;
				}
	}

	protected void removeEventInvite(int id)
	{
		if (eventInvites != null)
			for (Event e : eventInvites)
				if (e.getID() == id)
				{
					eventInvites.remove(eventInvites.indexOf(e));
					break;
				}
	}

	protected void removeEventPast(int id)
	{
		if (eventsPast != null)
			for (Event e : eventsPast)
				if (e.getID() == id)
				{
					eventsPast.remove(eventsPast.indexOf(e));
					break;
				}
	}

	protected void removeEventDeclined(int id)
	{
		if (eventsDeclined != null)
			for (Event e : eventsDeclined)
				if (e.getID() == id)
				{
					eventsDeclined.remove(eventsDeclined.indexOf(e));
					break;
				}
	}

	protected void removeFriendRequest(String email)
	{
		if (friendRequests != null)
			for (User u : friendRequests)
				if (u.getEmail().equals(email))
				{
					friendRequests.remove(friendRequests.indexOf(u));
					break;
				}
	}

	// SETTERS
	protected void setLocation(String location)
	{
		this.location = location;
	}

	protected void setBirthday(String birthday)
	{
		System.out.println(birthday);
		this.birthday = birthday;
		this.birthdayText = GLOBAL.toYearTextFormatFromRawNoTime(birthday);
		if (!birthday.equals(""))
		{
			SimpleDateFormat raw = new SimpleDateFormat("yyyy-M-d");
			Date birthDate = null;
			try
			{
				birthDate = raw.parse(birthday);
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
			int ageInt;
			Calendar dob = Calendar.getInstance();
			dob.setTime(birthDate);
			Calendar today = Calendar.getInstance();
			// if current month is less than date of birth month
			// or today month == dob month and today <= day of month
			if ((today.get(Calendar.MONTH) > dob.get(Calendar.MONTH))
					|| (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) >= dob
							.get(Calendar.DAY_OF_MONTH)))
			{
				// year --
				ageInt = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
			}
			else
			{
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

	// GETTERS
	protected String getFirstName()
	{
		String[] n = getName().split(" ");
		return n[0];
	}

	protected String getLastName()
	{
		String[] n = getName().split(" ");
		return n[1];
	}

	protected String getLocation()
	{
		return location;
	}

	protected String getBirthday()
	{
		return birthday;
	}

	protected String getBirthdayText()
	{
		return birthdayText;
	}

	protected int getAge()
	{
		return age;
	}

	protected int getNumBadges()
	{
		int size = 0;
		for (Badge b : badges)
			if (b.getLevel() > 0)
				size++;
		return size;
	}

	protected int getNumGroups()
	{
		if (groups != null)
			return groups.size();
		else
			return 0;
	}

	protected int getNumFriendRequests()
	{
		if (friendRequests != null)
			return friendRequests.size();
		else
			return 0;
	}

	protected int getNumGroupInvites()
	{
		if (groupInvites != null)
			return groupInvites.size();
		else
			return 0;
	}

	protected int getNumEventsPending()
	{
		if (eventsPending != null)
			return eventsPending.size();
		else
			return 0;
	}

	protected int getNumEventsDeclined()
	{
		if (eventsDeclined != null)
			return eventsDeclined.size();
		else
			return 0;
	}

	protected int getNumEventsPast()
	{
		if (eventsPast != null)
			return eventsPast.size();
		else
			return 0;
	}

	protected int getNumEventsInvites()
	{
		if (eventInvites != null)
			return eventInvites.size();
		else
			return 0;
	}

	protected int getNumEventsUpcoming()
	{
		if (eventsUpcoming != null)
			return eventsUpcoming.size();
		else
			return 0;
	}

	protected ArrayList<Badge> getBadges()
	{
		return badges;
	}

	protected ArrayList<User> getFriendRequests()
	{
		return friendRequests;
	}

	protected ArrayList<Group> getGroups()
	{
		return groups;
	}

	protected ArrayList<Group> getGroupInvites()
	{
		return groupInvites;
	}

	protected ArrayList<Event> getEventsPending()
	{
		return eventsPending;
	}

	protected ArrayList<Event> getEventsUpcoming()
	{
		return eventsUpcoming;
	}

	protected ArrayList<Event> getEventInvites()
	{
		return eventInvites;
	}

	protected ArrayList<Event> getEventsDeclined()
	{
		return eventsDeclined;
	}

	protected ArrayList<Event> getEventsPast()
	{
		return eventsPast;
	}

	// METHODS
	private void initBadges()
	{
		// adding all badges to the user with level 0
		badges.add(new Badge("Environmentalist", 0, null));
		badges.add(new Badge("Helping Hand", 0, null));
		badges.add(new Badge("Active", 0, null));
		badges.add(new Badge("Social", 0, null));
		badges.add(new Badge("Jack of all Trades", 0, null));
		badges.add(new Badge("Professional", 0, null));
	}

	protected void addToBadges(Badge b)
	{
		for (Badge t : badges)
			if (t.getName().equals(b.getName()))
			{
				t.setLevel(b.getLevel());
			}
	}

	protected void addToFriendRequests(User u)
	{
		boolean inFriendRequests = false;
		for (User t : friendRequests)
			if (t.getEmail().equals(u.getEmail()))
				inFriendRequests = true;
		if (!inFriendRequests) // TODO: this?
			friendRequests.add(u);
	}

	protected void addToGroups(Group g)
	{
		boolean inGroups = false;
		for (Group t : groups)
			if (t.getID() == g.getID())
				inGroups = true;
		if (!inGroups)
			groups.add(g);
	}

	protected void addToGroupInvites(Group g)
	{
		boolean inGroupInvites = false;
		for (Group t : groupInvites)
			if (t.getID() == g.getID())
				inGroupInvites = true;
		if (!inGroupInvites)
			groupInvites.add(g);
	}

	protected void addToEventsDeclined(Event e)
	{
		boolean inEventsDeclined = false;
		for (Event t : eventsDeclined)
			if (t.getID() == e.getID())
				inEventsDeclined = true;
		if (!inEventsDeclined)
			eventsDeclined.add(e);
	}

	protected void addToEventsPending(Event e)
	{
		boolean inEventsPending = false;
		for (Event t : eventsPending)
			if (t.getID() == e.getID())
				inEventsPending = true;
		if (!inEventsPending)
			eventsPending.add(e);
	}

	protected void addToEventsInvites(Event e)
	{
		boolean inEventInvites = false;
		for (Event t : eventInvites)
			if (t.getID() == e.getID())
				inEventInvites = true;
		if (!inEventInvites)
			eventInvites.add(e);
	}

	protected void addToEventsUpcoming(Event e)
	{
		boolean inEventsUpcoming = false;
		for (Event t : eventsUpcoming)
			if (t.getID() == e.getID())
				inEventsUpcoming = true;
		if (!inEventsUpcoming)
			eventsUpcoming.add(e);
	}

	protected void addToEventsPast(Event e)
	{
		boolean inEventsPast = false;
		for (Event t : eventsPast)
			if (t.getID() == e.getID())
				inEventsPast = true;
		if (!inEventsPast)
			eventsPast.add(e);
	}

	protected void addToGroupRoles(int id, String role)
	{
		groupRoles.put(id, role);
	}

	protected void addToEventRoles(int id, String role)
	{
		eventRoles.put(id, role);

	}

	protected String getGroupRole(int id)
	{
		if (groupRoles.get(id) != null)
			return groupRoles.get(id);
		return "U";
	}

	protected String getEventRole(int id)
	{
		if (eventRoles.get(id) != null)
			return eventRoles.get(id);

		return "U";
	}

	// fetches all profile information for the user
	protected int fetchUserInfo()
	{
		AsyncTask<String, Void, String> task = new getUserInfoTask()
				.execute("http://68.59.162.183/android_connect/get_user_info.php");
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
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
		catch (TimeoutException e)
		{
			// TODO Auto-generated catch block
			Log.d("fetchUserInfo", "TIMED OUT INTERNET TO SLOW!");
			e.printStackTrace();
		}
		return 1;
	}

	private class getUserInfoTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			System.out.println("ABOT TO GET USER INFO for " + getEmail());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", getEmail()));

			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				// getting json object from the result string
				JSONObject jsonObject = new JSONObject(result);
				// gotta make a json array
				// json fetch was successful
				if (jsonObject.getString("success").toString().equals("1"))
				{
					JSONArray jsonArray = jsonObject.getJSONArray("userInfo");
					Log.d("getUserInfoOnPost", "success1");
					String fName = (String) jsonArray.get(0);
					String lName = (String) jsonArray.get(1);
					setName(fName + " " + lName);
					Object about = jsonArray.get(3);
					if (about.toString().equals("null"))
						setAbout("");
					else
						setAbout(about.toString());
					// set location
					Object location = jsonArray.get(4);
					if (location.toString().equals("null"))
						setLocation("");
					else
						setLocation(location.toString());
					Object dob = jsonArray.get(2);
					if (dob.toString().equals("null") || dob.toString().equals("0000-00-00"))
						setBirthday("");
					else
						setBirthday(dob.toString());// panda
					Log.d("getUserInfoOnPost", "age: " + age);
				}
				// unsuccessful
				else
				{
					// failed
					Log.d("UserFetchInfoOnPost", "FAILED");
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
			// do next thing here
		}
	}

	protected int fetchFriends()
	{
		AsyncTask<String, Void, String> task = new getFriendsTask()
				.execute("http://68.59.162.183/android_connect/get_friends.php?email=" + getEmail());
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
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
		catch (TimeoutException e)
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
			return GLOBAL.readJSONFeed(urls[0], null);
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
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						// function adds friend to the friends map
						User u = new User(o.getString("email"));
						u.setName(o.getString("first") + " " + o.getString("last"));
						addToUsers(u);
					}
				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("fetchFriends", "failed = 2 return");
				}
			}
			catch (Exception e)
			{
				Log.d("fetchFriends", "exception caught");
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	/*
	 * Should be getting the friendRequest key->vals here
	 */
	protected int fetchFriendRequests()
	{
		AsyncTask<String, Void, String> task = new getFriendRequestsTask();
		task.execute("http://68.59.162.183/android_connect/get_friend_requests.php?receiver=" + getEmail());
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
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
		catch (TimeoutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}

	class getFriendRequestsTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			return GLOBAL.readJSONFeed(urls[0], null);
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
					JSONArray jsonArray = jsonObject.getJSONArray("friendRequests");
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						// at each iteration set to hashmap friendEmail ->
						// 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						// function adds friend to the friends map
						addToFriendRequests(new User(o.getString("email")));
					}
				}
				// user has no friend requests
				if (jsonObject.getString("success").toString().equals("2"))
				{
					// no friend requests
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	/*
	 * should be getting the groups key->vals here
	 */
	protected int fetchGroups()
	{
		AsyncTask<String, Void, String> task = new getGroupsTask()
				.execute("http://68.59.162.183/android_connect/get_groups.php?email=" + getEmail());
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
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
		catch (TimeoutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}

	private class getGroupsTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			return GLOBAL.readJSONFeed(urls[0], null);
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
						JSONObject o = (JSONObject) jsonArray.get(i);
						// function adds friend to the friends map
						Group g = new Group(Integer.parseInt(o.getString("gid")));
						g.setName(o.getString("gname"));
						addToGroups(g);
					}
				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("getGroups", "ERROR WITH JSON");
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	/*
	 * 
	 * should be getting the groupInvites key->vals here
	 */
	// Get numFriends, TODO: work on returning the integer
	protected int fetchGroupInvites()
	{
		AsyncTask<String, Void, String> task = new getGroupInvitesTask()
				.execute("http://68.59.162.183/android_connect/get_group_invites.php?email=" + getEmail());
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
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
		catch (TimeoutException e)
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
			return GLOBAL.readJSONFeed(urls[0], null);
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
						Group g = new Group(Integer.parseInt(o.getString("gid")));
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
			}
			catch (Exception e)
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
	protected int fetchEventsPending()
	{
		AsyncTask<String, Void, String> task = new getEventsPendingTask()
				.execute("http://68.59.162.183/android_connect/get_events_pending.php");
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
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
		catch (TimeoutException e)
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
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", getEmail()));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
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
					JSONArray jsonArray = jsonObject.getJSONArray("eventsPending");
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						// function adds friend to the friends map
						Event e = new Event(o.getInt("e_id"));
						e.setName(o.getString("name"));
						e.setInviter(o.getString("sender"));
						e.setMinPart(o.getInt("minPart"));
						e.setMaxPart(o.getInt("maxPart"));
						e.setStartDate(o.getString("startDate"));
						e.fetchParticipants();
						addToEventsPending(e);
					}
				}
				// user has no group invites
				if (jsonObject.getString("success").toString().equals("2"))
				{
					// no group invites
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	/*
	 * 
	 * should be getting the groupInvites key->vals here
	 */
	// Get numFriends, TODO: work on returning the integer
	protected int fetchEventsDeclined()
	{
		AsyncTask<String, Void, String> task = new getEventsDeclinedTask()
				.execute("http://68.59.162.183/android_connect/get_events_declined.php");
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
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
		catch (TimeoutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// while (task.getStatus() != Status.FINISHED);
		return 1;
	}

	private class getEventsDeclinedTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", getEmail()));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
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
					JSONArray jsonArray = jsonObject.getJSONArray("eventsDeclined");
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						// function adds friend to the friends map
						Event e = new Event(o.getInt("e_id"));
						e.setName(o.getString("name"));
						e.setMinPart(o.getInt("minPart"));
						e.setMaxPart(o.getInt("maxPart"));
						e.setStartDate(o.getString("startDate"));
						e.fetchParticipants();
						addToEventsDeclined(e);
					}
				}
				// user has no group invites
				if (jsonObject.getString("success").toString().equals("2"))
				{
					// no group invites
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	protected int fetchEventsPast()
	{
		AsyncTask<String, Void, String> task = new getEventsPastTask()
				.execute("http://68.59.162.183/android_connect/get_events_past.php");
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
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
		catch (TimeoutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}

	private class getEventsPastTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", getEmail()));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
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
						JSONObject o = (JSONObject) jsonArray.get(i);
						// function adds friend to the friends map
						Event e = new Event(o.getInt("e_id"));
						e.setName(o.getString("name"));
						e.setInviter(o.getString("sender"));
						e.setMinPart(o.getInt("minPart"));
						e.setMaxPart(o.getInt("maxPart"));
						e.setStartDate(o.getString("startDate"));
						e.fetchParticipants();
						addToEventsPast(e);
					}
				}
				// user has no group invites
				if (jsonObject.getString("success").toString().equals("2"))
				{
					// no group invites
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	protected int fetchEventInvites()
	{
		AsyncTask<String, Void, String> task = new getEventsInvitesTask()
				.execute("http://68.59.162.183/android_connect/get_event_invites.php");
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
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
		catch (TimeoutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}

	private class getEventsInvitesTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", getEmail()));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
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
					JSONArray jsonArray = jsonObject.getJSONArray("eventsInvites");
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						Event e = new Event(o.getInt("e_id"));
						e.setName(o.getString("name"));
						e.setInviter(o.getString("sender"));
						e.setMinPart(o.getInt("minPart"));
						e.setMaxPart(o.getInt("maxPart"));
						e.setStartDate(o.getString("startDate"));
						e.fetchParticipants();
						addToEventsInvites(e);
					}
				}
				// user has no group invites
				if (jsonObject.getString("success").toString().equals("2"))
				{
					// no group invites
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	protected int fetchEventsUpcoming()
	{
		AsyncTask<String, Void, String> task = new getEventsUpcomingTask()
				.execute("http://68.59.162.183/android_connect/get_events_upcoming.php");
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
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
		catch (TimeoutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}

	private class getEventsUpcomingTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", getEmail()));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
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
					JSONArray jsonArray = jsonObject.getJSONArray("eventsUpcoming");
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						Event e = new Event(Integer.parseInt(o.getString("eid")));
						e.setName(o.getString("name"));
						e.setStartDate(o.getString("startDate"));
						addToEventsUpcoming(e);
					}
				}
				// user has no group invites
				if (jsonObject.getString("success").toString().equals("2"))
				{

				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	protected int fetchNewBadges()
	{
		AsyncTask<String, Void, String> task = new getNewBadgesTask()
				.execute("http://68.59.162.183/android_connect/get_new_badges.php");
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
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
		catch (TimeoutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}

	private class getNewBadgesTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", getEmail()));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				JSONArray jsonArray = jsonObject.getJSONArray("badges");
				if (jsonObject.getString("success").toString().equals("1"))
				{

					// success
				}

			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	protected int fetchBadges()
	{
		AsyncTask<String, Void, String> task = new getBadgesTask()
				.execute("http://68.59.162.183/android_connect/get_badges.php");
		try
		{
			task.get(10000, TimeUnit.MILLISECONDS);
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
		catch (TimeoutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}

	private class getBadgesTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", getEmail()));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);

				if (jsonObject.getString("success").toString().equals("1"))
				{
					JSONArray jsonArray = jsonObject.getJSONArray("badges");
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						Badge b = new Badge(o.getString("name"), Integer.parseInt(o.getString("level")),
								o.getString("date"));
						addToBadges(b);
					}
				}

			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
}
