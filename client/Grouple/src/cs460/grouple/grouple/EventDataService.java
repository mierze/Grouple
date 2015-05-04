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
import android.view.View;
import android.widget.ImageView.ScaleType;

/**
 * 
 * @author Brett, Todd, Scott EventDataService helps generate event information
 *         updates
 * 
 */
public class EventDataService extends Service
{

	private Event event;
	// private Group group;
	// private Event event;
	private static Global GLOBAL;
	private String fetch;
	private Context context;

	enum FETCH_TYPE
	{
		INFO, IMAGE, PARTICIPANTS, ITEMS;
	}

	public EventDataService(Global global, Event e)
	{
		GLOBAL = global;
		// user to update info of
		event = e;
	}

	// FUNCTIONS BELOW
	private void sendBroadcast()
	{
		Intent intent = new Intent("event_data");

		// intent.setAction("USER_DATA");
		intent.putExtra("message", "testing");
		// intent.setAction(")
		// could potentially add how many things were updated

		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

	public void fetchContent(final String fetch, Context context)
	{
		// for access in onpost and beyond
		this.fetch = fetch;
		this.context = context;
		if (fetch.equals(FETCH_TYPE.INFO.toString()))
			new getInfoTask().execute("http://68.59.162.183/android_connect/get_event_info.php");
		else if (fetch.equals(FETCH_TYPE.IMAGE.toString()))
			new getImageTask().execute("http://68.59.162.183/android_connect/get_profile_image.php");
		else if (fetch.equals(FETCH_TYPE.PARTICIPANTS.toString()))
			new getParticipantsTask().execute("http://68.59.162.183/android_connect/get_event_participants.php?eid="
					+ event.getID());
		else if (fetch.equals(FETCH_TYPE.ITEMS.toString()))
			new getItemsTask().execute("http://68.59.162.183/android_connect/get_items_tobring.php");
	}

	// task for grabbing group info
	private class getInfoTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("eid", Integer.toString(event.getID())));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				// getting json object from the result string
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					JSONArray jsonArray = jsonObject.getJSONArray("eventInfo");
					event.setName((String) jsonArray.get(0));
					event.setEventState((String) jsonArray.get(1));
					event.setStartDate((String) jsonArray.get(2));
					event.setEndDate((String) jsonArray.get(3));
					event.setRecurringType((String) jsonArray.getString(4));
					event.setCategory((String) jsonArray.get(5));
					event.setAbout((String) jsonArray.get(6));
					event.setLocation((String) jsonArray.get(7));
					event.setMinPart((Integer) jsonArray.get(8));
					event.setMaxPart((Integer) jsonArray.get(9));
					event.setEmail((String) jsonArray.get(10));
					event.setPub((Integer) jsonArray.get(11));
					sendBroadcast();
				}
				// unsuccessful
				else
				{
					// failed
					Log.d("eventFetchInfoOnPost", "FAILED");
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
			// do next thing here
		}
	}

	// task for grabbing image
	private class getImageTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String id = Integer.toString(event.getID());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("eid", id));
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
					event.setImage(image);
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
				event.getUsers().clear();
				if (jsonObject.getString("success").toString().equals("1"))
				{
					JSONArray jsonArray = jsonObject.getJSONArray("eattending");
					// looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						User u = GLOBAL.getUser(o.getString("email"));
						System.out.println("ADDIGN NEW PARTICIPANT - " + u.getEmail());
						u.setName(o.getString("first") + " " + o.getString("last"));
						event.addToUsers(u);
					}
					sendBroadcast();
				}
				// event has none attending
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("Fetch Event Attending", "failed = 2 return");
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	private class getItemsTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("e_id", Integer.toString(event.getID())));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				event.getItems().clear();
				if (jsonObject.getString("success").toString().equals("1"))
				{
					JSONArray jsonArray = jsonObject.getJSONArray("itemsToBring");
					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						EventItem item = new EventItem(Integer.parseInt(o.getString("id")), o.getString("name"),
								o.getString("email"));
						event.addToItems(item);
					}
					sendBroadcast();
				}
				// success, but no items returned
				else if (jsonObject.getString("success").toString().equals("2"))
				{

				}
				else
				{
					System.out.println("\"" + jsonObject.getString("success").toString() + "\"");
					Log.d("fetchItems", "failed to return success");
				}
			}
			catch (Exception e)
			{
				Log.d("fetchItems", "exception caught");
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
