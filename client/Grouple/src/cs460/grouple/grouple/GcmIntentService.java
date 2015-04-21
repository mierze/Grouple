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
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
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
		FRIEND_REQUEST, USER_MESSAGE, GROUP_MESSAGE, EVENT_MESSAGE, GROUP_INVITE, EVENT_INVITE, EVENT_UPDATE;
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
			sendNotification("Send error: " + EXTRAS.toString());
		}
		else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
		{
			sendNotification("Deleted messages on server: " + EXTRAS.toString());
		}
		else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
		{
			Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
			// Post notification of received message.
			sendNotification(MESSAGE);
			Log.i(TAG, "Received: " + EXTRAS.toString());
		}
		
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message inRECEIVER a notification and post it.
	// This is just one simple example of what you might choose RECEIVER do with
	// a GCM message.
	private void sendNotification(String msg)
	{
		updateMyActivity(this);// crash seems RECEIVER be here
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		//Group Message Notification.
		if (TYPE.equals(CONTENT_TYPE.GROUP_MESSAGE.toString()))
		{
			Intent notificationIntent = new Intent(this, EntityMessagesActivity.class);

			notificationIntent.putExtra("CONTENT_TYPE", "GROUP");
			notificationIntent.putExtra("GID", GROUP_ID);
			notificationIntent.putExtra("email", SENDER_EMAIL);
			notificationIntent.putExtra("name", GROUP_NAME);
			
			Group g = new Group(Integer.parseInt(GROUP_ID));
			g.fetchGroupInfo();
			GLOBAL.setGroupBuffer(g);
			
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
		//Event Message Notification.
		else if(TYPE.equals(CONTENT_TYPE.EVENT_MESSAGE.toString()))
		{
			Intent notificationIntent = new Intent(this, EntityMessagesActivity.class);
			
			notificationIntent.putExtra("CONTENT_TYPE", "EVENT");
			notificationIntent.putExtra("EID", EVENT_ID);
			notificationIntent.putExtra("email", SENDER_EMAIL);
			notificationIntent.putExtra("name", EVENT_NAME);
			
			Event e = new Event(Integer.parseInt(EVENT_ID));
			e.fetchEventInfo();
			GLOBAL.setEventBuffer(e);
			
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
		//User Message Notification
		else if (TYPE.equals(CONTENT_TYPE.USER_MESSAGE.toString()))
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
		//Friend request notification.
		else if (TYPE.equals(CONTENT_TYPE.FRIEND_REQUEST.toString()))
		{
			Intent notificationIntent = new Intent(getApplicationContext(), ListActivity.class);
			notificationIntent.putExtra("email", GLOBAL.getCurrentUser().getEmail());
			notificationIntent.putExtra("name", SENDER_FIRST + " " + SENDER_LAST);
			notificationIntent.putExtra("content", CONTENT_TYPE.FRIEND_REQUEST.toString());
			GLOBAL.getCurrentUser().fetchFriendRequests();
			GLOBAL.getCurrentUser().fetchUserInfo();

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
		//Group Invite notification.
		else if (TYPE.equals(CONTENT_TYPE.GROUP_INVITE.toString()))
		{
			Intent notificationIntent = new Intent(getApplicationContext(), ListActivity.class);
			
			notificationIntent.putExtra("email", GLOBAL.getCurrentUser().getEmail());
			notificationIntent.putExtra("name", SENDER_FIRST + " " + SENDER_LAST);
			notificationIntent.putExtra("content", "GROUPS_INVITES");
			
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
		//Event Invite Notification.
		else if (TYPE.equals(CONTENT_TYPE.EVENT_INVITE.toString()))
		{
			Intent notificationIntent = new Intent(getApplicationContext(), ListActivity.class);
			
			notificationIntent.putExtra("email", GLOBAL.getCurrentUser().getEmail());
			notificationIntent.putExtra("name", SENDER_FIRST + " " + SENDER_LAST);
			notificationIntent.putExtra("content", "GROUPS_INVITES");
			
			//TODO:???GLOBAL.getCurrentUser().fetchFriendRequests();
			//GLOBAL.getCurrentUser().fetchUserInfo();
			
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
			.setContentTitle("New event invite to " + EVENT_NAME + " from " + SENDER_FIRST + " " + SENDER_LAST + "!")
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

		// send broadcast
		if (intent != null)
			context.sendBroadcast(intent);
	}
}
