package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends BaseActivity
{
    private ArrayList<String> settingsArray;
    private ArrayList<String> settingsNameArray;
    private ArrayList<Switch> switchArray;
    private User user;
    private SharedPreferences.Editor editor;
    //use sharedpreferences info that was filled during login to set initial switches to their correct positions
    private SharedPreferences prefs;  
    
    //edittexts for changeemail dialog
    private EditText CEDoldPassword;
    private EditText CEDnewEmail;
    private EditText CEDconfirmEmail;
	
	//edittexts for changepassword dialog
    private EditText CPDoldPassword;
    private EditText CPDnewPassword;
    private EditText CPDconfirmPassword;
	
	//edittext for deleteAccount dialog
    private EditText DADoldPassword;

    private AlertDialog emailAlertDialog;
    private AlertDialog passwordAlertDialog;
    private AlertDialog deleteAccountDialog;
	
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
		user = GLOBAL.getCurrentUser();
		setContentView(R.layout.activity_settings);
		initActionBar("Settings");
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
		//dialog box:
		//you must first type your old password, your new password, and confirm new password
		//this is NOT "forgot password". user must know old password.  Forget password will have to go through registered email to ensure authentication.
		//call updatePassword.php
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Change Account Password");
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.changepassword_dialog, null);
		dialogBuilder.setView(dialogView);

		CPDoldPassword = (EditText) dialogView.findViewById(R.id.passwordOldTextCPD);
		CPDnewPassword = (EditText) dialogView.findViewById(R.id.passwordNewTextCPD);
		CPDconfirmPassword = (EditText) dialogView.findViewById(R.id.passwordConfirmTextCPD);
		
		passwordAlertDialog = dialogBuilder.create();
		passwordAlertDialog.show();
	}

	public void ConfirmPasswordChangeButton(View view)
	{
		Context context = getApplicationContext();
		System.out.println("confirmpasswordchangebutton was activated.");
		//TODO: add code here to implement confirm changes
			
		//error if new password does not match confirm new password
		if(CPDnewPassword.getText().toString().compareTo(CPDconfirmPassword.getText().toString()) != 0)
		{			
			Toast toast = GLOBAL.getToast(context, "'New password' must match 'Confirm new password'");
			toast.show();
			CPDnewPassword.setText("");
			CPDconfirmPassword.setText("");
		}
		//error if empty new password field
		else if(CPDnewPassword.getText().toString().compareTo("") == 0)
		{
			Toast toast = GLOBAL.getToast(context, "Enter a new password first.");
			toast.show();
		}
		//error if empty current password field
		else if(CPDoldPassword.getText().toString().compareTo("") == 0)
		{
			Toast toast = GLOBAL.getToast(context, "You must enter your current password for account verification.");
			toast.show();
		}
		//error if password is already equal to newPassword
		else if(CPDoldPassword.getText().toString().compareTo(CPDnewPassword.getText().toString()) == 0)
		{
			Toast toast = GLOBAL.getToast(context, "Your 'New password' cannot be the same as your 'Current password'.");
			toast.show();
			CPDnewPassword.setText("");
			CPDconfirmPassword.setText("");
		}
		//attempt to change password
		else
		{
		    new ChangePasswordTask().execute("http://68.59.162.183/"
					+ "android_connect/update_password.php");
		}
	}

	public void changeEmailButton(View view)
	{
		System.out.println("changeEmail was activated.");
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Change Account Email");
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.changeemail_dialog, null);
		dialogBuilder.setView(dialogView);

		TextView currentEmail = (TextView) dialogView.findViewById(R.id.currentEmailCED);
		currentEmail.setText(currentEmail.getText().toString() + GLOBAL.getCurrentUser().getEmail());
		CEDoldPassword = (EditText) dialogView.findViewById(R.id.passwordOldTextCED);
		CEDnewEmail = (EditText) dialogView.findViewById(R.id.emailTextCED);
		CEDconfirmEmail = (EditText) dialogView.findViewById(R.id.emailConfirmTextCED);
		
		emailAlertDialog = dialogBuilder.create();
		emailAlertDialog.show();
	}
	
	public void ConfirmEmailChangeButton(View view)
	{
		System.out.println("confirmemailchangebutton was activated.");
		Context context = getApplicationContext();
		
		//error if new email does not match confirm new email
		if(CEDnewEmail.getText().toString().compareTo(CEDconfirmEmail.getText().toString()) != 0)
		{
			
			Toast toast = GLOBAL.getToast(context, "'New email address' must match 'Confirm new email address'");
			toast.show();
			CEDnewEmail.setText("");
			CEDconfirmEmail.setText("");
		}
		//error if empty email field
		else if(CEDnewEmail.getText().toString().compareTo("") == 0)
		{
			Toast toast = GLOBAL.getToast(context, "Enter a new email address first.");
			toast.show();
		}
		//error if empty password field
		else if(CEDoldPassword.getText().toString().compareTo("") == 0)
		{
			Toast toast = GLOBAL.getToast(context, "You must enter your current password for account verification.");
			toast.show();
		}
		//error if new email is already equal to current email
		else if(CEDnewEmail.getText().toString().compareTo(GLOBAL.getCurrentUser().getEmail()) == 0)
		{
			Toast toast = GLOBAL.getToast(context, "Your email is already set to that!");
			toast.show();
			CEDnewEmail.setText("");
			CEDconfirmEmail.setText("");
		}
		//attempt to change email address
		else
		{
		    new ChangeEmailTask().execute("http://68.59.162.183/"
					+ "android_connect/update_email.php");
		}
	}

	public void deleteAccountButton(View view)
	{
		System.out.println("deleteAccount was activated.");
		//TODO: add code here to implement deleteAccountButton
		//dialog box:
		//you must comfirm your account deletion.  After confirmation, user will be logged out.  
		//Deleted accounts will be flagged as 'deleted='timestamp' and then accounts will deleted flag will be cleared 30 days after timestamp. (using mysql eventscheduler)
		//If user logs back in before the 30 days, the flag will be removed and therefore account will not be deleted from database.
		//call deleteAccount.php
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Delete Account");
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.deleteaccount_dialog, null);
		dialogBuilder.setView(dialogView);

		DADoldPassword = (EditText) dialogView.findViewById(R.id.passwordOldTextDAD);
		
		deleteAccountDialog = dialogBuilder.create();
		deleteAccountDialog.show();
	}
	
	public void ConfirmDeleteAccountButton(View view)
	{
		System.out.println("confirmdeleteaccountbutton was activated.");
		Context context = getApplicationContext();
		
		//error if empty password field
		if(DADoldPassword.getText().toString().compareTo("") == 0)
		{
			Toast toast = GLOBAL.getToast(context, "You must enter your current password for account verification.");
			toast.show();
		}
		else
		{
			//confirm dialog box
			AlertDialog dialog = new AlertDialog.Builder(
					SettingsActivity.this)
					.setMessage("Are you sure you want to delete your Grouple Account?  All your information may be permanently deleted.")
					.setCancelable(true)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(
										DialogInterface dialog, int id)
								{
									//attempt to delete Account
									  new DeleteAccountTask().execute("http://68.59.162.183/"
												+ "android_connect/delete_account.php");
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(
										DialogInterface dialog,
										int which)
								{
									deleteAccountDialog.cancel();
								}
							}).show();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			//do nothing, already here
		}
		return super.onOptionsItemSelected(item);
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
	
	//aSynch class to update account password
		private class ChangePasswordTask extends AsyncTask<String, Void, String>
		{
			@Override
			protected String doInBackground(String... urls)
			{
				System.out.println("updating password...");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("email", GLOBAL.getCurrentUser().getEmail()));
				nameValuePairs.add(new BasicNameValuePair("password", CPDoldPassword.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("newPassword", CPDnewPassword.getText().toString()));
					
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
						passwordAlertDialog.cancel();
					}
					else if (jsonObject.getString("success").toString().equals("0"))
					{
						System.out.println("Failed to update password!");
						Context context = getApplicationContext();
						Toast toast = GLOBAL.getToast(context, jsonObject.getString("message"));
						toast.show();
						//failed to update user password for some reason. see error in toast.
					}
				} 
				catch (Exception e)
				{
					Log.d("readJSONFeed", e.getLocalizedMessage());
				}
			}
		}
		
		//aSynch class to update email address
		private class ChangeEmailTask extends AsyncTask<String, Void, String>
		{
			@Override
			protected String doInBackground(String... urls)
			{
				
				System.out.println("updating email...");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("email", GLOBAL.getCurrentUser().getEmail()));
				nameValuePairs.add(new BasicNameValuePair("password", CEDoldPassword.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("newEmail", CEDnewEmail.getText().toString()));
					
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
						System.out.println("updating email complete!");
						Context context = getApplicationContext();
						Toast toast = GLOBAL.getToast(context, jsonObject.getString("message"));
						toast.show();
						
						//now that database has been updated, we must update any client-side values of email
						User user = GLOBAL.getCurrentUser();
						user.setEmail(CEDnewEmail.getText().toString());
						GLOBAL.setCurrentUser(user);
						
						//update user's sharepreferences information
						SharedPreferences.Editor editor = prefs.edit();
						editor.putString("session_email", CEDnewEmail.getText().toString());
						editor.apply();

						emailAlertDialog.cancel();
						
					}
					else if (jsonObject.getString("success").toString().equals("0"))
					{
						System.out.println("Failed to update email!");
						Context context = getApplicationContext();
						Toast toast = GLOBAL.getToast(context, jsonObject.getString("message"));
						toast.show();
						//failed to update user email for some reasons. see error messagein toast for reasons.
					}
				} 
				catch (Exception e)
				{
					Log.d("readJSONFeed", e.getLocalizedMessage());
				}
			}
		}
		
		//aSynch class to permanently delete your account
		private class DeleteAccountTask extends AsyncTask<String, Void, String>
		{
			@Override
			protected String doInBackground(String... urls)
			{
				System.out.println("deleting account...");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("email", GLOBAL.getCurrentUser().getEmail()));
				nameValuePairs.add(new BasicNameValuePair("password", DADoldPassword.getText().toString()));
							
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
						System.out.println("delete account complete!");
						Context context = getApplicationContext();
						deleteAccountDialog.cancel();
						
						//show user final exit message
						// display confirmation box
						AlertDialog dialog = new AlertDialog.Builder(
								SettingsActivity.this)
								.setMessage("Okay we've deleted your account but we would like to sincerely thank you for trying Grouple!  Please check your email for your Account Deletion Confirmation.")
								.setCancelable(true)
								.setPositiveButton("Exit",
										new DialogInterface.OnClickListener()
										{
											@Override
											public void onClick(
													DialogInterface dialog, int id)
											{
												//force logout on user
												GLOBAL.destroySession();
												Intent login = new Intent(SettingsActivity.this, LoginActivity.class);
												startActivity(login);
												Intent intent = new Intent("CLOSE_ALL");
												SettingsActivity.this.sendBroadcast(intent);
											}
										}).show();
						// if user dimisses the confirmation box, gets logged out also
						dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
						{

							@Override
							public void onCancel(DialogInterface dialog)
							{
								//force logout on user
								GLOBAL.destroySession();
								Intent login = new Intent(SettingsActivity.this, LoginActivity.class);
								startActivity(login);
								Intent intent = new Intent("CLOSE_ALL");
								SettingsActivity.this.sendBroadcast(intent);
							}
						});
						
						
						
					}
					else if (jsonObject.getString("success").toString().equals("0"))
					{
						System.out.println("Failed to delete account!");
						Context context = getApplicationContext();
						Toast toast = GLOBAL.getToast(context, jsonObject.getString("message"));
						toast.show();
						//failed to delete account for some reason. see error in toast.
					}
				} 
				catch (Exception e)
				{
					Log.d("readJSONFeed", e.getLocalizedMessage());
				}
			}
		}
}