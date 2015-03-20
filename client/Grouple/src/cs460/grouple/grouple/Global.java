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
		//Get rid of sharepreferences for token login
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.remove("session_email");
		editor.remove("session_token");
		editor.commit();
		return 1;
	}
	
	public Dialog getLoadDialog(Dialog loadDialog)
	{
		
		//new Dialog(this);
        loadDialog.getWindow().getCurrentFocus();
        loadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
       // View v = li.inflate(R.layout.load, null);
       // ImageView loadImage = (ImageView) v.findViewById(R.id.loadIconImageView);
       // loadImage.startAnimation( 
        	 //   AnimationUtils.loadAnimation(this, R.anim.rotate));
        final Window window = loadDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
       // window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadDialog.setContentView(R.layout.load);
        loadDialog.setCancelable(false);
        loadDialog.getWindow().setDimAmount(0.7f);
        return loadDialog;
	}
	public Toast getToast(Context context, String message)
	{
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		return toast;
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
