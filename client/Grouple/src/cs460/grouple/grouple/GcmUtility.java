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
	private ArrayList<String> multipleEmailList = new ArrayList<String>();
	private ArrayList<String> chat_ids = new ArrayList<String>();
	
	enum CONTENT_TYPE 
	{
		FRIEND_REQUEST, USER_MESSAGE, GROUP_MESSAGE, EVENT_MESSAGE, GROUP_INVITE, EVENT_INVITE, EVENT_UPDATE,EVENT_APPROVED,FRIEND_REQUEST_ACCEPTED;   
	}
	
	public GcmUtility(Global g) 
	{		
		gcm = GoogleCloudMessaging.getInstance(this);
		GLOBAL = g;
		user = GLOBAL.getCurrentUser();	
	}
	
	//This is for sending friend request notifications. It's seperate because it doesn't group_id, event name etc.
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
	public void sendGroupNotification(ArrayList<String> multipleEmailList, String gName,String gid,String notificationType)
	{
		//Set class variables
		groupName = gName;
		groupID = gid;
		this.notificationType = notificationType;
		this.multipleEmailList = multipleEmailList;
		//Get Recipient's RegID and send the push in the post execute.
		
		//Get the RegID for each user.
		getMultipleRegIDs();
		
	}
	//Get the RegID for the given email address and add it to chat_ids
	public void getMultipleRegIDs()
	{
			new getMultipleRegIDsTask().execute("http://68.59.162.183/android_connect/get_chat_id.php", multipleEmailList.get(multipleEmailList.size()-1));			
	}
	
	public void sendEventNotification(ArrayList<String> multipleEmailList, String eName,String eid, String notificationType) 
	{
		//Set class variables
		eventName = eName;
		eventID = eid;
		this.notificationType = notificationType;
		this.multipleEmailList = multipleEmailList;
		
		//Get the RegID for each user.
		getMultipleRegIDs();
		
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
	
	private void sendFriendRequestAccepted() 
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
                   data.putString("content", "FRIEND_REQUEST_ACCEPTED");
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
	
	private void sendGroupInvite(final String gName,final String gid, final ArrayList<String> chat_ids) 
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
                   //data.putString("recipient",recipientRegID);
                   data.putString("recipient",chat_ids.get(chat_ids.size()-1));
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
            protected void onPostExecute(String msg) 
            {
            	//Remove that regID from the need-to-send list.
            	chat_ids.remove(chat_ids.size()-1);
            	//Use fucking recurrsion to send it to the next user.
            	if(!chat_ids.isEmpty())
            	{
            		sendGroupInvite(groupName,groupID,chat_ids);
            	}
          
            }
        }.execute(null, null, null);

	}
	
	private void sendEventInvite(final String eName, final String eid, final ArrayList<String> chat_ids) 
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
            protected void onPostExecute(String msg)
            {
            	//Remove that regID from the need-to-send list.
            	chat_ids.remove(chat_ids.size()-1);
            	//Use fucking recurrsion to send it to the next user.
            	if(!chat_ids.isEmpty())
            	{
            		sendEventInvite(groupName,groupID,chat_ids);
            	}
            }
        }.execute(null, null, null);       

	}
	
	private void sendEventApproved(final String eName, final String eid) 
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
                   data.putString("content", "EVENT_APPROVED");
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
	
	private void sendEventUpdated(final String eName, final String eid) 
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
                   data.putString("content", "EVENT_UPDATED");
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
					else if(notificationType.equals("FRIEND_REQUEST_ACCEPTED"))
					{
						sendFriendRequestAccepted();
					}
					else if(notificationType.equals("GROUP_INVITE"))
					{
						sendGroupInvite(groupName,groupID,chat_ids);
					}
					else if(notificationType.equals("EVENT_INVITE"))
					{
						sendEventInvite(eventName,eventID,chat_ids);
					}
					else if(notificationType.equals("EVENT_APPROVED"))
					{
						sendEventApproved(eventName,eventID);
					}
					else if(notificationType.equals("EVENT_UPDATED"))
					{
						sendEventUpdated(eventName,eventID);
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
    
	//This task gets your friend's regid
    private class getMultipleRegIDsTask extends AsyncTask<String, Void, String>
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
					
					chat_ids.add(recipientRegID);
					
					multipleEmailList.remove(multipleEmailList.size()-1);	
					
					//Use fucking recursion to get the next regID
					if(!multipleEmailList.isEmpty())
					{
						getMultipleRegIDs();
					}
					else
					{
						//We have all the IDs, now let send the push notifications, based on type.
						if(notificationType.equals("GROUP_INVITE"))
						{
							sendGroupInvite(groupName,groupID,chat_ids);
						}
						else if (notificationType.equals("EVENT_INVITE"))
						{
							sendEventInvite(eventName,groupID,chat_ids);
						}
						
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
