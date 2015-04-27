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
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/*
 * GroupCreateActivity allows a user to create a new group.
 */

public class GroupCreateActivity extends BaseActivity
{
	private SparseArray<String> added = new SparseArray<String>();    //holds list of name of all friend rows to be added
	private SparseArray<Character> role = new SparseArray<Character>();   //holds list of role of all friend rows to be added
	private ArrayList<User> allFriends = new ArrayList<User>();   //holds list of all current friends
	private User user;
	private Bitmap bmp;
	private String g_id;
	private EditText nameEditText;
	private EditText aboutEditText;
	private ImageView iv;
	private Button editGroupImageButton;
	private Group group;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_create);
		user = GLOBAL.getCurrentUser();
		//load our list of current friends.  key is friend email -> value is full names
		allFriends = user.getUsers();
		nameEditText = (EditText) findViewById(R.id.groupNameEditText);
		aboutEditText = (EditText) findViewById(R.id.groupAboutEditText);
		iv = (ImageView) findViewById(R.id.groupCreateImageView);
		editGroupImageButton = (Button) findViewById(R.id.groupEditImageButton);
		//populateGroupCreate(); //no longer allowing invites during create itself
		initActionBar("Create Group", true);
	}


	
	//onClick for Confirm create group button
	public void createGroupButton(View view)
	{		
		//first check to make sure a group name has been typed by the user
		//Check that a radio button was checked too.
		RadioButton publicButton = (RadioButton) findViewById(R.id.publicButton);
		RadioButton privateButton = (RadioButton) findViewById(R.id.privateButton);
		String name = nameEditText.getText().toString();
		
		//if empty group name, display error box
		if(name.compareTo("") == 0)
		{
			new AlertDialog.Builder(this)
			.setMessage("Please give your group a name before creating.")
			.setCancelable(true)
			.setNegativeButton("Ok", null).show();
		}
		else if(!publicButton.isChecked() && !privateButton.isChecked())
		{
			new AlertDialog.Builder(this)
			.setMessage("Please select public or private for your new group's privacy setting.")
			.setCancelable(true)
			.setNegativeButton("Ok", null).show();
		}
		//otherwise, display confirmation box
		else
		{
			new AlertDialog.Builder(this)
			.setMessage("Are you sure you want to create this group?")
			.setCancelable(true)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int id)
				{
					//initiate creation of group
					new CreateGroupTask().execute("http://68.59.162.183/"
					+ "android_connect/create_group.php");
				}
			}).setNegativeButton("Cancel", null).show();
		}
	}
	
	//Handles the radio buttons.
	public void radio (View view)
	{
		RadioButton publicButton = (RadioButton) findViewById(R.id.publicButton);
		RadioButton privateButton = (RadioButton) findViewById(R.id.privateButton);
		switch (view.getId())
		{
		case R.id.publicButton:
			if (publicButton.isChecked())
			{
				privateButton.setChecked(false);
			}
			
			break;
		case R.id.privateButton:
			
			if (privateButton.isChecked())
			{
				publicButton.setChecked(false);
			}
			break;
		}
	}

	//aSynch class to create group 
	private class CreateGroupTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... urls)
		{
			return readJSONFeed(urls[0]);
		}
		
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
					System.out.println("Done with proceess photo");
				}
				else
				{
					System.out.println("No photo set... skipping image processing.");
				}
				
				RadioButton privateButton = (RadioButton) findViewById(R.id.privateButton);
				
				//grab group name and bio from textviews
				String groupname = nameEditText.getText().toString();
				String groupbio = aboutEditText.getText().toString();	
								
				//1 for public, 0 for private.
				int publicStatus = 1;
				
				if(privateButton.isChecked())
				{
					publicStatus = 0;
				}
				
				System.out.println("about to add other fields");
				// add remaining fields to builder
				builder.addTextBody("g_name",groupname,ContentType.TEXT_PLAIN);
				builder.addTextBody("about",groupbio,ContentType.TEXT_PLAIN);
				builder.addTextBody("public", Integer.toString(publicStatus), ContentType.TEXT_PLAIN);
				builder.addTextBody("creator", user.getEmail(), ContentType.TEXT_PLAIN);
			
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

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);

				// group has been successfully created
				if (jsonObject.getString("success").toString().equals("1"))
				{
					//now we can grab the newly created g_id returned from the server
					//Note: g_id is the only unique identifier of a group and therefore must be used for any future calls concerning that group.
					g_id = jsonObject.getString("g_id").toString();
					
					System.out.println("g_id of newly created group is: "+g_id);
					
					//add yourself to the group as Creator
					new AddGroupMembersTask().execute("http://68.59.162.183/"
							+ "android_connect/add_groupmember.php", user.getEmail(), user.getEmail(), "C", g_id);
					
					
					//display confirmation box
					AlertDialog dialog = new AlertDialog.Builder(GroupCreateActivity.this)
					.setMessage("Nice work, you've successfully created a group!")
					.setCancelable(true)
					.setPositiveButton("Invite Friends to Your Group", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int id)
						{
							//add code here to take user to groupaddmembersactivity page.  (pass g_id as extra so invites can be sent to correct group id)
							//loadDialog.show();
							Intent intent = new Intent(GroupCreateActivity.this, InviteActivity.class);
							intent.putExtra("email", user.getEmail());
							intent.putExtra("g_id", g_id);
							startActivity(intent);
							finish();
						}
					}).setNegativeButton("View Your Group's Profile", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							//add code here to take user to newly created group profile page.  (pass g_id as extra so correct group profile can be loaded)
							//loadDialog.show();
							Intent intent = new Intent(GroupCreateActivity.this, GroupProfileActivity.class);
							intent.putExtra("email", user.getEmail());
							intent.putExtra("g_id", g_id);
							startActivity(intent);	
							finish();
						}
					}).show();
					// if user dimisses the confirmation box, gets sent to back
					// to groupsActivity.class
					dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
					{

						@Override
						public void onCancel(DialogInterface dialog)
						{
							finish();
						}
					});
				} 
				//Create group failed for some reasons.
				else if (jsonObject.getString("success").toString().equals("0"))
				{	
					//display error box
					new AlertDialog.Builder(GroupCreateActivity.this)
					.setMessage("Unable to create group! Please choose an option:")
					.setCancelable(true)
					.setPositiveButton("Try Again", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int id)
						{
							//initiate creation of group AGAIN
							new CreateGroupTask().execute("http://68.59.162.183/"
							+ "android_connect/create_group.php");
						}
					}).setNegativeButton("Cancel", null).show();
				}
			} 
			catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
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
	//aSynch task to add individual member to group.
	private class AddGroupMembersTask extends AsyncTask<String,Void,String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("sender", urls[2]));
			nameValuePairs.add(new BasicNameValuePair("role", urls[3]));
			nameValuePairs.add(new BasicNameValuePair("g_id", urls[4]));
			//pass url and nameValuePairs off to global to do the JSON call.  Code continues at onPostExecute when JSON returns.
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				// member has been successfully added
				if (jsonObject.getString("success").toString().equals("1"))
				{
					//all working correctly, continue to next user or finish.
				} 
				else if (jsonObject.getString("success").toString().equals("0"))
				{	
					//a particular user was unable to be added to database for some reason...
					//Don't tell the user!
				}
			} 
			catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}
		}		
	}
}
