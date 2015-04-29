package cs460.grouple.grouple;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import cs460.grouple.grouple.UserDataService;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

@SuppressLint("SimpleDateFormat")
public class User extends Entity
{
	private String location;
	private int age = -1;
	private int experience = 0;
	private String gender = "";
	private String birthday;
	private String birthdayText;
	private ArrayList<Group> groups = new ArrayList<Group>();
	private ArrayList<User> friendRequests = new ArrayList<User>();
	private ArrayList<Group> groupInvites = new ArrayList<Group>();
	private ArrayList<Event> eventsUpcoming = new ArrayList<Event>();
	private ArrayList<Event> eventsPast = new ArrayList<Event>();
	private ArrayList<Event> eventInvites = new ArrayList<Event>();
	private ArrayList<Event> eventsDeclined = new ArrayList<Event>();
	private ArrayList<Event> eventsPending = new ArrayList<Event>();
	private ArrayList<Badge> badges = new ArrayList<Badge>();
	private ArrayList<Badge> newBadges = new ArrayList<Badge>();
	private SparseArray<String> groupRoles = new SparseArray<String>();
	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	private SparseArray<String> eventRoles = new SparseArray<String>();
	private UserDataService dataService;
	private int numSocialEvents = 0;
	private int numEntertainmentEvents = 0;
	private int numProfessionalEvents = 0;
	private int numFitnessEvents = 0;
	private int numNatureEvents = 0;
	private int numSocialEventsCreated = 0;
	private int numEntertainmentEventsCreated = 0;
	private int numProfessionalEventsCreated = 0;
	private int numFitnessEventsCreated = 0;
	private int numNatureEventsCreated = 0;
	private int numTotalEvents = 0;
	private int numTotalEventsCreated = 0;
	private int numItemsBrought = 0;

	/*
	 * Constructor for User class
	 */
	protected User(String email)
	{
		super();
		dataService = new UserDataService(GLOBAL, this);
		setEmail(email);
		initBadges();
		System.out.println("Initializing new user.");
	}

	protected String getGender()
	{
		return gender;
	}

	protected void setGender(String gender)
	{
		this.gender = gender;
	}

	protected int getExperience()
	{
		return experience;
	}

	protected void setExperience()
	{
		int xp = numTotalEventsCreated * 2;
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
		
		xp += numItemsBrought;
		experience = xp;
	}

	// SETTERS

	protected int getNumTotalEvents()
	{
		return numTotalEvents;
	}

	protected void setNumTotalEvents(int numTotalEvents)
	{
		this.numTotalEvents = numTotalEvents;
	}

	protected int getNumTotalEventsCreated()
	{
		return numTotalEventsCreated;
	}

	protected void setNumTotalEventsCreated(int numTotalEventsCreated)
	{
		this.numTotalEventsCreated = numTotalEventsCreated;
	}

	protected int getNumItemsBrought()
	{
		return numItemsBrought;
	}

	protected void setNumItemsBrought(int numTotalItemsBrought)
	{
		this.numItemsBrought = numTotalItemsBrought;
	}

	protected void setLocation(String location)
	{
		this.location = location;
	}

	protected void setBirthday(String birthday)
	{
		System.out.println(birthday);
		this.birthday = birthday;
		this.birthdayText = GLOBAL.toYearTextFormatFromRawNoTime(birthday);
		if (!birthday.equals(""))
		{
			SimpleDateFormat raw = new SimpleDateFormat("yyyy-M-d");
			Date birthDate = null;
			try
			{
				birthDate = raw.parse(birthday);
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
			int ageInt;
			Calendar dob = Calendar.getInstance();
			dob.setTime(birthDate);
			Calendar today = Calendar.getInstance();
			// if current month is less than date of birth month
			// or today month == dob month and today <= day of month
			if ((today.get(Calendar.MONTH) > dob.get(Calendar.MONTH))
					|| (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) >= dob
							.get(Calendar.DAY_OF_MONTH)))
			{
				// year --
				ageInt = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
			}
			else
			{
				ageInt = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR) - 1;
			}
			this.age = ageInt;
		}
		else
		{
			System.out.println("SETTING AGE TO -1");
			this.age = -1;
		}
	}

	// GETTERS

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

	protected int getNumSocialEventsCreated()
	{
		return numSocialEventsCreated;
	}

	protected void setNumSocialEventsCreated(int numSocialEventsCreated)
	{
		this.numSocialEventsCreated = numSocialEventsCreated;
	}

	protected int getNumEntertainmentEventsCreated()
	{
		return numEntertainmentEventsCreated;
	}

	protected void setNumEntertainmentEventsCreated(int numEntertainmentEventsCreated)
	{
		this.numEntertainmentEventsCreated = numEntertainmentEventsCreated;
	}

	protected int getNumProfessionalEventsCreated()
	{
		return numProfessionalEventsCreated;
	}

	protected void setNumProfessionalEventsCreated(int numProfessionalEventsCreated)
	{
		this.numProfessionalEventsCreated = numProfessionalEventsCreated;
	}

	protected int getNumFitnessEventsCreated()
	{
		return numFitnessEventsCreated;
	}

	protected void setNumFitnessEventsCreated(int numFitnessEventsCreated)
	{
		this.numFitnessEventsCreated = numFitnessEventsCreated;
	}

	protected int getNumNatureEventsCreated()
	{
		return numNatureEventsCreated;
	}

	protected void setNumNatureEventsCreated(int numNatureEventsCreated)
	{
		this.numNatureEventsCreated = numNatureEventsCreated;
	}

	protected String getFirstName()
	{
		String first = "";
		if (getName() != null)
			first = getName().split(" ")[0];
		return first;
	}

	protected String getLastName()
	{
		String last = "";
		if (getName() != null)
			last = getName().split(" ")[1];
		return last;
	}

	protected String getLocation()
	{
		if (location != null)
			return location;
		else
			return "";
	}

	protected String getBirthday()
	{
		if (birthday != null)
			return birthday;
		else
			return "";
	}

	protected String getBirthdayText()
	{
		if (birthdayText != null)
			return birthdayText;
		else
			return "";
	}

	protected int getAge()
	{
		// returns -1 if not set, is checked for later
		return age;
	}

	protected int getNumBadges()
	{
		int size = 0;
		for (Badge b : badges)
			if (b.getLevel() > 0)
				size++;
		return size;
	}

	protected int getNumGroups()
	{
		return groups.size();
	}

	protected int getNumFriendRequests()
	{
		return friendRequests.size();
	}

	protected int getNumGroupInvites()
	{
		return groupInvites.size();
	}

	protected int getNumEventsPending()
	{
		return eventsPending.size();
	}

	protected int getNumEventsDeclined()
	{
		return eventsDeclined.size();
	}

	protected int getNumEventsPast()
	{
		return eventsPast.size();
	}

	protected int getNumEventsInvites()
	{
		return eventInvites.size();
	}

	protected int getNumEventsUpcoming()
	{
		return eventsUpcoming.size();
	}

	protected int getNumNewBadges()
	{
		return newBadges.size();
	}

	protected ArrayList<Contact> getContacts()
	{
		return contacts;
	}

	protected ArrayList<Badge> getBadges()
	{
		return badges;
	}

	protected ArrayList<Badge> getNewBadges()
	{
		return newBadges;
	}

	protected ArrayList<User> getFriendRequests()
	{
		return friendRequests;
	}

	protected ArrayList<Group> getGroups()
	{
		return groups;
	}

	protected ArrayList<Group> getGroupInvites()
	{
		return groupInvites;
	}

	protected ArrayList<Event> getEventsPending()
	{
		return eventsPending;
	}

	protected ArrayList<Event> getEventsUpcoming()
	{
		return eventsUpcoming;
	}

	protected ArrayList<Event> getEventInvites()
	{
		return eventInvites;
	}

	protected ArrayList<Event> getEventsDeclined()
	{
		return eventsDeclined;
	}

	protected ArrayList<Event> getEventsPast()
	{
		return eventsPast;
	}

	protected void removeGroup(int id)
	{
		if (groups != null)
			for (Group g : groups)
				if (g.getID() == id)
				{
					groups.remove(groups.indexOf(g));
					break;
				}
	}

	protected void removeGroupInvite(int id)
	{
		if (groupInvites != null)
			for (Group g : groupInvites)
				if (g.getID() == id)
				{
					groupInvites.remove(groupInvites.indexOf(g));
					break;
				}
	}

	protected void removeEventUpcoming(int id)
	{
		if (eventsUpcoming != null)
			for (Event e : eventsUpcoming)
				if (e.getID() == id)
				{
					eventsUpcoming.remove(eventsUpcoming.indexOf(e));
					break;
				}
	}

	protected void removeEventPending(int id)
	{
		if (eventsPending != null)
			for (Event e : eventsPending)
				if (e.getID() == id)
				{
					eventsPending.remove(eventsPending.indexOf(e));
					break;
				}
	}

	protected void removeEventInvite(int id)
	{
		if (eventInvites != null)
			for (Event e : eventInvites)
				if (e.getID() == id)
				{
					eventInvites.remove(eventInvites.indexOf(e));
					break;
				}
	}

	protected void removeEventPast(int id)
	{
		if (eventsPast != null)
			for (Event e : eventsPast)
				if (e.getID() == id)
				{
					eventsPast.remove(eventsPast.indexOf(e));
					break;
				}
	}

	protected void removeEventDeclined(int id)
	{
		if (eventsDeclined != null)
			for (Event e : eventsDeclined)
				if (e.getID() == id)
				{
					eventsDeclined.remove(eventsDeclined.indexOf(e));
					break;
				}
	}

	protected void removeFriendRequest(String email)
	{
		if (friendRequests != null)
			for (User u : friendRequests)
				if (u.getEmail().equals(email))
				{
					friendRequests.remove(friendRequests.indexOf(u));
					break;
				}
	}

	// METHODS
	private void initBadges()
	{
		// adding all badges to the user with level 0

		badges.add(new Badge("Gregarious", null)); // social count
		badges.add(new Badge("Congregator", null)); // create social count

		badges.add(new Badge("Amused", null)); // entertainment count
		badges.add(new Badge("Merrymaker", null)); // create entertainment count

		badges.add(new Badge("Diligent", null)); // professional count
		badges.add(new Badge("Productive", null)); // create professional count

		badges.add(new Badge("Agile", null)); // fitness count
		badges.add(new Badge("Health Nut", null)); // create fitness count

		badges.add(new Badge("Outdoorsman", null)); // nature count
		badges.add(new Badge("Environmentalist", null)); // create nature count

		badges.add(new Badge("Extrovert", null)); // total count
		//badges.add(new Badge("Creator", null)); // create total count

		badges.add(new Badge("Regular", null)); // the attend recurring event
		badges.add(new Badge("Routinist", null)); // the create recurring event

		badges.add(new Badge("Active", null)); // per week count
		badges.add(new Badge("Well Rounded", null)); // participated in all
														// categories
		badges.add(new Badge("Reaching Out", null));
		badges.add(new Badge("Mingler", null));
		badges.add(new Badge("Perseverance", null));
		badges.add(new Badge("Helping Hand", null)); // bring items to event

	}

	protected void addToBadges(Badge b)
	{
		for (Badge t : badges)
			if (t.getName().equals(b.getName()))
			{
				t.setDate(b.getDate());
				t.setLevel(b.getLevel());
			}
	}

	protected void addToContacts(Contact contact)
	{
		boolean contains = false;
		int indexFound = -1;
		for (Contact c : contacts)
		{
			String otherEmail2 = c.getSender().equals(getEmail()) ? c.getReceiver() : c.getSender();
			if (otherEmail2.equals(contact.getOtherEmail()))
			{
				contains = true;
				indexFound = contacts.indexOf(c);
				break;
			}
		}
		if (!contains)
		{
			// recent messages does not contain a message from
			// this user
			contacts.add(contact);
		}
		else if (contacts.get(indexFound).getID() < contact.getID())
		{
			contacts.set(indexFound, contact);
			System.out.println("The old message has been replaced and is now:" + contacts.get(indexFound).getMessage());
		}
		else
		{
			System.out.println("no replace happened.  Message is same as before: "
					+ contacts.get(indexFound).getMessage());
		}
	}

	protected void addToNewBadges(Badge b)
	{
		// just adding because it will be wiped when user views them
		// TODO: check that there aren't lingering badges a user has not seen
		newBadges.add(b);
	}

	protected void addToFriendRequests(User u)
	{
		boolean inFriendRequests = false;
		for (User t : friendRequests)
			if (t.getEmail().equals(u.getEmail()))
				inFriendRequests = true;
		if (!inFriendRequests)
			friendRequests.add(u);
		// TODO: should make new function for update / add user, / make this one
		// that, since storing stuff now
	}

	protected void addToGroups(Group g)
	{
		boolean inGroups = false;
		for (Group t : groups)
			if (t.getID() == g.getID())
				inGroups = true;
		if (!inGroups)
			groups.add(g);
	}

	protected void addToGroupInvites(Group g)
	{
		boolean inGroupInvites = false;
		for (Group t : groupInvites)
			if (t.getID() == g.getID())
				inGroupInvites = true;
		if (!inGroupInvites)
			groupInvites.add(g);
	}

	protected void addToEventsDeclined(Event e)
	{
		boolean inEventsDeclined = false;
		for (Event t : eventsDeclined)
			if (t.getID() == e.getID())
				inEventsDeclined = true;
		if (!inEventsDeclined)
			eventsDeclined.add(e);
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

	protected void addToEventsInvites(Event e)
	{
		boolean inEventInvites = false;
		for (Event t : eventInvites)
			if (t.getID() == e.getID())
				inEventInvites = true;
		if (!inEventInvites)
			eventInvites.add(e);
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

	protected void addToGroupRoles(int id, String role)
	{
		groupRoles.put(id, role);
	}

	protected void addToEventRoles(int id, String role)
	{
		eventRoles.put(id, role);

	}

	protected String getGroupRole(int id)
	{
		if (groupRoles.get(id) != null)
			return groupRoles.get(id);
		// TODO: this shoul dnot return U if not found
		return "U";
	}

	protected String getEventRole(int id)
	{
		if (eventRoles.get(id) != null)
			return eventRoles.get(id);
		// TODO: this shoul dnot return U if not found
		return "U";
	}

	// ALL DATA APP FETCH CALLS BELOW
	protected void fetchInfo(Context context)
	{
		dataService.fetchContent("INFO", context);
	}

	protected void fetchFriends(Context context)
	{
		dataService.fetchContent("FRIENDS_CURRENT", context);
	}

	protected void fetchFriendRequests(Context context)
	{
		dataService.fetchContent("FRIEND_INVITES", context);
	}

	protected void fetchGroups(Context context)
	{
		dataService.fetchContent("GROUPS_CURRENT", context);
	}

	protected void fetchGroupInvites(Context context)
	{
		dataService.fetchContent("GROUP_INVITES", context);
	}

	protected void fetchEventsPending(Context context)
	{
		dataService.fetchContent("EVENTS_PENDING", context);
	}

	protected void fetchEventsDeclined(Context context)
	{
		dataService.fetchContent("EVENTS_DECLINED", context);
	}

	protected void fetchEventsPast(Context context)
	{
		dataService.fetchContent("EVENTS_PAST", context);
	}

	protected void fetchEventInvites(Context context)
	{
		dataService.fetchContent("EVENT_INVITES", context);
	}

	protected void fetchEventsUpcoming(Context context)
	{
		dataService.fetchContent("EVENTS_UPCOMING", context);
	}

	protected void fetchBadges(Context context)
	{
		dataService.fetchContent("BADGES", context);
	}

	protected void fetchExperience(Context context)
	{
		dataService.fetchContent("EXPERIENCE", context);
	}

	protected void fetchImage(Context context)
	{
		dataService.fetchContent("IMAGE", context);
	}

	protected void fetchContacts(Context context)
	{
		dataService.fetchContent("CONTACTS", context);
	}

}
