package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class Event extends Entity
{
	private int id;
	private String eventState = "";
	private String startDate = "";
	private String startText = "";
	private String startTextNoTime = "";
	private String startTextListDisplay = "";
	private String endText = "";
	private String endDate = "";
	private String recurringType;
	private String category = "";
	private int minPart;
	private String location = "";
	private int maxPart;
	private EventDataService dataService;

	private ArrayList<EventItem> items = new ArrayList<EventItem>();

	/*
	 * Constructor for User class
	 */

	protected Event(int id)
	{
		super();
		this.id = id;
		dataService = new EventDataService(GLOBAL, this);
	}

	protected void setID(int id)
	{
		this.id = id;
	}

	protected void setEventState(String eventState)
	{
		this.eventState = eventState;
	}

	protected void setCategory(String category)
	{
		this.category = category;
	}

	protected void setMinPart(int minPart)
	{
		this.minPart = minPart;
	}

	protected void setMaxPart(int maxPart)
	{
		this.maxPart = maxPart;
	}

	protected void setLocation(String location)
	{
		this.location = location;
	}

	protected void setStartDate(String startDate)
	{
		this.startDate = startDate;
		// string is format from json, parsedate converts
		startText = GLOBAL.toDayTextFormatFromRaw(startDate);
		startTextNoTime = GLOBAL.toNoTimeTextFormatFromRaw(startDate);
		startTextListDisplay = GLOBAL.toYearTextFormatFromRawNoTime(startDate);
	}

	protected void setEndDate(String endDate)
	{
		this.endDate = endDate;
		endText = GLOBAL.toDayTextFormatFromRaw(endDate);
	}

	protected void setRecurringType(String recurringType)
	{
		this.recurringType = recurringType;
	}

	// getters
	protected int getID()
	{
		return id;
	}

	protected String getEventState()
	{
		return eventState;
	}

	protected String getCategory()
	{
		return category;
	}

	protected int getMinPart()
	{
		return minPart;
	}

	protected int getMaxPart()
	{
		return maxPart;
	}

	protected int getNumUnclaimedItems()
	{
		int numUnclaimed = 0;
		for (EventItem i : items)
			if (i.getEmail().equals("null"))
				numUnclaimed++;
		return numUnclaimed;
	}

	protected void addToItems(EventItem item)
	{
		boolean inItems = false;
		for (EventItem i : items)
			if (i.getID() == item.getID())
				inItems = true;
		if (!inItems)
			items.add(item);
		// TODO: should make new function for update / add user, / make this one
		// that, since storing stuff now

	}

	protected ArrayList<EventItem> getItems()
	{
		return items;
	}

	protected String getLocation()
	{
		return location;
	}

	protected String getStartDate()
	{
		return startDate;
	}

	protected String getEndDate()
	{
		return endDate;
	}

	protected String getRecurringType()
	{
		return recurringType;
	}

	protected String getEndText()
	{
		return endText;
	}

	protected String getStartText()
	{
		return startText;
	}

	protected String getStartTextNoTime()
	{
		return startTextNoTime;
	}

	protected String getStartTextListDisplay()
	{
		return startTextListDisplay;
	}

	protected void fetchInfo(Context context)
	{
		dataService.fetchContent("INFO", context);
	}

	protected void fetchImage(Context context)
	{
		dataService.fetchContent("IMAGE", context);
	}

	protected void fetchParticipants(Context context)
	{
		dataService.fetchContent("PARTICIPANTS", context);
	}

	protected void fetchItems(Context context)
	{
		dataService.fetchContent("ITEMS", context);
	}

}
