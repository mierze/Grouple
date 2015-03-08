package cs460.grouple.grouple;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
public class SettingsActivity extends ActionBarActivity
{
private BroadcastReceiver broadcastReceiver;
private static Global GLOBAL;
@Override
protected void onCreate(Bundle savedInstanceState)
{
super.onCreate(savedInstanceState);
initKillswitchListener();
GLOBAL = ((Global) getApplicationContext());
setContentView(R.layout.activity_settings);
/* Action bar */
ActionBar ab = getSupportActionBar();
ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
ab.setCustomView(R.layout.actionbar);
ab.setDisplayHomeAsUpEnabled(false);
TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
actionbarTitle.setText("Settings");
}
public void friendSwitch(View view)
{
}
public void groupSwitch(View view)
{
}
public void eventSwitch(View view)
{
}
public void proposeSwitch(View view)
{
}
public void changePasswordButton(View view)
{
}
public void changeEmailButton(View view)
{
}
public void deleteAccountButton(View view)
{
}
@Override
public boolean onCreateOptionsMenu(Menu menu)
{
// Inflate the menu; this adds items to the action bar if it is present.
getMenuInflater().inflate(R.menu.settings, menu);
return true;
}
@Override
public boolean onOptionsItemSelected(MenuItem item)
{
// Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
int id = item.getItemId();
if (id == R.id.action_logout)
{
GLOBAL.destroySession();
Intent login = new Intent(this, LoginActivity.class);
startActivity(login);
Intent intent = new Intent("CLOSE_ALL");
this.sendBroadcast(intent);
return true;
}
if (id == R.id.action_home)
{
Intent intent = new Intent(this, HomeActivity.class);
startActivity(intent);
}
return super.onOptionsItemSelected(item);
}
public void startParentActivity(View view)
{
String className = "HomeActivity";
Intent newIntent = null;
try
{
newIntent = new Intent(this, Class.forName("cs460.grouple.grouple."
+ className));
} catch (ClassNotFoundException e)
{
e.printStackTrace();
}
startActivity(newIntent);
finish();
}
public void initKillswitchListener()
{
// START KILL SWITCH LISTENER
IntentFilter intentFilter = new IntentFilter();
intentFilter.addAction("CLOSE_ALL");
broadcastReceiver = new BroadcastReceiver()
{
@Override
public void onReceive(Context context, Intent intent)
{
// close activity
if (intent.getAction().equals("CLOSE_ALL"))
{
Log.d("app666", "we killin the login it");
// System.exit(1);
finish();
}
}
};
registerReceiver(broadcastReceiver, intentFilter);
// End Kill switch listener
}
}