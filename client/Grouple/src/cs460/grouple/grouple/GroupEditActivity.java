package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/*
 * EditProfileActivity allows user to make changes to his/her profile.
 */
public class GroupEditActivity extends BaseActivity
{
	// Set up fields. Most are just for the camera.
	private ImageView iv;
	private Bitmap bmp;
	private Group group;
	private User user;
	private EditText aboutEditText;
	private EditText nameEditText;
	private TextView errorTextView;
	private RadioButton privateButton;
	private RadioButton publicButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Set the activity layout to activity_edit_profile.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_edit);
		iv = (ImageView) findViewById(R.id.groupEditImageView);
		Bundle extras = getIntent().getExtras();
		// Resetting error text view
		user = GLOBAL.getCurrentUser();
		group = GLOBAL.getGroup(extras.getInt("g_id"));
		aboutEditText = (EditText) findViewById(R.id.groupAboutEditText);
		nameEditText = (EditText) findViewById(R.id.groupNameEditText);
		errorTextView = (TextView) findViewById(R.id.errorTextViewEPA);
		privateButton = (RadioButton) findViewById(R.id.privateButton);
		publicButton = (RadioButton) findViewById(R.id.publicButton);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("group_data"));
		load();
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


	private void load()
	{

		
		fetchData();
		updateUI();
		initActionBar("Edit " + group.getName(), true);
	}
	
	private void fetchData()
	{
		group.fetchImage(this);
		group.fetchInfo(this);
	}

	public void deleteGroup(View view)
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Confirm Delete Group");
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_delete_entity, null);
		dialogBuilder.setView(dialogView);
		Button confirmDeleteButton = (Button) dialogView
				.findViewById(R.id.confirmDeleteButton);
		final EditText confirmEditText = (EditText) dialogView
				.findViewById(R.id.confirmEditText);
		final AlertDialog deleteGroupDialog = dialogBuilder.create();
		confirmDeleteButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (confirmEditText.getText().toString().equals("Yes"))
				{
					System.out.println("DELETE THID BITCH");
					new deleteGroupTask().execute("http://68.59.162.183/android_connect/update_group.php");
				}
				else if (confirmEditText.getText().toString().equals("No"))
				{
					//dismiss 
					deleteGroupDialog.cancel();
					
				}
				else
				{
					Toast toast = GLOBAL.getToast(GroupEditActivity.this, "Please enter 'Yes' or 'No'!");
					toast.show();
				}
				// call delete task
			}
		});
		
		deleteGroupDialog.show();

	}

	/*
	 * Get profile executes get_groupprofile.php. It uses the current groups gid
	 * to retrieve the groups name, about, and other info.
	 */
	private void updateUI()
	{
		// Add the info to the textviews for editing.
		nameEditText.setText(group.getName());
		aboutEditText.setText(group.getAbout());
		System.out.println("group.getpub() value is: " + group.getPub());
		if (group.getPub() == 1)
			publicButton.setChecked(true);
		else
			privateButton.setChecked(true);
		if (group.getImage() != null)
			iv.setImageBitmap(group.getImage());
	}

	public void manageGroup(View view)
	{
		Intent intent = new Intent(this, ManageMembersActivity.class);
		intent.putExtra("g_id", group.getID());
		startActivity(intent);
	}

	// Button Listener for submit changes. It the profile in the database.
	// This executes the
	public void submitButton(View view)
	{
		String about = aboutEditText.getText().toString();
		String name = nameEditText.getText().toString();
		if (about.length() > 100)
		{
			errorTextView.setText("About is too many characters.");
			errorTextView.setVisibility(View.VISIBLE);
		} else if (name.isEmpty() || name.compareTo("") == 0)
		{
			errorTextView.setText("Please enter a name.");
			errorTextView.setVisibility(View.VISIBLE);
		} else
		{
			new setProfileTask()
					.execute("http://68.59.162.183/android_connect/update_group.php");
		}
	}

	public void radio(View view)
	{
		switch (view.getId())
		{
		case R.id.publicButton:
			if (publicButton.isChecked())
			{
				System.out.println("Case public button and is checked.");
				privateButton.setChecked(false);
			}
			break;
		case R.id.privateButton:
			if (privateButton.isChecked())
			{
				System.out.println("Case private button and is checked.");
				publicButton.setChecked(false);
			}
			break;
		}
	}
	
	//task to delete the group
	private class deleteGroupTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			String id = Integer.toString(group.getID());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("g_id", id));
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
					Toast toast = GLOBAL.getToast(GroupEditActivity.this, jsonObject.getString("message"));
					toast.show();
					finish();
				} else
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

	

	/*
	 * Set profile executes update_profile.php. It uses the current users email
	 * address to update the users name, age, and bio.
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
			// update_group.php using name/value pair
			HttpPost httpPost = new HttpPost(URL);
			try
			{
				// If private is not checked, then assume public. yes that means
				// if the user selects nothing then it is assumed public.
				
				int publicStatus = 1;
				if (privateButton.isChecked())
				{
					// Set the public column in the database to false.
					publicStatus = 0;
				}
				MultipartEntityBuilder builder = MultipartEntityBuilder
						.create();
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				byte[] data;
				System.out.println("about to process photo...");

				// process photo if set and add it to builder
				if (bmp != null)
				{
					System.out.println("Photo is being added");
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bmp.compress(CompressFormat.JPEG, 100, bos);
					data = bos.toByteArray();
					ByteArrayBody bab = new ByteArrayBody(data, ".jpg");
					builder.addPart("pic", bab);
					data = null;
					bab = null;
					bos.close();
				}

				System.out
						.println("Finished with photo. Moving on to remaining fields...");
				// add remaining fields to builder (g_name, about, public,
				// g_id), then execute
				builder.addTextBody("g_name",
						nameEditText.getText().toString(),
						ContentType.TEXT_PLAIN);
				builder.addTextBody("about",
						aboutEditText.getText().toString(),
						ContentType.TEXT_PLAIN);
				builder.addTextBody("public", Integer.toString(publicStatus),
						ContentType.TEXT_PLAIN);
				builder.addTextBody("g_id", Integer.toString(group.getID()),
						ContentType.TEXT_PLAIN);
				System.out.println("Done building.");
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
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				// success: group profile was either successfully updated in
				// database or no changes were necesssary
				if (jsonObject.getString("success").toString().equals("1")
						|| jsonObject.getString("success").toString()
								.equals("2"))
				{
					// loadDialog.show();
					// Success
					Context context = getApplicationContext();
					Toast toast = GLOBAL.getToast(context,
							"Group profile changed successfully.");
					toast.show();
					finish();
				} else
				{
					// Fail
					Context context = getApplicationContext();
					Toast toast = GLOBAL.getToast(context,
							"Failed to update group profile.");
					toast.show();
					System.out.println("Fail");
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	public void onClick(View v)
	{
		super.onClick(v);
		switch (v.getId()) 
		{
		case R.id.groupEditImageButton:
			final CharSequence[] items = {"Take Photo", "Choose from Gallery",
					"Cancel" };

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Choose your group picture:");
			builder.setItems(items, new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int item) 
				{
					if (items[item].equals("Take Photo")) 
					{
						Intent i = new Intent(
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
}