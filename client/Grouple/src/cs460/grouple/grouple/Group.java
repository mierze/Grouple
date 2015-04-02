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

public class Group extends Entity 
{
	private int id; //id of the group
	private String dateCreated;
	private String dateCreatedText;
	/*
	 * Constructor for Group class
	 */
	public Group(int id)
	{
		this.id = id;
		System.out.println("Initializing new group.");
	}
	
	
	//GETTERS
	public int getID()
	{
		return id;
	}
	protected String getDateCreated()
	{
		return dateCreated;
	}
	protected String getDateCreatedText()
	{
		return dateCreatedText;
	}
	
	
	//SETTERS
	protected void setDateCreated(String dateCreated)
	{
		this.dateCreated = dateCreated;
		// string is format from json, parsedate converts
		dateCreatedText = GLOBAL.toYearTextFormatFromRaw(dateCreated);
	}
	
	
	/*
	 * 
	 * will be fetching the members email->name pairs
	 * 
	 */
	// Get numFriends, TODO: work on returning the integer
	protected int fetchMembers()
	{
		
		AsyncTask<String, Void, String> task = new getMembersTask()
		.execute("http://68.59.162.183/android_connect/get_group_members.php?gid="
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
	
		
		return 1;
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
					if (getUsers() != null) getUsers().clear();
					//looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						System.out.println("fetching a group members");
						//at each iteration set to hashmap friendEmail -> 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						//function adds friend to the friends map
						User u = new User(o.getString("email"));
						u.setName(o.getString("first") + " " + o.getString("last"));
						addToUsers(u);
						//GLOBAL.addToUsers(u);
					}
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
	
	
	/*
	 * 
	 * will be fetching the group info
	 * 
	 */
	// Get numFriends, TODO: work on returning the integer
	protected int fetchGroupInfo()
	{
		AsyncTask<String, Void, String> task = new getGroupInfoTask()
		.execute("http://68.59.162.183/android_connect/get_group_info.php", Integer.toString(getID()));
        
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

	private class getGroupInfoTask extends AsyncTask<String, Void, String>
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
				
					setName(name); 
					//set group bio
				
					String about = (String) jsonArray.get(1);
					setAbout(about);
					
					//get that image
					
					String creator = (String) jsonArray.get(2);
					setEmail(creator);
					
					int pub = (Integer) jsonArray.get(3);
					setPub(pub); 
					
					String dateCreated = (String) jsonArray.get(4);
					setDateCreated(dateCreated);
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
}
