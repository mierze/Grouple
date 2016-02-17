package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * @author Brett, Todd, Scott UserDataService fetches user data from our server
 */
public class UserDataService extends Service
{
	private User user;
	private static Global GLOBAL;
	private String fetch;
	private String extra;
	private Context context;

	enum fetch_TYPE
	{
		INFO, IMAGE, FRIENDS_CURRENT, FRIEND_INVITES, GROUP_INVITES, GROUPS_CURRENT, EVENTS_UPCOMING, EVENTS_PAST, EVENTS_DECLINED, EVENTS_PENDING, EVENT_INVITES, BADGES_NEW, BADGES, EXPERIENCE, CONTACTS, MESSAGES;
	}

	public UserDataService(Global g, User u)
	{
		GLOBAL = g;
		// user to update info of
		user = u;
	}

	// FUNCTIONS BELOW
	private void sendBroadcast()
	{
		Intent intent = new Intent("user_data");

		// intent.setAction("USER_DATA");
		intent.putExtra("message", "testing");
		// intent.setAction(")
		// could potentially add how many things were updated

		if (context != null)
			LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

	public void fetchContent(final String fetch, Context context, String extra)
	{
		// for access in onpost and beyond
		this.fetch = fetch;
		this.context = context;
		this.extra = extra;

		if (fetch.equals(fetch_TYPE.INFO.toString()))
			new getInfoTask().execute("http://mierze.gear.host/grouple/android_connect/get_user_info.php");
		else if (fetch.equals(fetch_TYPE.IMAGE.toString()))
			new getImageTask().execute("http://mierze.gear.host/grouple/android_connect/get_profile_image.php");
		else if (fetch.equals(fetch_TYPE.FRIENDS_CURRENT.toString()))
			new getFriendsTask().execute("http://mierze.gear.host/grouple/android_connect/get_friends.php?email="
					+ user.getEmail());
		else if (fetch.equals(fetch_TYPE.FRIEND_INVITES.toString()))
			new getFriendInvitesTask().execute("http://mierze.gear.host/grouple/android_connect/get_friend_requests.php?receiver="
					+ user.getEmail());
		else if (fetch.equals(fetch_TYPE.GROUPS_CURRENT.toString()))
			new getGroupsTask().execute("http://mierze.gear.host/grouple/android_connect/get_groups.php?email=" + user.getEmail());
		else if (fetch.equals(fetch_TYPE.GROUP_INVITES.toString()))
			new getGroupInvitesTask().execute("http://mierze.gear.host/grouple/android_connect/get_group_invites.php?email="
					+ user.getEmail());
		else if (fetch.equals(fetch_TYPE.EVENTS_UPCOMING.toString()))
			new getEventsUpcomingTask().execute("http://mierze.gear.host/grouple/android_connect/get_events_upcoming.php");
		else if (fetch.equals(fetch_TYPE.EVENTS_PAST.toString()))
			new getEventsPastTask().execute("http://mierze.gear.host/grouple/android_connect/get_events_past.php");
		else if (fetch.equals(fetch_TYPE.EVENTS_DECLINED.toString()))
			new getEventsDeclinedTask().execute("http://mierze.gear.host/grouple/android_connect/get_events_declined.php");
		else if (fetch.equals(fetch_TYPE.EVENTS_PENDING.toString()))
			new getEventsPendingTask().execute("http://mierze.gear.host/grouple/android_connect/get_events_pending.php");
		else if (fetch.equals(fetch_TYPE.EVENT_INVITES.toString()))
			new getEventsInvitesTask().execute("http://mierze.gear.host/grouple/android_connect/get_event_invites.php");
		else if (fetch.equals(fetch_TYPE.BADGES.toString()))
			new getBadgesTask().execute("http://mierze.gear.host/grouple/android_connect/get_badges.php");
		else if (fetch.equals(fetch_TYPE.BADGES_NEW.toString()))
			new getNewBadgesTask().execute("http://mierze.gear.host/grouple/android_connect/get_new_badges.php");
		else if (fetch.equals(fetch_TYPE.EXPERIENCE.toString()))
			new getExperienceTask().execute("http://mierze.gear.host/grouple/android_connect/get_user_experience.php");
		else if (fetch.equals(fetch_TYPE.CONTACTS.toString()))
			new getContactsTask().execute("http://mierze.gear.host/grouple/android_connect/get_recent_contacts.php");
		else if (fetch.equals(fetch_TYPE.MESSAGES.toString()))
			new getMessagesTask().execute("http://mierze.gear.host/grouple/android_connect/get_messages.php", extra);
	}

	// FETCHES ARE BELOW
	private class getInfoTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
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
					user.setName(fName + " " + lName);
					Object about = jsonArray.get(3);
					if (about.toString().equals("null"))
						user.setAbout("");
					else
						user.setAbout(about.toString());
					// set location
					Object location = jsonArray.get(4);
					if (location.toString().equals("null"))
						user.setLocation("");
					else
						user.setLocation(location.toString());
					Object dob = jsonArray.get(2);
					if (dob.toString().equals("null") || dob.toString().equals("0000-00-00"))
						user.setBirthday("");
					else
						user.setBirthday(dob.toString());// panda

					Object gender = jsonArray.get(5);
					if (!gender.toString().equals("null"))
					{
						user.setGender(gender.toString());
					}

					sendBroadcast();
				}
				// unsuccessful
				else
				{
					// failed
					Log.d("UserFetchInfoOnPost", "FAILED");
				}
			}
			catch (NullPointerException e)
			{
				System.out.println("SATACK TRACE:\n" + e.getStackTrace().toString() + "\n\n\ncause:\n" + e.getCause());
				System.out.println(e.getMessage());
				System.out.println(e.getLocalizedMessage());

			}
			catch (Exception e)
			{
				System.out.println("SATACK TRACE:\n" + e.getStackTrace().toString() + "\n\n\ncause:\n" + e.getCause());
				System.out.println(e.getMessage());
				System.out.println(e.getLocalizedMessage());
			}
			// do next thing here
		}
	}

	private class getFriendsTask extends AsyncTask<String, Void, String>
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
				user.getUsers().clear();
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("friends");
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						// function adds friend to the friends map
						User u = GLOBAL.getUser(o.getString("email"));
						u.setName(o.getString("first") + " " + o.getString("last"));
						user.addToUsers(u);
					}
				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("fetchFriends", "failed = 2 return");
				}
				sendBroadcast();
			}
			catch (Exception e)
			{
				Log.d("fetchFriends", "exception caught");
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	private class getFriendInvitesTask extends AsyncTask<String, Void, String>
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
				user.getFriendRequests().clear();
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
						User u = GLOBAL.getUser(o.getString("email"));
						user.addToFriendRequests(u);
					}

				}
				// user has no friend requests
				if (jsonObject.getString("success").toString().equals("2"))
				{
					// no friend requests
				}
				sendBroadcast();
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
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
				user.getGroups().clear();
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("groups");
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						// function adds friend to the friends map
						Group g = GLOBAL.getGroup(Integer.parseInt(o.getString("gid")));
						g.setName(o.getString("gname"));
						user.addToGroups(g);
					}
				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("getGroups", "ERROR WITH JSON");
				}
				sendBroadcast();
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
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
				user.getGroupInvites().clear();
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
						Group g = GLOBAL.getGroup(Integer.parseInt(o.getString("gid")));
						g.setName(o.getString("gname"));
						g.setInviter(o.getString("sender"));
						user.addToGroupInvites(g);
					}

				}
				// user has no group invites
				if (jsonObject.getString("success").toString().equals("2"))
				{
					// setNumFriends(0); //PANDA need to set the user class not
					// global
				}
				sendBroadcast();
			}
			catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	private class getEventsUpcomingTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				user.getEventsUpcoming().clear();
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("eventsUpcoming");

					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						Event e = GLOBAL.getEvent(Integer.parseInt(o.getString("eid")));
						e.setName(o.getString("name"));
						e.setStartDate(o.getString("startDate"));
						user.addToEventsUpcoming(e);
					}
				}
				// user has no group invites
				if (jsonObject.getString("success").toString().equals("2"))
				{

				}
				sendBroadcast();
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	private class getEventsPendingTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				user.getEventsPending().clear();
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("eventsPending");
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						// function adds friend to the friends map
						Event e = GLOBAL.getEvent(o.getInt("e_id"));
						e.setName(o.getString("name"));
						e.setInviter(o.getString("sender"));
						e.setMinPart(o.getInt("minPart"));
						e.setMaxPart(o.getInt("maxPart"));
						e.setStartDate(o.getString("startDate"));
						// TODO: may bneed to get the number of participants
						// here
						// e.fetchParticipants();
						user.addToEventsPending(e);
					}
				}
				// user has no group invites
				if (jsonObject.getString("success").toString().equals("2"))
				{
					// no group invites
				}
				sendBroadcast();
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	private class getEventsDeclinedTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				user.getEventsDeclined().clear();
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("eventsDeclined");
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						// function adds friend to the friends map
						Event e = GLOBAL.getEvent(o.getInt("e_id"));
						e.setName(o.getString("name"));
						e.setMinPart(o.getInt("minPart"));
						e.setMaxPart(o.getInt("maxPart"));
						e.setStartDate(o.getString("startDate"));
						// TODO: may need to fetch in more data here
						// e.fetchParticipants();
						user.addToEventsDeclined(e);
					}
				}
				// user has no group invites
				if (jsonObject.getString("success").toString().equals("2"))
				{
					// no group invites
				}
				sendBroadcast();
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	private class getEventsPastTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				user.getEventsPast().clear();
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("eventsPast");
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						// function adds friend to the friends map
						Event e = GLOBAL.getEvent(o.getInt("e_id"));
						e.setName(o.getString("name"));
						e.setInviter(o.getString("sender"));
						e.setMinPart(o.getInt("minPart"));
						e.setMaxPart(o.getInt("maxPart"));
						e.setStartDate(o.getString("startDate"));
						// TODO: may need to fetch in more data here
						// e.fetchParticipants();
						user.addToEventsPast(e);
					}
				}
				// user has no group invites
				if (jsonObject.getString("success").toString().equals("2"))
				{
					// no group invites
				}
				sendBroadcast();
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	private class getEventsInvitesTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				user.getEventInvites().clear();
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// gotta make a json array

					JSONArray jsonArray = jsonObject.getJSONArray("eventsInvites");

					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						Event e = GLOBAL.getEvent(o.getInt("e_id"));
						e.setName(o.getString("name"));
						e.setInviter(o.getString("sender"));
						e.setMinPart(o.getInt("minPart"));
						e.setMaxPart(o.getInt("maxPart"));
						e.setStartDate(o.getString("startDate"));
						// TODO: may need to fetch in more data here
						// e.fetchParticipants();
						user.addToEventsInvites(e);
					}
				}
				// user has no group invites
				if (jsonObject.getString("success").toString().equals("2"))
				{
					// no group invites
				}
				sendBroadcast();
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	private class getExperienceTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
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

					int numTotalEvents = jsonObject.getInt("numTotal");
					int numSocialEvents = jsonObject.getInt("numSocial");
					int numEntertainmentEvents = jsonObject.getInt("numEntertainment");
					int numProfessionalEvents = jsonObject.getInt("numProfessional");
					int numFitnessEvents = jsonObject.getInt("numFitness");
					int numNatureEvents = jsonObject.getInt("numNature");
					int numTotalEventsCreated = jsonObject.getInt("numTotalCreate");
					int numSocialEventsCreated = jsonObject.getInt("numSocialCreate");
					int numEntertainmentEventsCreated = jsonObject.getInt("numEntertainmentCreate");
					int numProfessionalEventsCreated = jsonObject.getInt("numProfessionalCreate");
					int numFitnessEventsCreated = jsonObject.getInt("numFitnessCreate");
					int numNatureEventsCreated = jsonObject.getInt("numNatureCreate");
					int numItemsBrought = jsonObject.getInt("numItems");

					// TODO: get count in each type
					user.setNumTotalEvents(numTotalEvents);
					user.setNumSocialEvents(numSocialEvents);
					user.setNumEntertainmentEvents(numEntertainmentEvents);
					user.setNumProfessionalEvents(numProfessionalEvents);
					user.setNumFitnessEvents(numFitnessEvents);
					user.setNumNatureEvents(numNatureEvents);
					user.setNumTotalEventsCreated(numTotalEventsCreated);
					user.setNumSocialEventsCreated(numSocialEventsCreated);
					user.setNumEntertainmentEventsCreated(numEntertainmentEventsCreated);
					user.setNumProfessionalEventsCreated(numProfessionalEventsCreated);
					user.setNumFitnessEventsCreated(numFitnessEventsCreated);
					user.setNumNatureEventsCreated(numNatureEventsCreated);
					user.setNumItemsBrought(numItemsBrought);
					user.setExperience();

					sendBroadcast();
				}

			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	private class getNewBadgesTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
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
					user.getNewBadges().clear();
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						Badge b = new Badge(o.getString("name"), null);
						b.setLevel(o.getInt("level"));
						user.addToNewBadges(b);
					}
					sendBroadcast();
				}

			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	private class getBadgesTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
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
						Badge b = new Badge(o.getString("name"), o.getString("date"));
						b.setLevel(Integer.parseInt(o.getString("level")));
						user.addToBadges(b);
					}
					sendBroadcast();
				}

			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	private class getImageTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type = "email";
			String id = user.getEmail();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair(type, id));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				// json fetch was successful
				if (jsonObject.getString("success").toString().equals("1"))
				{
					String image = jsonObject.getString("image").toString();

					user.setImage(image);

					sendBroadcast();
				}
				else
				{
					// failed
					Log.d("fetchImage", "FAILED");
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
			// do next thing here
		}
	}

	private class getContactsTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
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
					ArrayList<Contact> contacts = user.getContacts();
					// gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("contacts");
					contacts.clear();
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						Contact contact = new Contact(o.getString("message"), o.getString("senddate"),
								o.getString("sender"), o.getString("first") + " " + o.getString("last"),
								o.getString("receiver"), o.getString("read_date"));
						contact.setID(Integer.parseInt(o.getString("id")));
						contact.setImage(o.getString("image"));

						user.addToContacts(contact);
					}
					// done fetching, now populate to scrollview
					sendBroadcast();
				}
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("fetchRecentContacts", "failed = 2 return");
				}
			}
			catch (Exception e)
			{
				Log.d("fetchRecentContacts", "exception caught");
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	private class getMessagesTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String sender = urls[1];
			String receiver = user.getEmail();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("sender", sender));
			nameValuePairs.add(new BasicNameValuePair("receiver", receiver));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (user.getMessages(extra) != null) user.getMessages(extra).clear();
				if (jsonObject.getString("success").toString().equals("1"))
				{
					JSONArray jsonArray = jsonObject.getJSONArray("messages");
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						Message m = new Message(o.getString("message"), o.getString("send_date"),
								o.getString("sender"), "name", o.getString("receiver"), null);
						user.addToMessages(extra, m); // adding message to
														// message array
					}
				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("fetchMessages", "failed = 2 return");
				}
				if (jsonObject.getString("success").toString().equals("0"))
				{

				}
				sendBroadcast();
			}
			catch (Exception e)
			{
				Log.d("fetchMessages", "exception caught");
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	@Override
	public IBinder onBind(Intent arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
