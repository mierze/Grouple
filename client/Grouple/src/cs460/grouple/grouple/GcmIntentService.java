
/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed RECEIVER in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cs460.grouple.grouple;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} RECEIVER
 * release the wake lock.
 */
public class GcmIntentService extends IntentService
{
	private static final int NOTIFICATION_ID = 1;
	private static final String TAG = "GCM_Intent_Service";
	private NotificationManager mNotificationManager;
	private Bundle EXTRAS;
	private String SENDER_FIRST; 
	private String SENDER_LAST; 
	private String SENDER_EMAIL;
	private String TYPE;
	// group, event name
	private String NAME; 
	 // TODO: user getting message, invite, request or group/event id receiving something
	private String RECEIVER;
	// message in messages
	private String MESSAGE; 
	private Global GLOBAL;
	private String GROUP_ID;
	private String GROUP_NAME;
	private String EVENT_ID;
	private String EVENT_NAME;

	// types of intents
	enum CONTENT_TYPE
	{
		FRIEND_REQUEST, USER_MESSAGE, GROUP_MESSAGE, EVENT_MESSAGE, GROUP_INVITE, EVENT_INVITE, EVENT_UPDATED,EVENT_APPROVED,FRIEND_REQUEST_ACCEPTED;
	}

	// construcRECEIVERr
	public GcmIntentService()
	{
		super("GcmIntentService");
	}

	/*
	 * Intents come in SENDER other devices, this parses the bundle and sets all
	 * variables
	 */
	@Override
	protected void onHandleIntent(Intent intent)
	{
		GLOBAL = ((Global) getApplicationContext());
		EXTRAS = intent.getExtras();
		
		TYPE = EXTRAS.getString("content");
		
		if (TYPE != null)
		{
			if(TYPE.equals(CONTENT_TYPE.USER_MESSAGE.toString()))
			{
				SENDER_FIRST = EXTRAS.getString("first");
				SENDER_LAST = EXTRAS.getString("last");
				MESSAGE = EXTRAS.getString("msg");
				SENDER_EMAIL = EXTRAS.getString("sender");
				RECEIVER = EXTRAS.getString("receiver");
			}
			else if (TYPE.equals(CONTENT_TYPE.FRIEND_REQUEST.toString()))
			{
				SENDER_FIRST = EXTRAS.getString("first");
				SENDER_LAST = EXTRAS.getString("last");
				SENDER_EMAIL = EXTRAS.getString("sender");
				//???
				RECEIVER = EXTRAS.getString("receiver");
			}
			else if(TYPE.equals(CONTENT_TYPE.GROUP_INVITE.toString()))
			{
				SENDER_FIRST = EXTRAS.getString("first");
				SENDER_LAST = EXTRAS.getString("last");
				SENDER_EMAIL = EXTRAS.getString("sender");
				GROUP_ID = EXTRAS.getString("group_id");
				GROUP_NAME = EXTRAS.getString("group_name");
				//???
				RECEIVER = EXTRAS.getString("receiver");
			}
			else if(TYPE.equals(CONTENT_TYPE.EVENT_INVITE.toString()))
			{
				SENDER_FIRST = EXTRAS.getString("first");
				SENDER_LAST = EXTRAS.getString("last");
				SENDER_EMAIL = EXTRAS.getString("sender");
				EVENT_ID = EXTRAS.getString("event_id");
				EVENT_NAME = EXTRAS.getString("event_name");
				//???
				RECEIVER = EXTRAS.getString("receiver");
			}
			else if(TYPE.equals(CONTENT_TYPE.EVENT_APPROVED.toString()))
			{
				SENDER_FIRST = EXTRAS.getString("first");
				SENDER_LAST = EXTRAS.getString("last");
				SENDER_EMAIL = EXTRAS.getString("sender");
				EVENT_ID = EXTRAS.getString("event_id");
				EVENT_NAME = EXTRAS.getString("event_name");
				//???
				RECEIVER = EXTRAS.getString("receiver");
			}
			else if(TYPE.equals(CONTENT_TYPE.FRIEND_REQUEST_ACCEPTED.toString()))
			{
				SENDER_FIRST = EXTRAS.getString("first");
				SENDER_LAST = EXTRAS.getString("last");
				SENDER_EMAIL = EXTRAS.getString("sender");
				//???
				RECEIVER = EXTRAS.getString("receiver");
			}
		}

		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		
		/*
		 * Filter messages based on message type. Since it is likely that
		 * GCM will be extended in the future with new message types, just
		 * ignore any message types you're not interested in, or that you
		 * don't recognize.
		 */
		if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
		{
			sendNotification("Send error: " + EXTRAS.toString(), intent);
		}
		else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
		{
			sendNotification("Deleted messages on server: " + EXTRAS.toString(), intent);
		}
		else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
		{
			Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
			// Post notification of received message.
			if(GLOBAL.getCurrentUser() != null)
			{
				if(GLOBAL.isCurrentUser(RECEIVER))
				{
					//SEND THE NOTIFICATION TO A DEVICE IF AND ONLY WHEN RECEIVER IS THE USER LOGGED IN ON THAT DEVICE
					sendNotification(MESSAGE, intent);
				}
				else
				{
					//CURRENT USER FOR THIS DEVICE DIDNT MATCH RECEIPIENT
					//don't create notification on this device
					//sendNotification(MESSAGE, intent);
				}
			}
			else
			{
				//NO CURRENT USER FOR THIS DEVICE
				//don't create notification on this device
			}
			
			Log.i(TAG, "Received: " + EXTRAS.toString());
		}
	}

	// Put the message inRECEIVER a notification and post it.
	// This is just one simple example of what you might choose RECEIVER do with
	// a GCM message.
	private void sendNotification(String msg, Intent intent)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);



		//Note to todd/brett:  This line (updateMyActivity) will be not be called if one of the two if statements above ("don't create //notification on this device") are tripped.
		//This won't happen often but it is certainly possible. Don't know what it does so didn't know if that mattered.  
		//If it NEEDS to be called, maybe move it up before sendNotification method is activated?
		//-Scott 

		updateMyActivity(this);// crash seems RECEIVER be here

		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		//Group Message Notification.
		if (TYPE.equals(CONTENT_TYPE.GROUP_MESSAGE.toString()))
		{
			//first check whether user had setting was set to 'ON'
			String setting = prefs.getString("androidGroupMessage", null);
			if(setting.equals("1"))
			{
			
				Intent notificationIntent = new Intent(this, EntityMessagesActivity.class);
				notificationIntent.putExtra("CONTENT_TYPE", "GROUP");
				notificationIntent.putExtra("g_id", GROUP_ID);
				notificationIntent.putExtra("email", SENDER_EMAIL);
				notificationIntent.putExtra("name", GROUP_NAME);
				
				
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setContentTitle(NAME)
				.setStyle(new NotificationCompat.BigTextStyle()
				.bigText(SENDER_FIRST + " " + SENDER_LAST + " in " +GROUP_NAME+ ":" + msg))
				.setSmallIcon(R.drawable.icon_grouple).setSound(soundUri).setContentText(msg);
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder.setAutoCancel(true);

				// null check
				mBuilder.setContentIntent(contentIntent);
				mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());		
			}
		}
		//Event Message Notification.
		else if(TYPE.equals(CONTENT_TYPE.EVENT_MESSAGE.toString()))
		{
			//first check whether user had setting was set to 'ON'
			String setting = prefs.getString("androidEventMessage", null);
			if(setting.equals("1"))
			{
				Intent notificationIntent = new Intent(this, EntityMessagesActivity.class);
				
				notificationIntent.putExtra("CONTENT_TYPE", "EVENT");
				notificationIntent.putExtra("e_id", EVENT_ID);
				notificationIntent.putExtra("email", SENDER_EMAIL);
				notificationIntent.putExtra("name", EVENT_NAME);
				
				
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setContentTitle(NAME)
				.setStyle(new NotificationCompat.BigTextStyle()
				.bigText(SENDER_FIRST + " " + SENDER_LAST + " in " +EVENT_NAME+ ":" + msg))
				.setSmallIcon(R.drawable.icon_grouple).setSound(soundUri).setContentText(msg);
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder.setAutoCancel(true);
			
				// null check
				mBuilder.setContentIntent(contentIntent);
				mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
			}
		}
		//User Message Notification
		else if (TYPE.equals(CONTENT_TYPE.USER_MESSAGE.toString()))
		{
			//first check whether user had setting was set to 'ON'
			String setting = prefs.getString("androidFriendMessage", null);
			if(setting.equals("1"))
			{
				Intent notificationIntent = new Intent(getApplicationContext(), MessagesActivity.class);
				
				notificationIntent.putExtra("sender", SENDER_EMAIL);
				notificationIntent.putExtra("name", SENDER_FIRST + " " + SENDER_LAST);
				//notificationIntent.putExtra("email", RECEIVER);
				notificationIntent.putExtra("email", SENDER_EMAIL);
				
				
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setContentTitle(SENDER_FIRST + " " + SENDER_LAST).setStyle(new NotificationCompat.BigTextStyle()
				.bigText(msg)).setSmallIcon(R.drawable.icon_grouple).setSound(soundUri).setContentText(msg);
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder.setAutoCancel(true);
	
				// null check
				mBuilder.setContentIntent(contentIntent);
				mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
			}
		}
		//Friend request notification.
		else if (TYPE.equals(CONTENT_TYPE.FRIEND_REQUEST.toString()))
		{
			//first check whether user had setting was set to 'ON'
			String setting = prefs.getString("androidFriendReq", null);
			if(setting.equals("1"))
			{
				Intent notificationIntent = new Intent(getApplicationContext(), UserListActivity.class);
				notificationIntent.putExtra("name", SENDER_FIRST + " " + SENDER_LAST);
				notificationIntent.putExtra("content", "FRIEND_REQUESTS");
				notificationIntent.putExtra("email", RECEIVER);
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setContentTitle("New friend request from " + SENDER_FIRST + " " + SENDER_LAST + "!")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(SENDER_FIRST + " " + SENDER_LAST))
				.setSmallIcon(R.drawable.icon_grouple).setSound(soundUri).setContentText(msg);
	
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder.setAutoCancel(true);
	
				// null check
				mBuilder.setContentIntent(contentIntent);
				mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
			}
		}
		//Group Invite notification.
		else if (TYPE.equals(CONTENT_TYPE.GROUP_INVITE.toString()))
		{
			//first check whether user had setting was set to 'ON'
			String setting = prefs.getString("androidGroupReq", null);
			if(setting.equals("1"))
			{
				Intent notificationIntent = new Intent(getApplicationContext(), GroupListActivity.class);
				
				notificationIntent.putExtra("name", SENDER_FIRST + " " + SENDER_LAST);
				notificationIntent.putExtra("content", "GROUP_INVITES");
				
				//TODO:???GLOBAL.getCurrentUser().fetchFriendRequests();
				//GLOBAL.getCurrentUser().fetchUserInfo();
				
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setContentTitle("New group invite to " + GROUP_NAME + "!")
				.setContentText("From: " + SENDER_FIRST + " " + SENDER_LAST + "!")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(SENDER_FIRST + " " + SENDER_LAST))
				.setSmallIcon(R.drawable.icon_grouple).setSound(soundUri).setContentText(msg);
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,	PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder.setAutoCancel(true);

				// null check
				mBuilder.setContentIntent(contentIntent);
				mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
			}
		}
		//Event Invite Notification.
		else if(TYPE.equals(CONTENT_TYPE.EVENT_INVITE.toString()))
		{
			//first check whether user had setting was set to 'ON'
			String setting = prefs.getString("androidEventReq", null);
			if(setting.equals("1"))
			{
				Intent notificationIntent = new Intent(getApplicationContext(), EventListActivity.class);
			
				notificationIntent.putExtra("name", SENDER_FIRST + " " + SENDER_LAST);
				notificationIntent.putExtra("content", "EVENT_INVITES");
	
				//TODO:???GLOBAL.getCurrentUser().fetchFriendRequests();
				//GLOBAL.getCurrentUser().fetchUserInfo();
				
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setContentTitle("New Event Invite")
				.setContentText(EVENT_NAME)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(SENDER_FIRST + " " + SENDER_LAST))
				.setSmallIcon(R.drawable.icon_grouple).setSound(soundUri).setContentText(msg);
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,	PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder.setAutoCancel(true);
	
				// null check
				mBuilder.setContentIntent(contentIntent);
				mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
			}
		}
		//Event Invite Notification.
		else if (TYPE.equals(CONTENT_TYPE.EVENT_APPROVED.toString()))
		{
			//first check whether user had setting was set to 'ON'
			//this setting is the one called 'Event Updates' button in settingsActivity
			String setting = prefs.getString("androidEventUpcoming", null);
			if(setting.equals("1"))
			{
				Intent notificationIntent = new Intent(getApplicationContext(), EventProfileActivity.class);
				
				notificationIntent.putExtra("e_id", EVENT_ID);
				notificationIntent.putExtra("name", SENDER_FIRST + " " + SENDER_LAST);
				//TODO
				
				//TODO:???GLOBAL.getCurrentUser().fetchFriendRequests();
				//GLOBAL.getCurrentUser().fetchUserInfo();
						
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setContentTitle("Grouple event has been approved!")
				.setContentText(EVENT_NAME)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(SENDER_FIRST + " " + SENDER_LAST))
				.setSmallIcon(R.drawable.icon_grouple).setSound(soundUri).setContentText(msg);
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,	PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder.setAutoCancel(true);
	
				// null check
				mBuilder.setContentIntent(contentIntent);
				mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
			}
		}
		else if (TYPE.equals(CONTENT_TYPE.EVENT_UPDATED.toString()))
		{
			//first check whether user had setting was set to 'ON'
			//this setting is the one called 'Event Updates' button in settingsActivity
			String setting = prefs.getString("androidEventUpcoming", null);
			if(setting.equals("1"))
			{
				Intent notificationIntent = new Intent(getApplicationContext(), EventProfileActivity.class);
				
				notificationIntent.putExtra("e_id", EVENT_ID);
				notificationIntent.putExtra("name", SENDER_FIRST + " " + SENDER_LAST);
				//TODO
				
				//TODO:???GLOBAL.getCurrentUser().fetchFriendRequests();
				//GLOBAL.getCurrentUser().fetchUserInfo();
						
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setContentTitle("Grouple event has been updated!")
				.setContentText(EVENT_NAME)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(SENDER_FIRST + " " + SENDER_LAST))
				.setSmallIcon(R.drawable.icon_grouple).setSound(soundUri).setContentText(msg);
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,	PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder.setAutoCancel(true);
	
				// null check
				mBuilder.setContentIntent(contentIntent);
				mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
			}
		}
		else if (TYPE.equals(CONTENT_TYPE.FRIEND_REQUEST_ACCEPTED.toString()))
		{
			//first check whether user had setting was set to 'ON'
			String setting = prefs.getString("androidFriendReq", null);
			if(setting.equals("1"))
			{
				Intent notificationIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
				
				notificationIntent.putExtra("email", SENDER_EMAIL);
				notificationIntent.putExtra("name", SENDER_FIRST + " " + SENDER_LAST);
						
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setContentTitle("Grouple")
				.setContentText(SENDER_FIRST+" "+SENDER_LAST+" accepted your friend request!")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(SENDER_FIRST + " " + SENDER_LAST))
				.setSmallIcon(R.drawable.icon_grouple).setSound(soundUri).setContentText(msg);
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,	PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder.setAutoCancel(true);
	
				// null check
				mBuilder.setContentIntent(contentIntent);
				mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
			}
		}	
		
		// Now that GCM creation is finished, release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// This function will create an intent. This intent must take as parameter
	// the "unique_name" that you registered your activity with
	private void updateMyActivity(Context context)
	{
		Intent intent = null;
		// change intent based on type
		if (TYPE.equals(CONTENT_TYPE.GROUP_MESSAGE.toString()))
		{
			intent = new Intent("ENTITY_MESSAGE");
			intent.putExtra("ID", GROUP_ID);
			intent.putExtra("TYPE", TYPE);
			intent.putExtra("SENDER", SENDER_EMAIL);
			intent.putExtra("name", SENDER_FIRST + " " + SENDER_LAST);
		}
		else if (TYPE.equals(CONTENT_TYPE.EVENT_MESSAGE.toString()))
		{
			intent = new Intent("ENTITY_MESSAGE");
			intent.putExtra("ID", EVENT_ID);
			intent.putExtra("TYPE", TYPE);
			intent.putExtra("SENDER", SENDER_EMAIL);
			intent.putExtra("name", SENDER_FIRST + " " + SENDER_LAST);
		}
		else if (TYPE.equals(CONTENT_TYPE.USER_MESSAGE.toString()))
		{
			intent = new Intent("USER_MESSAGE");
			intent.putExtra("sender", SENDER_EMAIL);
			intent.putExtra("name", SENDER_FIRST + " " + SENDER_LAST);
			intent.putExtra("receiver", RECEIVER);
		}
		else if (TYPE.equals(CONTENT_TYPE.FRIEND_REQUEST.toString()))
		{
			//intent = new Intent("?");
		}
		else if (TYPE.equals(CONTENT_TYPE.GROUP_INVITE.toString()))
		{
			//intent = new Intent("?");
		}
		else if (TYPE.equals(CONTENT_TYPE.EVENT_INVITE.toString()))
		{
			//intent = new Intent("?");
		}
		else if (TYPE.equals(CONTENT_TYPE.EVENT_APPROVED.toString()))
		{
			//intent = new Intent("?");
		}
		else if (TYPE.equals(CONTENT_TYPE.FRIEND_REQUEST_ACCEPTED.toString()))
		{
			//intent = new Intent("?");
		}

		// send broadcast
		if (intent != null)
			LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}
}
