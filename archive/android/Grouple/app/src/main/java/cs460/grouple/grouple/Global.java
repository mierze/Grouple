package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * 
 * @author Brett, Todd, Scott 
 * Global holds data that has been loaded into the application.
 * Is accessible throughout app for some global functions as well.
 */
@SuppressLint("SimpleDateFormat") public class Global extends Application
{
	// all users in system
	private static ArrayList<User> users = new ArrayList<User>(); 
	// all groups in system
	private static ArrayList<Group> groups = new ArrayList<Group>();
	// all events in system
	private static ArrayList<Event> events = new ArrayList<Event>();
	//contains a copy of the current user from the users array
	private static User currentUser; 

	protected void login(String email, Context context)
	{
		User u = new User(email);
		u.fetchInfo(context);
		u.fetchFriendRequests(context);
		u.fetchFriends(context);
		u.fetchGroups(context);
		u.fetchGroupInvites(context);
		u.fetchEventInvites(context);
		u.fetchEventsUpcoming(context);
		u.fetchEventsPast(context);
		u.fetchEventsDeclined(context);
		u.fetchEventsPending(context);
		u.fetchExperience(context);
		u.fetchBadges(context);
		setCurrentUser(u);
		users.add(u);
	}

	// SETTERS
	protected void setCurrentUser(User u)
	{
		currentUser = u;
	}
	
	// GETTERS
	protected User getCurrentUser()
	{
		return currentUser;
	}
	
	protected User getUser(String email)
	{
		for (User u : users)
			if (u.getEmail().equals(email))
				return u;
		User u = new User(email);
		users.add(u);
		return u;
	}

	protected Group getGroup(int id)
	{
		for (Group g : groups)
			if (g.getID() == id)
				return g;
		Group g = new Group(id);
		groups.add(g);
		return g;
	}

	protected Event getEvent(int id)
	{
		for (Event e : events)
			if (e.getID() == id)
				return e;
		Event e = new Event(id);
		events.add(e);
		return e;
	}

	// METHODS
	protected boolean isCurrentUser(String email)
	{
		if (getCurrentUser().getEmail().equals(email))
			return true;
		else
			return false;
	}
	
	protected boolean containsEvent(int id)
	{
		for (Event e : events)
			if (e.getID() == id)
				return true;
		return false;
	}

	protected boolean containsGroup(int id)
	{
		for (Group g : groups)
			if (g.getID() == id)
				return true;
		return false;
	}

	protected boolean containsUser(String email)
	{
		for (User u : users)
			if (u.getEmail().equals(email))
				return true;
		return false;
	}

	// destroy session is used when logging out to clear all data
	protected int destroySession()
	{
		System.out.println("destroying session...");
		currentUser = null;
		users.clear();
		groups.clear();
		events.clear();
		// Get rid of sharepreferences for token login
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.remove("session_email");
		editor.remove("session_token");
		// Get rid of sharepreferences for usersettings
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

	// returns an umbrella loading icon for switching between activities
	protected Dialog getLoadDialog(Dialog loadDialog)
	{
		loadDialog.getWindow().getCurrentFocus();
		loadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		final Window window = loadDialog.getWindow();
		window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		loadDialog.setContentView(R.layout.dialog_load);
		loadDialog.setCancelable(false);
		loadDialog.getWindow().setDimAmount(0.7f);
		return loadDialog;
	}

	// takes in a message and returns it in the universal toast style for the
	// app
	@SuppressLint("ShowToast") protected Toast getToast(Context context, String message)
	{
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		return toast;
	}

	// asynctasks around the app call this function to get the json return from the server
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
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
					String line;
					while ((line = reader.readLine()) != null)
					{
						System.out.println("New line: " + line);
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
		else
		{
			HttpPost httpPost = new HttpPost(URL);
			try
			{
				UrlEncodedFormEntity form;
				// if dealing with messaging, allow for emojis
				if (URL.compareTo("http://mierze.gear.host/grouple/android_connect/send_message.php") == 0
						|| URL.compareTo("http://mierze.gear.host/grouple/android_connect/send_group_message.php") == 0
						|| URL.compareTo("http://mierze.gear.host/grouple/android_connect/send_event_message.php") == 0)
				{
					System.out.println("enabling UTF_8");
					form = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
					form.setContentEncoding(HTTP.UTF_8);
				}
				else
				{
					form = new UrlEncodedFormEntity(nameValuePairs);
				}

				httpPost.setEntity(form);

				HttpResponse response = httpClient.execute(httpPost);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200)
				{
					HttpEntity entity = response.getEntity();
					InputStream inputStream = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
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
	}// end readJSONFeed

	// DATE FORMATTING METHODS BELOW
	protected String toRawFormatFromDayText(String date)
	{
		String rawDate = "";
		SimpleDateFormat raw = new SimpleDateFormat("yyyy-M-d h:mm:ss");
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, h:mma");
		try
		{
			Date parsedDate = (Date) dateFormat.parse(date);
			rawDate = raw.format(parsedDate);
		}
		catch (ParseException ex)
		{
			System.out.println("Exception " + ex);
		}
		return rawDate;
	}
	protected String toNoTimeTextFormatFromRaw(String dateString)
	{
		String date = "";
		SimpleDateFormat raw = new SimpleDateFormat("yyyy-M-d h:mm:ss");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try
		{
			Date parsedDate = (Date) raw.parse(dateString);
			date = dateFormat.format(parsedDate);
		}
		catch (ParseException ex)
		{
			System.out.println("Exception " + ex);
		}
		return date;
	}
	protected String toDayTextFormatFromRaw(String dateString)
	{
		String date = "";
		SimpleDateFormat raw = new SimpleDateFormat("yyyy-M-d h:mm:ss");
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, h:mma");
		try
		{
			Date parsedDate = (Date) raw.parse(dateString);
			date = dateFormat.format(parsedDate);
		}
		catch (ParseException ex)
		{
			System.out.println("Exception " + ex);
		}
		return date;
	}
	protected String toDayTextFormatFromRawNoSeconds(String dateString)
	{
		String date = "";
		SimpleDateFormat raw = new SimpleDateFormat("yyyy-M-d h:mm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, h:mma");
		try
		{
			Date parsedDate = (Date) raw.parse(dateString);
			date = dateFormat.format(parsedDate);
		}
		catch (ParseException ex)
		{
			System.out.println("Exception " + ex);
		}
		return date;
	}
	protected String toYearTextFormatFromRaw(String dateString)
	{
		String date = "";
		SimpleDateFormat raw = new SimpleDateFormat("yyyy-M-d h:mm:ss");
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
		try
		{
			Date parsedDate = (Date) raw.parse(dateString);
			date = dateFormat.format(parsedDate);
		}
		catch (ParseException ex)
		{
			System.out.println("Exception " + ex);
		}
		return date;
	}
	protected String toYearTextFormatFromRawNoTime(String dateString)
	{
		String date = "";
		SimpleDateFormat raw = new SimpleDateFormat("yyyy-M-d");
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
		try
		{
			Date parsedDate = (Date) raw.parse(dateString);
			date = dateFormat.format(parsedDate);
		}
		catch (ParseException ex)
		{
			System.out.println("Exception " + ex);
		}
		return date;
	}
}// end Global class