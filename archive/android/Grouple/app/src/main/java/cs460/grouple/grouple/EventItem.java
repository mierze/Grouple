package cs460.grouple.grouple;

/**
 * 
 * @author Brett, Todd, Scott
 * EventItem holds information about an individual event item.
 *
 */
public class EventItem
{
	private String name;
	private String email;
	private int id;
	
	protected EventItem(int id, String name, String email)
	{
		this.id = id;
		this.name = name;
		this.email = email;
	}
	protected String getName()
	{
		return name;
	}
	protected void setName(String name)
	{
		this.name = name;
	}
	protected String getEmail()
	{
		return email;
	}
	protected void setEmail(String email)
	{
		this.email = email;
	}
	protected int getID()
	{
		return id;
	}
	protected void setId(int id)
	{
		this.id = id;
	}
}
