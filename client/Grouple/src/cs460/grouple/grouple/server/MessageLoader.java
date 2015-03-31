package cs460.grouple.grouple.server;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class MessageLoader extends AsyncTaskLoader<ArrayList<String>>
{
	public MessageLoader(Context context)
	{
		super(context);
	}

	@Override
	public ArrayList<String> loadInBackground()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
