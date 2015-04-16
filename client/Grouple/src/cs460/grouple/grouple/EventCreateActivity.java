package cs460.grouple.grouple;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

/*
 * GroupCreateActivity allows a user to create a new group.
 */

@SuppressLint("SimpleDateFormat")
public class EventCreateActivity extends BaseActivity
{
	private User user;
	private String ID;
	private String startDate = "";
	private String endDate = "";
	private String minimum = "";
	private String maximum = "";
	private String category = "";
	private String location = "";
	private String name = "";
	private EditText categoryEditText;
	private EditText startDateEditText;
	private EditText endDateEditText;
	private EditText locationEditText;
	private EditText aboutEditText;
	private EditText nameEditText;
	private AlertDialog categoryDialog;
	private AlertDialog toBringDialog;
	private Button addToBringRowButton;
	private Button toBringButton;
	private Bitmap bmp;
	private ImageView iv;
	private Button editEventImageButton;
	private View toBringLayout;
	private Calendar currentCal;
	private Calendar startCal;
	private Calendar endCal;
	private LayoutInflater inflater;
	private final ArrayList<EditText> toBringEditTexts = new ArrayList<EditText>();
	private int year, month, day, hour, minute;
	private ArrayList<String> itemNames = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_create);
		load();
	}

	private void load()
	{
		currentCal = Calendar.getInstance();
		year = currentCal.get(Calendar.YEAR);
		month = currentCal.get(Calendar.MONTH);
		day = currentCal.get(Calendar.DAY_OF_MONTH);
		hour = currentCal.get(Calendar.HOUR_OF_DAY);
		minute = currentCal.get(Calendar.MINUTE);
		categoryEditText = (EditText) findViewById(R.id.eventCategoryEditText);
		toBringButton = (Button) findViewById(R.id.toBringButton);
		startDateEditText = (EditText) findViewById(R.id.startTimeButton);
		locationEditText = (EditText) findViewById(R.id.eventLocationEditText);
		endDateEditText = (EditText) findViewById(R.id.endTimeButton);
		aboutEditText = (EditText) findViewById(R.id.eventAboutEditText);
		nameEditText = (EditText) findViewById(R.id.eventNameEditText);
		iv = (ImageView) findViewById(R.id.eventCreateImageView);
		editEventImageButton = (Button) findViewById(R.id.editEventImageButton);
		// grab the email of current users from our GLOBAL class
		user = GLOBAL.getCurrentUser();
		initActionBar("Create Event", true);
	}

	// onClick for items to bring
	public void toBringButton(View view)
	{
		// Creating and Building the Dialog
		toBringEditTexts.clear();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add Items To Bring");
		inflater = EventCreateActivity.this.getLayoutInflater();
		toBringLayout = inflater.inflate(R.layout.dialog_tobring, null);
		final LinearLayout layout = (LinearLayout) toBringLayout.findViewById(R.id.toBringInnerLayout);
		this.addToBringRowButton = (Button) toBringLayout.findViewById(R.id.toBringAddRowButton);

		if (!itemNames.isEmpty())
		{
			for (String itemName : itemNames)
			{
				View editTextLayout = inflater.inflate(R.layout.list_item_edittext, null);
				EditText toBringEditText = (EditText) editTextLayout.findViewById(R.id.toBringEditText);
				toBringEditText.setText(itemName);
				toBringEditTexts.add(toBringEditText);
				layout.addView(editTextLayout);
			}
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
		itemNames.clear();
		for (EditText toBringItem : toBringEditTexts)
		{
			// when list is saved, save to final list but ignore any blank line
			// entries
			if (!(toBringItem.getText().toString().compareTo("") == 0))
			{
				// save to final list that can be used to send to db
				itemNames.add(toBringItem.getText().toString());
				System.out.println("\n\nSaving item #" + toBringEditTexts.indexOf(toBringItem) + ": "
						+ toBringItem.getText().toString());
			}
		}
		toBringDialog.dismiss();
		toBringButton.setText("Items (" + itemNames.size() + ")");
	}

	// onClick for category button
	public void selectCategoryButton(View view)
	{
		System.out.println("clicked on category");

		// Strings to Show In Dialog with Radio Buttons
		final CharSequence[] items =
		{ "Social ", "Entertainment / Games ", "Professional / Education ", "Sports / Fitness ", "Nature" };

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

	// onClick for start date button
	public void selectStartDateButton(View view)
	{
		System.out.println("clicked on startdate");
		// startDate is not currently set. load datepicker set to current
		// calendar date.
		if (startDateEditText.getText().toString().compareTo("") == 0)
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
			// startDate = startDateEditText.getText().toString();

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

	public void onClick(View view)
	{
		switch (view.getId())
		{
		case R.id.editEventImageButton:
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

	// onClick for end date button
	public void selectEndDateButton(View view)
	{
		System.out.println("clicked on enddate");
		// endDate is not currently set. load datepicker set to current
		// calendar date.
		if (endDateEditText.getText().toString().compareTo("") == 0)
		{
			if (startDateEditText.getText().toString().compareTo("") == 0)
			{
				DatePickerDialog dpd;
				dpd = new DatePickerDialog(this, myEndDateListener, year, month, day);
				dpd.show();
			}
			else
			{
				endCal = Calendar.getInstance();
				endDate = startDate;

				// parse to our calendar object
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				try
				{
					endCal.setTime(sdf.parse(endDate));
					System.out.println("cal was parsed from tmpStartDate!");
				}
				catch (ParseException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// all done

				DatePickerDialog dpd;
				dpd = new DatePickerDialog(this, myEndDateListener, endCal.get(Calendar.YEAR),
						endCal.get(Calendar.MONTH), endCal.get(Calendar.DAY_OF_MONTH));
				dpd.show();
			}

		}
		// load the datepicker using the date that was previously set in endDate
		else
		{
			endCal = Calendar.getInstance();
			// endDate = endDateEditText.getText().toString();

			// parse to our calendar object
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			try
			{
				endCal.setTime(sdf.parse(endDate));
				System.out.println("cal was parsed from tmpEndDate!");
			}
			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// all done
			DatePickerDialog dpd;
			dpd = new DatePickerDialog(this, myEndDateListener, endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH),
					endCal.get(Calendar.DAY_OF_MONTH));
			dpd.show();
		}
	}

	// onClick for Confirm create event button
	public void createEventButton(View view)
	{
		// Checking user inputs on event name, category, start date, end date,
		// and min_part
		location = locationEditText.getText().toString();
		name = nameEditText.getText().toString();
		// TODO: not grab right from here
		System.out.println("startdate to be used is: " + startDate);
		category = categoryEditText.getText().toString();
		EditText minimumEditText = (EditText) findViewById(R.id.minPartButton);
		EditText maximumEditText = (EditText) findViewById(R.id.maxPartButton);
		minimum = minimumEditText.getText().toString();
		maximum = maximumEditText.getText().toString();
		Date start = null;
		Date end = null;

		if (minimum.compareTo("") == 0)
		{
			minimum = "1";
		}

		if (!(startDate.compareTo("") == 0) && !(endDate.compareTo("") == 0))
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
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

			System.out.println(sdf.format(start));
			System.out.println(sdf.format(end));

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
		if (name.compareTo("") == 0)
		{
			new AlertDialog.Builder(this).setMessage("Please give your event a name before creating.")
					.setCancelable(true).setNegativeButton("Ok", null).show();
		}
		// if empty start or end date
		else if (startDate.compareTo("") == 0 || endDate.compareTo("") == 0)
		{
			new AlertDialog.Builder(this).setMessage("Please specify a Start Date and End Date before creating.")
					.setCancelable(true).setNegativeButton("Ok", null).show();
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
			new AlertDialog.Builder(this).setMessage("Please select a Category before creating.").setCancelable(true)
					.setNegativeButton("Ok", null).show();
		}
		else if (!(maximum.compareTo("") == 0) && (Integer.parseInt(maximum) < Integer.parseInt(minimum)))
		{
			new AlertDialog.Builder(this).setMessage("Your Minimum size cannot be larger than your Maximum size.")
					.setCancelable(true).setNegativeButton("Ok", null).show();
		}
		// otherwise, display confirmation box to proceed
		else
		{
			new AlertDialog.Builder(this).setMessage("Are you sure you want to create this event?").setCancelable(true)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int id)
						{
							// initiate creation of event
							new CreateEventTask().execute("http://68.59.162.183/" + "android_connect/create_event.php");
						}
					}).setNegativeButton("Cancel", null).show();
		}
	}

	// aSynch class to create event
	private class CreateEventTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			// grab group name and bio from textviews
			String name = nameEditText.getText().toString();
			String about = aboutEditText.getText().toString();

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("e_name", name));
			nameValuePairs.add(new BasicNameValuePair("about", about));
			nameValuePairs.add(new BasicNameValuePair("creator", user.getEmail()));
			nameValuePairs.add(new BasicNameValuePair("start_date", startDate));
			nameValuePairs.add(new BasicNameValuePair("end_date", endDate));
			nameValuePairs.add(new BasicNameValuePair("category", category));
			nameValuePairs.add(new BasicNameValuePair("min_part", minimum));
			nameValuePairs.add(new BasicNameValuePair("max_part", maximum));
			nameValuePairs.add(new BasicNameValuePair("location", location));

			// loop through toBringList, adding each member into php array
			// toBring[]
			for (int i = 0; i < itemNames.size(); i++)
			{
				System.out.println("adding the toBring entries");
				nameValuePairs.add(new BasicNameValuePair("toBring[]", itemNames.get(i)));
			}

			// pass url and nameValuePairs off to GLOBAL to do the JSON call.
			// Code continues at onPostExecute when JSON returns.
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			System.out.println("in onPostExecute.");
			try
			{
				JSONObject jsonObject = new JSONObject(result);

				System.out.println("success value: " + jsonObject.getString("success").toString());

				// event has been successfully created
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// now we can grab the newly created e_id returned from the
					// server
					// Note: g_id is the only unique identifier of a group and
					// therefore must be used for any future calls concerning
					// that group.
					ID = jsonObject.getString("e_id").toString();
					System.out.println("MEssage: " + jsonObject.getString("message"));
					System.out.println("e_id of newly created group is: " + ID);
					user.fetchEventInvites();
					user.fetchEventsPending();
					user.fetchEventsUpcoming();
					Event e = new Event(Integer.parseInt(ID));
					e.fetchEventInfo();
					e.fetchParticipants();
					GLOBAL.setCurrentUser(user);
					GLOBAL.setEventBuffer(e);

					// display confirmation box
					AlertDialog dialog = new AlertDialog.Builder(EventCreateActivity.this)
							.setMessage("You've successfully created an event!").setCancelable(true)
							.setPositiveButton("Invite Groups to Your Event", new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int id)
								{
									// code here to take user to
									// eventaddmembersactivity page.
									// (pass e_id as extra so invites
									// can be sent to correct event id)
									Intent intent = new Intent(EventCreateActivity.this, EventAddGroupsActivity.class);
									intent.putExtra("CONTENT", "EVENT");
									intent.putExtra("e_id", ID);
									intent.putExtra("email", user.getEmail());
									startActivity(intent);
									finish();
								}
							}).setNegativeButton("View Your Event Profile", new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									// code here to take user to newly
									// created event profile page. (pass
									// e_id as extra so correct event
									// profile can be loaded)
									Intent intent = new Intent(EventCreateActivity.this, EventProfileActivity.class);
									intent.putExtra("e_id", ID);
									intent.putExtra("email", user.getEmail());
									startActivity(intent);
									finish();
								}
							}).show();
					// if user dimisses the confirmation box, gets sent to back
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
				// Create event failed for some reasons. Allow user to retry.
				else if (jsonObject.getString("success").toString().equals("0"))
				{
					// display error box
					new AlertDialog.Builder(EventCreateActivity.this)
							.setMessage("Unable to create event! Please choose an option:").setCancelable(true)
							.setPositiveButton("Try Again", new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int id)
								{
									// initiate creation of event AGAIN
									new CreateEventTask().execute("http://68.59.162.183/"
											+ "android_connect/create_event.php");
								}
							}).setNegativeButton("Cancel", null).show();
				}
			}
			catch (Exception e)
			{
				Log.d("onPostreadJSONFeed", e.getLocalizedMessage());
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
				startDate = year + "-" + tmpMonth + "-" + day;
				startDateEditText.setText(year + "-" + tmpMonth + "-" + day);

				// start the TimePicker using hour and minute previously set in
				// startCal
				if (startCal != null)
				{
					System.out.println("Hour:" + startCal.get(Calendar.HOUR_OF_DAY));
					System.out.println("Minute:" + startCal.get(Calendar.MINUTE));
					TimePickerDialog tpd;
					tpd = new TimePickerDialog(EventCreateActivity.this, myStartTimeListener,
							startCal.get(Calendar.HOUR_OF_DAY), startCal.get(Calendar.MINUTE), false);
					tpd.show();
					tpd.setOnCancelListener(new DialogInterface.OnCancelListener()
					{

						@Override
						public void onCancel(DialogInterface dialog)
						{
							startDateEditText.setText("");
						}
					});
				}
				// start the TimePicker using current system time
				else
				{
					TimePickerDialog tpd;
					tpd = new TimePickerDialog(EventCreateActivity.this, myStartTimeListener, hour, minute, false);
					tpd.show();
					tpd.setOnCancelListener(new DialogInterface.OnCancelListener()
					{

						@Override
						public void onCancel(DialogInterface dialog)
						{
							startDateEditText.setText("");
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
				startDate += " " + hourOfDay + ":" + minute + ":00";
				startDateEditText.setText(GLOBAL.toDayTextFormatFromRaw(startDate));
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
				endDate = year + "-" + tmpMonth + "-" + day;
				endDateEditText.setText(year + "-" + tmpMonth + "-" + day);

				// start the TimePicker using hour and minute previously set in
				// startCal
				if (endCal != null)
				{
					System.out.println("Hour:" + endCal.get(Calendar.HOUR_OF_DAY));
					System.out.println("Minute:" + endCal.get(Calendar.MINUTE));
					TimePickerDialog tpd;
					tpd = new TimePickerDialog(EventCreateActivity.this, myEndTimeListener,
							endCal.get(Calendar.HOUR_OF_DAY), endCal.get(Calendar.MINUTE), false);
					tpd.show();
					tpd.setOnCancelListener(new DialogInterface.OnCancelListener()
					{

						@Override
						public void onCancel(DialogInterface dialog)
						{
							endDateEditText.setText("");
						}
					});
				}
				// start the TimePicker using current system time
				else
				{
					TimePickerDialog tpd;
					tpd = new TimePickerDialog(EventCreateActivity.this, myEndTimeListener, hour, minute, false);
					tpd.show();
					tpd.setOnCancelListener(new DialogInterface.OnCancelListener()
					{

						@Override
						public void onCancel(DialogInterface dialog)
						{
							endDateEditText.setText("");
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
				endDate += " " + hourOfDay + ":" + minute + ":00";
				endDateEditText.setText(GLOBAL.toDayTextFormatFromRaw(endDate));
			}
		}
	};

}
