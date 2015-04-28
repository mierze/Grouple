package cs460.grouple.grouple;

public class Badge
{
	private static int id_AUTOINC = 0;
	int id;
	private String name;
	private int aboutID;
	private int level = 0;
	private int imageID; // ref in drawable folder
	private String date;
	private Global GLOBAL = new Global();

	protected String getDate()
	{
		return date;
	}

	protected void setDate(String date)
	{
		if (date != null)
			this.dateText = GLOBAL.toYearTextFormatFromRawNoTime(date);
		this.date = date;
	}

	protected String getDateText()
	{
		return dateText;
	}

	protected void setImageID()
	{
		if (name.equals("Environmentalist"))
		{
			if (level > 0)
				imageID = R.drawable.badge_nature_grey;
			else
				imageID = R.drawable.badge_nature_grey;
		}
		else if (name.equals("Gregarious"))
		{
			if (level > 0)
				imageID = R.drawable.badge_nature_grey;
			else
				imageID = R.drawable.badge_nature_grey;
		}
		else if (name.equals("Active"))
		{
			if (level > 0)
				imageID = R.drawable.badge_nature;
			else
				imageID = R.drawable.badge_nature_grey;
			
		}
		else if (name.equals("Professional"))
		{
			if (level > 0)
				imageID = R.drawable.badge_nature_grey;
			else
				imageID = R.drawable.badge_nature_grey;
		}
		else if (name.equals("Amused"))
		{
			if (level > 0)
				imageID = R.drawable.badge_nature_grey;
			else
				imageID = R.drawable.badge_nature_grey;
		}
		else if (name.equals("Extrovert"))
		{
			if (level > 0)
				imageID = R.drawable.badge_nature_grey;
			else
				imageID = R.drawable.badge_nature_grey;
		}
		else if (name.equals("Merrymaker"))
		{
			if (level > 0)
				imageID = R.drawable.badge_nature_grey;
			else
				imageID = R.drawable.badge_nature_grey;
		}
		else if (name.equals("Creator"))
		{
			if (level > 0)
				imageID = R.drawable.badge_nature_grey;
			else
				imageID = R.drawable.badge_nature_grey;
		}
		else if (name.equals("Productive"))
		{
			if (level > 0)
				imageID = R.drawable.badge_nature_grey;
			else
				imageID = R.drawable.badge_nature_grey;
		}
		else if (name.equals("Routinist"))
		{
			if (level > 0)
				imageID = R.drawable.badge_routinist;
			else
				imageID = R.drawable.badge_nature_grey;
		}
		else if (name.equals("Reaching Out"))
		{
			if (level > 0)
				imageID = R.drawable.badge_reachingout;
			else
				imageID = R.drawable.badge_nature_grey;
		}
		else if (name.equals("Perseverance"))
		{
			if (level > 0)
				imageID = R.drawable.badge_perseverance;
			else
				imageID = R.drawable.badge_nature_grey;
		}
		else
		{
			if (level > 0)
				imageID = R.drawable.badge_nature_grey;
			else
				imageID = R.drawable.badge_nature_grey;
		}
	}

	private String dateText;

	protected Badge(String name, String date)
	{
		this.name = name;
		setDate(date);

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
		else if (name.equals("Amused"))
		{
			aboutID = R.string.amused_about;
		}
		else if (name.equals("Extrovert"))
		{
			aboutID = R.string.wellrounded_about;
		}
		else if (name.equals("Creator"))
		{
			aboutID = R.string.wellrounded_about;
		}
		else if (name.equals("Fun Creator"))
		{
			aboutID = R.string.wellrounded_about;
		}
		else if (name.equals("Productive"))
		{
			aboutID = R.string.wellrounded_about;
		}
		else if (name.equals("Mother Nature Lover"))
		{
			aboutID = R.string.wellrounded_about;
		}
		else
		{
			aboutID = R.string.wellrounded_about;
		}

	}

	protected int getLevel()
	{
		return level;
	}

	protected void setLevel(int level)
	{
		this.level = level;
		setImageID();
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
