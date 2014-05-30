package com.ahsrav.incentives;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JSONResponseHandler implements ResponseHandler<List<Result>>{

	private static final String RESULTS_TAG = "results";
	private static final String JOBTITLE_TAG = "jobtitle";
	private static final String COMPANY_TAG = "company";
	private static final String LOCATION_TAG = "formattedLocationFull";
	private static final String TIME_TAG = "formattedRelativeTime";
	
	@Override
	public List<Result> handleResponse(HttpResponse response)
			throws ClientProtocolException, IOException {
		List<Result> results = new ArrayList<Result>();
		String JSONResponse = new BasicResponseHandler().handleResponse(response);

		try { 
			JSONObject responseObject = (JSONObject) new JSONTokener(JSONResponse).nextValue();
			
			JSONArray resultsArray = responseObject.getJSONArray(RESULTS_TAG);

			for (int i=0; i < resultsArray.length(); i++)
			{
				try {
					JSONObject oneResult = (JSONObject) resultsArray.get(i);
					// Pulling items from the array
					results.add(new Result(oneResult.getString(JOBTITLE_TAG), 
							oneResult.getString(COMPANY_TAG), oneResult.getString(LOCATION_TAG),
							oneResult.getString(TIME_TAG)));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return results;
	}

}
