package com.ahsrav.incentives;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class JobsListActivity extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ListView mNavDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	
	private String[] mNavBar;
	private CharSequence mTitle;
	private CharSequence mDrawerTitle;
	private Integer mNavItemClickedPos = 1;
	private String mDataToParse;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jobs_list);
		
		navDrawerSetup();
		
		if (savedInstanceState == null) {
			selectItem("", 0);
		}
		
		
	}

	private void navDrawerSetup() {
		mNavBar = getResources().getStringArray(R.array.nav_drawer_items);
		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mNavDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		// Set the adapter for the ListView
		mNavDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.nav_bar_list_item, mNavBar));
		mNavDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		// ActionBarDrawerToggle ties together the proper interactions between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, 
				R.string.drawer_open, R.string.drawer_closed) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // Creates a call to onPrepareOptionsMenu()
			}
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.jobs_list, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	// Called whenever we call invalidateOptionsMenu()
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide items in the action bar that are related to the content in the main frame
		boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(mNavDrawerList);
		menu.findItem(R.id.filter_jobs).setVisible(!isDrawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
			// TODO: Add the action that needs to be carried out
		}
		if (id == R.id.filter_jobs) {
			return true;
			// TODO: Add the action that needs to be carried out
		}
		return super.onOptionsItemSelected(item);
	}
		
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// Auto-generated method stub
			mNavItemClickedPos = position;
			if (mNavItemClickedPos == 0) {
				// TODO: Get default settings for location and other search parameters and create the necessary url
				String url = "http://api.indeed.com/ads/apisearch?publisher=9616825572719411&v=2&format=xml&l=austin";
				new GetJobsTask().execute(url);
				// TODO: mDataToParse needs to be parsed and saved in parsedData
				
			}
			else 
				mDataToParse = mNavItemClickedPos.toString()+" was clicked";
			selectItem(mDataToParse, mNavItemClickedPos);
			
		}
	}

	@Override
	public void setTitle(CharSequence title) {
	    mTitle = title;
	    getActionBar().setTitle(mTitle);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggle
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	

	/** Swaps fragments in the main content view */
	private void selectItem(String parsedData, int position) {
	    // Create a new fragment and specify the planet to show based on position
	    Fragment fragment = new MainContentFragment();
	    Bundle args = new Bundle();
	    args.putString(MainContentFragment.ARG_DATA_TO_DISPLAY, parsedData);
	    fragment.setArguments(args);

	    // Insert the fragment by replacing any existing fragment
	    FragmentManager fragmentManager = getFragmentManager();
	    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

	    // Highlight the selected item, update the title, and close the drawer
	    mNavDrawerList.setItemChecked(position, true);
	    setTitle(mNavBar[position]);
	    mDrawerLayout.closeDrawer(mNavDrawerList);
	}
	

	/**
	 * Fragment the controls the "content_frame". Displays content corresponding to nav bar item selected
	 */
	public static class MainContentFragment extends Fragment {
		public static final String ARG_DATA_TO_DISPLAY = "parsed_data";

		public MainContentFragment() {
			// Empty constructor
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_jobs_list,
					container, false);
			TextView mTextView = (TextView) rootView.findViewById(R.id.textView1);
			String dataToDisplay = getArguments().getString(ARG_DATA_TO_DISPLAY);
			Log.i("JobsListActivity", "Fragment display entered for "+dataToDisplay);
			mTextView.setText(dataToDisplay);
			return rootView;

		}
	}
	
	private class GetJobsTask extends AsyncTask<String, Void, String> {

		private static final String TAG = "GetJobsTask";
		//private static final String URL = "http://api.indeed.com/ads/apisearch?publisher=9616825572719411&v=2&format=xml&l=austin";
		
		@Override
		protected String doInBackground(String... url) {
			// Auto-generated method stub
			String data = "";
			HttpURLConnection urlConnection = null;
			
			try {
				urlConnection = (HttpURLConnection) new URL(url[0]).openConnection();
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				data = readStream(in);
			} catch (MalformedURLException exception) {
				Log.e(TAG, "MalformedURLException");
			} catch (IOException exception) {
				Log.e(TAG, "IOException");
			} finally {
				if (null != urlConnection)
					urlConnection.disconnect();
			}
			return data;
		}
		
		@Override
		protected void onPostExecute(String result) {
			mDataToParse = result;
		}

		private String readStream(InputStream in) {
			// Auto-generated method stub
			BufferedReader reader = null;
			StringBuffer data = new StringBuffer("");
			try {
				reader = new BufferedReader(new InputStreamReader(in));
				String line = "";
				while ((line = reader.readLine()) != null) {
					data.append(line);
				}
			} catch (IOException e) {
				Log.e(TAG, "IOException");
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return data.toString();
		}
		
	}

}
