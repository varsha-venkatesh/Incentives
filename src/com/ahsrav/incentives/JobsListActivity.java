package com.ahsrav.incentives;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.res.Configuration;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class JobsListActivity extends ActionBarActivity {

	private int mDrawerTitle = R.string.app_name;
	private String[] mNavBar;
	private DrawerLayout mDrawerLayout;
	private ListView mNavDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private static String currentURL;
	private static int preLast;
	public static List<Result> currentResults;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jobs_list);
		navDrawerSetup();
		currentURL = 
				"http://api.indeed.com/ads/apisearch?publisher=9616825572719411&v=2&format=json&l=austin&limit=20&sort=date";
		if (savedInstanceState == null) {
			selectItem("currentURL", 1);
		}
	}

	// Setup action bar options menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.jobs_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	// Handle action bar item clicks
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
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
	
	
	/** Start Navigation Drawer set up **/
	
	private void navDrawerSetup() {
		mNavBar = getResources().getStringArray(R.array.nav_drawer_items);
		final CharSequence mTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mNavDrawerList = (ListView) findViewById(R.id.left_drawer);

		// Set the adapter for the ListView
		mNavDrawerList.setAdapter(new ArrayAdapter<String>(this, 
				R.layout.nav_bar_list_item, mNavBar));
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

	// Called whenever we call invalidateOptionsMenu()
	// If the nav drawer is open, hide items in the action bar that are related to the content in the main frame
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(mNavDrawerList);
		menu.findItem(R.id.filter_jobs).setVisible(!isDrawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (position == 0) {
				selectItem("Please wait while data is being loaded", position);
				try {
					currentResults = new GetJobsTask().execute(currentURL).get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
			else {
				Integer clickedPosition = position;
				String mDataToDisplay = clickedPosition.toString()+" was clicked";
				selectItem(mDataToDisplay, position);
			}
		}
	}

	@Override
	public void setTitle(CharSequence mTitle) {
		getActionBar().setTitle(mTitle);
	}

	// Sync the toggle state after onRestoreInstanceState has occurred
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	// Pass any configuration change to the drawer toggle
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}	

	/** End Navigation Drawer set up **/

	
	

	/** Start JSON Data Handling **/
	
	public class GetJobsTask extends AsyncTask<String, Void, List<Result>> {

		AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

		@Override
		protected List<Result> doInBackground(String... url) {
			HttpGet request = new HttpGet(url[0]);
			JSONResponseHandler responseHandler = new JSONResponseHandler();
			try {
				return mClient.execute(request, responseHandler);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Result> results) {
			if (mClient != null)
				mClient.close();
			Log.i("onPostExecute", "There are "+results.size()+" entries");
			Fragment fragment = new ListMainContentFragment();
			Bundle args = new Bundle();
			args.putParcelableArrayList(ListMainContentFragment.ARG_DATA_TO_DISPLAY, (ArrayList<? extends Parcelable>) results);
			fragment.setArguments(args);

			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
			mNavDrawerList.setItemChecked(0, true);
			setTitle(mNavBar[0]);
			mDrawerLayout.closeDrawer(mNavDrawerList);
		}
	}

	/** End JSON Data Handling **/
	
	
	
	

	/** Start Handling Fragments **/

	// Fragment the controls the "content_frame". Displays content corresponding 
	// to nav bar item selected
	
	public static class ListMainContentFragment extends ListFragment {
		public static final String ARG_DATA_TO_DISPLAY = "parsed_data";

		public ListMainContentFragment() {
			// Empty constructor
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			JobsListAdaptor adapter = new JobsListAdaptor(inflater.getContext(),
					(List<Result>) getArguments().get(ARG_DATA_TO_DISPLAY));
			setListAdapter(adapter);
			//return super.onCreateView(inflater, container, savedInstanceState);
			Log.i("Scroll", "List generating");
			return inflater.inflate(R.layout.list_of_jobs, container, false);
		}
		
		public void onViewCreated(View view, Bundle savedInstanceState){
			Log.i("Scroll", "OnViewCreated entered");
			ListView mListView = getListView();
			mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					Log.i("Scroll", "List scrolled");
					switch(view.getId()) {
					case android.R.id.list:
						Integer lastItem = firstVisibleItem + visibleItemCount;
						if ((lastItem == totalItemCount) & (preLast != lastItem)) {
							Log.i("Scroll", "Last item reached");
							currentURL = currentURL+lastItem.toString();
							GetJobsTask jobActivity = new JobsListActivity.GetJobsTask();
							
							try {
								currentResults = (List<Result>) jobActivity.execute(currentURL).get();
							} catch (InterruptedException | ExecutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							// Log.i("Scroll", "Number of new results = "+currentResults.size());
							// JobsListAdaptor adapter = (JobsListAdaptor) view.getAdapter();
							// adapter.add(newestResults);
							preLast = lastItem;
						}
					}
				}
			});
		}
		
		public void onListItemClick(ListView mListView, View mView, int position, long id) {
			// TODO: Create explicit intent to open JobDetailsActivity. Send the item of type 
			// Result that is at the current position (get this from allResults or currentResults) 
		}
	}
	
	
	private void selectItem(String parsedData, int position) {
		// Create a new fragment and specify the content to show based on position in nav drawer
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

	public static class MainContentFragment extends Fragment {
		public static final String ARG_DATA_TO_DISPLAY = "parsed_data";

		public MainContentFragment() {
			// Empty constructor
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_text_view,
					container, false);
			TextView mTextView = (TextView) rootView.findViewById(R.id.textView1);
			String dataToDisplay = getArguments().getString(ARG_DATA_TO_DISPLAY);
			Log.i("JobsListActivity", "Fragment display entered for "+dataToDisplay);
			mTextView.setText(dataToDisplay);
			return rootView;
		}
	}

	/** End Handling Fragments **/


}
