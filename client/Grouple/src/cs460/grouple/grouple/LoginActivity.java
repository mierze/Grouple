package cs460.grouple.grouple;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

/*
 * HomeActivity is Launcher activity and allows the user to log in to his/her acccount.
 */
public class LoginActivity extends Activity
{
	private Button loginButton;
	private BroadcastReceiver broadcastReceiver;
	private ProgressBar progBar;
	private TextView loginFail;
	private Global GLOBAL;// = 
	private Dialog loadDialog = null;
	boolean tokenFlag;
	SharedPreferences prefs;
	CheckBox rememberLogin;
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		GLOBAL = (Global) getApplicationContext();
		//before showing login screen, attempt to login user using session token stored in SharedPreferences
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String token = prefs.getString("session_token", null);
		String email = prefs.getString("session_email", null);
		loadDialog = GLOBAL.getLoadDialog(new Dialog(this));
        loadDialog.setOwnerActivity(this);
		if(token !=null && email != null)
		{
			System.out.println("token was found... initiating login with email: "+email + ", token: "+token);
			
			tokenFlag = true;
			//Token and email was found in SharedPreferences, try to use it to login.
			new getLoginTask()
			.execute("http://68.59.162.183/android_connect/get_login.php?email="
					+ email + "&token=" + token);
		}
		else
		{
			tokenFlag = false;
			System.out.println("no token found... initiating normal login activity.");
			setContentView(R.layout.activity_login);
			// sets up an progress bar spinner that will appear when user hits
			// login.
			progBar = (ProgressBar) findViewById(R.id.progressBar);
			progBar.setVisibility(View.INVISIBLE);

			rememberLogin = (CheckBox) findViewById(R.id.rememberLoginCB);
			// sets up error message that will appear if user enters invalid
			// login/pass.
			loginFail = (TextView) findViewById(R.id.loginFailTextViewLA);
			loginFail.setVisibility(View.GONE);

			Log.d("app666", "we created");
			initKillswitchListener();
		}	
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		loadDialog.hide();
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	public void startRegisterActivity(View view)
	{
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}

	public void startHomeActivity(String email)
	{
		Intent intent = new Intent(this, HomeActivity.class);
		System.out.println("Email: " + email);
		intent.putExtra("email", email);
		startActivity(intent);
	}
	

	public void loginButton(View view)
	{
		// Create helper and if successful, will bring the correct home
		// activity.

		// Removes any previous error message from previous failed login
		loginFail.setVisibility(View.GONE);

		// Makes progress bar visible during processing of login
		progBar.setVisibility(View.VISIBLE);

		EditText emailEditText = (EditText) findViewById(R.id.emailEditTextLA);
		EditText passwordEditText = (EditText) findViewById(R.id.passwordEditTextLA);
		String email = emailEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		new getLoginTask()
					.execute("http://68.59.162.183/android_connect/get_login.php?email="
							+ email + "&password=" + password);

	}

	private class getLoginTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			System.out.println("in doInBackground");
			return GLOBAL.readJSONFeed(urls[0], null);
		}

		@Override
		protected void onPostExecute(String result)
		{
			System.out.println("in onPostExecute");
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				//validated for login
				System.out.println("After JSONObject");
				if (jsonObject.getString("success").toString().equals("1"))
				{
					String email;
					//logged in using token
					if(tokenFlag)
					{
						System.out.println("token login successful!");
						email = prefs.getString("session_email", null);
						SharedPreferences.Editor editor = prefs.edit();
						//client updates the session token returned by the server
						editor.putString("session_token", jsonObject.getString("token").toString());
						editor.apply();
					}
					//logged in using email/password
					else
					{
						// display message from json (successful login message)
						loginFail.setText(jsonObject.getString("message"));
						loginFail.setTextColor(getResources().getColor(
								R.color.light_green));
						loginFail.setVisibility(View.VISIBLE);

						EditText emailEditText = (EditText) findViewById(R.id.emailEditTextLA);
						//get the email
						email = emailEditText.getText().toString();
						User u = new User(email);
						GLOBAL.setCurrentUser(u);
						
						if(rememberLogin.isChecked())
						{
							System.out.println("checkbox ticked... storing login to sharedpreferences.");
							//update user's sharepreferences information
							SharedPreferences.Editor editor = prefs.edit();
							editor.putString("session_email", email);
							editor.putString("session_token", jsonObject.getString("token").toString());
							editor.apply();
						}
					}
				
					//load the user into the system
					User u = new User(email);
					u.fetchUserInfo();
					u.fetchEventsInvites();
					u.fetchFriendRequests();
					u.fetchGroupInvites();
				
					GLOBAL.setCurrentUser(u);

					//starting the home activity with the current users email
					startHomeActivity(email);
				} 
				//user failed to log in
				else
				{
					//failed token login
					if(tokenFlag)
					{
						tokenFlag = false;
						System.out.println("token login failed... initiating normal login activity.");
						setContentView(R.layout.activity_login);
						// sets up an progress bar spinner that will appear when user hits
						// login.
						progBar = (ProgressBar) findViewById(R.id.progressBar);
						progBar.setVisibility(View.INVISIBLE);

						rememberLogin = (CheckBox) findViewById(R.id.rememberLoginCB);
						// sets up error message that will appear if user enters invalid
						// login/pass.
						loginFail = (TextView) findViewById(R.id.loginFailTextViewLA);
						loginFail.setVisibility(View.GONE);

						Log.d("app666", "we created");
						initKillswitchListener();
					}
					//failed email/password login
					else
					{
						//login validation failed
						// Login processing finished: progress bar disappear again
						progBar.setVisibility(View.INVISIBLE);
						System.out.println("failed");
						// display message from json (failed login reason)
						loginFail.setText(jsonObject.getString("message"));
						loginFail.setTextColor(getResources().getColor(R.color.red));
						loginFail.setVisibility(View.VISIBLE);
					}
					
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	@Override
	public void onBackPressed() 
	{
		Intent intent = new Intent("CLOSE_ALL");
		this.sendBroadcast(intent);
		return;
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
