package cs460.grouple.grouple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmUtility extends Application 
{
	
	private String SENDER_ID = "957639483805"; 
	private GoogleCloudMessaging gcm;
	private String recipientRegID = "";
	private Global GLOBAL;
	private User user;
	private AtomicInteger msgId = new AtomicInteger();
	
	public GcmUtility() 
	{		
		gcm = GoogleCloudMessaging.getInstance(this);
		GLOBAL = ((Global) getApplicationContext());
		user = GLOBAL.getCurrentUser();	
	}
	
	public void sendNotification(String recipient, String notificationType) 
	{
		//Get Recipient's RegID
		new getRegIDTask().execute("http://68.59.162.183/android_connect/get_chat_id.php", recipient);
		
		if(notificationType.equals("FRIEND_REQUEST"))
		{
			//Send a friend request to the
			sendFriendRequest();
		}
		
	}

	private void sendFriendRequest() 
	{
        new AsyncTask<Void, Void, String>() 
        {
            @Override
            protected String doInBackground(Void... params) 
            {
                String message = "";
                try 
                {
                   Bundle data = new Bundle();
                   //Set bundle
                   data.putString("my_action", "cs460.grouple.grouple.ECHO_NOW");
                   data.putString("CONTENT_TYPE", "FRIEND_REQUEST");
                   data.putString("sender", user.getEmail());
                   data.putString("recipient",recipientRegID);
                   //This is where we put our first and last name. That way the recipient knows who sent it.
                   data.putString("first", user.getFirstName());
                   data.putString("last", user.getLastName());
                   String id = Integer.toString(msgId.incrementAndGet());
                   gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                   message = "Sent message";
                } 
                catch (IOException ex) 
                {
					//Toast toast = GLOBAL.getToast(MessagesActivity.this, "Error sending message. Please try again.");
					//toast.show();
					//sendMessageButton.setClickable(true);
                    //message = "Error :" + ex.getMessage();
                }
                return message;
            }
            @Override
            protected void onPostExecute(String msg) {
            	
            }
        }.execute(null, null, null);

	}
	//This task gets your friend's regid
    private class getRegIDTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			//The recipient's email is urls[1]
			nameValuePairs.add(new BasicNameValuePair("email", urls[1]));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{
					String id = jsonObject.getString("regid").toString();
					recipientRegID = id;
				} 
				else
				{
					//Toast toast = GLOBAL.getToast(MessagesActivity.this, "Error getting GCM REG_ID.");
					//toast.show();
				}
			} 
			catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
    

}
