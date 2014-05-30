//package com.ahsrav.incentives;
//
//import android.util.Xml;
//
//import org.xmlpull.v1.XmlPullParser;
//import org.xmlpull.v1.XmlPullParserException;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * This class parses XML feeds from indeed.com.
// * Given an InputStream representation of a feed, it returns a List of entries,
// * where each list element represents a single Result (post) in the XML feed.
// */
//public class JobsListXMLParser {
//    private static final String ns = null;
//
//    // We don't use namespaces
//
//    public List<Result> parse(InputStream in) throws XmlPullParserException, IOException {
//        try {
//            XmlPullParser parser = Xml.newPullParser();
//            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
//            parser.setInput(in, null);
//            parser.nextTag();
//            return readFeed(parser);
//        } finally {
//            in.close();
//        }
//    }
//
//    private List<Result> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
//        List<Result> entries = new ArrayList<Result>();
//
//        parser.require(XmlPullParser.START_TAG, ns, "feed");
//        while (parser.next() != XmlPullParser.END_TAG) {
//            if (parser.getEventType() != XmlPullParser.START_TAG) {
//                continue;
//            }
//            String name = parser.getName();
//            // Starts by looking for the result tag
//            if (name.equals("result")) {
//                entries.add(readResult(parser));
//            } else {
//                skip(parser);
//            }
//        }
//        return entries;
//    }
//
//    // This class represents a single result (post) in the XML feed.
//    // It includes the data members "title," "link," and "summary."
//    public static class Result {
//        public final String jobTitle;
//        public final String companyName;
//        public final String location;
//        public final String snippet;
//        public final String url;
//        public final String relativeTime;
//
//        private Result(String jobTitle, String companyName, String location, String snippet, String url, String relativeTime) {
//            this.jobTitle = jobTitle;
//            this.companyName = companyName;
//            this.location = location;
//            this.snippet = snippet;
//            this.url = url;
//            this.relativeTime = relativeTime;
//        }
//    }
//
//    // Parses the contents of a result. If it encounters a jobtitle, company, 
//    // formattedLocationFull, snippet, url, formattedRelativeTime tag, hands them
//    // off to their respective "read" methods for processing. 
//    // Otherwise, skips the tag.
//    private Result readResult(XmlPullParser parser) throws XmlPullParserException, IOException {
//        parser.require(XmlPullParser.START_TAG, ns, "result");
//        String jobTitle = null;
//        String companyName = null;
//        String location = null;
//        String snippet = null;
//        String url = null;
//        String relativeTime = null;
//        while (parser.next() != XmlPullParser.END_TAG) {
//            if (parser.getEventType() != XmlPullParser.START_TAG) {
//                continue;
//            }
//            String name = parser.getName();
//            if (name.equals("jobtitle")) {
//                jobTitle = readTitle(parser);
//            } else if (name.equals("company")) {
//                companyName = readCompany(parser);
//            } else if (name.equals("formattedLocationFull")) {
//                location = readLocation(parser);
//            } else if (name.equals("snippet")) {
//                snippet = readSnippet(parser);
//            } else if (name.equals("url")) {
//                url = readURL(parser);
//            } else if (name.equals("formattedRelativeTime")) {
//                relativeTime = readRelativeTime(parser);
//            } else {
//                skip(parser);
//            }
//        }
//        return new Result(jobTitle, companyName, location, snippet, url, relativeTime);
//    }
//
//    // Processes jobtitle tags in the feed.
//    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
//        parser.require(XmlPullParser.START_TAG, ns, "jobtitle");
//        String title = readText(parser);
//        parser.require(XmlPullParser.END_TAG, ns, "jobtitle");
//        return title;
//    }
//    
//    // Processes company tags in the feed.
//    private String readCompany(XmlPullParser parser) throws IOException, XmlPullParserException {
//        parser.require(XmlPullParser.START_TAG, ns, "company");
//        String company = readText(parser);
//        parser.require(XmlPullParser.END_TAG, ns, "company");
//        return company;
//    }
//
//    // Processes formattedLocationFull tags in the feed.
//    private String readLocation(XmlPullParser parser) throws IOException, XmlPullParserException {
//    	parser.require(XmlPullParser.START_TAG, ns, "formattedLocationFull");
//    	String location = readText(parser);
//    	parser.require(XmlPullParser.END_TAG, ns, "formattedLocationFull");
//    	return location;
//    }
//    
//    // Processes snippet tags in the feed.
//    private String readSnippet(XmlPullParser parser) throws IOException, XmlPullParserException {
//    	parser.require(XmlPullParser.START_TAG, ns, "snippet");
//    	String snippet = readText(parser);
//    	parser.require(XmlPullParser.END_TAG, ns, "snippet");
//    	return snippet;
//    }
//    
//    // Processes url tags in the feed.
//    private String readURL(XmlPullParser parser) throws IOException, XmlPullParserException {
//    	parser.require(XmlPullParser.START_TAG, ns, "url");
//    	String url = readText(parser);
//    	parser.require(XmlPullParser.END_TAG, ns, "url");
//    	return url;
//    }
//    
//    // Processes formattedRelativeTime tags in the feed.
//    private String readRelativeTime(XmlPullParser parser) throws IOException, XmlPullParserException {
//    	parser.require(XmlPullParser.START_TAG, ns, "formattedRelativeTime");
//    	String time = readText(parser);
//    	parser.require(XmlPullParser.END_TAG, ns, "formattedRelativeTime");
//    	return time;
//    }
//
//    // Extract text value for each tag.
//    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
//        String result = "";
//        if (parser.next() == XmlPullParser.TEXT) {
//            result = parser.getText();
//            parser.nextTag();
//        }
//        return result;
//    }
//
//    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
//    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
//    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
//    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
//        if (parser.getEventType() != XmlPullParser.START_TAG) {
//            throw new IllegalStateException();
//        }
//        int depth = 1;
//        while (depth != 0) {
//            switch (parser.next()) {
//            case XmlPullParser.END_TAG:
//                    depth--;
//                    break;
//            case XmlPullParser.START_TAG:
//                    depth++;
//                    break;
//            }
//        }
//    }
//}
