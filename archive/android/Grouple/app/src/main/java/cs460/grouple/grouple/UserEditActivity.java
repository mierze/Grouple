package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Brett, Todd, Scott
 * UserEditActivity allows user to make changes to user profile information.
 */
public class UserEditActivity extends BaseActivity
{
	// Set up fields. Most are just for the camera.
	private ImageView iv;
	private Bitmap bmp;
	private User user;
	private EditText birthdayEditText;
	private EditText aboutEditText;
	private EditText locationEditText;
	private EditText nameEditText;
	private String birthday;
	private Button submitButton;
	private TextView errorTextView;
	private RadioButton maleButton;
	private RadioButton femaleButton;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Set the activity layout to activity_edit_profile.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_edit);
		iv = (ImageView) findViewById(R.id.editUserImageView);
		nameEditText = (EditText) findViewById(R.id.nameEditTextEPA);
		birthdayEditText = (EditText) findViewById(R.id.birthdayEditTextEPA);
		locationEditText = (EditText) findViewById(R.id.locationEditTextEPA);
		aboutEditText = (EditText) findViewById(R.id.aboutEditTextEPA);
		submitButton = (Button) findViewById(R.id.submitButtonEPA);
		errorTextView = (TextView) findViewById(R.id.errorTextView);
		maleButton = (RadioButton) findViewById(R.id.maleButton);
		femaleButton = (RadioButton) findViewById(R.id.femaleButton);
		user = GLOBAL.getCurrentUser();
		errorTextView.setVisibility(View.GONE);
		fetchData();
		initActionBar("Edit Profile", true);
	}



	private void fetchData()
	{
		user.fetchImage(this);
		user.fetchInfo(this);
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("user_data"));
		updateUI();
	}

	@Override
	protected void onPause()
	{
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
		super.onPause();

	}

	// This listens for pings from the data service to let it know that there
	// are updates
	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// Extract data included in the Intent
			String type = intent.getStringExtra("message");
			// repopulate views
			updateUI();
		}
	};
	
	

	public void genderRadioButton(View view)
	{
		
		switch (view.getId())
		{
		case R.id.maleButton:
			if (maleButton.isChecked())
			{
				femaleButton.setChecked(false);
			}
			break;
		case R.id.femaleButton:
			if (femaleButton.isChecked())
			{
				maleButton.setChecked(false);
			}
			break;
		}
	}

	/*
	 * Get profile executes get_profile.php. It uses the current users email
	 * address to retrieve the users name, age, and bio.
	 */
	private void updateUI()
	{
		// Add the info to the textviews for editing.
		nameEditText.setText(user.getName());
		birthdayEditText.setText(user.getBirthdayText());
		birthday = user.getBirthday();
		aboutEditText.setText(user.getAbout());
		locationEditText.setText(user.getLocation());
		if (!(user.getImage() == null))
			iv.setImageBitmap(user.getImage());
		if (user.getGender().equals("M"))
		{
			maleButton.setChecked(true);
		}
		else if (user.getGender().equals("F"))
		{
			femaleButton.setChecked(true);
		}
	}

	// Button Listener for selecting birthdate.
	public void selectBirthdate(View view)
	{
		int year, month, day;
		// if a previous birthdate had been set, use that date when initializing
		// datepicker
		if (!birthday.equalsIgnoreCase(""))
		{
			// parse the date out of textfield
			year = Integer.parseInt(birthday.substring(0, 4));
			month = Integer.parseInt(birthday.substring(5, 7));

			day = Integer.parseInt(birthday.substring(8, 10));
			System.out.println(year + " " + month + " " + day);
			new DatePickerDialog(this, myBirthdateListener, year, month - 1, day).show();
		}
		else
		{
			// get the current date to initialize datepicker
			Calendar calendar;
			calendar = Calendar.getInstance();
			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH);
			day = calendar.get(Calendar.DAY_OF_MONTH);
			System.out.println(year + " " + month + " " + day);
			new DatePickerDialog(this, myBirthdateListener, year, month, day).show();
		}
	}

	// Button Listener for submit changes. It updates the profile in the
	// database.
	public void submitButton(View view) throws InterruptedException, ExecutionException, TimeoutException
	{
		submitButton.setEnabled(false);
		// error checking:
		// bio no more than 100 characters
		String about = aboutEditText.getText().toString();
		if (about.length() > 100)
		{
			errorTextView.setText("About is too many characters.");
			errorTextView.setVisibility(View.VISIBLE);
			submitButton.setEnabled(true);
		}
		else
		{
			new setProfileTask().execute("http://http://mierze.gear.host/grouple/android_connect/update_profile.php");
		}
	}

	/*
	 * Set profile executes update_profile.php. It uses the current users email
	 * address to update the users name, birthdate, and bio.
	 */
	private class setProfileTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			return readJSONFeed(urls[0]);
		}

		// Grab the data from the textviews and push it to the database.
		public String readJSONFeed(String URL)
		{
			StringBuilder stringBuilder = new StringBuilder();
			HttpClient httpClient = new DefaultHttpClient();
			// kaboom
			HttpPost httpPost = new HttpPost(URL);
			try
			{
				String gender = "";
				if (maleButton.isChecked())
				{
					gender = "M";
				}
				else if (femaleButton.isChecked())
				{
					gender = "F";
				}
				String email = user.getEmail();
				String name = nameEditText.getText().toString();
				// Split name by space because sleep.
				String[] splitted = name.split("\\s+");
				String firstName = splitted[0];
				String lastName = splitted[1];
				String about = aboutEditText.getText().toString();
				String location = locationEditText.getText().toString();
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				byte[] data;
				// process photo if set and add it to builder
				if (bmp != null)
				{
					System.out.println("We are processing photo!");
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bmp.compress(CompressFormat.JPEG, 100, bos);
					data = bos.toByteArray();
					ByteArrayBody bab = new ByteArrayBody(data, ".jpg");
					builder.addPart("profilepic", bab);
					data = null;
					bab = null;
					bos.close();
					System.out.println("WE PROCESSED IT");
				}

				// add remaining fields to builder, then execute
				builder.addTextBody("first", firstName, ContentType.TEXT_PLAIN);
				builder.addTextBody("last", lastName, ContentType.TEXT_PLAIN);
				builder.addTextBody("age", birthday, ContentType.TEXT_PLAIN);
				builder.addTextBody("about", about, ContentType.TEXT_PLAIN);
				builder.addTextBody("location", location, ContentType.TEXT_PLAIN);
				builder.addTextBody("email", email, ContentType.TEXT_PLAIN);
				builder.addTextBody("gender", gender, ContentType.TEXT_PLAIN);
				httpPost.setEntity(builder.build());

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
					// cleanup
					inputStream.close();
					reader.close();
					bmp = null;
					builder = null;
					submitButton.setEnabled(true);
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
			return stringBuilder.toString();
		}

		// need to do more error checking.
		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				// success: user profile was either successfully updated (1) in
				// database or no changes were necesssary (2)
				if (jsonObject.getString("success").toString().equals("1")
						|| jsonObject.getString("success").toString().equals("2"))
				{
					System.out.println("IN SUCCESS PROFILE EDIT");
					// Success
					Toast toast = GLOBAL.getToast(UserEditActivity.this, "User profile changed successfully!");
					toast.show();
					System.out.println("Success");
					finish();
					// fail: likely a problem with profilepic processing on php
					// side
				}
				else if (jsonObject.getString("success").toString().equals("3"))
				{
					String imagetype = jsonObject.getString("case").toString();
					System.out.println("Profile pic sent for processing was of type: " + imagetype);
					Context context = getApplicationContext();
					Toast toast = GLOBAL.getToast(context, "Error updating profile.  Please try again!");
					toast.show();
					finish();
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	public void onClick(View v)
	{
		super.onClick(v);
		switch (v.getId())
		{
		case R.id.editUserImageButton:
			final CharSequence[] items =
			{ "Take Photo", "Choose from Gallery", "Cancel" };

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Choose your profile picture:");
			builder.setItems(items, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int item)
				{
					if (items[item].equals("Take Photo"))
					{
						Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(i, 1);
					}
					else if (items[item].equals("Choose from Gallery"))
					{
						Intent intent = new Intent(Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						intent.setType("image/*");
						startActivityForResult(Intent.createChooser(intent, "Select Photo"), 2);
					}
					else if (items[item].equals("Cancel"))
					{
						dialog.dismiss();
					}
				}
			});
			builder.show();
			break;
		}
	}

	@Override
	protected void onActivityResult(int reqCode, int resCode, Intent data)
	{
		super.onActivityResult(reqCode, resCode, data);
		if (resCode == RESULT_OK)
		{
			if (reqCode == 1)
			{
				Bundle extras = data.getExtras();
				bmp = (Bitmap) extras.get("data");
				iv.setImageBitmap(bmp);
			}
			else if (reqCode == 2)
			{
				Uri selectedImageUri = data.getData();
				try
				{
					bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
				}
				catch (FileNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				iv.setImageURI(selectedImageUri);
			}
		}
	}

	private DatePickerDialog.OnDateSetListener myBirthdateListener = new DatePickerDialog.OnDateSetListener()
	{
		@Override
		public void onDateSet(DatePicker view, int year, int month, int day)
		{
			if (view.isShown())
			{
				// TODO: make a formatter in GLOBAL for this
				int tmpMonth = month + 1;
				// add missing '0' digit to months and day
				if (tmpMonth < 10 && day < 10)
				{
					birthday = year + "-0" + tmpMonth + "-0" + day;
				}
				// add missing '0' digit to just months
				else if (tmpMonth < 10)
				{
					birthday = year + "-0" + tmpMonth + "-" + day;
				}
				// add missing '0' digit to just days
				else if (day < 10)
				{
					birthday = year + "-" + tmpMonth + "-0" + day;
				}
				else
				{
					birthday = year + "-" + tmpMonth + "-" + day;
				}
				birthdayEditText.setText(GLOBAL.toYearTextFormatFromRawNoTime(birthday));
			}
		}
	};
}
