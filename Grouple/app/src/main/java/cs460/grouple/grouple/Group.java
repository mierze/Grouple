package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.Collections;
import android.content.Context;

/**
 * 
 * @author Brett, Todd, Scott
 * Group holds information about an individual Group.
 * 
 */
public class Group extends Entity 
{
	private int id; //id of the group
	private String dateCreated = "";
	private GroupDataService dataService;
	private String dateCreatedText = "";
	private int experience = 0;
	private int numParticipants = 0;
	private int numSocialEvents = 0;
	private int numEntertainmentEvents = 0;
	private int numProfessionalEvents = 0;
	private int numFitnessEvents = 0;
	private int numNatureEvents = 0;
	private ArrayList<Event> eventsUpcoming = new ArrayList<Event>();
	private ArrayList<Event> eventsPending = new ArrayList<Event>();
	private ArrayList<Event> eventsPast = new ArrayList<Event>();
	


	protected int getExperience()
	{
		return experience;
	}

	protected void setExperience()
	{
		this.experience = numParticipants;
		ArrayList<Integer> nums = new ArrayList<Integer>();
		nums.add(numSocialEvents);
		nums.add(numEntertainmentEvents);
		nums.add(numProfessionalEvents);
		nums.add(numFitnessEvents);
		nums.add(numNatureEvents);
		Collections.sort(nums);
		
		for (int i = 0; i < nums.size(); i++)
		{
			experience += nums.get(i) * (i+1);
		}
		experience += getUsers().size();
		
	}
	/*
	 * Constructor for Group class
	 */
	public Group(int id)
	{
		this.id = id;
		dataService = new GroupDataService(GLOBAL, this);
		System.out.println("Initializing new group.");
	}
	
	
	//GETTERS
	public int getID()
	{
		return id;
	}
	protected String getDateCreated()
	{
		return dateCreated;
	}
	protected String getDateCreatedText()
	{
		return dateCreatedText;
	}
	
	
	//SETTERS
	protected void setDateCreated(String dateCreated)
	{
		this.dateCreated = dateCreated;
		// string is format from json, parsedate converts
		dateCreatedText = GLOBAL.toYearTextFormatFromRaw(dateCreated);
	}
	
	protected int getNumParticipants()
	{
		return numParticipants;
	}

	protected void setNumParticipants(int numParticipants)
	{
		this.numParticipants = numParticipants;
	}

	protected int getNumSocialEvents()
	{
		return numSocialEvents;
	}

	protected void setNumSocialEvents(int numSocialEvents)
	{
		this.numSocialEvents = numSocialEvents;
	}

	protected int getNumEntertainmentEvents()
	{
		return numEntertainmentEvents;
	}

	protected void setNumEntertainmentEvents(int numEntertainmentEvents)
	{
		this.numEntertainmentEvents = numEntertainmentEvents;
	}

	protected int getNumProfessionalEvents()
	{
		return numProfessionalEvents;
	}

	protected void setNumProfessionalEvents(int numProfessionalEvents)
	{
		this.numProfessionalEvents = numProfessionalEvents;
	}

	protected int getNumFitnessEvents()
	{
		return numFitnessEvents;
	}

	protected void setNumFitnessEvents(int numFitnessEvents)
	{
		this.numFitnessEvents = numFitnessEvents;
	}

	protected int getNumNatureEvents()
	{
		return numNatureEvents;
	}


	protected void setNumNatureEvents(int numNatureEvents)
	{
		this.numNatureEvents = numNatureEvents;
	}
	
	protected int getNumEventsUpcoming()
	{
		return eventsUpcoming.size();
	}
	
	protected int getNumEventsPast()
	{
		return eventsPast.size();
	}
	
	protected int getNumEventsPending()
	{
		return eventsPending.size();
	}
	protected ArrayList<Event> getEventsUpcoming()
	{
		return eventsUpcoming;
	}
	
	protected ArrayList<Event> getEventsPast()
	{
		return eventsPast;
	}
	
	protected ArrayList<Event> getEventsPending()
	{
		return eventsPending;
	}
	
	protected void addToEventsPending(Event e)
	{
		boolean inEventsPending = false;
		for (Event t : eventsPending)
			if (t.getID() == e.getID())
				inEventsPending = true;
		if (!inEventsPending)
			eventsPending.add(e);
	}

	protected void addToEventsUpcoming(Event e)
	{
		boolean inEventsUpcoming = false;
		for (Event t : eventsUpcoming)
			if (t.getID() == e.getID())
				inEventsUpcoming = true;
		if (!inEventsUpcoming)
			eventsUpcoming.add(e);
	}

	protected void addToEventsPast(Event e)
	{
		boolean inEventsPast = false;
		for (Event t : eventsPast)
			if (t.getID() == e.getID())
				inEventsPast = true;
		if (!inEventsPast)
			eventsPast.add(e);
	}

	protected void fetchMembers(Context context)
	{

		dataService.fetchContent("MEMBERS", context);
	}
	
	protected void fetchImage(Context context)
	{
		dataService.fetchContent("IMAGE", context);
	}


	protected void fetchInfo(Context context)
	{
		dataService.fetchContent("INFO", context);
	}
	protected void fetchExperience(Context context)
	{
		dataService.fetchContent("EXPERIENCE", context);
	}
	
	protected void fetchEvents(Context context)
	{
		dataService.fetchContent("EVENTS", context);
	}

	
}
