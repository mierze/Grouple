package cs460.grouple.grouple;
/**
 * 
 * @author Brett, Todd, Scott
 * Badge holds information about an individual user reward
 */
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

	protected int getImageID(String gender)
	{

		if (name.equals("Health Nut"))
		{
			if (level > 0)
				imageID = R.drawable.badge_healthnut;
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Mingler"))
		{
			if (level > 0)
				if (gender.equals("female"))
				{
					imageID = R.drawable.badge_mingler_female;
				}
				else
				{
					imageID = R.drawable.badge_mingler;
				}
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Agile"))
		{
			if (level > 0)
				if (gender.equals("female"))
				{
					imageID = R.drawable.badge_agile_female;
				}
				else
				{
					imageID = R.drawable.badge_agile;
				}
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Congregator"))
		{
			if (level > 0)
				if (gender.equals("female"))
				{
					imageID = R.drawable.badge_congregator_female;
				}
				else
				{
					imageID = R.drawable.badge_congregator;
				}
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Well Rounded"))
		{
			if (level > 0)
				imageID = R.drawable.badge_rounded;
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Regular"))
		{
			if (level > 0)
				imageID = R.drawable.badge_regular;
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Environmentalist"))
		{
			if (level > 0)
				imageID = R.drawable.badge_environmentalist;
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Gregarious"))
		{
			if (level > 0)
				imageID = R.drawable.badge_gregarious;
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Amused"))
		{
			if (level > 0)
				imageID = R.drawable.badge_amused;
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Active"))
		{
			if (level > 0)
				if (gender.equals("female"))
				{
					imageID = R.drawable.badge_active_female;
				}
				else
				{
					imageID = R.drawable.badge_active;
				}
			else
				imageID = R.drawable.badge_unknown;
			
		}
		else if (name.equals("Productive"))
		{
			if (level > 0)
				imageID = R.drawable.badge_professionalcreate;
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Extrovert"))
		{
			if (level > 0)
				imageID = R.drawable.badge_extrovert;
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Merrymaker"))
		{
			if (level > 0)
				imageID = R.drawable.badge_merrymaker;
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Creator"))
		{
			if (level > 0)
				imageID = R.drawable.badge_unknown;
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Outdoorsman"))
		{
			if (level > 0)
				if (gender.equals("female"))
				{
					imageID = R.drawable.badge_outdoorsman_female;
				}
				else
				{
					imageID = R.drawable.badge_outdoorsman;
				}
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Productive"))
		{
			if (level > 0)
				imageID = R.drawable.badge_diligent2;
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Routinist"))
		{
			if (level > 0)
				imageID = R.drawable.badge_routinist;
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Reaching Out"))
		{
			if (level > 0)
				imageID = R.drawable.badge_reachingout;
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Perseverance"))
		{
			if (level > 0)
				imageID = R.drawable.badge_perseverance;
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Diligent"))
		{
			if (level > 0)
				imageID = R.drawable.badge_diligent2;
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Diversity"))
		{
			if (level > 0)
				imageID = R.drawable.badge_diversity;
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Extrovert"))
		{
			if (level > 0)
				imageID = R.drawable.badge_extrovert;
			else
				imageID = R.drawable.badge_unknown;
		}
		else if (name.equals("Helping Hand"))
		{
			if (level > 0)
				imageID = R.drawable.badge_helpinghand;
			else
				imageID = R.drawable.badge_unknown;
		}
		else
		{
			if (level > 0)
				imageID = R.drawable.badge_unknown;
			else
				imageID = R.drawable.badge_unknown;
		}
		
		return imageID;
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
		else if (name.equals("Gregarious"))
		{
			aboutID = R.string.gregarious_about;
		}
		else if (name.equals("Health Nut"))
		{
			aboutID = R.string.healthnut_about;
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
			aboutID = R.string.extrovert_about;
		}
		//else if (name.equals("Creator"))
		//{
		//	aboutID = R.string.creator_about;
		//}
		else if (name.equals("Well Rounded"))
		{
			aboutID = R.string.wellrounded_about;
		}
		else if (name.equals("Productive"))
		{
			aboutID = R.string.perseverance_about;
		}
		else if (name.equals("Outdoorsman"))
		{
			aboutID = R.string.outdoorsman_about;
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
	}


	protected void setImageID(int imageID)
	{
		this.imageID = imageID;
	}
}
