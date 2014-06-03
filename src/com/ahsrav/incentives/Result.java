package com.ahsrav.incentives;

public class Result {
	// This class represents a single result (post) in the XML feed.
	public final String jobTitle;
	public final String companyName;
	public final String location;
	public final String snippet;
	public final String url;
	public final String relativeTime;

	public Result(String jobTitle, String companyName, String location, String snippet, String url, String relativeTime) {
		this.jobTitle = jobTitle;
		this.companyName = companyName;
		this.location = location;
		this.snippet = snippet;
		this.url = url;
		this.relativeTime = relativeTime;
	}
	
	public Result(String jobTitle, String companyName, String location, String relativeTime) {
		this.jobTitle = jobTitle;
		this.companyName = companyName;
		this.location = location;
		this.snippet = null;
		this.url = null;
		this.relativeTime = relativeTime;

	}
}
