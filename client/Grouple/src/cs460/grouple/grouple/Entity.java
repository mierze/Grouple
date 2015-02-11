package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public abstract class Entity
{
	private String email; //email of user, email of group creator, email of event creator
	private String name; //fullname of user, fName of group creator, fName of event creator ?or possibly not
	private ArrayList<User> users; //group users, event participants, friends
	private String about; //about user, group, event
	private Bitmap image; //all entities have images

	/*
	 * Constructor for our parent entity of users, groups, events...
	 */
	//nothing for now, shouldn't be making any entities alone
	
	/*
	 * Setters for user class below
	 */
	public void setEmail(String email)
	{
		this.email = email;
	}
	public void setName(String name)
	{
		System.out.println("Name is being set to: " + name);
		this.name = name;
	}
	
	public void setAbout(String about)
	{
		this.about = about;
	}
	//img is taken from json string
	protected void setImage(String img)
	{
		Bitmap bmp;
		//jsonArray.getString("image");
	
		// decode image back to android bitmap format
		byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
		if (decodedString != null)
		{
			bmp = BitmapFactory.decodeByteArray(decodedString, 0,
					decodedString.length);
			//setting bmp;
			this.image = bmp;
		}
		else
		{
			image = null;
		}
	}
	
	/*
	 * Getters for user class below
	 */
	public String getEmail()
	{
		return email;
	}
	protected String getName()
	{
		return name;
	}

	public String getAbout()
	{
		return about;
	}
	public Bitmap getImage()
	{
		return image;
	}
	public void removeUser(String email)
	{
		for (User u : users)
			if (u.getEmail().equals(email))
				users.remove(u);
	}
	
	public ArrayList<User> getUsers()
	{
		return users;
	}
	public int getNumUsers()
	{
		if (users != null)
			return users.size();
		else 
			return 0;
	}
	public void addToUsers(User u)
	{
		if (users == null)
		{
			users = new ArrayList<User>();
		}
		
		users.add(u);
	}
	
	/*
	 * 
	 * READJSONFEEDBELOW
	 */
	public String readJSONFeed(String URL, List<NameValuePair> nameValuePairs)
	{
		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();

		if (nameValuePairs == null)
		{
			HttpGet httpGet = new HttpGet(URL);

			try
			{
				HttpResponse response = httpClient.execute(httpGet);
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
						System.out.println("New line: " + line);
						stringBuilder.append(line);
					}
					inputStream.close();
				} else
				{
					Log.d("JSON", "Failed to download file");
				}
			} catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}

		}

		else
		{
			HttpPost httpPost = new HttpPost(URL);
			try
			{
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

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
					inputStream.close();
				} else
				{
					Log.d("JSON", "Failed to download file");
				}
			} catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}
		}
		return stringBuilder.toString();
	}

}
