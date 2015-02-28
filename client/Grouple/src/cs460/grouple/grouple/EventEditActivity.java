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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
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
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/*
 * EventEditActivity allows user to make changes to his/her event.
 */
public class EventEditActivity extends ActionBarActivity implements
		View.OnClickListener
{
	// Set up fields. Most are just for the camera.
	private Button b;
	private ImageView iv;
	private final static int CAMERA_DATA = 0;
	private Bitmap bmp;
	private Intent i;
	private Event event;
	private BroadcastReceiver broadcastReceiver;
	private Global GLOBAL;
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
	private Calendar calendar;
	private int year, month, day, hour, minute;
	private AlertDialog categoryDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Set the activity layout to activity_edit_profile.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_edit);
		calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
	    month = calendar.get(Calendar.MONTH);
	    day = calendar.get(Calendar.DAY_OF_MONTH);
	    hour = calendar.get(Calendar.HOUR_OF_DAY);
	    minute = calendar.get(Calendar.MINUTE);
		
		load();		
	}

	private void load()
	{
		GLOBAL = ((Global) getApplicationContext());
		// Resetting error text view
		TextView errorTextView = (TextView) findViewById(R.id.errorTextViewEPA);
		errorTextView.setVisibility(1);

		Bundle extras = getIntent().getExtras();
		event = GLOBAL.getEventBuffer();
		
		if (event != null)
			getEventProfile();

		initActionBar();
		initKillswitchListener();
	}
	
	private void initActionBar()
	{
		// Set up the action bar.
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		//ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);		
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		//ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);

		actionbarTitle.setText(event.getName() + "'s Profile");
	}
	
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
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
				 
				//json fetch was successful
				if (jsonObject.getString("success").toString().equals("1"))
				{
					if (iv == null)
						iv = (ImageView) findViewById(R.id.eventPhoto);
					String image = jsonObject.getString("image").toString();
					event.setImage(image);
					iv.setImageBitmap(event.getImage());
	
				} 
				else
				{
					// failed
					Log.d("FETCH ROLE FAILED", "FAILED");
				}
			} 
			catch (Exception e)
			{
				Log.d("atherjsoninuserpost", "here");
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
			//do next thing here
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
		if (iv == null)
		{
			Log.d("scott", "7th");
			iv = (ImageView) findViewById(R.id.eventPhoto);
		}
		
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

	private String toRawDate(String date)
	{
		String rawDate = "";
        SimpleDateFormat raw = new SimpleDateFormat("yyyy-M-d h:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, h:mma");
        try
        {
    		Date parsedDate = (Date) dateFormat.parse(date);
    		rawDate = raw.format(parsedDate); 
    		
        }
        catch (ParseException ex)
        {
            System.out.println("Exception "+ex);
        }
        return rawDate;
	}
	private String fromRawDate(String dateString)
	{
		System.out.println("\n\nDATE IS FIRST: " + dateString);
		String date = "";
        SimpleDateFormat raw = new SimpleDateFormat("yyyy-M-d h:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, h:mma");
        try
        {
    		Date parsedDate = (Date) raw.parse(dateString);
    		date = dateFormat.format(parsedDate); 

        }
        catch (ParseException ex)
        {
            System.out.println("Exception "+ex);
        }
		return date;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation_actions, menu);
		// Set up the edit button and image view
		b = (Button) findViewById(R.id.editEventPhotoButton);
		b.setOnClickListener(this);
		if (iv == null)
		{
			iv = (ImageView) findViewById(R.id.eventPhoto);
		}

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

	// Button Listener for submit changes.
	public void submitButton(View view)
	{
		//Checking user inputs on event name, category, start date, end date, and min_part
				String location = locationEditText.getText().toString();
				String eventname = nameEditText.getText().toString();
				startDate.concat(":00");
				endDate.concat(":00");
				System.out.println("startdate to be used is: "+startDate);
				String category = categoryEditText.getText().toString();
				String minimum = minEditText.getText().toString();
				String maximum = maxEditText.getText().toString();
				Date start = null;
				Date end = null;
				
				
				String date = "";
	
		      
				if(!(startDate.compareTo("") == 0) && !(endDate.compareTo("") == 0))
				{
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d hh:mm");
					//SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM DD, h:mma");
					try {
						
						start = sdf.parse(startDate);		
						end = sdf.parse(endDate);
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				
					System.out.println("Dates: " + start + " nn " + end);
					//startDate = sdf.format(start);
					//endDate = sdf.format(end);

					if(start.compareTo(end)>0){
						System.out.println("Start is after End");
					}else if(start.compareTo(end)<0){
						System.out.println("start is before end");
					}else if(start.compareTo(end)==0){
						System.out.println("start is equal to end");
					}else{
						System.out.println("How to get here?");
					}
				}
				
				//if empty group name, display error box
				if(eventname.compareTo("") == 0)
				{
					new AlertDialog.Builder(this)
					.setMessage("you must specify a Name for your event.")
					.setCancelable(true)
					.setNegativeButton("Ok", null).show();
				}
				//if empty start or end date
				else if(startDate.compareTo("") == 0 || endDate.compareTo("") == 0)
				{
					new AlertDialog.Builder(this)
					.setMessage("You must specify a Start Date and End Date.")
					.setCancelable(true)
					.setNegativeButton("Ok", null).show();
				}
				//if endDate is prior to startDate
				else if(start.compareTo(end) >=0)
				{
					new AlertDialog.Builder(this)
					.setMessage("Your Start Date must come before your End Date.")
					.setCancelable(true)
					.setNegativeButton("Ok", null).show();
				}
				//if empty category
				else if(category.compareTo("") == 0)
				{
					new AlertDialog.Builder(this)
					.setMessage("You must specify a Category for your event.")
					.setCancelable(true)
					.setNegativeButton("Ok", null).show();
				}
				//if empty minimum
				else if(minimum.compareTo("") == 0)
				{
					new AlertDialog.Builder(this)
					.setMessage("You must specify a Minimum Size for your event.")
					.setCancelable(true)
					.setNegativeButton("Ok", null).show();
				}
				//if maximum is set and it is less than minimum
				else if(!(maximum.compareTo("") == 0) && (Integer.parseInt(maximum) < Integer.parseInt(minimum)))
				{
					new AlertDialog.Builder(this)
					.setMessage("Your Minimum size cannot be larger than your Maximum size.")
					.setCancelable(true)
					.setNegativeButton("Ok", null).show();
				}
				//otherwise, display confirmation box to proceed
				else
				{ 
					new SetProfileTask().execute("http://68.59.162.183/"
					+ "android_connect/update_event.php");
				}

			
	}
	
	// Button Listener for when user clicks on startDate.
	public void selectStartDateButton(View view)
	{
		System.out.println("select startdate button clicked.");
		new DatePickerDialog(this, myStartDateListener, year, month, day).show();
	}

	// Button Listener for when user clicks on endDate.
	public void selectEndDateButton(View view)
	{
		System.out.println("select enddate button clicked.");
		new DatePickerDialog(this, myEndDateListener, year, month, day).show();
	}	
			
	// Button Listener for when user clicks on category.
	public void selectCategoryButton(View view)
	{
		System.out.println("clicked on category");
		
		// Strings to Show In Dialog with Radio Buttons
		final CharSequence[] items = {"Food ","Sports ","Party ","Work ","School"};
		            
		// Creating and Building the Dialog 
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select your category");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int item) {
		                   	                
	    switch(item)
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
			//update_event.php using name/value pair
			HttpPost httpPost = new HttpPost(URL);
			try
			{
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();

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

				// add remaining fields to builder (g_name, about, public, g_id), then execute
				builder.addTextBody("e_name", nameEditText.getText().toString(), ContentType.TEXT_PLAIN);
				builder.addTextBody("about", aboutEditText.getText().toString(), ContentType.TEXT_PLAIN);
				builder.addTextBody("e_id", Integer.toString(event.getID()), ContentType.TEXT_PLAIN);
				System.out.println("About to add start date as " + startDate);
				builder.addTextBody("start_date", startDate, ContentType.TEXT_PLAIN);
				builder.addTextBody("end_date", endDate, ContentType.TEXT_PLAIN);
				builder.addTextBody("category", categoryEditText.getText().toString(), ContentType.TEXT_PLAIN);
				builder.addTextBody("min_part", minEditText.getText().toString(), ContentType.TEXT_PLAIN);
				builder.addTextBody("max_part", maxEditText.getText().toString(), ContentType.TEXT_PLAIN);
				builder.addTextBody("location", locationEditText.getText().toString(), ContentType.TEXT_PLAIN);
				
				//mustbringlist not implemented in client yet
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
					Log.d("JSON", "Failed to download file");
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
			System.out.println("entering onpostexecute");

			try
			{
				JSONObject jsonObject = new JSONObject(result);
				//success: event profile was either successfully updated in database, or no changes were necesssary
				if (jsonObject.getString("success").toString().equals("1") || jsonObject.getString("success").toString().equals("2"))
				{
					// Success			
					Context context = getApplicationContext();
					Toast toast = GLOBAL.getToast(context, "Event profile changed successfully!");
					toast.show();
					event.fetchEventInfo();
					event.fetchParticipants();
					GLOBAL.setEventBuffer(event);
					finish();
				} else
				{
					// Fail
					Context context = getApplicationContext();
					Toast toast = Toast.makeText(context, "Failed to update event profile.", Toast.LENGTH_SHORT);
					toast.show();
					System.out.println("Fail");
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		switch (v.getId()) 
		{
		case R.id.editEventPhotoButton:
			final CharSequence[] items = {"Take Photo", "Choose from Gallery",
					"Cancel" };

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Choose your profile picture:");
			builder.setItems(items, new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int item) 
				{
					if (items[item].equals("Take Photo")) 
					{
						i = new Intent(
								android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(i, 1);
					}
					else if (items[item].equals("Choose from Gallery")) 
					{
						Intent intent = new Intent(
								Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						intent.setType("image/*");
						startActivityForResult(
								Intent.createChooser(intent, "Select Photo"), 2);
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
			} else if (reqCode == 2) 
			{
				Uri selectedImageUri = data.getData();
				try {
					bmp= MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				iv.setImageURI(selectedImageUri);
			}
		}
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
	
	private DatePickerDialog.OnDateSetListener myStartDateListener
	   = new DatePickerDialog.OnDateSetListener() 
	 {

	   @Override
	   public void onDateSet(DatePicker view, int year, int month, int day) 
	   {
		   if (view.isShown()) 
		   {
			   int tmpMonth = month+1;
			   startDate = year + "-" + tmpMonth + "-" + day;
			   startEditText.setText(tmpMonth + "/" + day);
			   new TimePickerDialog(EventEditActivity.this, myStartTimeListener, hour, minute, false).show();
		   }
	   }
	};
	
	private TimePickerDialog.OnTimeSetListener myStartTimeListener
	   = new TimePickerDialog.OnTimeSetListener() 
	{

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
		{
			if (view.isShown()) 
			{
				startDate += " " + hourOfDay + ":" + minute;
				startEditText.setText(fromRawDate(startDate));
				
			}
		}
	};
	
	private DatePickerDialog.OnDateSetListener myEndDateListener
	   = new DatePickerDialog.OnDateSetListener() 
	 {

	   @Override
	   public void onDateSet(DatePicker view, int year, int month, int day) 
	   {
		   if (view.isShown()) 
		   {
			   int tmpMonth = month+1;
			   endDate = year + "-" + tmpMonth + "-" + day;
			   endEditText.setText(tmpMonth + "/" + day);
			   new TimePickerDialog(EventEditActivity.this, myEndTimeListener, hour, minute, false).show();
		   }
	   }
	};
	
	private TimePickerDialog.OnTimeSetListener myEndTimeListener
	   = new TimePickerDialog.OnTimeSetListener() 
	{

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
		{
			if (view.isShown()) 
			{
				endDate += " "+hourOfDay+":"+minute;
				endEditText.setText(fromRawDate(endDate));
			}
		}
	};
}