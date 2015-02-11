package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
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
import org.json.JSONObject;
import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/*
 * Global stores user values needed for notifications.
 */
public class Global extends Application
{
	private static User currentUser; //contains the current user, is updated on every pertinent activity call
	private static Group groupBuffer;
	private static User userBuffer;
	private static Event eventBuffer;
	
	/*
	 * Adds a user to the users arraylist
	 */
	public void setCurrentUser(User u)
	{
		currentUser = u;
	}
	
	public User getCurrentUser()
	{
		return currentUser;
	}
	public void setGroupBuffer(Group g)
	{
		groupBuffer = g;
	}
	public Group getGroupBuffer()
	{
		return groupBuffer;
	}
	public void setUserBuffer(User u)
	{
		userBuffer = u;
	}
	public void setEventBuffer(Event e)
	{
		eventBuffer = e;
	}
	public User getUserBuffer()
	{
		return userBuffer;
	}
	public Event getEventBuffer()
	{
		return eventBuffer;
	}
	public boolean isCurrentUser(String email)
	{
		if (getCurrentUser().getEmail().equals(email))
			return true;
		else 
			return false;
	}
	
	public int destroySession()
	{
		currentUser = null;
		groupBuffer = null;
		userBuffer = null;
		return 1;
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
				} 
				else
				{
					Log.d("JSON", "Failed to download file");
				}
			} 
			catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}
		}
		return stringBuilder.toString();
	}//end readJSONFeed
}//end Global class
