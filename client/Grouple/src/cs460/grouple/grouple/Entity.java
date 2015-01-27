package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.Map;

import android.graphics.Bitmap;

public class Entity
{
	private String email; //email of user, email of group creator, email of event creator
	private String fName; //fName of user, fName of group creator, fName of event creator ?or possibly not
	private String lName; //last name...
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
	public void setFirstName(String fName)
	{
		this.fName = fName;
	}
	public void setLastName(String lName)
	{
		this.lName = lName;
	}
	public void setAbout(String about)
	{
		this.about = about;
	}
	
	/*
	 * Getters for user class below
	 */
	public String getEmail()
	{
		return email;
	}
	public String getFirstName()
	{
		return fName;
	}
	public String getLastName()
	{
		return lName;
	}
	public String getFullName()
	{
		String fullName = fName + " " + lName;
		return fullName;
	}
	public String getAbout()
	{
		return about;
	}

}
