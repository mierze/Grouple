package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Brett, Todd, Scott
 * LoginActivity is Launcher activity and allows the user to perform various account functions.
 */
public class LoginActivity extends Activity
{
	private BroadcastReceiver broadcastReceiver;
	private ProgressBar progBar;
	private TextView loginFail;
	private Global GLOBAL;// = 
	private Dialog loadDialog;
	private EditText emailEditText;
	private boolean tokenFlag;
	private SharedPreferences prefs;
	private CheckBox rememberLogin;
	private AlertDialog forgotPasswordAlertDialog, createPasswordAlertDialog;
	private EditText forgotEmail, forgotCode, newPassword, confirmPassword;
	private String forgotEmailString;
	private Button requestButton, confirmButton,changePasswordButton;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		
		GLOBAL = ((Global) getApplicationContext());
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
			emailEditText = (EditText) findViewById(R.id.emailEditText);
			rememberLogin = (CheckBox) findViewById(R.id.rememberLoginCB);
			loginFail = (TextView) findViewById(R.id.loginFailTextViewLA);
			progBar = (ProgressBar) findViewById(R.id.progressBar);
			progBar.setVisibility(View.INVISIBLE);
			loginFail.setVisibility(View.GONE);
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
						//get the email
						email = emailEditText.getText().toString();
						
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
					//User u = new User(email);
					GLOBAL.login(email, LoginActivity.this);
			

					//starting the home activity with the current users email
					startHomeActivity(email);
				} 
				//user failed to log in
				else
				{
					//failed token login
					if(tokenFlag)
					{
						setContentView(R.layout.activity_login);
						// sets up an progress bar spinner that will appear when user hits
						// login.
						emailEditText = (EditText) findViewById(R.id.emailEditText);
						
						tokenFlag = false;
						System.out.println("token login failed... initiating normal login activity.");
						// sets up an progress bar spinner that will appear when user hits
						// login
						progBar = (ProgressBar) findViewById(R.id.progressBar);
						progBar.setVisibility(View.INVISIBLE);
						rememberLogin = (CheckBox) findViewById(R.id.rememberLoginCB);
						// sets up error message that will appear if user enters invalid
						// login/pass.

						loginFail = (TextView) findViewById(R.id.loginFailTextViewLA);

						loginFail.setVisibility(View.GONE);


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
			} 
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
	public void forgotPasswordButton(View view)
	{
		String email = emailEditText.getText().toString();
		// Removes any previous error message from previous failed login
		loginFail.setVisibility(View.GONE);
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Forgot Password");
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_forgot_password, null);
		dialogBuilder.setView(dialogView);

		forgotEmail = (EditText) dialogView.findViewById(R.id.confirmEditText);
		if (!email.equals(""))
			forgotEmail.setText(email);
		forgotCode  = (EditText) dialogView.findViewById(R.id.resetCodeFPD);
		requestButton = (Button) dialogView.findViewById(R.id.confirmDeleteButton);
		confirmButton = (Button) dialogView.findViewById(R.id.confirmFPD);
		
		forgotPasswordAlertDialog = dialogBuilder.create();
		forgotPasswordAlertDialog.show();
	}
	
	public void RequestResetCodeButton(View view)
	{
		//set button to unclickable to prevent spamming until finished
		requestButton.setEnabled(false);
		//make sure user has specified an email address to be reset
		if(forgotEmail.getText().toString().compareTo("") == 0)
		{
			Toast toast = GLOBAL.getToast(this, "Must specify an email address to be reset.");
			toast.show();
			//make button clickable again
			requestButton.setEnabled(true);
		}
		else
		{
			//attempt to request a reset code.
			new RequestResetCodeTask().execute("http://68.59.162.183/"
					+ "android_connect/request_resetcode.php");
		}	
	}
	
	//aSynch class to generate a reset code for an account to reset their password
	private class RequestResetCodeTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				
			//add all pairs
			nameValuePairs.add(new BasicNameValuePair("email", forgotEmail.getText().toString()));
			
			//pass url and nameValuePairs off to global to do the JSON call.  Code continues at onPostExecute when JSON returns.
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}
			@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				// check json response for whether reset code was created
				if (jsonObject.getString("success").toString().equals("1"))
				{
					System.out.println("request code was successfully generated!");
					Context context = getApplicationContext();
					Toast toast = GLOBAL.getToast(context, jsonObject.getString("message"));
					toast.show();
				}
				else if (jsonObject.getString("success").toString().equals("0"))
				{
					System.out.println("request code failed to generate!");
					Context context = getApplicationContext();
					Toast toast = GLOBAL.getToast(context, jsonObject.getString("message"));
					toast.show();
				}
				//make button clickable again
				requestButton.setEnabled(true);
			} 
			catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}
		}
	}

	public void ConfirmResetCodeButton(View view)
	{
		//set button to unclickable to prevent spamming until finished
		confirmButton.setEnabled(false);
		Context context = getApplicationContext();
		
		//make sure user has specified a reset code
		if(forgotCode.getText().toString().compareTo("") == 0)
		{
			Toast toast = GLOBAL.getToast(context, "Please type your reset code you received by email.");
			toast.show();
			//make button clickable again
			confirmButton.setEnabled(true);
		}
		//make sure user has specified their email address
		else if(forgotEmail.getText().toString().compareTo("") == 0)
		{
			Toast toast = GLOBAL.getToast(context, "Must specify an email address to be reset.");
			toast.show();
			//make button clickable again
			confirmButton.setEnabled(true);
		}
		else
		{
			//attempt to confirm the reset code
			new ConfirmResetCodeTask().execute("http://68.59.162.183/"
					+ "android_connect/confirm_resetcode.php");
		}
	}
	
	//aSynch class to generate a reset code for an account to reset their password
	private class ConfirmResetCodeTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				
			//add all pairs
			nameValuePairs.add(new BasicNameValuePair("code", forgotCode.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("email", forgotEmail.getText().toString()));
			forgotEmailString = forgotEmail.getText().toString();
			
			//pass url and nameValuePairs off to global to do the JSON call.  Code continues at onPostExecute when JSON returns.
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}
			@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				// check json response for whether reset code was a match
				if (jsonObject.getString("success").toString().equals("1"))
				{	
					//Allow user to create new password.
					AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
					dialogBuilder.setTitle("Create New Password");
					LayoutInflater inflater = LoginActivity.this.getLayoutInflater();
					View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
					dialogBuilder.setView(dialogView);
					EditText passwordOld = (EditText) dialogView.findViewById(R.id.passwordOldTextCPD);
					passwordOld.setVisibility(View.GONE);
					newPassword = (EditText) dialogView.findViewById(R.id.passwordNewTextCPD);
					confirmPassword = (EditText) dialogView.findViewById(R.id.passwordConfirmTextCPD);
					changePasswordButton = (Button) dialogView.findViewById(R.id.loginButtonLA);					
					createPasswordAlertDialog = dialogBuilder.create();
					createPasswordAlertDialog.show();
				}
				else if (jsonObject.getString("success").toString().equals("0"))
				{
					System.out.println("request code failed to match!");
					Context context = getApplicationContext();
					Toast toast = GLOBAL.getToast(context, jsonObject.getString("message"));
					toast.show();
				}
				//make button clickable again
				confirmButton.setEnabled(true);
			} 
			catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}
		}
	}
	
	public void ConfirmPasswordChangeButton(View view)
	{
		Context context = getApplicationContext();
		System.out.println("confirmpasswordchangebutton was activated.");
		//set button to unclickable to prevent spamming until finished
				changePasswordButton.setEnabled(false);
			
		//error if new password does not match confirm new password
		if(newPassword.getText().toString().compareTo(confirmPassword.getText().toString()) != 0)
		{			
			Toast toast = GLOBAL.getToast(context, "'New password' must match 'Confirm new password'");
			toast.show();
			newPassword.setText("");
			confirmPassword.setText("");
			//make button clickable again
			changePasswordButton.setEnabled(true);
		}
		//error if empty new password field
		else if(newPassword.getText().toString().compareTo("") == 0)
		{
			Toast toast = GLOBAL.getToast(context, "Enter a new password first.");
			toast.show();
			//make button clickable again
			changePasswordButton.setEnabled(true);
		}
		//attempt to change password
		else
		{
		    new ChangePasswordTask().execute("http://68.59.162.183/"
					+ "android_connect/update_password_by_code.php");
		}
	}
	
	//aSynch class to update account password
	private class ChangePasswordTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			System.out.println("updating password...");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", forgotEmailString));
			nameValuePairs.add(new BasicNameValuePair("newPassword", newPassword.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("code", forgotCode.getText().toString()));
				
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
					System.out.println("updating password complete!");
					Context context = getApplicationContext();
					Toast toast = GLOBAL.getToast(context, jsonObject.getString("message"));
					toast.show();
					createPasswordAlertDialog.cancel();
					forgotPasswordAlertDialog.cancel();
				}
				else if (jsonObject.getString("success").toString().equals("0"))
				{
					System.out.println("Failed to update password!");
					Context context = getApplicationContext();
					Toast toast = GLOBAL.getToast(context, jsonObject.getString("message"));
					toast.show();
					//failed to update user password for some reason. see error in toast.
					//make button clickable again
					changePasswordButton.setEnabled(true);
				}
			} 
			catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
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


}
