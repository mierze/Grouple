package cs460.grouple.grouple;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message
{
    private String message;
    private String dateString;
    private String rawDateString;
    private Date date;
    private String sender;
    private String senderName;
    private String receiver; //GID/EID/EMAIL
    private String readByDateString;
    
    protected Message(String message, String rawDateString, String sender, String senderName, String receiver, String readByDateString)
    {
    	this.message = message;
    	this.rawDateString = rawDateString;
    	this.sender = sender;
    	this.senderName = senderName;
    	this.receiver = receiver;
    	this.readByDateString = readByDateString;
    }
    
    //GETTERS
    protected String getMessage()
    {
    	return message;
    }
    protected String getDateString()
    {
    	return dateString;
    }
    protected Date getDate()
    {
    	return date;
    }
    protected String getSender()
    {
    	return sender;
    }
    protected String getSenderName()
    {
    	return senderName;
    }
    protected String getReceiver()
    {
    	return receiver;
    }
    
    //SETTERS
    protected void setMessage(String message)
    {
    	this.message = message;
    }
    protected void setDateString(String dateString)
    {
    	this.dateString = dateString;
    }
    protected void setDate(String rawDateString)
    {
    	this.rawDateString = rawDateString;
    	this.dateString = parseDate(rawDateString);
    }
    protected void setSender(String sender)
    {
    	this.sender = sender;
    }
    protected void setSenderName(String senderName)
    {
    	this.senderName = senderName;
    }
    protected void setReceiver(String receiver)
    {
    	this.receiver = receiver;
    }
    
    

	private String parseDate(String rawDateString)
	{
		System.out.println("\n\nDATE IS FIRST: " + rawDateString);
		String dateString = "";
		SimpleDateFormat raw = new SimpleDateFormat("yyyy-M-d h:mm:ss");
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"EEEE, MMMM d, h:mma");
		try
		{

			date = (Date) raw.parse(dateString);
			dateString = dateFormat.format(date);
			// date = raw.format(parsedDate);
			System.out.println("\nDATE IN RAW TRANSLATION: "
					+ raw.format(date));
			System.out.println("\nDATE IN FINAL: "
					+ dateFormat.format(date) + "\n\n");
		} catch (ParseException ex)
		{
			System.out.println("Exception " + ex);
		}
		return dateString;
	}
}
