package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;


public class Event extends Entity
{
	private int id;
	private String eventState;
	private String startDate;
	private String endDate;
	private String category;
	private int minPart;
	private String location;
	private int maxPart;
	private ArrayList<String> itemList; //will come into play soon
	
	/*
	 * Constructor for User class
	 */
	public Event(int id)
	{
		this.id = id;
	}
	
	public void setID(int id)
	{
		this.id = id;
	}
	public void setEventState(String eventState)
	{
		this.eventState = eventState;
	}
	public void setCategory(String category)
	{
		this.category = category;
	}
	public void setMinPart(int minPart)
	{
		this.minPart = minPart;
	}
	public void setMaxPart(int maxPart)
	{
		this.maxPart = maxPart;
	}
	public void setLocation(String location)
	{
		this.location = location;
	}
	public void setStartDate(String startDate)
	{
		//string is format from json, parsedate converts
		this.startDate = parseDate(startDate);
	}
	public void setEndDate(String endDate)
	{
		this.endDate = parseDate(endDate);
	}
	private String parseDate(String dateString)
	{
		System.out.println("\n\nDATE IS FIRST: " + dateString);
		String date = "";
        SimpleDateFormat raw = new SimpleDateFormat("yyyy-M-d h:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, h:mma");
        try
        {
    		Date parsedDate = (Date) raw.parse(dateString);
    		date = dateFormat.format(parsedDate); 
    		//date = raw.format(parsedDate);   
    		System.out.println("\nDATE IN RAW TRANSLATION: " + raw.format(parsedDate));
    		System.out.println("\nDATE IN FINAL: " + dateFormat.format(parsedDate) + "\n\n");
        }
        catch (ParseException ex)
        {
            System.out.println("Exception "+ex);
        }
		return date;
	}
	
	//getters
	public int getID()
	{
		return id;
	}
	public String getEventState()
	{
		return eventState;
	}
	public String getCategory()
	{
		return category;
	}
	public int getMinPart()
	{
		return minPart;
	}
	public int getMaxPart()
	{
		return maxPart;
	}
	public String getLocation()
	{
		return location;
	}
	public String getStartDate()
	{
		return startDate;
	}
	public String getEndDate()
	{
		return endDate;
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
	public int fetchEventInfo()
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
		
		//while (task.getStatus() != Status.FINISHED);
		return 1;
	}
		

	private class getEventInfoTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("eid", Integer.toString(getID())));
			//ADD ALL NAME VALUE REQuirEMENTS
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
					JSONArray jsonArray = jsonObject.getJSONArray("eventInfo");
					Log.d("getEventInfoOnPost", "success1");
					//$name, $state, $startdate, $enddate, $category, $about, $location, $minpart, $maxpart, $mustbringlist, $creator);
					//at each iteration set to hashmap friendEmail -> 'first last'

					setName((String) jsonArray.get(0));
					setEventState((String) jsonArray.get(1));
					setStartDate((String) jsonArray.get(2));
					setEndDate((String) jsonArray.get(3));
					setCategory((String) jsonArray.get(4));
					setAbout((String) jsonArray.get(5));
					setLocation((String) jsonArray.get(6));						
					setMinPart((Integer) jsonArray.get(7));	
					setMaxPart((Integer) jsonArray.get(8));	
					//9 = mustbringlist
					setEmail((String) jsonArray.get(10));		
					setImage((String) jsonArray.get(11));
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
	 * will be fetching the confirmed participants (email -> name)
	 * 
	 */
	public int fetchParticipants()
	{
		AsyncTask<String, Void, String> task = new getParticipantsTask()
		.execute("http://68.59.162.183/android_connect/get_event_participants.php?eid="+ getID());
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
		return 1; //return success
	}

	private class getParticipantsTask extends AsyncTask<String, Void, String>
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
					JSONArray jsonArray = jsonObject.getJSONArray("eattending");
					
					//looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						User u = new User(o.getString("email"));
						System.out.println("ADDIGN NEW PARTICIPANT - " + u.getEmail());
						u.setName(o.getString("first") + " " + o.getString("last"));
						addToUsers(u);
					}
				}
				
				//event has none attending
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("Fetch Event Attending", "failed = 2 return");
					//setNumFriends(0); //PANDA need to set the user class not global
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}	
}
