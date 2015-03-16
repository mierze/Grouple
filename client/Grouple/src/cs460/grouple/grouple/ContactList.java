package cs460.grouple.grouple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*
 * MessagesActivity has not been implemented yet.
 */
public class ContactList extends ActionBarActivity
{
	private BroadcastReceiver broadcastReceiver;
	private Global GLOBAL;
	private static int alt = 1;
	private User user; //will be null for now
	
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    //Tag to search for when logging info.
    static final String TAG = "GCM";
    //Sender ID is the project number from API console. Needs to be secret.
    String SENDER_ID = "957639483805";
    ArrayList<String> emails = new ArrayList<String>();
    
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;
    String regid;
    String recipientRegID = "";

	public String getRecipientRegID() {
		return recipientRegID;
	}

	public void setRecipientRegID(String recipientRegID) {
		this.recipientRegID = recipientRegID;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		GLOBAL = ((Global) getApplicationContext());
		super.onCreate(savedInstanceState);
		user = GLOBAL.getCurrentUser();
		setContentView(R.layout.activity_contact_list);
		/* Action bar */
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		populateChats();
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText("Brett Mierzejewski");
		initKillswitchListener();
		context = getApplicationContext();
		
		//Get the recipient id ig..
		new getRegIDTask().execute("http://68.59.162.183/android_connect/get_chat_id.php","mierze@gmail.com");
		//new getContactsTask().execute("http://68.59.162.183/android_connect/get_chat_id.php");
        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);
            if (regid.isEmpty()) {
                registerInBackground();
            } 
            //Send reg_id with a login message type to server and how the server store it. May
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
        
      
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        // Check device for Play Services APK.
        checkPlayServices();
    }
	
	/**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
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
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        Log.i(TAG, "RegID: " + regId);
        System.out.println("Regid: "+regId);
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
    
    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
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
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.messages, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_logout)
		{
			
			Intent login = new Intent(this, LoginActivity.class);
			GLOBAL.destroySession();
			startActivity(login);
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		if (id == R.id.action_home)
		{
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	public void startMessages(View view)
	{
		Intent intent = new Intent(this, MessagesActivity.class);
		intent.putExtra("email", emails.get(view.getId()));
		startActivity(intent);
	}
	
	private void populateChats()
	{
		//populate all of the chats between peers, groups and events that are most active
		//layout to inflate into
		LinearLayout messageLayout = (LinearLayout) findViewById(R.id.contactLayout);
		//layout inflater
		LayoutInflater li = getLayoutInflater();
		TextView messageBody, messageDate;
		View row = null;
		String message = "";
		
		if (emails.size() == 0)
			emails.add("tfeipel@gmail.com");
		else
			emails.add("mierze@gmail.com");
		//messages consist of some things (messagebody, date, sender, receiver)
		
		//loop through messages (newest first), maybe a map String String with messagebody, date
		row =  li.inflate(R.layout.contact_row, null); //inflate this message row
		row.setId(emails.size() - 1);
		messageBody = (TextView) row.findViewById(R.id.messageBody);
		messageDate = (TextView) row.findViewById(R.id.messageDate);
		ImageView 	contactImage = (ImageView) row.findViewById(R.id.contactImage);
		contactImage.setImageBitmap(user.getImage());

		//add row into scrollable layout
		messageLayout.addView(row);
	}
	
	
    
	public void initKillswitchListener()
	{
		// START KILL SWITCH LISTENER
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("CLOSE_ALL");
		broadcastReceiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				// close activity
				if (intent.getAction().equals("CLOSE_ALL"))
				{
					Log.d("app666", "we killin the login it");
					// System.exit(1);
					finish();
				}

			}
		};
		registerReceiver(broadcastReceiver, intentFilter);
		// End Kill switch listener
	}
	
	/**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MessagesActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
      // Your implementation here.
    	new setRegIDTask().execute("http://68.59.162.183/android_connect/add_chat_id.php");
    }
    
    //Store the reg_id in the database if you are newly registered.
    private class setRegIDTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());

			String email = global.getCurrentUser().getEmail();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair("reg_id", regid));

			return global.readJSONFeed(urls[0], nameValuePairs);
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
					Toast toast = Toast.makeText(context, "Error Registering GCM REGID. Contact Devs", Toast.LENGTH_LONG);
					toast.show();
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
    
    private class getRegIDTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			//The recipient's email is urls[1]
			nameValuePairs.add(new BasicNameValuePair("email", urls[1]));

			return global.readJSONFeed(urls[0], nameValuePairs);
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
					String id = jsonObject.getString("regid").toString();
					setRecipientRegID(id);
					
				} else
				{
					Context context = getApplicationContext();
					Toast toast = Toast.makeText(context, "Error Getting GCM REGID. Contact Devs", Toast.LENGTH_LONG);
					toast.show();
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
    
    //Stores the message in the database.
    private class storeMessageTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			//The msg is urls[1], sender urls[2], receiver urls[3]
			nameValuePairs.add(new BasicNameValuePair("msg", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("sender", urls[2]));
			nameValuePairs.add(new BasicNameValuePair("receiver", urls[3]));
			
			return global.readJSONFeed(urls[0], nameValuePairs);
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
					/*//Message was successfully stored, now notify the user.
					String msg = "";
					try {
                        Bundle data = new Bundle();
                        //Get message from edit text
                        EditText mymessage   = (EditText)findViewById(R.id.messageEditText);
                        msg = mymessage.getText().toString();
                        messages.add(msg);
                        data.putString("my_message", msg);
                        data.putString("my_action", "cs460.grouple.grouple.ECHO_NOW");
                        data.putString("recipient",getRecipientRegID());
                        String id = Integer.toString(msgId.incrementAndGet());
                        gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                        msg = "Sent message";
                    } catch (IOException ex) {
                        msg = "Error :" + ex.getMessage();
                    }*/
					
				} else
				{
					Context context = getApplicationContext();
					Toast toast = Toast.makeText(context, "Error Storing Message. Message not sent. Contact Devs", Toast.LENGTH_LONG);
					toast.show();
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

}
