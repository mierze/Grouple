package cs460.grouple.grouple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/*
 * HomeActivity displays the primary navigation and welcome screen after logging in.
 */
public class HomeActivity extends BaseActivity
{
	private User user; // current user
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	// Tag to search for when logging info.
	static final String TAG = "GCM";
	// Sender ID is the project number from API console. Needs to be secret.
	private String SENDER_ID = "957639483805";
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	Context context;
	SharedPreferences prefs;

	String regid;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		// Register with the GCM servers and store it in the db.
		context = getApplicationContext();

		if (checkPlayServices())
		{
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);
			if (regid.isEmpty())
			{
				registerInBackground();
			}
			// Send reg_id with a login message type to server and how the
			// server store it. May
		} 
		else
		{
			Log.i(TAG, "No valid Google Play Services APK found.");
		}
		load();
	}

	private void load()
	{
		GLOBAL = ((Global) getApplicationContext());
		// grabbing the logged in user
		user = GLOBAL.getCurrentUser();

		// Load in user_settings from database and update sharedpreferences file
		new GetSettingsTask().execute("http://68.59.162.183/"
				+ "android_connect/get_userssettings.php");

		// initializing action bar and killswitch listener
		initActionBar("Welcome, " + user.getFirstName() + "!", false);
	}


	// This method closes the app when back button is pressed on main page
	@Override
	public void onBackPressed()
	{
		Log.d("backPress", "Back was pressed on home screen.");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_home)
		{
			return true;
			//already here do nothing
		}
		return super.onOptionsItemSelected(item);
	}

	public void navigate(View view)
	{
		// originally setting intent to null
		Intent intent = null;

		// WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		// lp.dimAmount=0.0f; // Dim level. 0.0 - no dim, 1.0 - completely
		// opaque
		// dialog.getWindow().setAttributes(lp);

		loadDialog.show();

		switch (view.getId())
		{
		case R.id.friendsButtonHA:
			intent = new Intent(this, FriendsActivity.class);
			user.fetchFriendRequests();
			user.fetchFriends();
			break;
		case R.id.settingsButtonHA:
			intent = new Intent(this, SettingsActivity.class);
			break;
		case R.id.eventsButtonHA:
			user.fetchEventsInvites();
			user.fetchEventsPending();
			user.fetchEventsUpcoming();
			user.fetchEventsPast();
			intent = new Intent(this, EventsActivity.class);
			break;
		case R.id.messagesButtonHA:
			intent = new Intent(this, RecentMessagesActivity.class);
			break;
		case R.id.groupsButtonHA:
			user.fetchGroupInvites();
			user.fetchGroups();
			intent = new Intent(this, GroupsActivity.class);
			break;
		case R.id.userButtonHA:
			user.fetchUserInfo();
			user.fetchEventsPast();
			user.fetchEventsUpcoming();
			user.fetchFriends();
			user.fetchGroups();
			intent = new Intent(this, ProfileActivity.class);
			intent.putExtra("CONTENT", "USER");
			break;
		default: // default just break out
			break;
		}

		GLOBAL.setCurrentUser(user);// update

		// checking that intent was assigned
		if (intent != null)
		{
			intent.putExtra("EMAIL", user.getEmail());
			startActivity(intent);
		}
		// else do nothing
	}


	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices()
	{
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS)
		{
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
			{
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else
			{
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Stores the registration ID and the app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId)
	{
		final SharedPreferences prefs = getGcmPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		Log.i(TAG, "RegID: " + regId);
		System.out.println("Regid: " + regId);
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * Gets the current registration ID for application on GCM service, if there
	 * is one.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context)
	{
		final SharedPreferences prefs = getGcmPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty())
		{
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion)
		{
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground()
	{
		new AsyncTask<Void, Void, String>()
		{
			@Override
			protected String doInBackground(Void... params)
			{
				String msg = "";
				try
				{
					if (gcm == null)
					{
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;

					// You should send the registration ID to your server over
					// HTTP, so it
					// can use GCM/HTTP or CCS to send messages to your app.
					sendRegistrationIdToBackend();

					// For this demo: we don't need to send it because the
					// device will send
					// upstream messages to a server that echo back the message
					// using the
					// 'from' address in the message.

					// Persist the regID - no need to register again.
					storeRegistrationId(context, regid);
				} 
				catch (IOException ex)
				{
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg)
			{
				// mDisplay.append(msg + "\n");
			}
		}.execute(null, null, null);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context)
	{
		try
		{
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} 
		catch (NameNotFoundException e)
		{
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGcmPreferences(Context context)
	{
		// This sample app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(MessagesActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use
	 * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
	 * since the device sends upstream messages to a server that echoes back the
	 * message using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend()
	{
		// Your implementation here.
		new setRegIDTask()
				.execute("http://68.59.162.183/android_connect/add_chat_id.php");
	}

	// Store the reg_id in the database if you are newly registered.
	private class setRegIDTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String email = GLOBAL.getCurrentUser().getEmail();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair("reg_id", regid));

			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// account registered successfully
					System.out.println("success!");

				} else
				{
					Context context = getApplicationContext();
					Toast toast = Toast.makeText(context,
							"Error Registering GCM REGID. Contact Devs",
							Toast.LENGTH_LONG);
					toast.show();
				}
			} 
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	// aSynch class to get user settings from database based on email of current
	// user
	private class GetSettingsTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String email = user.getEmail();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", email));
			// pass url and nameValuePairs off to global to do the JSON call.
			// Code continues at onPostExecute when JSON returns.
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				// user settings have been returned
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// update sharedpreference file now
					JSONArray settings = jsonObject.getJSONArray("settings");
					JSONObject jsonSettings = settings.getJSONObject(0);

					SharedPreferences.Editor editor = prefs.edit();
					editor.putString("emailFriendReq",
							jsonSettings.getString("emailFriendReq").toString());
					editor.putString("emailGroupReq",
							jsonSettings.getString("emailGroupReq").toString());
					editor.putString("emailEventReq",
							jsonSettings.getString("emailEventReq").toString());
					editor.putString("emailFriendMessage", jsonSettings
							.getString("emailFriendMessage").toString());
					editor.putString("emailGroupMessage", jsonSettings
							.getString("emailGroupMessage").toString());
					editor.putString("emailEventMessage", jsonSettings
							.getString("emailEventMessage").toString());
					editor.putString("emailEventUpcoming", jsonSettings
							.getString("emailEventUpcoming").toString());
					editor.putString("emailUmbrella",
							jsonSettings.getString("emailUmbrella").toString());
					editor.putString("androidFriendReq", jsonSettings
							.getString("androidFriendReq").toString());
					editor.putString("androidGroupReq",
							jsonSettings.getString("androidGroupReq")
									.toString());
					editor.putString("androidEventReq",
							jsonSettings.getString("androidEventReq")
									.toString());
					editor.putString("androidFriendMessage", jsonSettings
							.getString("androidFriendMessage").toString());
					editor.putString("androidGroupMessage", jsonSettings
							.getString("androidGroupMessage").toString());
					editor.putString("androidEventMessage", jsonSettings
							.getString("androidEventMessage").toString());
					editor.putString("androidEventUpcoming", jsonSettings
							.getString("androidEventUpcoming").toString());
					editor.putString("androidUmbrella",
							jsonSettings.getString("androidUmbrella")
									.toString());

					editor.apply();
					System.out.println("user settings in sharedpreferences have been initilized and is now up to date!");

				} 
				else if (jsonObject.getString("success").toString().equals("0"))
				{
					// no user_settings json was returned. Will not happen
					// unless database connection problems.
				}
			} 
			catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}
		}
	}
}
