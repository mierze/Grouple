package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public abstract class Entity
{
	private String email; //email of user, email of group creator, email of event creator
	private String name; //fullname of user, fName of group creator, fName of event creator ?or possibly not
	private Map<String, String> users; //group users, event participants, friends
	private String about; //about user, group, event
	Bitmap image; //all entities have images

	
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
		if (users.containsKey(email))
			users.remove(email);
	}
	
	public Map<String, String> getUsers()
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
	public void addToUsers(String email, String name)
	{
		if (users == null)
		{
			users = new HashMap<String, String>();
		}
		
		users.put(email, name);
		Log.d("Name for " + email, users.get(email));
	}

}
