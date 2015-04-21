package cs460.grouple.grouple;

public class Badge
{
	private static int id_AUTOINC = 0;
	int id;
	private String name;
	private int aboutID;
	private int level;
	private int imageID; // ref in drawable folder
	private String date;
	private Global GLOBAL = new Global();

	protected String getDate()
	{
		return date;
	}

	protected void setDate(String date)
	{
		this.date = date;
	}

	protected String getDateText()
	{
		return dateText;
	}

	protected void setDateText(String dateText)
	{
		this.dateText = dateText;
	}

	protected void setImageID()
	{
		if (name.equals("Environmentalist"))
		{
			imageID = R.drawable.user_image_default;
		}
		else
		{
			System.out.println("HAKFHJALSDFJ");
		}
	}

	private String dateText;

	protected Badge(String name, int level, String date)
	{
		this.name = name;
		this.date = date;
		if (date != null)
			this.dateText = GLOBAL.toYearTextFormatFromRawNoTime(date);
		this.level = level;
		setID();
		setAbout();

	}

	protected int getID()
	{
		return id;
	}

	private void setID()
	{
		this.id = id_AUTOINC++;
	}

	protected String getName()
	{
		return name;
	}

	protected void setName(String name)
	{
		this.name = name;
	}

	protected int getAboutID()
	{
		return aboutID;
	}

	protected void setAbout()
	{
		if (name.equals("Environmentalist"))
		{
			aboutID = R.string.environmentalist_about;
		}
		else if (name.equals("Social"))
		{
			aboutID = R.string.social_about;
		}
		else if (name.equals("Professional"))
		{
			aboutID = R.string.professional_about;
		}
		else if (name.equals("Active"))
		{
			aboutID = R.string.active_about;
		}
		else if (name.equals("Jack of all Trades"))
		{
			aboutID = R.string.jackoftrades_about;
		}
		else if (name.equals("Helping Hand"))
		{
			aboutID = R.string.helpinghand_about;
		}

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
