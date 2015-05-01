package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import cs460.grouple.grouple.GcmIntentService.CONTENT_TYPE;
import android.support.v4.content.LocalBroadcastManager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView.ScaleType;

/**
 * 
 * @author Brett, Todd, Scott
 * GroupDataService helps generate group information updates
 * 
 */
public class GroupDataService extends Service {
	
	private User user;
	private Group group;
	//private Group group;
	//private Event event;
	private static Global GLOBAL;
	private String FETCH;
	private Context context;

	enum FETCH_TYPE
	{
		INFO, IMAGE, MEMBERS, EXPERIENCE, EVENTS;
	}
	
	public GroupDataService(Global global, Group g) 
	{		
		GLOBAL = global;
		group = g;
	}
	
	//FUNCTIONS BELOW
	private void sendBroadcast()
	{
		Intent intent = new Intent("group_data");

			//intent.setAction("USER_DATA");
			intent.putExtra("message", "testing");
		//	intent.setAction(")
			//could potentially add how many things were updated


			LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}
	public void fetchContent(final String FETCH, Context context)
	{
		//for access in onpost and beyond
		this.FETCH = FETCH;
		this.context = context;
		
		if (FETCH.equals(FETCH_TYPE.INFO.toString()))
			new getInfoTask().execute("http://68.59.162.183/android_connect/get_group_info.php", Integer.toString(group.getID()));
		else if (FETCH.equals(FETCH_TYPE.IMAGE.toString()))
			new getImageTask().execute("http://68.59.162.183/android_connect/get_profile_image.php");
		else if (FETCH.equals(FETCH_TYPE.MEMBERS.toString()))
			new getMembersTask().execute("http://68.59.162.183/android_connect/get_group_members.php?gid=" + group.getID());	
		else if (FETCH.equals(FETCH_TYPE.EXPERIENCE.toString()))
			new getExperienceTask().execute("http://68.59.162.183/android_connect/get_group_experience.php");
		else if (FETCH.equals(FETCH_TYPE.EVENTS.toString()))
			new getEventsTask().execute("http://68.59.162.183/android_connect/get_group_events.php");

		
	}
	
	private class getInfoTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("gid", urls[1]));
			System.out.println("Finding group with gid = " + urls[1]);
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				System.out.println(result);
				JSONObject jsonObject = new JSONObject(result);
				System.out.println("After declare");
				//successful run
				if (jsonObject.getString("success").toString().equals("1"))
				{
					JSONArray jsonArray = jsonObject.getJSONArray("groupInfo");
					//set group name
					System.out.println("In on post user success 1");
					String name = (String) jsonArray.get(0);
				
					group.setName(name); 
					//set group bio
				
					String about = (String) jsonArray.get(1);
					group.setAbout(about);
					
					//get that image
					
					String creator = (String) jsonArray.get(2);
					group.setEmail(creator);
					
					int pub = (Integer) jsonArray.get(3);
					group.setPub(pub); 
					
					String dateCreated = (String) jsonArray.get(4);
					group.setDateCreated(dateCreated);
				}
				//unsuccessful
				if (jsonObject.getString("success").toString().equals("2"))
				{
					//shouldnt
					System.out.println("success 2 in on post");
				}
			} catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
	// TASK FOR GRABBING IMAGE OF EVENT/USER/GROUP
	private class getImageTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type;
			String id;

			type = "gid";
			id = Integer.toString(group.getID());

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

					group.setImage(image);
					
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
	
	private class getMembersTask extends AsyncTask<String, Void, String>
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
					//gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("gmembers");
					if (group.getUsers() != null) group.getUsers().clear();
					//looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						System.out.println("fetching a group members");
						//at each iteration set to hashmap friendEmail -> 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						//function adds friend to the friends map
						User u = new User(o.getString("email"));
						u.setName(o.getString("first") + " " + o.getString("last"));
						group.addToUsers(u);
						//GLOBAL.addToUsers(u);
					}
					sendBroadcast();
				}
				
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("fetchgrupmembers", "failed = 2 return");
					//setNumFriends(0); //PANDA need to set the user class not global
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}	
	
	// TASK GOR GETTING gropu EXPERIENCE
	private class getExperienceTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("g_id", Integer.toString(group.getID())));
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
					int numParticipants = Integer.parseInt(jsonObject.getString("numParticipants").toString());
					int numProfessional = Integer.parseInt(jsonObject.getString("numProfessional").toString());
					int numSocial = Integer.parseInt(jsonObject.getString("numSocial").toString());
					int numEntertainment = Integer.parseInt(jsonObject.getString("numEntertainment").toString());
					int numFitness = Integer.parseInt(jsonObject.getString("numFitness").toString());
					int numNature = Integer.parseInt(jsonObject.getString("numNature").toString());
					group.setNumParticipants(numParticipants);
					group.setNumFitnessEvents(numFitness);
					group.setNumNatureEvents(numNature);
					group.setNumEntertainmentEvents(numEntertainment);
					group.setNumProfessionalEvents(numProfessional);
					group.setNumSocialEvents(numSocial);
					group.setExperience();
					//TODO: redo this section
					//group.setPoints();
					sendBroadcast();
				}
				else
				{
					// failed
					Log.d("getUserExperience", "FAILED");
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
			// do next thing here
		}
	}
	
	private class getEventsTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("g_id", Integer.toString(group.getID())));
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
					JSONArray upcomingJsonArray = jsonObject.getJSONArray("upcoming");

					for (int i = 0; i < upcomingJsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) upcomingJsonArray.get(i);
						Event e = new Event(o.getInt("e_id"));
						e.setName(o.getString("name"));
						e.setMinPart(o.getInt("minPart"));
						e.setMaxPart(o.getInt("maxPart"));
						group.addToEventsUpcoming(e);
					}
					
					JSONArray pendingJsonArray = jsonObject.getJSONArray("pending");

					for (int i = 0; i < pendingJsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) pendingJsonArray.get(i);
						Event e = new Event(o.getInt("e_id"));
						e.setName(o.getString("name"));
						e.setMinPart(o.getInt("minPart"));
						e.setMaxPart(o.getInt("maxPart"));
						group.addToEventsPending(e);
					}
					
					JSONArray pastJsonArray = jsonObject.getJSONArray("past");

					for (int i = 0; i < pastJsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) pastJsonArray.get(i);
						Event e = new Event(o.getInt("e_id"));
						e.setName(o.getString("name"));
						e.setMinPart(o.getInt("minPart"));
						e.setMaxPart(o.getInt("maxPart"));
						group.addToEventsPast(e);
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

	@Override
	public IBinder onBind(Intent arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}


}
