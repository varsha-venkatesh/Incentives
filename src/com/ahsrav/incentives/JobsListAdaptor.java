package com.ahsrav.incentives;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class JobsListAdaptor extends BaseAdapter {

	// List of Results
	private List<Result> mItems = new ArrayList<Result>();
	
	private final Context mContext;

	public JobsListAdaptor(Context context, List<Result> items) {

		this.mContext = context;
		this.mItems = items;

	}

	// Add a Result to the adapter
	// Notify observers that the data set has changed
	public void add(Result item) {

		mItems.add(item);
		notifyDataSetChanged();

	}
	
	public void add(List<Result> items) {
		for (Result item:items){

			mItems.add(item);
		}
		notifyDataSetChanged();

	}
	
	// Clears the list adapter of all items.	
	public void clear(){

		mItems.clear();
		notifyDataSetChanged();
	
	}

	// Returns the number of Results
	@Override
	public int getCount() {

		return mItems.size();

	}

	// Retrieve the number of Results
	@Override
	public Object getItem(int pos) {

		return mItems.get(pos);

	}

	// Get the ID for the Result
	// In this case it's just the position
	@Override
	public long getItemId(int pos) {

		return pos;

	}

	//Create a View to display the Result 
	// at specified position in mItems
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = 
				(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemLayout = inflater.inflate(R.layout.fragment_jobs_list, parent, false);
		
		// Get the current Result
		final Result mResult = (Result) getItem(position);

		// Fill in specific Result data
		final TextView titleView = (TextView) itemLayout.findViewById(R.id.textView1);
		titleView.setText(mResult.jobTitle);
		final TextView companyView = (TextView) itemLayout.findViewById(R.id.textView3);
		companyView.setText(mResult.companyName);
		final TextView timeView = (TextView) itemLayout.findViewById(R.id.textView2);
		timeView.setText(mResult.relativeTime);
		final TextView locationView = (TextView) itemLayout.findViewById(R.id.textView4);
		locationView.setText(mResult.location);

		return itemLayout;

	}

}
