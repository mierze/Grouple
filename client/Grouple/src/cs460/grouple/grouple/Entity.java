package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Entity
{
	private String email; //email of user, email of group creator, email of event creator
	private String name; //fName of user, fName of group creator, fName of event creator ?or possibly not
	
	private String about; //about user, group, event
	//private String location;
	//private int age;
	Bitmap image; //all entities have images
	//private Map <String, String> friends; //friends emails(key) -> friends names(value)
	//private Map<Integer, String> groups; 
	//private ArrayList<String> friendRequests; //friendRequest emails->names
	//private Map <Integer, String> groupInvites; //group invite ids
	//private boolean isCurrentUser;
	
	/*
	 * Constructor for are parent entity of users, groups, events...
	 */
	public Entity()
	{
		
	}
	
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

}
