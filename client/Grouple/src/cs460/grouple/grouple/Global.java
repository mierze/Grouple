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

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

/*
 * Global holds data that has been loaded into the application
 */
public class Global extends Application
{
	private static ArrayList<User> users = new ArrayList<User>();
	private static ArrayList<Group> groups = new ArrayList<Group>();
	private static ArrayList<Event> events = new ArrayList<Event>();
	private static User currentUser; //contains the current user, is updated on every pertinent activity call
	private static Group groupBuffer;
	private static User userBuffer;
	private static Event eventBuffer;
	private static Context context;
	
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		context = getApplicationContext();
		
	}
	protected static Context getContext()
	{
		return context;
	}
	//SETTERS
	protected void setCurrentUser(User u)
	{
		currentUser = u;
	}
	protected void setUserBuffer(User u)
	{
		userBuffer = u;
	}
	protected void setGroupBuffer(Group g)
	{
		groupBuffer = g;
	}
	protected void setEventBuffer(Event e)
	{
		eventBuffer = e;
	}
	
	//GETTERS
	protected User getCurrentUser()
	{
		return currentUser;
	}
	protected User getUserBuffer()
	{
		return userBuffer;
	}
	protected Group getGroupBuffer()
	{
		return groupBuffer;
	}
	protected Event getEventBuffer()
	{
		return eventBuffer;
	}
	
	//METHODS
	protected boolean isCurrentUser(String email)
	{
		if (getCurrentUser().getEmail().equals(email))
			return true;
		else 
			return false;
	}
	protected int addToUsers(User u)
	{
		int size = users.size();
		if (!containsUser(u.getEmail()))
		{
			users.add(u);
			if (users.size() == size+1)
				return 1;
			else
				return -1;
		}
		else
		{
			//TODO: think over before saving
		}
		return 0;
	}
	protected User getUser(String email)
	{
		for (User u : users)
			if (u.getEmail().equals(email))
				return u;
		return null;
	}
	protected boolean containsUser(String email)
	{
		if (!users.isEmpty())
			for (User u : users)
				if (u.getEmail().equals(email))
					return true;
		return false;
	}
	
	//destroy session is used when logging out to clear all data
	protected int destroySession()
	{
		System.out.println("destroying session...");
		currentUser = null;
		groupBuffer = null;
		userBuffer = null;
		users.clear();
		groups.clear();
		events.clear();
		//Get rid of sharepreferences for token login
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.remove("session_email");
		editor.remove("session_token");
		
		//Get rid of sharepreferences for usersettings
		editor.remove("emailFriendReq");
		editor.remove("emailGroupReq");
		editor.remove("emailEventReq");
		editor.remove("emailFriendMessage");
		editor.remove("emailGroupMessage");
		editor.remove("emailEventMessage");
		editor.remove("emailEventUpcoming");
		editor.remove("emailUmbrella");
		
		editor.remove("androidFriendReq");
		editor.remove("androidGroupReq");
		editor.remove("androidEventReq");
		editor.remove("androidFriendMessage");
		editor.remove("androidGroupMessage");
		editor.remove("androidEventMessage");
		editor.remove("androidEventUpcoming");
		editor.remove("androidUmbrella");
		
		editor.commit();
		System.out.println("session destroyed!");
		return 1;
	}
	
	//returns an umbrella loading icon for switching between activities
	protected Dialog getLoadDialog(Dialog loadDialog)
	{
        loadDialog.getWindow().getCurrentFocus();
        loadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        final Window window = loadDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadDialog.setContentView(R.layout.load);
        loadDialog.setCancelable(false);
        loadDialog.getWindow().setDimAmount(0.7f);
        return loadDialog;
	}
	
	//takes in a message and returns it in the universal toast style for the app
	protected Toast getToast(Context context, String message)
	{
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		return toast;
	}
	
	//asynctasks around the app call this function to get the json return from the server
	protected String readJSONFeed(String URL, List<NameValuePair> nameValuePairs)
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