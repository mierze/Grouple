package cs460.grouple.grouple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * 
 * @author Brett, Todd, Scott
 * GcmUtility handles the GCM for various areas of the application
 * 
 */
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
	private String recipientEmail;
	private ArrayList<String> multipleEmailList = new ArrayList<String>();
	private ArrayList<String> groupInviteEmailList = new ArrayList<String>();
	private ArrayList<String> reg_ids = new ArrayList<String>();
	private ArrayList<Integer> g_id_list = new ArrayList<Integer>();
	private ArrayList<String> eventApprovedList = new ArrayList<String>();
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
		recipientEmail = recipient;
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
	//Pass a g_id and it will return all of the reg_ids. If a member is in both groups, it will only add the reg_id once.
	public void getRegIdByGid(ArrayList<Integer> g_ids)
	{
		//TODO: maybe use new php
		new getRegIdsByGidTask().execute("http://68.59.162.183/android_connect/get_reg_ids_by_g_id.php",g_id_list.get(g_id_list.size()-1).toString());
	}
	
	//Get the RegID for the given email address and add it to chat_ids
	public void sendEventInvite(ArrayList<Integer> g_ids, String eid)
	{
		//Need this filler method so we can set the notification type and call getRegIdByGid.
		notificationType = "EVENT_INVITE";
		eventID = eid;
		g_id_list = g_ids;
		getRegIdByGid(g_id_list);		
	}
	
	public void sendEventUpdated(String eName, String e_id)
	{
		notificationType = "EVENT_UPDATE";
		eventName = eName;
		eventID = e_id;
		new getRegIdsByEidTask().execute("http://68.59.162.183/android_connect/get_reg_ids_by_e_id.php", e_id);	
	}
	
	public void sendEventApproved(String eName, String e_id)
	{
		notificationType = "EVENT_APPROVED";
		eventName = eName;
		eventID = e_id;
		new getRegIdsByEidTask().execute("http://68.59.162.183/android_connect/get_reg_ids_by_e_id.php", e_id);	
	}
	
	//public void sendEventNotification(String eName,String e_id, String notificationType) 
	//{
		//Set class variables
	//	eventName = eName;
	//	eventID = e_id;
	//	this.notificationType = notificationType;
		
		//Get the RegID for each user in the event.
	//	new getRegIdsByEidTask().execute("http://68.59.162.183/android_connect/get_reg_ids_by_e_id.php", e_id);	
		
	//}


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
                   data.putString("receiver", recipientEmail);
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
                   data.putString("receiver",groupInviteEmailList.get(groupInviteEmailList.size()-1));
                   data.putString("group_name", groupName);
                   data.putString("group_id", groupID);
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
	
	private void sendEventInviteTask() 
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
                   data.putString("recipient",reg_ids.get(reg_ids.size()-1));
                   //data.putString("event_name", eventName);
                   data.putString("receiver",multipleEmailList.get(multipleEmailList.size()-1));
                   data.putString("event_id", eventID);
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
            	reg_ids.remove(reg_ids.size()-1);
            	multipleEmailList.remove(multipleEmailList.size()-1);
            	//Use  recurrsion to send it to the next user.
            	if(!reg_ids.isEmpty())
            	{
            		sendEventInviteTask();
            	}
            }
        }.execute(null, null, null);       

	}
	
	private void sendEventApprovedTask() 
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
                   data.putString("recipient",reg_ids.get(reg_ids.size()-1));
                   data.putString("receiver", eventApprovedList.get(eventApprovedList.size()-1));
                   data.putString("event_name", eventName);
                   data.putString("event_id", eventID);
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
            	reg_ids.remove(reg_ids.size()-1);
            	eventApprovedList.remove(eventApprovedList.size()-1);
            	//Use  recurrsion to send it to the next user.
            	if(!reg_ids.isEmpty())
            	{
            		sendEventApprovedTask();
            	}
            }
        }.execute(null, null, null);

	}
	
	private void sendEventUpdatedTask() 
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
                   data.putString("recipient",reg_ids.get(reg_ids.size()-1));
                   data.putString("event_name", eventName);
                   data.putString("event_id", eventID);
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
            	reg_ids.remove(reg_ids.size()-1);
            	//Use  recurrsion to send it to the next user.
            	if(!reg_ids.isEmpty())
            	{
            		sendEventUpdatedTask();
            	}
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
					
					reg_ids.add(recipientRegID);
					
					groupInviteEmailList.add(multipleEmailList.get(multipleEmailList.size()-1));
					
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
							sendGroupInvite(groupName,groupID,reg_ids);
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
    
	//This task gets your friend's regid
    private class getRegIdsByEidTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			//The recipient's email is urls[1]
			nameValuePairs.add(new BasicNameValuePair("e_id", urls[1]));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				JSONArray jsonArray = jsonObject.getJSONArray("reg_ids");
				System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{
					for(int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						reg_ids.add(o.getString("reg_id"));
						eventApprovedList.add(o.getString("email"));
					}
					
					if(notificationType.equals("EVENT_APPROVED"))
					{
						sendEventApprovedTask();
					}
					else if(notificationType.equals("EVENT_UPDATE"))
					{
						sendEventUpdatedTask();
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
    private class getRegIdsByGidTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			//The recipient's email is urls[1]
			nameValuePairs.add(new BasicNameValuePair("g_id", urls[1]));
			return GLOBAL.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		//TODO: 
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				JSONArray jsonArray = jsonObject.getJSONArray("reg_ids");
				System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{
					for(int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject o = (JSONObject) jsonArray.get(i);
						if(!reg_ids.contains(o.getString("reg_id")))
						{
							reg_ids.add(o.getString("reg_id"));
							multipleEmailList.add(o.getString("email"));
						}
					
						
					}
					
					g_id_list.remove(g_id_list.size()-1);	
					
					//Use  recursion to get the next regID
					if(!g_id_list.isEmpty())
					{
						getRegIdByGid(g_id_list);
					}
					else
					{
						//We have all the IDs, now let send the push notifications, based on type.
						if(notificationType.equals("EVENT_INVITE"))
						{
							sendEventInviteTask();
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
