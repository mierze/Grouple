package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 
 * @author Brett, Todd, Scott
 * RegisterActivity allows the user to register for a new Grouple account.
 */
public class RegisterActivity extends Activity
{
	private BroadcastReceiver broadcastReceiver;
	private Global GLOBAL;
	private Dialog loadDialog = null;
	
	@Override
	public void onBackPressed() 
	{
		finish();
	    return;
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		loadDialog.hide();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		GLOBAL = ((Global) getApplicationContext());
		loadDialog = GLOBAL.getLoadDialog(new Dialog(this));
        loadDialog.setOwnerActivity(this);

		initKillswitchListener();
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	public void registerButton(View view)
	{
		TextView errorMessageTextView = (TextView) findViewById(R.id.errorMessageTextView);
		EditText passwordEditText = (EditText) findViewById(R.id.passwordEditTextRA);
		EditText rePasswordEditText = (EditText) findViewById(R.id.rePasswordEditTextRA);
		String password = passwordEditText.getText().toString();
		String rePassword = rePasswordEditText.getText().toString();
		// Confirms that both passwords match before executing php
		if (password.equals(rePassword))
		{
			new getRegisterTask()
					.execute("http://mierze.gear.host/grouple/android_connect/register_account.php");

		} else
		{
			// Passwords did not match. Clear just the passwords, and display
			// the error message.
			passwordEditText.setText("");
			rePasswordEditText.setText("");
			errorMessageTextView.setText("Passwords must match!");
			errorMessageTextView.setVisibility(0);
			passwordEditText.requestFocus();
		}
	}

	private class getRegisterTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());

			EditText emailEditText = (EditText) findViewById(R.id.emailEditTextRA);
			System.out.println("i made it1!");
			EditText passwordEditText = (EditText) findViewById(R.id.passwordEditTextRA);
			EditText fNameEditText = (EditText) findViewById(R.id.fNameEditText);
			EditText lNameEditText = (EditText) findViewById(R.id.lNameEditText);
			// EditText rePasswordEditText = (EditText)
			// findViewById(R.id.rePasswordEditTextRA);
			String email = emailEditText.getText().toString();
			String fName = fNameEditText.getText().toString();
			String lName = lNameEditText.getText().toString();
			String password = passwordEditText.getText().toString();

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("first", fName));
			nameValuePairs.add(new BasicNameValuePair("last", lName));

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
					startLoginActivity();
				} else
				{
					// Email already in system
					if (jsonObject.getString("success").toString().equals("2"))
					{
						TextView email = (TextView) findViewById(R.id.emailEditTextRA);
						email.setText("");
						email.requestFocus();
					}
					// Not an email address
					if (jsonObject.getString("success").toString().equals("3"))
					{
						TextView email = (TextView) findViewById(R.id.emailEditTextRA);
						email.setText("");
						email.requestFocus();
					}
					// Password is too short or too long
					else if (jsonObject.getString("success").toString()
							.equals("4"))
					{
						TextView password = (TextView) findViewById(R.id.passwordEditTextRA);
						TextView repassword = (TextView) findViewById(R.id.rePasswordEditTextRA);
						password.setText("");
						repassword.setText("");
						password.requestFocus();
					}
					// First and/or Last name are blank.
					else if (jsonObject.getString("success").toString()
							.equals("5"))
					{
						TextView fName = (TextView) findViewById(R.id.fNameEditText);
						TextView lName = (TextView) findViewById(R.id.lNameEditText);

						fName.setText("");
						lName.setText("");

						fName.requestFocus();
					}
					// Couldn't create account. Change error message to whatever
					// the PHP error message is.
					TextView registerFail = (TextView) findViewById(R.id.errorMessageTextView);
					registerFail.setText(jsonObject.getString("message"));
					registerFail.setVisibility(0);
				}
			} 
			catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	public void loginButton(View view)
	{
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

	public void startLoginActivity()
	{
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
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
