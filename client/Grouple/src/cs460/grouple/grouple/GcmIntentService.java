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
	private String SENDER; // sender email of message, invite, request, update
							// or id of event being updated
	private String SENDER_FIRST; // SENDER_FIRST name of sender if user
	private String SENDER_LAST; // last name of sender if user
	private String TYPE;
	private String NAME; // group, event name
	private String RECEIVER; // user getting message, invite, request or
								// group/event id receiving something
	private String MESSAGE; // message in messages
	private Global GLOBAL;

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
		MESSAGE = EXTRAS.getString("msg");
		SENDER = EXTRAS.getString("sender");
		RECEIVER = EXTRAS.getString("receiver");
		TYPE = EXTRAS.getString("content");

		NAME = EXTRAS.getString("name");

		if (TYPE != null)
		{
			if (TYPE.equals(CONTENT_TYPE.GROUP_MESSAGE.toString())
					|| TYPE.equals(CONTENT_TYPE.EVENT_MESSAGE.toString())
					|| TYPE.equals(CONTENT_TYPE.USER_MESSAGE.toString())
					|| TYPE.equals(CONTENT_TYPE.FRIEND_REQUEST.toString())
					|| TYPE.equals(CONTENT_TYPE.EVENT_INVITE.toString())
					|| TYPE.equals(CONTENT_TYPE.EVENT_INVITE.toString()))
			{
				SENDER_FIRST = EXTRAS.getString("first");
				SENDER_LAST = EXTRAS.getString("last");
			}
		}

		// String test = EXTRAS.getString("my_action");

		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!EXTRAS.isEmpty())
		{ // has effect of unparcelling Bundle
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
				// If it's a regular GCM message, do some work.
			}
			else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
			{
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
				// Post notification of received message.
				sendNotification(MESSAGE);
				Log.i(TAG, "Received: " + EXTRAS.toString());
			}
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
		if (TYPE.equals(CONTENT_TYPE.GROUP_MESSAGE.toString()) || TYPE.equals(CONTENT_TYPE.EVENT_MESSAGE.toString()))
		{
			Intent notificationIntent = new Intent(this, EntityMessagesActivity.class);
			if (TYPE.equals(CONTENT_TYPE.GROUP_MESSAGE.toString()))
			{
				notificationIntent.putExtra("CONTENT_TYPE", "GROUP");
				notificationIntent.putExtra("GID", RECEIVER);
				Group g = new Group(Integer.parseInt(RECEIVER));
				g.fetchGroupInfo();
				GLOBAL.setGroupBuffer(g);
			}
			else
			{
				notificationIntent.putExtra("CONTENT_TYPE", "EVENT");
				notificationIntent.putExtra("EID", RECEIVER);
				Event e = new Event(Integer.parseInt(RECEIVER));
				e.fetchEventInfo();
				GLOBAL.setEventBuffer(e);
			}
			notificationIntent.putExtra("EMAIL", SENDER);
			notificationIntent.putExtra("NAME", NAME);
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setContentTitle(NAME)
					.setStyle(new NotificationCompat.BigTextStyle()
					// .bigText(msg))
							.bigText(SENDER_FIRST + " " + SENDER_LAST + ": " + msg))
					.setSmallIcon(R.drawable.icon_grouple).setSound(soundUri).setContentText(msg);
			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setAutoCancel(true);

			// null check
			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		}
		else if (TYPE.equals(CONTENT_TYPE.USER_MESSAGE.toString()))
		{
			Intent notificationIntent = new Intent(getApplicationContext(), MessagesActivity.class);
			notificationIntent.putExtra("sender", SENDER);
			notificationIntent.putExtra("name", SENDER_FIRST + " " + SENDER_LAST);
			notificationIntent.putExtra("receiver", RECEIVER);
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
					.setContentTitle(SENDER_FIRST + " " + SENDER_LAST).setStyle(new NotificationCompat.BigTextStyle()
					// .bigText(msg))
							.bigText(msg)).setSmallIcon(R.drawable.icon_grouple).setSound(soundUri).setContentText(msg);
			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setAutoCancel(true);

			// null check
			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		}
		else if (TYPE.equals(CONTENT_TYPE.FRIEND_REQUEST.toString()))
		{
			// Send friend request.
			Intent notificationIntent = new Intent(getApplicationContext(), ListActivity.class);
			notificationIntent.putExtra("EMAIL", GLOBAL.getCurrentUser().getEmail());
			notificationIntent.putExtra("NAME", SENDER_FIRST + " " + SENDER_LAST);
			notificationIntent.putExtra("CONTENT", CONTENT_TYPE.FRIEND_REQUEST.toString());
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
		else if (TYPE.equals(CONTENT_TYPE.GROUP_INVITE.toString()))
		{
			// Send friend request.
			Intent notificationIntent = new Intent(getApplicationContext(), ListActivity.class);
			notificationIntent.putExtra("EMAIL", GLOBAL.getCurrentUser().getEmail());
			notificationIntent.putExtra("NAME", SENDER_FIRST + " " + SENDER_LAST);
			notificationIntent.putExtra("CONTENT", "GROUPS_INVITES");
			GLOBAL.getCurrentUser().fetchFriendRequests();
			GLOBAL.getCurrentUser().fetchUserInfo();
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
					.setContentTitle("New group invite to " + NAME + " from " + SENDER_FIRST + " " + SENDER_LAST + "!")
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

	// This function will create an intent. This intent must take as parameter
	// the "unique_name" that you registered your activity with
	private void updateMyActivity(Context context)
	{
		Intent intent = null;
		// change intent based on type
		if (TYPE.equals(CONTENT_TYPE.GROUP_MESSAGE.toString()) || TYPE.equals(CONTENT_TYPE.EVENT_MESSAGE.toString()))
		{
			intent = new Intent("ENTITY_MESSAGE");
			intent.putExtra("ID", RECEIVER);
			intent.putExtra("TYPE", TYPE);
			intent.putExtra("SENDER", SENDER);
			intent.putExtra("NAME", SENDER_FIRST + " " + SENDER_LAST);
		}
		else if (TYPE.equals(CONTENT_TYPE.USER_MESSAGE.toString()))
		{
			intent = new Intent("USER_MESSAGE");
			intent.putExtra("sender", SENDER);
			intent.putExtra("name", SENDER_FIRST + " " + SENDER_LAST);
			intent.putExtra("receiver", RECEIVER);
		}
		else if (TYPE.equals(CONTENT_TYPE.FRIEND_REQUEST.toString()))
		{
			// intent = new I
		}

		// send broadcast
		if (intent != null)
			context.sendBroadcast(intent);
	}
}
