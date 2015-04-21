package cs460.grouple.grouple;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GcmUtility extends Application {
	
	private String SENDER_ID = "957639483805"; 
	private GoogleCloudMessaging gcm;
	private String recipientRegID = "";
	private Global GLOBAL;
	private User user;
	private AtomicInteger msgId = new AtomicInteger();
	//Email of user recieving the push notification.
	private String groupName;
	private String groupID;
	private String notificationType;
	private String eventName;
	private String eventID;
	
	enum CONTENT_TYPE 
	{
		FRIEND_REQUEST, USER_MESSAGE, GROUP_MESSAGE, EVENT_MESSAGE, GROUP_INVITE, EVENT_INVITE, EVENT_UPDATE;   
	}
	
	public GcmUtility(Global g) 
	{		
		gcm = GoogleCloudMessaging.getInstance(this);
		GLOBAL = g;
		user = GLOBAL.getCurrentUser();	
	}
	
	//This is for sending friend request notifications. 
	public void sendNotification(String recipient, String notificationType) 
	{
		//Set class variables
		this.notificationType = notificationType;
		//Get Recipient's RegID
		new getRegIDTask().execute("http://68.59.162.183/android_connect/get_chat_id.php", recipient);
	}
	/*
	 * This is for sending group notifications. Only implemented invite for now..
	 * The naming convention is weird, but that's because we have to send the additional param, group name.
	 * So for now, this is for all group type notifications.
	 */
	public void sendGroupNotification(String recipient, String gName,String gid, String notificationType)
	{
		//Set class variables
		groupName = gName;
		groupID = gid;
		this.notificationType = notificationType;
		//Get Recipient's RegID and send the push in the post execute.
		new getRegIDTask().execute("http://68.59.162.183/android_connect/get_chat_id.php", recipient);
		
		//Here we can implement other types of Group Notifications.
	}
	
	public void sendEventNotification(String recipient, String eName,String eid, String notificationType) 
	{
		//Set class variables
		eventName = eName;
		eventID = eid;
		this.notificationType = notificationType;
		//Get Recipient's RegID
		new getRegIDTask().execute("http://68.59.162.183/android_connect/get_chat_id.php", recipient);
		
		//Here we can implement other types of Event Notifications.
	}


	private void sendFriendRequest() 
	{
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String message = "";
                try 
                {
                   Bundle data = new Bundle();
                   //Set bundle
                   data.putString("my_action", "cs460.grouple.grouple.ECHO_NOW");
                   data.putString("content", "FRIEND_REQUEST");
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
	private void sendGroupInvite(final String gName,final String gid) 
	{
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String message = "";
                try 
                {
                   Bundle data = new Bundle();
                   //Set bundle
                   data.putString("my_action", "cs460.grouple.grouple.ECHO_NOW");
                   data.putString("content", "GROUP_INVITE");
                   data.putString("sender", user.getEmail());
                   data.putString("recipient",recipientRegID);
                   data.putString("group_name", gName);
                   data.putString("group_id", gid);
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
	
	private void sendEventInvite(final String eName, final String eid) 
	{
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String message = "";
                try 
                {
                   Bundle data = new Bundle();
                   //Set bundle
                   data.putString("my_action", "cs460.grouple.grouple.ECHO_NOW");
                   data.putString("content", "EVENT_INVITE");
                   data.putString("sender", user.getEmail());
                   data.putString("recipient",recipientRegID);
                   data.putString("event_name", eName);
                   data.putString("event_id", eid);
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
	public boolean getRegID(String recipient)
	{
		//Get Recipient's RegID
		try {
			new getRegIDTask().execute("http://68.59.162.183/android_connect/get_chat_id.php", recipient).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
		
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
					
					if(notificationType.equals(CONTENT_TYPE.FRIEND_REQUEST.toString()))
					{
						sendFriendRequest();
					}
					else if(notificationType.equals("GROUP_INVITE"))
					{
						sendGroupInvite(groupName,groupID);
					}
					else if(notificationType.equals("EVENT_INVITE"))
					{
						sendEventInvite(eventName,eventID);
					}
				} 
				else
				{
					//Toast toast = GLOBAL.getToast(MessagesActivity.this, "Error getting GCM REG_ID.");
					//toast.show();
				}
			} catch (Exception e)
			{
				Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
}
