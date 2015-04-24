package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import cs460.grouple.grouple.GcmIntentService.CONTENT_TYPE;
import android.support.v4.content.LocalBroadcastManager;
import android.app.Activity;
import android.app.Application;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;


public class DataService extends Service {
	
	private User user;
	//private Group group;
	//private Event event;
	private static Global GLOBAL;
	private String FETCH;

	enum FETCH_TYPE
	{
		INFO, IMAGE, FRIENDS_CURRENT, FRIEND_INVITES, GROUP_INVITES, GROUPS_CURRENT, 
		EVENTS_UPCOMING, EVENTS_PAST, EVENTS_DECLINED, EVENTS_PENDING;
	}
	
	public DataService(Global g, User u) 
	{		
		GLOBAL = g;
		//user to update info of
		user = u;
	}
	
	//FUNCTIONS BELOW
	public void fetchContent(final String FETCH)
	{
		this.FETCH = FETCH;
		if (FETCH.equals(FETCH_TYPE.INFO.toString()))
		{
			new getUserInfoTask().execute("http://68.59.162.183/android_connect/get_user_info.php");
		}
		else if (FETCH.equals(FETCH_TYPE.IMAGE.toString()))
		{
			
		}
		else if (FETCH.equals(FETCH_TYPE.FRIENDS_CURRENT.toString()))
		{
			
		}
		else if (FETCH.equals(FETCH_TYPE.FRIEND_INVITES.toString()))
		{
			
		}
		else if (FETCH.equals(FETCH_TYPE.GROUP_INVITES.toString()))
		{
			
		}
		else if (FETCH.equals(FETCH_TYPE.GROUPS_CURRENT.toString()))
		{
			
		}
		else if (FETCH.equals(FETCH_TYPE.EVENTS_UPCOMING.toString()))
		{
			
		}
		else if (FETCH.equals(FETCH_TYPE.EVENTS_PAST.toString()))
		{
			
		}
		else if (FETCH.equals(FETCH_TYPE.EVENTS_DECLINED.toString()))
		{
			
		}
		else if (FETCH.equals(FETCH_TYPE.EVENTS_PENDING.toString()))
		{
			
		}
	}
	
	//FETCHES BELOW
	//USER FETCHES BELOW

	public class getUserInfoTask extends AsyncTask<String, Void, String>
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
					
					sendBroadcast(DataService.this);
					
					
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
	
	// This function will create an intent. This intent must take as parameter
	// the "unique_name" that you registered your activity with
	private void sendBroadcast(Context context)
	{
		/*Intent intent = null;
		// change intent based on type
		intent = new Intent("USER_DATA");
		if (FETCH.equals(FETCH_TYPE.INFO.toString()))
		{
			//intent.setAction("USER_DATA");
			intent.putExtra("type", FETCH);
		//	intent.setAction(")
			//could potentially add how many things were updated
		}
		// send broadcast
		if (intent != null)
			context.sendBroadcast(intent);*/
	}

	@Override
	public IBinder onBind(Intent arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}


}
