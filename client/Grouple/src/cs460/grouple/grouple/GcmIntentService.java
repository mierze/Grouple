/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
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
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService
{
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	String from;
	String first;
	String TYPE;
	String NAME;
	String to;
	String last;

	public GcmIntentService()
	{
		super("GcmIntentService");
	}

	public static final String TAG = "GCM Demo";

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Bundle extras = intent.getExtras();
		String msg = extras.getString("msg");		
		from = extras.getString("sender");
		to = extras.getString("receiver");
		TYPE = extras.getString("CONTENT_TYPE");
		first = extras.getString("first");
		last = extras.getString("last");
		

		if (TYPE != null)
		{
			if (TYPE.equals("GROUP_MESSAGE") || TYPE.equals("EVENT_MESSAGE"))
			{
				NAME = extras.getString("NAME");
			} else if (TYPE.equals("USER_MESSAGE"))
			{

			}
		}
		else
			TYPE = "USER_MESSAGE";
		//String test = extras.getString("my_action");

		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty())
		{ // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType))
			{
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType))
			{
				sendNotification("Deleted messages on server: "
						+ extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType))
			{
				// This loop represents the service doing some work.
				// for (int i = 0; i < 5; i++) {
				// Log.i(TAG, "Working... " + (i + 1)
				// + "/5 @ " + SystemClock.elapsedRealtime());
				// try {
				// Thread.sleep(5000);
				// } catch (InterruptedException e) {
				// }
				// }
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
				// Post notification of received message.
				sendNotification(msg);
				// DemoActivity demoActivity = (DemoActivity)
				// getApplicationContext();
				// demoActivity.injectMessage(msg);
				Log.i(TAG, "Received: " + extras.toString());
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// This function will create an intent. This intent must take as parameter
	// the "unique_name" that you registered your activity with
	private void updateMyActivity(Context context)
	{
		Intent intent = null;
		// change intent based on type
		if (TYPE.equals("GROUP_MESSAGE") || TYPE.equals("EVENT_MESSAGE"))
		{
			intent = new Intent("ENTITY_MESSAGE");
			intent.putExtra("ID", to);
			intent.putExtra("TYPE", TYPE);
			intent.putExtra("FROM", from);
			intent.putExtra("NAME", first + " " + last);
		} else if (TYPE.equals("USER_MESSAGE"))
		{
			intent = new Intent("USER_MESSAGE");
			intent.putExtra("FROM", from);
			intent.putExtra("NAME", first + " " + last);
			intent.putExtra("TO", to);
		}
		else if (TYPE.equals("FRIEND_REQUEST"))
		{
			//intent = new I
		}

		// send broadcast
		if (intent != null)
			context.sendBroadcast(intent);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(String msg)
	{
		Global global = (Global) getApplicationContext();
		updateMyActivity(this);//crash seems to be here
		// TODO: take this message and send it to the MessageActivity, possibly
		// call a function to populate the new message
		Uri soundUri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);



		if (TYPE.equals("GROUP_MESSAGE") || TYPE.equals("EVENT_MESSAGE"))
		{
			Intent notificationIntent = new Intent(this,
					EntityMessagesActivity.class);

			if (TYPE.equals("GROUP_MESSAGE"))
			{
				notificationIntent.putExtra("CONTENT_TYPE", "GROUP");
				notificationIntent.putExtra("GID", to);
				Group g = new Group(Integer.parseInt(to));
				g.fetchGroupInfo();
				global.setGroupBuffer(g);
			} else
			{
				notificationIntent.putExtra("CONTENT_TYPE", "EVENT");
				notificationIntent.putExtra("EID", to);
				Event e = new Event(Integer.parseInt(to));
				e.fetchEventInfo();
				global.setEventBuffer(e);
			}
			notificationIntent.putExtra("EMAIL", from);
			notificationIntent.putExtra("NAME", NAME);
			
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
					.setContentTitle(NAME)
					.setStyle(new NotificationCompat.BigTextStyle()
					// .bigText(msg))
							.bigText(first + " " + last + ": " + msg))
					.setSmallIcon(R.drawable.icon_grouple).setSound(soundUri)
					.setContentText(msg);
			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
					0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setAutoCancel(true);

			// null check
			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		} 
		else if (TYPE.equals("USER_MESSAGE"))
		{
			Intent notificationIntent = new Intent(getApplicationContext(),
					MessagesActivity.class);
			notificationIntent.putExtra("EMAIL", from);
			notificationIntent.putExtra("NAME", first + " " + last);
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
					.setContentTitle(first + " " + last)
					.setStyle(new NotificationCompat.BigTextStyle()
					// .bigText(msg))
							.bigText(msg))
					.setSmallIcon(R.drawable.icon_grouple).setSound(soundUri)
					.setContentText(msg);
			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
					0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setAutoCancel(true);

			// null check
			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		}
		else if(TYPE.equals("FRIEND_REQUEST"))
		{
			//Send friend request.
			Intent notificationIntent = new Intent(getApplicationContext(),MessagesActivity.class);
			notificationIntent.putExtra("EMAIL", from);
			notificationIntent.putExtra("NAME", first + " " + last);
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
			.setContentTitle("Friend request from "+first+" "+last)
					.setStyle(new NotificationCompat.BigTextStyle()
					.bigText(first+ " "+last))
					.setSmallIcon(R.drawable.icon_grouple).setSound(soundUri)
					.setContentText(msg);
			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
					0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setAutoCancel(true);

			// null check
			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		}

	}
}
