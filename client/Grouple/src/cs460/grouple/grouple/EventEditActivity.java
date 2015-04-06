package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


//TODO: clean up the date formatting to one function / clean up in general

/*
 * EventEditActivity allows user to make changes to his/her event.
 */
public class EventEditActivity extends BaseActivity
{
	// Set up fields. Most are just for the camera.
	private ImageView iv;
	private Bitmap bmp;
	private Event event;
	private String startDate;
	private String endDate;
	private EditText nameEditText;
	private EditText categoryEditText;
	private EditText aboutEditText;
	private EditText locationEditText;
	private EditText minEditText;
	private EditText maxEditText;
	private EditText startEditText;
	private EditText endEditText;
	private Calendar currentCal;
	private Calendar startCal;
	private Calendar endCal;
	private int year, month, day, hour, minute;
	private AlertDialog categoryDialog;
	private Bundle EXTRAS;
	private TextView errorTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Set the activity layout to activity_edit_profile.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_edit);
		load();
	}

	private void load()
	{
		// init variables
		currentCal = Calendar.getInstance();
		year = currentCal.get(Calendar.YEAR);
		month = currentCal.get(Calendar.MONTH);
		day = currentCal.get(Calendar.DAY_OF_MONTH);
		hour = currentCal.get(Calendar.HOUR_OF_DAY);
		minute = currentCal.get(Calendar.MINUTE);
		// Resetting error text view
		errorTextView = (TextView) findViewById(R.id.errorTextViewEPA);
		EXTRAS = getIntent().getExtras();
		iv = (ImageView) findViewById(R.id.editEventImageView);
		event = GLOBAL.getEventBuffer();
		if (event != null)
			getEventProfile();
		initActionBar("Edit " + event.getName(), true);
	}
	

	/* TASK FOR GRABBING IMAGE OF EVENT/USER/GROUP */
	private class getImageTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String type;
			String id;
			type = "eid";
			id = Integer.toString(event.getID());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair(type, id));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				// json fetch was successful
				if (jsonObject.getString("success").toString().equals("1"))
				{
					String image = jsonObject.getString("image").toString();
					event.setImage(image);
					iv.setImageBitmap(event.getImage());
				} else
				{
					// failed
					Log.d("getImage", "FAILED");
				}
			} catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
			// do next thing here
		}
	}

	/*
	 * Get profile executes get_groupprofile.php. It uses the current groups gid
	 * to retrieve the groups name, about, and other info.
	 */
	private void getEventProfile()
	{
		// Find the edit texts.
		nameEditText = (EditText) findViewById(R.id.nameEditTextEPA);
		categoryEditText = (EditText) findViewById(R.id.categoryEditTextEPA);
		aboutEditText = (EditText) findViewById(R.id.aboutEditTextEPA);
		locationEditText = (EditText) findViewById(R.id.locationEditTextEPA);
		minEditText = (EditText) findViewById(R.id.minPartButtonEEA);
		maxEditText = (EditText) findViewById(R.id.maxPartButtonEEA);
		startEditText = (EditText) findViewById(R.id.startTimeButtonEEA);
		endEditText = (EditText) findViewById(R.id.endTimeButtonEEA);
		// Add the info to the edittexts.
		nameEditText.setText(event.getName());
		categoryEditText.setText(event.getCategory());
		aboutEditText.setText(event.getAbout());
		locationEditText.setText(event.getLocation());
		if (event.getImage() == null)
			new getImageTask();
		else
			iv.setImageBitmap(event.getImage());
		minEditText.setText(String.valueOf(event.getMinPart()));
		if (event.getMaxPart() > 0)
			maxEditText.setText(String.valueOf(event.getMaxPart()));
		startEditText.setText(event.getStartText());
		startDate = event.getStartDate();
		endEditText.setText(event.getEndText());
		endDate = event.getEndDate();
	}
	
	// Button Listener for submit changes.
	public void submitButton(View view)
	{
		// Checking user inputs on event name, category, start date, end date,
		// and min_part
		String eventname = nameEditText.getText().toString();
		startDate.concat(":00");
		endDate.concat(":00");
		System.out.println("startdate to be used is: " + startDate);
		String category = categoryEditText.getText().toString();
		String minimum = minEditText.getText().toString();
		String maximum = maxEditText.getText().toString();
		Date start = null;
		Date end = null;
		if (!(startDate.compareTo("") == 0) && !(endDate.compareTo("") == 0))
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d hh:mm");
			// SimpleDateFormat dateFormat = new
			// SimpleDateFormat("EEEE, MMMM DD, h:mma");
			try
			{
				start = sdf.parse(startDate);
				end = sdf.parse(endDate);
			} 
			catch (ParseException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("Dates: " + start + " nn " + end);
			// startDate = sdf.format(start);
			// endDate = sdf.format(end);
			if (start.compareTo(end) > 0)

				System.out.println("Start is after End");
			else if (start.compareTo(end) < 0)
				System.out.println("start is before end");
			else if (start.compareTo(end) == 0)
				System.out.println("start is equal to end");
			else
				System.out.println("How to get here?");
		}
		// if empty group name, display error box
		if (eventname.compareTo("") == 0)
		{
			new AlertDialog.Builder(this)
					.setMessage("you must specify a Name for your event.")
					.setCancelable(true).setNegativeButton("Ok", null).show();
		}
		// if empty start or end date
		else if (startDate.compareTo("") == 0 || endDate.compareTo("") == 0)
		{
			new AlertDialog.Builder(this)
					.setMessage("You must specify a Start Date and End Date.")
					.setCancelable(true).setNegativeButton("Ok", null).show();
		}
		// if endDate is prior to startDate
		else if (start.compareTo(end) >= 0)
		{
			new AlertDialog.Builder(this)
					.setMessage(
							"Your Start Date must come before your End Date.")
					.setCancelable(true).setNegativeButton("Ok", null).show();
		}
		// if empty category
		else if (category.compareTo("") == 0)
		{
			new AlertDialog.Builder(this)
					.setMessage("You must specify a Category for your event.")
					.setCancelable(true).setNegativeButton("Ok", null).show();
		}
		// if empty minimum

		// if maximum is set and it is less than minimum
		else if (!(maximum.compareTo("") == 0) && !(maximum.compareTo("") == 0)
				&& (Integer.parseInt(maximum) < Integer.parseInt(minimum)))
		{
			new AlertDialog.Builder(this)
					.setMessage(
							"Your Minimum size cannot be larger than your Maximum size.")
					.setCancelable(true).setNegativeButton("Ok", null).show();
		}
		// otherwise, display confirmation box to proceed
		else
		{
			new SetProfileTask().execute("http://68.59.162.183/"
					+ "android_connect/update_event.php");
		}
	}

	// onClick for start date button
	public void selectStartDateButton(View view)
	{
		System.out.println("clicked on startdate");
		// startDate is not currently set. load datepicker set to current
		// calendar date.
		if (startEditText.getText().toString().compareTo("") == 0)
		{
			new DatePickerDialog(this, myStartDateListener, year, month, day)
					.show();
		}
		// load the datepicker using the date that was previously set in
		// startDate
		else
		{
			startCal = Calendar.getInstance();

			// parse to our calendar object
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			try
			{
				startCal.setTime(sdf.parse(startDate));
				System.out.println("cal was parsed from tmpStartDate!");
			} catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// all done

			new DatePickerDialog(this, myStartDateListener,
					startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH),
					startCal.get(Calendar.DAY_OF_MONTH)).show();
		}
	}

	// onClick for end date button
	public void selectEndDateButton(View view)
	{
		System.out.println("clicked on enddate");
		// endDate is not currently set. load datepicker set to current
		// calendar date.
		if (endEditText.getText().toString().compareTo("") == 0)
		{
			if (startEditText.getText().toString().compareTo("") == 0)
			{
				new DatePickerDialog(this, myEndDateListener, year, month, day)
						.show();
			} 
			else
			{
				endCal = Calendar.getInstance();

				// parse to our calendar object
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				try
				{
					endCal.setTime(sdf.parse(endDate));
					System.out.println("cal was parsed from tmpStartDate!");
				} catch (ParseException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// all done

				new DatePickerDialog(this, myEndDateListener,
						endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH),
						endCal.get(Calendar.DAY_OF_MONTH)).show();
			}

		}
		// load the datepicker using the date that was previously set in endDate
		else
		{
			endCal = Calendar.getInstance();

			// parse to our calendar object
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			try
			{
				if (startDate.compareTo(endDate) > 0)
				{
					endCal.setTime(sdf.parse(startDate));
				}
				else
					endCal.setTime(sdf.parse(endDate));
				System.out.println("cal was parsed from tmpEndDate!");
			} catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// all done

			new DatePickerDialog(this, myEndDateListener,
					endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH),
					endCal.get(Calendar.DAY_OF_MONTH)).show();
		}
	}

	// Button Listener for when user clicks on category.
	public void selectCategoryButton(View view)
	{
		// THINKING OUT LOUD
		// Food
		// Entertainment
		// Sports / Games
		// Party / Nightlife ?SOCIAL?
		// Professional / Education
		// Community
		// Strings to Show In Dialog with Radio Buttons
		final CharSequence[] items =
		{ "Social ", "Sports + Games ", "Professional ", "Entertainment ", "Food" };
		// Creating and Building the Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select your category");
		builder.setSingleChoiceItems(items, -1,
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int item)
					{
						switch (item)
						{
						case 0:
							categoryEditText.setText(items[0]);
							break;
						case 1:
							categoryEditText.setText(items[1]);
							break;
						case 2:
							categoryEditText.setText(items[2]);
							break;
						case 3:
							categoryEditText.setText(items[3]);
							break;
						case 4:
							categoryEditText.setText(items[4]);
							break;
						}
						categoryDialog.cancel();
					}
				});
		categoryDialog = builder.create();
		categoryDialog.show();
	}

	/*
	 * Set profile executes update_profile.php. It uses the current users email
	 * address to update the users name, age, and bio.
	 */
	private class SetProfileTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			return readJSONFeed(urls[0]);
		}

		// Grab the data from the editTexts and push it to the database.
		public String readJSONFeed(String URL)
		{
			StringBuilder stringBuilder = new StringBuilder();
			HttpClient httpClient = new DefaultHttpClient();
			// update_event.php using name/value pair
			HttpPost httpPost = new HttpPost(URL);
			try
			{
				MultipartEntityBuilder builder = MultipartEntityBuilder
						.create();
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				byte[] data;
				System.out.println("about to proceess photo");
				// process photo if set and add it to builder
				if (bmp != null)
				{
					System.out.println("We are processing photo!");
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bmp.compress(CompressFormat.JPEG, 100, bos);
					data = bos.toByteArray();
					ByteArrayBody bab = new ByteArrayBody(data, ".jpg");
					builder.addPart("pic", bab);
					data = null;
					bab = null;
					bos.close();
				}
				System.out.println("Done with proceess photo");
				System.out.println("about to add other fields");
				// add remaining fields to builder (g_name, about, public,
				// g_id), then execute
				builder.addTextBody("e_name",
						nameEditText.getText().toString(),
						ContentType.TEXT_PLAIN);
				builder.addTextBody("about",
						aboutEditText.getText().toString(),
						ContentType.TEXT_PLAIN);
				builder.addTextBody("e_id", Integer.toString(event.getID()),
						ContentType.TEXT_PLAIN);
				System.out.println("About to add start date as " + startDate);
				builder.addTextBody("start_date", startDate,
						ContentType.TEXT_PLAIN);
				builder.addTextBody("end_date", endDate, ContentType.TEXT_PLAIN);
				builder.addTextBody("category", categoryEditText.getText()
						.toString(), ContentType.TEXT_PLAIN);
				builder.addTextBody("min_part", minEditText.getText()
						.toString(), ContentType.TEXT_PLAIN);
				builder.addTextBody("max_part", maxEditText.getText()
						.toString(), ContentType.TEXT_PLAIN);
				builder.addTextBody("location", locationEditText.getText()
						.toString(), ContentType.TEXT_PLAIN);
				// mustbringlist not implemented in client yet
				builder.addTextBody("mustbringlist", "", ContentType.TEXT_PLAIN);
				System.out.println("done adding other fields");
				httpPost.setEntity(builder.build());
				HttpResponse response = httpClient.execute(httpPost);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200)
				{
					HttpEntity entity = response.getEntity();
					InputStream inputStream = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(inputStream));
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
				} else
				{
					Log.d("SetProfileJSON", "Failed to download file");
				}
			} catch (Exception e)
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
				// success: event profile was either successfully updated in
				// database, or no changes were necesssary
				if (jsonObject.getString("success").toString().equals("1")
						|| jsonObject.getString("success").toString()
								.equals("2"))
				{
					// Success
					Context context = getApplicationContext();
					Toast toast = GLOBAL.getToast(context,
							"Event profile changed successfully!");
					toast.show();
					event.fetchEventInfo();
					event.fetchParticipants();
					GLOBAL.setEventBuffer(event);
					finish();
				} else
				{
					// Fail
					Toast toast = GLOBAL.getToast(EventEditActivity.this,
							"Failed to update event profile.");
					toast.show();
				}
			} catch (Exception e)
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
		case R.id.editEventImageButton:
			AlertDialog.Builder builder = getImageBuilder(this);
			//builder 
			builder.show();
			break;
		case R.id.manageEventButton:
			Intent intent = new Intent(this, ManageParticipantsActivity.class);
			intent.putExtra("EMAIL", GLOBAL.getCurrentUser().getEmail());
			intent.putExtra("EID", event.getID());
			startActivity(intent);
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
					bmp = MediaStore.Images.Media.getBitmap(
							this.getContentResolver(), selectedImageUri);
				} catch (FileNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				iv.setImageURI(selectedImageUri);
			}
		}
	}

	private DatePickerDialog.OnDateSetListener myStartDateListener = new DatePickerDialog.OnDateSetListener()
	{

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day)
		{
			if (view.isShown())
			{
				int tmpMonth = month + 1;
				startEditText.setText(year + "-" + tmpMonth + "-" + day);
				startDate = year + "-" + tmpMonth + "-" + day;

				// start the TimePicker using hour and minute previously set in
				// startCal
				if (startCal != null)
				{
					System.out.println("Hour:"
							+ startCal.get(Calendar.HOUR_OF_DAY));
					System.out.println("Minute:"
							+ startCal.get(Calendar.MINUTE));
					new TimePickerDialog(EventEditActivity.this,
							myStartTimeListener,
							startCal.get(Calendar.HOUR_OF_DAY),
							startCal.get(Calendar.MINUTE), false).show();
				}
				// start the TimePicker using current system time
				else
				{
					new TimePickerDialog(EventEditActivity.this,
							myStartTimeListener, hour, minute, false).show();
				}
			}
		}
	};

	private TimePickerDialog.OnTimeSetListener myStartTimeListener = new TimePickerDialog.OnTimeSetListener()
	{
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute)
		{
			if (view.isShown())
			{
				startDate += " " + hourOfDay + ":" + minute;
				startEditText.setText(GLOBAL
						.toDayTextFormatFromRawNoSeconds(startDate));
			}
		}
	};

	private DatePickerDialog.OnDateSetListener myEndDateListener = new DatePickerDialog.OnDateSetListener()
	{
		@Override
		public void onDateSet(DatePicker view, int year, int month, int day)
		{
			if (view.isShown())
			{
				int tmpMonth = month + 1;
				endEditText.setText(year + "-" + tmpMonth + "-" + day);
				endDate = year + "-" + tmpMonth + "-" + day;

				// start the TimePicker using hour and minute previously set in
				// startCal
				if (endCal != null)
				{
					System.out.println("Hour:"
							+ endCal.get(Calendar.HOUR_OF_DAY));
					System.out.println("Minute:" + endCal.get(Calendar.MINUTE));
					new TimePickerDialog(EventEditActivity.this,
							myEndTimeListener,
							endCal.get(Calendar.HOUR_OF_DAY),
							endCal.get(Calendar.MINUTE), false).show();
				}
				//TODO: add check if start date changed greater than end date, make this start there
				else if (startDate.compareTo(endDate) > 0)
				{
					new TimePickerDialog(EventEditActivity.this,
							myEndTimeListener,
							startCal.get(Calendar.HOUR_OF_DAY),
							startCal.get(Calendar.MINUTE), false).show();
				}
				// start the TimePicker using current system time
				else
				{
					new TimePickerDialog(EventEditActivity.this,
							myEndTimeListener, hour, minute, false).show();
				}
			}
		}
	};

	private TimePickerDialog.OnTimeSetListener myEndTimeListener = new TimePickerDialog.OnTimeSetListener()
	{
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute)
		{
			if (view.isShown())
			{
				endDate += " " + hourOfDay + ":" + minute;
				endEditText.setText(GLOBAL
						.toDayTextFormatFromRawNoSeconds(endDate));
			}
		}
	};
}