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
	private ArrayList<User> users = new ArrayList<User>(); //group users, event participants, friends
	private String about; //about user, group, event
	private Bitmap image; //all entities have images
	private String inviter;
	private int pub; //public 1=yes, 0=no(private)
	protected static Global GLOBAL;// = new Global();

	/*
	 * Constructor for our parent entity of users, groups, events...
	 */
	public Entity()
	{
		GLOBAL = new Global();
	}
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
	public void setInviter(String inviter)
	{
		this.inviter = inviter;
	}
	public void setPub(int pub)
	{
		this.pub = pub;
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
	public int getPub()
	{
		return pub;
	}
	public String getInviter()
	{
		return inviter;
	}
	
	public void removeUser(String email)
	{
		if (users != null)
		for (User u : users)
			if (u.getEmail().equals(email))
			{
				System.out.println("WE FOUND A MATCH");
				users.remove(users.indexOf(u));
				System.out.println("REMOVE SEEMS SUCCS");
				break;
			}
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
			if (!inUsers(u.getEmail()))
				users.add(u);
	}
	public boolean inUsers(String email)
	{
			if (!users.isEmpty())
				for (User u : users)
					if (u.getEmail().equals(email))
						return true;
			return false;
	}
}
