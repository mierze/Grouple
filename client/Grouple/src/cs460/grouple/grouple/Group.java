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

public class Group extends Entity 
{
	private int id; //id of the group
	private String dateCreated = "";
	private GroupDataService dataService;
	private String dateCreatedText = "";
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

	
}
