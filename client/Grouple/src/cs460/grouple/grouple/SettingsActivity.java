package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends ActionBarActivity
{
	private BroadcastReceiver broadcastReceiver;
	private Dialog loadDialog = null;
	private static Global GLOBAL;
    ArrayList<String> settingsArray;
    ArrayList<String> settingsNameArray;
    ArrayList<Switch> switchArray;
    User user;
    SharedPreferences.Editor editor;
    //use sharedpreferences info that was filled during login to set initial switches to their correct positions
    SharedPreferences prefs;  

	
	@Override
	protected void onStop()
	{
		super.onStop();
		loadDialog.hide();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent e)  
	{
		
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	loadDialog.show();
	    	finish();
	    }
	    return true;
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    System.out.println("we hit onPause!");
	    
	    //since we're leaving settingsActivity, update settings in database now
	    new UpdateSettingsTask().execute("http://68.59.162.183/"
				+ "android_connect/update_userssettings.php");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		initKillswitchListener();
		GLOBAL = ((Global) getApplicationContext());
		user = GLOBAL.getCurrentUser();
		setContentView(R.layout.activity_settings);
		/* Action bar */
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText("Settings");
		loadDialog = GLOBAL.getLoadDialog(new Dialog(this));
        loadDialog.setOwnerActivity(this);
        
        settingsArray = new ArrayList<String>();
        switchArray = new ArrayList<Switch>();
        settingsNameArray = new ArrayList<String>();
              
        Switch emailFriendSwitch = (Switch)  findViewById(R.id.emailFriendSwitch); 
        Switch emailGroupSwitch = (Switch)  findViewById(R.id.emailGroupSwitch); 
        Switch emailEventSwitch = (Switch)  findViewById(R.id.emailEventSwitch); 
        Switch emailUpcomingEventSwitch = (Switch)  findViewById(R.id.emailUpcomingEventSwitch); 
        
        //these four switches not currently coded in layout
        Switch emailFriendMessageSwitch = null;
        Switch emailGroupMessageSwitch = null;
        Switch emailEventMessageSwitch = null;
        Switch emailUmbrellaSwitch = null;
        
        Switch androidFriendSwitch = (Switch)  findViewById(R.id.androidFriendSwitch); 
        Switch androidGroupSwitch = (Switch)  findViewById(R.id.androidGroupSwitch); 
        Switch androidEventSwitch = (Switch)  findViewById(R.id.androidEventSwitch); 
        Switch androidUpcomingEventSwitch = (Switch)  findViewById(R.id.androidUpcomingEventSwitch);
        
        //these four switches not currently coded in layout
        Switch androidFriendMessageSwitch = null;
        Switch androidGroupMessageSwitch = null;
        Switch androidEventMessageSwitch = null;
        Switch androidUmbrellaSwitch = null;
            
    	prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	String emailFriendReq = prefs.getString("emailFriendReq", null);
    	editor = prefs.edit();
    	
    	//initial setup which will make sure all switch elements are enabled correctly
    	settingsArray.add(emailFriendReq);
    	switchArray.add(emailFriendSwitch);
    	settingsNameArray.add("emailFriendReq");
    	
    	String emailGroupReq = prefs.getString("emailGroupReq", null);
    	settingsArray.add(emailGroupReq);
    	switchArray.add(emailGroupSwitch);
    	settingsNameArray.add("emailGroupReq");
    	
    	String emailEventReq = prefs.getString("emailEventReq", null);
    	settingsArray.add(emailEventReq);
    	switchArray.add(emailEventSwitch);
    	settingsNameArray.add("emailEventReq");
    	
    	String emailFriendMessage = prefs.getString("emailFriendMessage", null);
    	settingsArray.add(emailFriendMessage);
    	switchArray.add(emailFriendMessageSwitch);
    	settingsNameArray.add("emailFriendMessage");
    	
    	String emailGroupMessage = prefs.getString("emailGroupMessage", null);
    	settingsArray.add(emailGroupMessage);
    	switchArray.add(emailGroupMessageSwitch);
    	settingsNameArray.add("emailGroupMessage");
    	
    	String emailEventMessage = prefs.getString("emailEventMessage", null);
    	settingsArray.add(emailEventMessage);
    	switchArray.add(emailEventMessageSwitch);
    	settingsNameArray.add("emailEventMessage");
    	
    	String emailEventUpcoming = prefs.getString("emailEventUpcoming", null);
    	settingsArray.add(emailEventUpcoming);
    	switchArray.add(emailUpcomingEventSwitch);
    	settingsNameArray.add("emailEventUpcoming");
    	
    	String emailUmbrella = prefs.getString("emailUmbrella", null);
    	settingsArray.add(emailUmbrella);
    	switchArray.add(emailUmbrellaSwitch);
    	settingsNameArray.add("emailUmbrella");
    	
    	String androidFriendReq = prefs.getString("androidFriendReq", null);
    	settingsArray.add(androidFriendReq);
    	switchArray.add(androidFriendSwitch);
    	settingsNameArray.add("androidFriendReq");
    	
    	String androidGroupReq = prefs.getString("androidGroupReq", null);
    	settingsArray.add(androidGroupReq);
    	switchArray.add(androidGroupSwitch);
    	settingsNameArray.add("androidGroupReq");
    	
    	String androidEventReq = prefs.getString("androidEventReq", null);
    	settingsArray.add(androidEventReq);
    	switchArray.add(androidEventSwitch);
    	settingsNameArray.add("androidEventReq");
    	
    	String androidFriendMessage = prefs.getString("androidFriendMessage", null);
    	settingsArray.add(androidFriendMessage);
    	switchArray.add(androidFriendMessageSwitch);
    	settingsNameArray.add("androidFriendMessage");
    	
    	String androidGroupMessage = prefs.getString("androidGroupMessage", null);
    	settingsArray.add(androidGroupMessage);
    	switchArray.add(androidGroupMessageSwitch);
    	settingsNameArray.add("androidGroupMessage");
    	
    	String androidEventMessage = prefs.getString("androidEventMessage", null);
    	settingsArray.add(androidEventMessage);
    	switchArray.add(androidEventMessageSwitch);
    	settingsNameArray.add("androidEventMessage");
    	
    	String androidEventUpcoming = prefs.getString("androidEventUpcoming", null);
    	settingsArray.add(androidEventUpcoming);
    	switchArray.add(androidUpcomingEventSwitch);
    	settingsNameArray.add("androidEventUpcoming");
    	
    	String androidUmbrella = prefs.getString("androidUmbrella", null);
    	settingsArray.add(androidUmbrella);
    	switchArray.add(androidUmbrellaSwitch);
    	settingsNameArray.add("androidUmbrella");
    	
    	//loop through and change switches state for initial load
    	int index = 0;
    	for ( String setting : settingsArray )
    	{
        	if(switchArray.get(index) != null)
        	{
        		switchArray.get(index).setId(index);
        		if(setting !=null)
        		{
        			if(setting.compareTo("1") == 0)
               	   	{
               	   	 	switchArray.get(index).setChecked(true);
               	   	}
                	else if(setting.compareTo("0") == 0)
                	{
                		switchArray.get(index).setChecked(false);
                	}
        		}
        	}
        	index++;
    	}
    	
    	//single listener for all switches that controls behaviour of switch when activated by user.
    	OnCheckedChangeListener listener = new OnCheckedChangeListener()
    	{
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) 
			{
				System.out.println("switch activation for button id: "+buttonView.getId());
				//update sharedpreference file of switch that was activated
				if(isChecked)
            	{
					System.out.println(settingsNameArray.get(buttonView.getId())+" set to ON");
					editor.putString(settingsNameArray.get(buttonView.getId()), "1");	
            	}
            	else
            	{
            		System.out.println(settingsNameArray.get(buttonView.getId())+" set to OFF");
            		editor.putString(settingsNameArray.get(buttonView.getId()), "0");
            	}
				editor.apply();
			}
    	};
    	
    	//add switches to listener
    	//Note: commented out switches have not been yet implemented in GUI
        emailFriendSwitch.setOnCheckedChangeListener(listener);
        emailGroupSwitch.setOnCheckedChangeListener(listener);
        emailEventSwitch.setOnCheckedChangeListener(listener);
        //emailEventMessageSwitch.setOnCheckedChangeListener(listener);
        //emailFriendMessageSwitch.setOnCheckedChangeListener(listener);
        //emailGroupMessageSwitch.setOnCheckedChangeListener(listener);
        emailUpcomingEventSwitch.setOnCheckedChangeListener(listener);
        //emailUmbrellaSwitch.setOnCheckedChangeListener(listener);
        androidFriendSwitch.setOnCheckedChangeListener(listener);
        androidGroupSwitch.setOnCheckedChangeListener(listener);
        androidEventSwitch.setOnCheckedChangeListener(listener);    
        //androidEventMessageSwitch.setOnCheckedChangeListener(listener);
        //androidFriendMessageSwitch.setOnCheckedChangeListener(listener);
        //androidGroupMessageSwitch.setOnCheckedChangeListener(listener);
        androidUpcomingEventSwitch.setOnCheckedChangeListener(listener);
        //androidUmbrellaSwitch.setOnCheckedChangeListener(listener);
	}
	
	public void changePasswordButton(View view)
	{
		System.out.println("changePassword was activated.");
		//TODO: add code here to implement changePassword
	}

	public void changeEmailButton(View view)
	{
		System.out.println("changeEmail was activated.");
		//TODO: add code here to implement changeEmail
	}

	public void deleteAccountButton(View view)
	{
		System.out.println("deleteAccount was activated.");
		//TODO: add code here to implement deleteAccountButton
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation_actions, menu);
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
			GLOBAL.destroySession();
			Intent login = new Intent(this, LoginActivity.class);
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

	public void startParentActivity(View view)
	{
		String className = "HomeActivity";
		Intent newIntent = null;
		try
		{
			newIntent = new Intent(this, Class.forName("cs460.grouple.grouple."
					+ className));
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		startActivity(newIntent);
		finish();
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
	
	//aSynch class to update user settings in database 
	private class UpdateSettingsTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			
			System.out.println("updating settings...");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			if(GLOBAL.getCurrentUser() !=null)
			{
				//add all pairs based on data in sharedpreferences
				nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
				nameValuePairs.add(new BasicNameValuePair("emailFriendReq", prefs.getString("emailFriendReq", null)));
				nameValuePairs.add(new BasicNameValuePair("emailGroupReq", prefs.getString("emailGroupReq", null)));
				nameValuePairs.add(new BasicNameValuePair("emailEventReq", prefs.getString("emailEventReq", null)));
				nameValuePairs.add(new BasicNameValuePair("emailFriendMessage", prefs.getString("emailFriendMessage", null)));
				nameValuePairs.add(new BasicNameValuePair("emailGroupMessage", prefs.getString("emailGroupMessage", null)));
				nameValuePairs.add(new BasicNameValuePair("emailEventMessage", prefs.getString("emailEventMessage", null)));
				nameValuePairs.add(new BasicNameValuePair("emailEventUpcoming", prefs.getString("emailEventUpcoming", null)));
				nameValuePairs.add(new BasicNameValuePair("emailUmbrella", prefs.getString("emailUmbrella", null)));
	
				nameValuePairs.add(new BasicNameValuePair("androidFriendReq", prefs.getString("androidFriendReq", null)));
				nameValuePairs.add(new BasicNameValuePair("androidGroupReq", prefs.getString("androidGroupReq", null)));
				nameValuePairs.add(new BasicNameValuePair("androidEventReq", prefs.getString("androidEventReq", null)));
				nameValuePairs.add(new BasicNameValuePair("androidFriendMessage", prefs.getString("androidFriendMessage", null)));
				nameValuePairs.add(new BasicNameValuePair("androidGroupMessage", prefs.getString("androidGroupMessage", null)));
				nameValuePairs.add(new BasicNameValuePair("androidEventMessage", prefs.getString("androidEventMessage", null)));
				nameValuePairs.add(new BasicNameValuePair("androidEventUpcoming", prefs.getString("androidEventUpcoming", null)));
				nameValuePairs.add(new BasicNameValuePair("androidUmbrella", prefs.getString("androidUmbrella", null)));
			}
				
			//pass url and nameValuePairs off to global to do the JSON call.  Code continues at onPostExecute when JSON returns.
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				// profile settings have been successfully updated
				if (jsonObject.getString("success").toString().equals("1"))
				{
					System.out.println("updating settings complete!");
					Context context = getApplicationContext();
					Toast toast = GLOBAL.getToast(context, jsonObject.getString("message"));
					toast.show();
				}
				else if (jsonObject.getString("success").toString().equals("0"))
				{
					System.out.println("Failed to update settings!");
					//failed to update user settings for some reasons
				}
			} 
			catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}
		}
	}
}