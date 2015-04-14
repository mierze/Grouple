package cs460.grouple.grouple;

public class Badge
{
	private int id;
	private String name;
	private String about;
	private int level;
	private int imageID; //ref in drawable folder
	
	protected Badge()
	{
		
	}
	
	protected int getID()
	{
		return id;
	}
	protected void setID(int id)
	{
		this.id = id;
	}
	protected String getName()
	{
		return name;
	}
	protected void setName(String name)
	{
		this.name = name;
	}
	protected String getAbout()
	{
		return about;
	}
	protected void setAbout(String about)
	{
		this.about = about;
	}
	protected int getLevel()
	{
		return level;
	}
	protected void setLevel(int level)
	{
		this.level = level;
	}
	protected int getImageID()
	{
		return imageID;
	}
	protected void setImageID(int imageID)
	{
		this.imageID = imageID;
	}
}
