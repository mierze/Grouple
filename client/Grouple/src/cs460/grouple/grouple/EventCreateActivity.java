package cs460.grouple.grouple;

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
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

/*
 * GroupCreateActivity allows a user to create a new group.
 */

@SuppressLint("SimpleDateFormat") public class EventCreateActivity extends ActionBarActivity
{
	private BroadcastReceiver broadcastReceiver;
	private User user;
	private String ID;
	private String startDate = "";
	private String endDate = "";
	private String minimum = "";
	private String maximum = "";
	private String category = "";
	private String location = "";
	private EditText categoryEditText;
	private EditText startDateEditText;
	private EditText endDateEditText;
	private EditText locationEditText;
	private AlertDialog categoryDialog;
	private AlertDialog toBringDialog;
	private Button addToBringRowButton;
	private Button toBringButton;
	private View toBringLayout;
	private Dialog loadDialog;
	private Calendar currentCal;
	private Calendar startCal;
	private Calendar endCal;
	private LayoutInflater inflater;
	private final ArrayList<EditText> toBringEditTexts = new ArrayList<EditText>();
	private int year, month, day, hour, minute;
	private Global GLOBAL;

	@Override
	public void onBackPressed() 
	{
	    finish();
	    return;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_create);
		load();
	}

	private void load()
	{
		GLOBAL = ((Global) getApplicationContext());
		currentCal = Calendar.getInstance();
		year = currentCal.get(Calendar.YEAR);
		month = currentCal.get(Calendar.MONTH);
		day = currentCal.get(Calendar.DAY_OF_MONTH);
		hour = currentCal.get(Calendar.HOUR_OF_DAY);
		minute = currentCal.get(Calendar.MINUTE);
		categoryEditText = (EditText) findViewById(R.id.category);
		toBringButton = (Button) findViewById(R.id.toBringButton);
		startDateEditText = (EditText) findViewById(R.id.startTimeButton);
		endDateEditText = (EditText) findViewById(R.id.endTimeButton);
		// grab the email of current users from our GLOBAL class
		user = GLOBAL.getCurrentUser();
		loadDialog = GLOBAL.getLoadDialog(new Dialog(this));
		loadDialog.setOwnerActivity(this);
		initActionBar();
		initKillswitchListener();
	}

	private void initActionBar()
	{
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionBarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionBarTitle.setText("Create Event");
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	// onClick for items to bring
	public void toBringButton(View view)
	{
		// Creating and Building the Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add Items To Bring");
		inflater = EventCreateActivity.this.getLayoutInflater();
		toBringLayout = inflater.inflate(R.layout.tobring_dialog, null);
		final LinearLayout layout = (LinearLayout) toBringLayout
				.findViewById(R.id.toBringInnerLayout);
		this.addToBringRowButton = (Button) toBringLayout
				.findViewById(R.id.toBringAddRowButton);
		View editTextLayout = inflater.inflate(R.layout.tobring_edittext, null);
		EditText toBringEditText = (EditText) editTextLayout
				.findViewById(R.id.toBringEditText);
		toBringEditTexts.add(toBringEditText);
		layout.addView(editTextLayout);
		addToBringRowButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				try
				{
					View editTextLayout = inflater.inflate(
							R.layout.tobring_edittext, null);
					EditText toBringEditText = (EditText) editTextLayout
							.findViewById(R.id.toBringEditText);
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
		for (EditText toBringItem : toBringEditTexts)
		{
			System.out.println("\n\nItem #"
					+ toBringEditTexts.indexOf(toBringItem) + " is "
					+ toBringItem.getText().toString());
		}
		toBringDialog.dismiss();
		toBringButton.setText("Items (" + toBringEditTexts.size() + ")");
	}

	// onClick for category button
	public void selectCategoryButton(View view)
	{
		System.out.println("clicked on category");

		// Strings to Show In Dialog with Radio Buttons
		final CharSequence[] items =
		{ "Food ", "Sports ", "Party ", "Work ", "School" };

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

	// onClick for start date button
	public void selectStartDateButton(View view)
	{
		System.out.println("clicked on startdate");
		// startDate is not currently set. load datepicker set to current
		// calendar date.
		if(startDateEditText.getText().toString().compareTo("") ==0)
		{
			new DatePickerDialog(this, myStartDateListener, year, month, day).show();
		}
		// load the datepicker using the date that was previously set in startDate
		else
		{
			startCal = Calendar.getInstance();
			//startDate = startDateEditText.getText().toString();
			
			//parse to our calendar object
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
		
			new DatePickerDialog(this, myStartDateListener, startCal.get(Calendar.YEAR),startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH)).show();
		}
	}

	// onClick for end date button
	public void selectEndDateButton(View view)
	{
		System.out.println("clicked on enddate");
		// endDate is not currently set. load datepicker set to current
		// calendar date.
		if(endDateEditText.getText().toString().compareTo("") ==0)
		{
			if(startDateEditText.getText().toString().compareTo("")==0)
			{
				new DatePickerDialog(this, myEndDateListener, year, month, day).show();
			}
			else
			{
				endCal = Calendar.getInstance();
				endDate = startDate;
				
				//parse to our calendar object
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
			
				new DatePickerDialog(this, myEndDateListener, endCal.get(Calendar.YEAR),endCal.get(Calendar.MONTH), endCal.get(Calendar.DAY_OF_MONTH)).show();
			}
			
		}
		// load the datepicker using the date that was previously set in endDate
		else
		{
			endCal = Calendar.getInstance();
			//endDate = endDateEditText.getText().toString();
			
			//parse to our calendar object
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
			new DatePickerDialog(this, myEndDateListener, endCal.get(Calendar.YEAR),endCal.get(Calendar.MONTH), endCal.get(Calendar.DAY_OF_MONTH)).show();
		}
	}

	// onClick for Confirm create event button
	public void createEventButton(View view)
	{
		// Checking user inputs on event name, category, start date, end date,
		// and min_part
		EditText eventNameEditText = (EditText) findViewById(R.id.eventName);
		locationEditText = (EditText) findViewById(R.id.locationEditTextECA);
		location = locationEditText.getText().toString();
		String eventname = eventNameEditText.getText().toString();
		//TODO: not grab right from here
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
		if (eventname.compareTo("") == 0)
		{
			new AlertDialog.Builder(this)
					.setMessage(
							"Please give your event a name before creating.")
					.setCancelable(true).setNegativeButton("Ok", null).show();
		}
		// if empty start or end date
		else if (startDate.compareTo("") == 0 || endDate.compareTo("") == 0)
		{
			new AlertDialog.Builder(this)
					.setMessage(
							"Please specify a Start Date and End Date before creating.")
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
					.setMessage("Please select a Category before creating.")
					.setCancelable(true).setNegativeButton("Ok", null).show();
		}		
		else if (!(maximum.compareTo("") == 0)
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
			new AlertDialog.Builder(this)
					.setMessage("Are you sure you want to create this event?")
					.setCancelable(true)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog,
										int id)
								{
									// initiate creation of event
									new CreateEventTask()
											.execute("http://68.59.162.183/"
													+ "android_connect/create_event.php");
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
			EditText eventNameEditText = (EditText) findViewById(R.id.eventName);
			EditText eventBioEditText = (EditText) findViewById(R.id.eventBio);

			// grab group name and bio from textviews
			String eventname = eventNameEditText.getText().toString();
			String eventbio = eventBioEditText.getText().toString();
		
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("e_name", eventname));
			nameValuePairs.add(new BasicNameValuePair("about", eventbio));
			nameValuePairs.add(new BasicNameValuePair("creator", user.getEmail()));
			nameValuePairs.add(new BasicNameValuePair("start_date", startDate));
			nameValuePairs.add(new BasicNameValuePair("end_date", endDate));
			nameValuePairs.add(new BasicNameValuePair("category", category));
			nameValuePairs.add(new BasicNameValuePair("min_part", minimum));
			nameValuePairs.add(new BasicNameValuePair("max_part", maximum));
			nameValuePairs.add(new BasicNameValuePair("location", location));
			
			//loop through toBringList, adding each member into php array toBring[]
		    for (int i = 0; i < toBringEditTexts.size(); i++) 
		    {
		    	System.out.println("adding the toBring entries");
		        nameValuePairs.add(new BasicNameValuePair("toBring[]", toBringEditTexts.get(i).getText().toString()));
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
				
				System.out.println("success value: "+jsonObject.getString("success").toString());

				// event has been successfully created
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// now we can grab the newly created e_id returned from the
					// server
					// Note: g_id is the only unique identifier of a group and
					// therefore must be used for any future calls concerning
					// that group.
					ID = jsonObject.getString("e_id").toString();
					System.out.println("MEssage: "
							+ jsonObject.getString("message"));
					System.out.println("e_id of newly created group is: "
							+ ID);
					user.fetchEventsInvites();
					user.fetchEventsPending();
					user.fetchEventsUpcoming();
					Event e = new Event(Integer.parseInt(ID));
					e.fetchEventInfo();
					e.fetchParticipants();
					GLOBAL.setCurrentUser(user);
					GLOBAL.setEventBuffer(e);

					// display confirmation box
					AlertDialog dialog = new AlertDialog.Builder(
							EventCreateActivity.this)
							.setMessage("You've successfully created an event!")
							.setCancelable(true)
							.setPositiveButton("Invite Groups to Your Event",
									new DialogInterface.OnClickListener()
									{
										@Override
										public void onClick(
												DialogInterface dialog, int id)
										{
											// code here to take user to
											// eventaddmembersactivity page.
											// (pass e_id as extra so invites
											// can be sent to correct event id)
											Intent intent = new Intent(
													EventCreateActivity.this,
													EventAddGroupsActivity.class);
											intent.putExtra("CONTENT", "EVENT");
											intent.putExtra("EID", ID);
											intent.putExtra("EMAIL",
													user.getEmail());
											startActivity(intent);
											finish();
										}
									})
							.setNegativeButton("View Your Event Profile",
									new DialogInterface.OnClickListener()
									{
										@Override
										public void onClick(
												DialogInterface dialog,
												int which)
										{
											// code here to take user to newly
											// created event profile page. (pass
											// e_id as extra so correct event
											// profile can be loaded)
											Intent intent = new Intent(
													EventCreateActivity.this,
													ProfileActivity.class);
											intent.putExtra("CONTENT", "EVENT");
											intent.putExtra("EID", ID);
											intent.putExtra("EMAIL",
													user.getEmail());
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
							.setMessage(
									"Unable to create event! Please choose an option:")
							.setCancelable(true)
							.setPositiveButton("Try Again",
									new DialogInterface.OnClickListener()
									{
										@Override
										public void onClick(
												DialogInterface dialog, int id)
										{
											// initiate creation of event AGAIN
											new CreateEventTask()
													.execute("http://68.59.162.183/"
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

			Intent login = new Intent(this, LoginActivity.class);
			GLOBAL.destroySession();
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
					finish();
				}
			}
		};
		registerReceiver(broadcastReceiver, intentFilter);
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
								
				//start the TimePicker using hour and minute previously set in startCal
				if(startCal != null)
				{
					System.out.println("Hour:"+startCal.get(Calendar.HOUR_OF_DAY));
					System.out.println("Minute:"+startCal.get(Calendar.MINUTE));
					new TimePickerDialog(EventCreateActivity.this,
							myStartTimeListener, startCal.get(Calendar.HOUR_OF_DAY), startCal.get(Calendar.MINUTE), false).show();
				}
				//start the TimePicker using current system time
				else
				{
					new TimePickerDialog(EventCreateActivity.this,
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
								
				//start the TimePicker using hour and minute previously set in startCal
				if(endCal != null)
				{
					System.out.println("Hour:"+endCal.get(Calendar.HOUR_OF_DAY));
					System.out.println("Minute:"+endCal.get(Calendar.MINUTE));
					new TimePickerDialog(EventCreateActivity.this,
							myEndTimeListener, endCal.get(Calendar.HOUR_OF_DAY), endCal.get(Calendar.MINUTE), false).show();
				}
				//start the TimePicker using current system time
				else
				{
					new TimePickerDialog(EventCreateActivity.this,
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
				endDate += " " + hourOfDay + ":" + minute + ":00";
				endDateEditText.setText(GLOBAL.toDayTextFormatFromRaw(endDate));
			}
		}
	};

}
