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
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/*
 * EventEditActivity allows user to make changes to his/her event.
 */
public class EventEditActivity extends BaseActivity
{
	// Set up fields. Most are just for the camera.
	private ImageView iv;
	private Bitmap bmp;
	private Event event;
	private String ID;
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
	private EditText recurringButton;
	private Calendar currentCal;
	private Calendar startCal;
	private Calendar endCal;
	private int year, month, day, hour, minute;
	private AlertDialog categoryDialog;
	private AlertDialog recurringDialog;
	private Bundle EXTRAS;
	private LayoutInflater inflater;
	private TextView errorTextView;
	private AlertDialog toBringDialog;
	private Button addToBringRowButton;
	private Button toBringButton;
	private Button manageEventButton;
	private Button submitButton;
	private View toBringLayout;
	private String recurring;
	private User user;
	private final ArrayList<EditText> toBringEditTexts = new ArrayList<EditText>();
	private ArrayList<EventItem> items = new ArrayList<EventItem>();

	// TODO: Get from database items to bring and populate the edittexts,
	// set the corresponding number to the button text,
	// on to bring button pressed populate rows with those texts,
	// allow for deletions
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
		// Find the edit texts.
		nameEditText = (EditText) findViewById(R.id.nameEditText);
		categoryEditText = (EditText) findViewById(R.id.categoryEditText);
		aboutEditText = (EditText) findViewById(R.id.aboutEditText);
		locationEditText = (EditText) findViewById(R.id.locationEditText);
		minEditText = (EditText) findViewById(R.id.minPartButton);
		maxEditText = (EditText) findViewById(R.id.maxPartButton);
		startEditText = (EditText) findViewById(R.id.startTimeButton);
		endEditText = (EditText) findViewById(R.id.endTimeButton);
		toBringButton = (Button) findViewById(R.id.toBringButton);
		manageEventButton = (Button) findViewById(R.id.manageEventButton);
		recurringButton = (EditText) findViewById(R.id.recurringButton);
		submitButton = (Button) findViewById(R.id.submitButton);
		// init variables
		currentCal = Calendar.getInstance();
		year = currentCal.get(Calendar.YEAR);
		month = currentCal.get(Calendar.MONTH);
		day = currentCal.get(Calendar.DAY_OF_MONTH);
		hour = currentCal.get(Calendar.HOUR_OF_DAY);
		minute = currentCal.get(Calendar.MINUTE);
		inflater = getLayoutInflater();
		user = GLOBAL.getCurrentUser();
		errorTextView = (TextView) findViewById(R.id.errorTextViewEPA);
		EXTRAS = getIntent().getExtras();
		iv = (ImageView) findViewById(R.id.eventEditImageView);
		event = GLOBAL.getEvent(EXTRAS.getInt("e_id"));

		// changes to layout in special case of repropose
		if (EXTRAS.containsKey("reproposed"))
		{
			System.out.println("case will be reproposeEvent");
			initActionBar("Repropose " + event.getName(), true);
			manageEventButton.setVisibility(View.GONE);
			submitButton.setText("Repropose Event");
			Toast toast = GLOBAL
					.getToast(this, "Note: You'll need to pick new Start and End Dates before reproposing.");
			toast.setDuration(Toast.LENGTH_LONG);
			toast.show();
		}
		// normal editEventActivity
		else
		{
			System.out.println("case will be normal editEvent");
			initActionBar("Edit " + event.getName(), true);
		}

		// grabbing items to bring from server

	}

	/*
	 * Get profile executes get_eventprofile.php. It uses the current groups eid
	 * to retrieve the groups name, about, and other info.
	 */
	private void updateUI()
	{
		// Add the info to the edittexts.
		nameEditText.setText(event.getName());
		categoryEditText.setText(event.getCategory());
		aboutEditText.setText(event.getAbout());
		locationEditText.setText(event.getLocation());
		iv.setImageBitmap(event.getImage());
		minEditText.setText(String.valueOf(event.getMinPart()));
		if (event.getMaxPart() > 0)
		{
			maxEditText.setText(String.valueOf(event.getMaxPart()));
		}

		toBringButton.setText("Items (" + items.size() + ")");
		startEditText.setText(event.getStartText());
		startDate = event.getStartDate();
		endEditText.setText(event.getEndText());
		endDate = event.getEndDate();
		recurring = event.getRecurringType();
		if (recurring.equals("A"))
		{
			recurringButton.setText("Recurring Event: Anually");
		}
		else if (recurring.equals("M"))
		{
			recurringButton.setText("Recurring Event: Monthly");
		}
		else if (recurring.equals("W"))
		{
			recurringButton.setText("Recurring Event: Weekly");
		}
		else if (recurring.equals("O"))
		{
			recurringButton.setText("One-time Event");
		}
	}

	private void fetchData()
	{
		event.fetchInfo(this);
		event.fetchItems(this);
		event.fetchImage(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("event_data"));
		fetchData();
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

	// onClick for items to bring
	public void toBringButton(View view)
	{
		// Creating and Building the Dialog
		toBringEditTexts.clear();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add Items To Bring");
		toBringLayout = inflater.inflate(R.layout.dialog_tobring, null);
		final LinearLayout layout = (LinearLayout) toBringLayout.findViewById(R.id.toBringInnerLayout);
		this.addToBringRowButton = (Button) toBringLayout.findViewById(R.id.toBringAddRowButton);

		// adding each item to the edit texts
		for (EventItem item : items)
		{
			View editTextLayout = inflater.inflate(R.layout.list_item_edittext, null);
			EditText toBringEditText = (EditText) editTextLayout.findViewById(R.id.toBringEditText);
			toBringEditText.setText(item.getName());
			toBringEditTexts.add(toBringEditText);
			layout.addView(editTextLayout);
		}

		// add a new blank row at end
		View editTextLayout = inflater.inflate(R.layout.list_item_edittext, null);
		EditText toBringEditText = (EditText) editTextLayout.findViewById(R.id.toBringEditText);
		toBringEditTexts.add(toBringEditText);
		layout.addView(editTextLayout);
		toBringEditText.requestFocus();

		addToBringRowButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				try
				{
					View editTextLayout = inflater.inflate(R.layout.list_item_edittext, null);
					EditText toBringEditText = (EditText) editTextLayout.findViewById(R.id.toBringEditText);
					toBringEditTexts.add(toBringEditText);
					layout.addView(editTextLayout);
				}
				catch (Exception e)
				{
					Log.d("ASDF", "Failed to create new edit text");
				}
			}
		});
		builder.setView(toBringLayout);
		toBringDialog = builder.create();
		toBringDialog.show();
	}

	public void saveToBringListButton(View view)
	{
		// first clear out final list to avoid any duplicate entries being added
		// items.clear();
		for (EditText toBringEditText : toBringEditTexts)
		{
			// when list is saved, save to final list but ignore any blank line
			// entries
			String name = toBringEditText.getText().toString();
			if (!name.equals(""))
			{
				int index = toBringEditTexts.indexOf(toBringEditText);
				EventItem item = items.get(index);
				if (item != null)
				{
					// check names
					if (!name.equals(item.getName()))
					{
						// update name
						items.get(index).setName(name);
					}
				}
				else
				{
					// item doesn't exist, add it
					item = new EventItem(-1, toBringEditText.getText().toString(), "");
					items.add(item);
				}
			}
		}
		updateItemChecklist();
		toBringDialog.dismiss();

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
			new AlertDialog.Builder(this).setMessage("you must specify a Name for your event.").setCancelable(true)
					.setNegativeButton("Ok", null).show();
		}
		// if empty start or end date
		else if (startDate.compareTo("") == 0 || endDate.compareTo("") == 0)
		{
			new AlertDialog.Builder(this).setMessage("You must specify a Start Date and End Date.").setCancelable(true)
					.setNegativeButton("Ok", null).show();
		}
		// if endDate is prior to startDate
		else if (start.compareTo(end) >= 0)
		{
			new AlertDialog.Builder(this).setMessage("Your Start Date must come before your End Date.")
					.setCancelable(true).setNegativeButton("Ok", null).show();
		}
		// if empty category
		else if (category.compareTo("") == 0)
		{
			new AlertDialog.Builder(this).setMessage("You must specify a Category for your event.").setCancelable(true)
					.setNegativeButton("Ok", null).show();
		}
		// if empty minimum

		// if maximum is set and it is less than minimum
		else if (!(maximum.compareTo("") == 0) && !(maximum.compareTo("") == 0)
				&& (Integer.parseInt(maximum) < Integer.parseInt(minimum)))
		{
			new AlertDialog.Builder(this).setMessage("Your Minimum size cannot be larger than your Maximum size.")
					.setCancelable(true).setNegativeButton("Ok", null).show();
		}
		// otherwise, display confirmation box to proceed
		else
		{
			// special case of reproposed event
			if (getIntent().getExtras().containsKey("reproposed"))
			{
				new AlertDialog.Builder(this).setMessage("Are you sure you want to repropose this event?")
						.setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int id)
							{
								// initiate creation of event
								new SetProfileTask().execute("http://68.59.162.183/"
										+ "android_connect/create_event.php");
							}
						}).setNegativeButton("Cancel", null).show();
			}
			// normal editEvent
			else
			{
				new SetProfileTask().execute("http://68.59.162.183/" + "android_connect/update_event.php");
			}

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
			DatePickerDialog dpd;
			dpd = new DatePickerDialog(this, myStartDateListener, year, month, day);
			dpd.show();
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
			}
			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// all done

			DatePickerDialog dpd;
			dpd = new DatePickerDialog(this, myStartDateListener, startCal.get(Calendar.YEAR),
					startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH));
			dpd.show();
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
				DatePickerDialog dpd = new DatePickerDialog(this, myEndDateListener, year, month, day);
				dpd.show();
			}
			else
			{
				endCal = Calendar.getInstance();

				// parse to our calendar object
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				try
				{
					endCal.setTime(sdf.parse(startDate));
					System.out.println("cal was parsed from tmpStartDate!");
				}
				catch (ParseException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// all done

				DatePickerDialog dpd = new DatePickerDialog(this, myEndDateListener, endCal.get(Calendar.YEAR),
						endCal.get(Calendar.MONTH), endCal.get(Calendar.DAY_OF_MONTH));
				dpd.show();
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
			}
			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// all done

			DatePickerDialog dpd = new DatePickerDialog(this, myEndDateListener, endCal.get(Calendar.YEAR),
					endCal.get(Calendar.MONTH), endCal.get(Calendar.DAY_OF_MONTH));
			dpd.show();
		}
	}

	// onClick for select recurring
	public void selectRecurring(View view)
	{
		System.out.println("clicked on recurring button");
		// display a dialog box that allows user to choose recurrig options
		// (Annually, monthly, or weekly)

		// Strings to Show In Dialog with Radio Buttons
		final CharSequence[] items =
		{ "Just Once", "Weekly", "Monthly", "Annually" };

		// Creating and Building the Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("How often do you want this event to occur?");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int item)
			{
				switch (item)
				{
				case 0:
					recurringButton.setText("One-time Event");
					recurring = "O";
					break;
				case 1:
					recurringButton.setText("Recurring Event: " + items[1]);
					recurring = "W";
					break;
				case 2:
					recurringButton.setText("Recurring Event: " + items[2]);
					recurring = "M";
					break;
				case 3:
					recurringButton.setText("Recurring Event: " + items[3]);
					recurring = "A";
					break;
				}
				recurringDialog.cancel();
			}
		});
		recurringDialog = builder.create();
		recurringDialog.show();

	}

	// Button Listener for when user clicks on category.
	public void selectCategoryButton(View view)
	{
		// Strings to Show In Dialog with Radio Buttons
		final CharSequence[] items =
		{ "Social", "Entertainment", "Professional", "Fitness", "Nature" };
		// Creating and Building the Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select your category");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener()
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
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				byte[] data;

				// check to see if this a reproposed event and if pic has not
				// been reset, re-use the old image loaded from old event
				if (bmp == null && EXTRAS.containsKey("reproposed"))
				{
					bmp = event.getImage();
				}

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
					System.out.println("Done with proceess photo.");
				}
				else
				{
					System.out.println("No photo set... skipping image processing.");
				}

				System.out.println("about to add other fields");
				// add remaining fields to builder (g_name, about, public,
				// g_id), then execute
				builder.addTextBody("e_name", nameEditText.getText().toString(), ContentType.TEXT_PLAIN);
				builder.addTextBody("about", aboutEditText.getText().toString(), ContentType.TEXT_PLAIN);
				// Note: e_id only used in case of editEvent case, however we
				// can send it either way
				builder.addTextBody("e_id", Integer.toString(event.getID()), ContentType.TEXT_PLAIN);
				System.out.println("About to add start date as " + startDate);
				builder.addTextBody("start_date", startDate, ContentType.TEXT_PLAIN);
				builder.addTextBody("end_date", endDate, ContentType.TEXT_PLAIN);
				builder.addTextBody("recurring", recurring, ContentType.TEXT_PLAIN);
				builder.addTextBody("recurring_type", recurring, ContentType.TEXT_PLAIN);
				builder.addTextBody("category", categoryEditText.getText().toString(), ContentType.TEXT_PLAIN);
				builder.addTextBody("min_part", minEditText.getText().toString(), ContentType.TEXT_PLAIN);
				builder.addTextBody("max_part", maxEditText.getText().toString(), ContentType.TEXT_PLAIN);
				builder.addTextBody("location", locationEditText.getText().toString(), ContentType.TEXT_PLAIN);
				// Note: creator only used in case of reproposeEvent case,
				// however we can send it either way
				builder.addTextBody("creator", GLOBAL.getCurrentUser().getEmail(), ContentType.TEXT_PLAIN);
				// loop through toBringList, adding each member into php array
				// toBring[]

				System.out.println("done adding other fields");
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
				}
				else
				{
					Log.d("SetProfileJSON", "Failed to download file");
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
				// case of reproposed (not editevent)
				if (EXTRAS.containsKey("reproposed"))
				{
					// reproposed event has been successfully created
					if (jsonObject.getString("success").toString().equals("1"))
					{
						// now we can grab the newly created e_id returned from
						// the
						// server
						// Note: e_id is the only unique identifier of an event
						// and
						// therefore must be used for any future calls
						// concerning
						// that event.
						ID = jsonObject.getString("e_id").toString();
						System.out.println("MEssage: " + jsonObject.getString("message"));
						System.out.println("e_id of newly created group is: " + ID);

						// display confirmation box
						AlertDialog dialog = new AlertDialog.Builder(EventEditActivity.this)
								.setMessage("You've successfully reproposed an event!").setCancelable(true)
								.setPositiveButton("Invite Groups to Your Event", new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface dialog, int id)
									{
										// code here to take user to
										// eventaddmembersactivity page.
										// (pass e_id as extra so invites
										// can be sent to correct event id)
										Intent intent = new Intent(EventEditActivity.this, EventAddGroupsActivity.class);
										intent.putExtra("CONTENT", "EVENT");
										intent.putExtra("e_id", ID);
										intent.putExtra("email", user.getEmail());
										startActivity(intent);
										finish();
									}
								}).setNegativeButton("View Your Event's Profile", new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface dialog, int which)
									{
										// code here to take user to newly
										// created event profile page. (pass
										// e_id as extra so correct event
										// profile can be loaded)
										Intent intent = new Intent(EventEditActivity.this, EventProfileActivity.class);
										intent.putExtra("e_id", Integer.parseInt(ID));
										intent.putExtra("email", user.getEmail());
										startActivity(intent);
										finish();
									}
								}).show();
						// if user dimisses the confirmation box, gets sent to
						// back
						// to eventActivity.class
						dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
						{

							@Override
							public void onCancel(DialogInterface dialog)
							{
								finish();
							}
						});
					}
					// Create event failed for some reasons. Allow user to
					// retry.
					else if (jsonObject.getString("success").toString().equals("0"))
					{
						// display error box
						new AlertDialog.Builder(EventEditActivity.this)
								.setMessage("Unable to repropose the event! Please choose an option:")
								.setCancelable(true)
								.setPositiveButton("Try Again", new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface dialog, int id)
									{
										// initiate creation of event AGAIN
										new SetProfileTask().execute("http://68.59.162.183/"
												+ "android_connect/create_event.php");
									}
								}).setNegativeButton("Cancel", null).show();
					}
				}
				// case of normal editEvent
				else
				{
					// success: event profile was either successfully updated in
					// database, or no changes were necesssary
					if (jsonObject.getString("success").toString().equals("1")
							|| jsonObject.getString("success").toString().equals("2"))
					{
						// Success
						Context context = getApplicationContext();
						Toast toast = GLOBAL.getToast(context, "Event profile changed successfully!");
						toast.show();
						finish();
					}
					else
					{
						// Fail
						Toast toast = GLOBAL.getToast(EventEditActivity.this, "Failed to update event profile.");
						toast.show();
					}
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
				System.out.println(result);
			}
		}
	}

	private void updateItemChecklist()
	{
		for (EventItem item : items)
		{
			String id = "";
			if (item.getID() >= 0)
				Integer.toString(item.getID());
			// grab the email of friend to add
			String email = item.getEmail();
			String name = item.getName();
			// grab the role of friend to add
			if (email.equals(user.getEmail()) || email.equals(""))
			{
				new updateItemChecklistTask().execute("http://68.59.162.183/android_connect/update_item_checklist.php",
						id, email, name);
			}
		}
	}

	// TODO: make this submit no id so that php catches it and lets it auto-inc
	private class updateItemChecklistTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("id", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("email", urls[2]));
			nameValuePairs.add(new BasicNameValuePair("name", urls[3]));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					System.out.println("WE HAD SUCCESS IN UPDATING TO BRING!");

				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("readMessage", "failed = 2 return");
				}
			}
			catch (Exception e)
			{
				Log.d("readMessage", "exception caught");
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	public void deleteEvent(View view)
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Confirm Delete Event");
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_delete_event, null);
		dialogBuilder.setView(dialogView);
		Button confirmDeleteButton = (Button) dialogView.findViewById(R.id.confirmDeleteButton);
		final EditText confirmEditText = (EditText) dialogView.findViewById(R.id.confirmEditText);
		final AlertDialog deleteGroupDialog = dialogBuilder.create();
		confirmDeleteButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (confirmEditText.getText().toString().equals("Yes"))
				{
					System.out.println("DELETE THID BITCH");
					new deleteEventTask().execute("http://68.59.162.183/android_connect/delete_event.php");
				}
				else if (confirmEditText.getText().toString().equals("No"))
				{
					// dismiss
					deleteGroupDialog.cancel();

				}
				else
				{
					Toast toast = GLOBAL.getToast(EventEditActivity.this, "Please enter 'Yes' or 'No'!");
					toast.show();
				}
				// call delete task
			}
		});

		deleteGroupDialog.show();

	}

	// task to delete the group
	private class deleteEventTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String id = Integer.toString(event.getID());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("e_id", id));
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
					Toast toast = GLOBAL.getToast(EventEditActivity.this, jsonObject.getString("message"));
					toast.show();
					finish();
				}
				else
				{
					// failed
					Log.d("deleteGroup", "FAILED");
				}
			}
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
			// do next thing here
		}
	}

	public void onClick(View v)
	{
		super.onClick(v);
		switch (v.getId())
		{
		case R.id.eventEditImageButton:
			final CharSequence[] items =
			{ "Take Photo", "Choose from Gallery", "Cancel" };

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Choose your event picture:");
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
		case R.id.manageEventButton:
			Intent intent = new Intent(this, ManageParticipantsActivity.class);
			intent.putExtra("email", GLOBAL.getCurrentUser().getEmail());
			intent.putExtra("e_id", event.getID());
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
					System.out.println("Hour:" + startCal.get(Calendar.HOUR_OF_DAY));
					System.out.println("Minute:" + startCal.get(Calendar.MINUTE));
					TimePickerDialog tpd = new TimePickerDialog(EventEditActivity.this, myStartTimeListener,
							startCal.get(Calendar.HOUR_OF_DAY), startCal.get(Calendar.MINUTE), false);
					tpd.show();
					tpd.setOnCancelListener(new DialogInterface.OnCancelListener()
					{

						@Override
						public void onCancel(DialogInterface dialog)
						{
							startEditText.setText("");
						}
					});
				}
				// start the TimePicker using current system time
				else
				{

					TimePickerDialog tpd = new TimePickerDialog(EventEditActivity.this, myStartTimeListener, hour,
							minute, false);
					tpd.show();
					tpd.setOnCancelListener(new DialogInterface.OnCancelListener()
					{

						@Override
						public void onCancel(DialogInterface dialog)
						{
							startEditText.setText("");
						}
					});
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
				startEditText.setText(GLOBAL.toDayTextFormatFromRawNoSeconds(startDate));
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
					System.out.println("Hour:" + endCal.get(Calendar.HOUR_OF_DAY));
					System.out.println("Minute:" + endCal.get(Calendar.MINUTE));
					TimePickerDialog tpd = new TimePickerDialog(EventEditActivity.this, myEndTimeListener,
							endCal.get(Calendar.HOUR_OF_DAY), endCal.get(Calendar.MINUTE), false);
					tpd.show();
					tpd.setOnCancelListener(new DialogInterface.OnCancelListener()
					{

						@Override
						public void onCancel(DialogInterface dialog)
						{
							endEditText.setText("");
						}
					});
				}
				// TODO: add check if start date changed greater than end date,
				// make this start there
				else if (startDate.compareTo(endDate) > 0)
				{
					TimePickerDialog tpd = new TimePickerDialog(EventEditActivity.this, myEndTimeListener,
							startCal.get(Calendar.HOUR_OF_DAY), startCal.get(Calendar.MINUTE), false);
					tpd.show();
					tpd.setOnCancelListener(new DialogInterface.OnCancelListener()
					{

						@Override
						public void onCancel(DialogInterface dialog)
						{
							endEditText.setText("");
						}
					});
				}
				// start the TimePicker using current system time
				else
				{
					TimePickerDialog tpd = new TimePickerDialog(EventEditActivity.this, myEndTimeListener, hour,
							minute, false);
					tpd.show();
					tpd.setOnCancelListener(new DialogInterface.OnCancelListener()
					{

						@Override
						public void onCancel(DialogInterface dialog)
						{
							endEditText.setText("");
						}
					});
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
				endEditText.setText(GLOBAL.toDayTextFormatFromRawNoSeconds(endDate));
			}
		}
	};
}