package cs460.grouple.grouple;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Contact
{
	private int id;
    private String message;
    private String dateString;
    private String rawDateString;
    private Date date;
    private String sender;
    private String senderName;
    private String otherEmail;
    private Global GLOBAL;
    protected String getOtherEmail()
	{
		return otherEmail;
	}
	private void setOtherEmail()
	{
		String otherEmail = sender.equals(GLOBAL.getCurrentUser().getEmail()) ? receiver : sender;
		this.otherEmail = otherEmail;
	}

	private String receiver; //GID/EID/EMAIL
    private String readByDateString;
	private Bitmap image; //all entities have images
    
    protected Contact(String message, String rawDateString, String sender, String senderName, String receiver, String readByDateString)
    {
    	GLOBAL = new Global();
    	this.message = message;
    	this.rawDateString = rawDateString;
    	dateString = parseDate(rawDateString);
    	this.sender = sender;
    	this.senderName = senderName;
    	this.receiver = receiver;
    	this.readByDateString = readByDateString;
    	setOtherEmail();
    }
    protected Contact(String message, Date date, String sender, String senderName, String receiver, String readByDateString)
    {
    	this.message = message;
    	this.date = date;
    	dateString = parseRealDate(date);
    	this.sender = sender;
    	this.senderName = senderName;
    	this.receiver = receiver;
    	this.readByDateString = readByDateString;
    }
    
    //GETTERS
    protected int getID()
    {
    	return id;
    }
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
    protected String getReadByDateString()
    {
    	return readByDateString;
    }
    protected Bitmap getImage()
    {
    	return image;
    }
    
    //SETTERS
    protected void setID(int id)
    {
    	this.id = id;
    }
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
  //img is taken from json string
  	protected void setImage(String img)
  	{
  		Bitmap bmp;
  		//jsonArray.getString("image");
  	
  		// decode image back to android bitmap format
  		byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
  		if (decodedString != null)
  		{
  			bmp = BitmapFactory.decodeByteArray(decodedString, 0,
  					decodedString.length);
  			//setting bmp;
  			this.image = bmp;
  		}
  		else
  		{
  			image = null;
  		}
  	}
 
	private String parseDate(String rawDateString)
	{
		String dateString = "";
		SimpleDateFormat raw = new SimpleDateFormat("yyyy-M-d h:mm:ss");
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"EEEE h:mma");
		try
		{

			date = (Date) raw.parse(rawDateString);
			dateString = dateFormat.format(date);
			// date = raw.format(parsedDate);

		} catch (ParseException ex)
		{
			System.out.println("Exception " + ex);
		}
		return dateString;
	}
	
	private String parseRealDate(Date date)
	{
		String dateString = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"EEEE h:mma");


		dateString = dateFormat.format(date);
			// date = raw.format(parsedDate);

		return dateString;
	}
}
