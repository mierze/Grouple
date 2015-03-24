package cs460.grouple.grouple;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends ActionBarActivity
{
	private BroadcastReceiver broadcastReceiver;
	private Dialog loadDialog = null;
	private static Global GLOBAL;

	
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
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initKillswitchListener();
		GLOBAL = ((Global) getApplicationContext());
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
              
        Switch emailFriendSwitch = (Switch)  findViewById(R.id.emailFriendSwitch); 
        Switch emailGroupSwitch = (Switch)  findViewById(R.id.emailGroupSwitch); 
        Switch emailEventSwitch = (Switch)  findViewById(R.id.emailEventSwitch); 
        Switch emailUpcomingEventSwitch = (Switch)  findViewById(R.id.emailUpcomingEventSwitch); 
        
        Switch androidFriendSwitch = (Switch)  findViewById(R.id.androidFriendSwitch); 
        Switch androidGroupSwitch = (Switch)  findViewById(R.id.androidGroupSwitch); 
        Switch androidEventSwitch = (Switch)  findViewById(R.id.androidEventSwitch); 
        Switch androidUpcomingEventSwitch = (Switch)  findViewById(R.id.androidUpcomingEventSwitch); 
        
        emailFriendSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            	System.out.println("emailFriendSwitch was changed");
                // todo: save updated settings to local sharedpreferenced file, the isChecked will be
                // true if the switch is in the On position
            }
        });    
        
        emailGroupSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            	System.out.println("emailGroupSwitch was changed");
                // todo: save updated settings to local sharedpreferenced file, the isChecked will be
                // true if the switch is in the On position
            }
        });    
        
        emailEventSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            	System.out.println("emailEventSwitch was changed");
                // todo: save updated settings to local sharedpreferenced file, the isChecked will be
                // true if the switch is in the On position
            }
        });    
        
        emailUpcomingEventSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            	System.out.println("emailUpcomingEventSwitch was changed");
                // todo: save updated settings to local sharedpreferenced file, the isChecked will be
                // true if the switch is in the On position
            }
        });    
        
        androidFriendSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            	System.out.println("androidFriendSwitch was changed");
                // todo: save updated settings to local sharedpreferenced file, the isChecked will be
                // true if the switch is in the On position
            }
        });    
        
        androidGroupSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            	System.out.println("androidGroupSwitch was changed");
                // todo: save updated settings to local sharedpreferenced file, the isChecked will be
                // true if the switch is in the On position
            }
        });    
        
        androidEventSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            	System.out.println("androidEventSwitch was changed");
                // todo: save updated settings to local sharedpreferenced file, the isChecked will be
                // true if the switch is in the On position
            }
        });    
        
        androidUpcomingEventSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            	System.out.println("androidUpcomingEventSwitch was changed");
                // todo: save updated settings to local sharedpreferenced file, the isChecked will be
                // true if the switch is in the On position
            }
        });    
        
        
        //todo: 1) load settings from sharedpreference file that gets created/filled upon login
        //      2) anytime the user leaves this page (whether by hitting back, home, any other way), it will update the database settings for this user using the sharedPreferences file(s).
       

	}
	
	public void changePasswordButton(View view)
	{
		System.out.println("changePassword was activated.");
	}

	public void changeEmailButton(View view)
	{
		System.out.println("changeEmail was activated.");
	}

	public void deleteAccountButton(View view)
	{
		System.out.println("deleteAccount was activated.");
	}
	
	public void emailFriendSwitch(View view)
	{
		System.out.println("emailFriendSwitch was activated.");
	}
	
	public void emailGroupSwitch(View view)
	{
		System.out.println("emailGroupSwitch was activated.");
	}
	
	public void emailEventSwitch(View view)
	{
		System.out.println("emailEventSwitch was activated.");
	}
	
	public void emailEventUpcomingSwitch(View view)
	{
		System.out.println("emailEventUpcomingEventSwitch was activated.");
	}
	
	public void androidFriendSwitch(View view)
	{
		System.out.println("androidFriendSwitch was activated.");
	}
	
	public void androidGroupSwitch(View view)
	{
		System.out.println("androidGroupSwitch was activated.");
	}
	
	public void androidEventSwitch(View view)
	{
		System.out.println("androidEventSwitch was activated.");
	}
	
	public void androidEventUpcomingSwitch(View view)
	{
		System.out.println("androidEventUpcomingSwitch was activated.");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
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
}