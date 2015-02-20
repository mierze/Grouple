package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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



import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class Group extends Entity 
{
	private int id; //id of the group
	/*
	 * Constructor for Group class
	 */
	public Group(int id)
	{
		this.id = id;
		System.out.println("Initializing new group.");
	}
	
	
	/*
	 * Getters for group class below
	 */
	public int getID()
	{
		return id;
	}


	
	
	/*
	 * 
	 * will be fetching the members email->name pairs
	 * 
	 */
	// Get numFriends, TODO: work on returning the integer
	public int fetchMembers()
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
					JSONArray jsonArray = jsonObject.getJSONArray("gmembers");
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
	public int fetchGroupInfo()
	{
		AsyncTask<String, Void, String> task = new getGroupInfoTask()
		.execute("http://68.59.162.183/android_connect/get_group_info.php", Integer.toString(getID()));
        
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

	private class getGroupInfoTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("gid", urls[1]));
			System.out.println("Finding group with gid = " + urls[1]);
			return readJSONFeed(urls[0], nameValuePairs);
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
					JSONArray jsonArray = jsonObject.getJSONArray("profile");
					//set group name
					System.out.println("In on post user success 1");
					String name = (String) jsonArray.get(0);
				
					setName(name); 
					//set group bio
				
					String about = (String) jsonArray.get(1);
					setAbout(about);
					
					//get that image niggi
					String image = (String) jsonArray.get(2);
					setImage(image);
					
					String creator = (String) jsonArray.get(3);
					setEmail(creator);
					
					int pub = (Integer) jsonArray.get(4);
					setPub(pub); 
					
					

				}
				//unsuccessful
				if (jsonObject.getString("success").toString().equals("2"))
				{
					//shouldnt
					System.out.println("success 2 in on post");
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	public String readJSONFeed(String URL, List<NameValuePair> nameValuePairs)
	{
		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();

		if (nameValuePairs == null)
		{
			HttpGet httpGet = new HttpGet(URL);

			try
			{
				HttpResponse response = httpClient.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200)
				{
					HttpEntity entity = response.getEntity();
					InputStream inputStream = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(inputStream));
					String line;
					while ((line = reader.readLine()) != null)
					{
						System.out.println("New line: " + line);
						stringBuilder.append(line);
					}
					inputStream.close();
				} else
				{
					Log.d("JSON", "Failed to download file");
				}
			} catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}

		}

		else
		{
			HttpPost httpPost = new HttpPost(URL);
			try
			{
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpClient.execute(httpPost);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200)
				{
					HttpEntity entity = response.getEntity();
					InputStream inputStream = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(inputStream));
					String line;
					while ((line = reader.readLine()) != null)
					{
						stringBuilder.append(line);
					}
					inputStream.close();
				} else
				{
					Log.d("JSON", "Failed to download file");
				}
			} catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}
		}
		return stringBuilder.toString();
	}
}
