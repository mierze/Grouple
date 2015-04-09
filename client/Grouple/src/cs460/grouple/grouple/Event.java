 package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class Event extends Entity
{
	private int id;
	private String eventState;
	private String startDate;
	private String startText;
	private String startTextNoTime;
	private String endText;
	private String endDate;
	private String category;
	private int minPart;
	private String location;
	private int maxPart;
	private ArrayList<String> itemList; // will come into play soon
	/*
	 * Constructor for User class
	 */

	protected Event(int id)
	{
		this.id = id;
	}

	protected void setID(int id)
	{
		this.id = id;
	}

	protected void setEventState(String eventState)
	{
		this.eventState = eventState;
	}

	protected void setCategory(String category)
	{
		this.category = category;
	}

	protected void setMinPart(int minPart)
	{
		this.minPart = minPart;
	}

	protected void setMaxPart(int maxPart)
	{
		this.maxPart = maxPart;
	}

	protected void setLocation(String location)
	{
		this.location = location;
	}

	protected void setStartDate(String startDate)
	{
		this.startDate = startDate;
		// string is format from json, parsedate converts
		startText = GLOBAL.toDayTextFormatFromRaw(startDate);
		startTextNoTime = GLOBAL.toNoTimeTextFormatFromRaw(startDate);
	}

	protected void setEndDate(String endDate)
	{
		this.endDate = endDate;
		endText = GLOBAL.toDayTextFormatFromRaw(endDate);
	}

	// getters
	protected int getID()
	{
		return id;
	}

	protected String getEventState()
	{
		return eventState;
	}

	protected String getCategory()
	{
		return category;
	}

	protected int getMinPart()
	{
		return minPart;
	}

	protected int getMaxPart()
	{
		return maxPart;
	}

	protected String getLocation()
	{
		return location;
	}

	protected String getStartDate()
	{
		return startDate;
	}

	protected String getEndDate()
	{
		return endDate;
	}

	protected String getEndText()
	{
		return endText;
	}

	protected String getStartText()
	{
		return startText;
	}
	
	protected String getStartTextNoTime()
	{
		return startTextNoTime;
	}

	/**
	 * 
	 * fetches the user name, bio, and everything
	 * 
	 */
	protected int fetchEventInfo()
	{
		AsyncTask<String, Void, String> task = new getEventInfoTask()
				.execute("http://68.59.162.183/android_connect/get_event_info.php");
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

	private class getEventInfoTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("eid", Integer
					.toString(getID())));
			// ADD ALL NAME VALUE REQuirEMENTS
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
				// JSONArray jsonArray = jsonObject.getJSONArray("userInfo");
				// json fetch was successful
				if (jsonObject.getString("success").toString().equals("1"))
				{
					JSONArray jsonArray = jsonObject.getJSONArray("eventInfo");
					Log.d("getEventInfoOnPost", "success1");
					// $name, $state, $startdate, $enddate, $category, $about,
					// $location, $minpart, $maxpart, $mustbringlist, $creator);
					// at each iteration set to hashmap friendEmail -> 'first
					// last'
					setName((String) jsonArray.get(0));
					setEventState((String) jsonArray.get(1));
					setStartDate((String) jsonArray.get(2));
					setEndDate((String) jsonArray.get(3));
					setCategory((String) jsonArray.get(4));
					setAbout((String) jsonArray.get(5));
					setLocation((String) jsonArray.get(6));
					setMinPart((Integer) jsonArray.get(7));
					setMaxPart((Integer) jsonArray.get(8));
					// 9 = mustbringlist
					setEmail((String) jsonArray.get(10));
					// setImage((String) jsonArray.get(11));
					setPub((Integer) jsonArray.get(12));
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
	 * will be fetching the confirmed participants (email -> name)
	 */
	protected int fetchParticipants()
	{
		AsyncTask<String, Void, String> task = new getParticipantsTask()
				.execute("http://68.59.162.183/android_connect/get_event_participants.php?eid="
						+ getID());
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
		return 1; // return success
	}

	private class getParticipantsTask extends AsyncTask<String, Void, String>
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
					if (getUsers() != null)
						getUsers().clear();
					// gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("eattending");
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						User u = new User(o.getString("email"));
						System.out.println("ADDIGN NEW PARTICIPANT - "
								+ u.getEmail());
						u.setName(o.getString("first") + " "
								+ o.getString("last"));
						addToUsers(u);
					}
				}
				// event has none attending
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("Fetch Event Attending", "failed = 2 return");
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
