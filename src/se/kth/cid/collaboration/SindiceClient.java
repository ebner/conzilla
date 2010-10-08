/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.collaboration;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Client for accessing the semantic indexer at sindice.com. 
 * 
 * @author Hannes Ebner
 */
public class SindiceClient {
	
	public static void main(String[] argv) {
		SindiceClient client = new SindiceClient();
		
		// Query a keyword
		System.out.println("Results for Organic.Edunet: " + client.queryTermAsJSON("Organic.Edunet").length());
		
		// List<SindiceQueryResultEntry> nativeResult = client.queryTerm("deri");
		// System.out.println(nativeResult.size());
		
		List<SindiceQueryResultEntry> nativeResult2 = client.queryProperty(URI.create("http://xmlns.com/foaf/0.1/name"), "Tim Berners-Lee");
		System.out.println(nativeResult2.size());
				
		// Submit files
//		List<URI> files = new ArrayList<URI>();
		//files.add(URI.create("http://collaborilla.conzilla.org/repository/people/amb/urn_path_/org/conzilla/people/amb/virginska/information.rdf"));
		//files.add(URI.create("http://collaborilla.conzilla.org/repository/people/amb/urn_path_/org/conzilla/people/amb/virginska/presentation.rdf"));
//		client.submitRDFLocations(files);
	}
	
	private static String SINDICE_API_BASE = "http://api.sindice.com/v2/";
	
	private static String SINDICE_POST_URL = "http://sindice.com/general/parse"; // TODO
	
	private static String SINDICE_QUERY_URL = SINDICE_API_BASE + "search";
	
	private static int TIMEOUT = 5000; // ms 
	
	private HttpClient client;
	
	private Log log = LogFactory.getLog(SindiceClient.class);
	
	private static class SindiceQueryType {
		
		public static String TERM = "term";
		
		public static String ADVANCED = "advanced";
		
	}
	
	public class SindiceQueryResultEntry {
			
		public List<String> title;
		
		public String content;
		
		public URI link;
		
		public List<Format> formats;
		
		public Date updated;
		
	}
	
	public enum Format {
		Microformat, RDF, RDFa, Other
	}
	
	public SindiceClient() {
		client = new HttpClient();
		client.getParams().setConnectionManagerTimeout(TIMEOUT);
		client.getParams().setSoTimeout(TIMEOUT);
		client.getParams().setParameter("http.connection.timeout", TIMEOUT); // there is no convenience method for this
	}
	
	public void submitRDFLocations(List<URI> files) {
		HttpMethodRetryHandler retryHandler = new DefaultHttpMethodRetryHandler(1, false);
		PostMethod method = new PostMethod(SINDICE_POST_URL);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, retryHandler);
	    
		String fileList = new String();
	    for (URI uri : files) {
			fileList += uri.toASCIIString() + "\n";
		}
	    
	    NameValuePair[] data = { new NameValuePair("url", fileList) };
	    method.setRequestBody(data);
	    
	    try {
			client.executeMethod(method);
		} catch (HttpException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			method.releaseConnection();
		}
	}
	
	private JSONArray sendQueryForJSON(String queryType, String queryValue) {
		List<String> results = new ArrayList<String>();
		JSONArray jsonResult = new JSONArray();
		
		try {
			int i = 1;
			int resultCount = 0;
			do {
				String queryResult = sendPagedQuery(queryType, queryValue, "application/json", i);
				JSONObject queryObject = new JSONObject(queryResult);
				resultCount = queryObject.getInt("totalResults"); // that's the total number of results for this query
				results.add(queryObject.getString("entries")); // "entries" is an array containing the results
			} while ((i++*10) < resultCount); // we get 10 results at once
		} catch (JSONException e) {
			log.error("Unable to create JSON from String: " + e.getMessage());
		}
		
		for (String part : results) {
			try {
				JSONArray partArray = new JSONArray(part);
				for (int i = 0; i < partArray.length(); i++) {
					jsonResult.put(partArray.get(i));
				}
			} catch (JSONException e) {
				log.error("Unable to handle JSON array", e);
			}
		}
		
		return jsonResult;
	}
	
	private String sendPagedQuery(String queryType, String queryValue, String mediaType, int page) {
		byte[] result = null;
		HttpMethod method = new GetMethod(SINDICE_QUERY_URL);
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
	    method.setFollowRedirects(true);
	    method.getParams().setSoTimeout(TIMEOUT);
	    method.setRequestHeader("Accept", mediaType);
	    
	    method.setQueryString(new NameValuePair[] {
	    		new NameValuePair("qt", queryType),
	    		new NameValuePair("q", queryValue),
	    		new NameValuePair("page", Integer.toString(page))
	    });
	    
	    log.debug("GET " + method.getPath() + "?" + method.getQueryString());

	    try {
			int statusCode = client.executeMethod(method);
			if (statusCode < 200 || statusCode > 299) {
				log.warn("Got status code " + statusCode + ", query was probably not successful");
			}
			result = method.getResponseBody();
		} catch (HttpException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			method.releaseConnection();
		}
		
		return new String(result);
	}
	
	public JSONArray queryURIAsJSON(URI uri) {
		return sendQueryForJSON(SindiceQueryType.TERM, uri.toASCIIString());
	}
	
	public List<SindiceQueryResultEntry> queryURI(URI uri) {
		return convertJSONtoNativeObject(queryURIAsJSON(uri));
	}
	
	public JSONArray queryTermAsJSON(String term) {
		return sendQueryForJSON(SindiceQueryType.TERM, term);
	}
	
	public List<SindiceQueryResultEntry> queryTerm(String term) {
		return convertJSONtoNativeObject(queryTermAsJSON(term));
	}
	
	public JSONArray queryPropertyAsJSON(URI property, String value) {
		return sendQueryForJSON(SindiceQueryType.ADVANCED, "* <" + property + "> " + "\"" + value + "\"");
	}
	
	public List<SindiceQueryResultEntry> queryProperty(URI property, String value) {
		return convertJSONtoNativeObject(queryPropertyAsJSON(property, value));
	}
	
	public JSONArray queryAdvancedAsJSON(String query) {
		return sendQueryForJSON(SindiceQueryType.ADVANCED, query);
	}
	
	/**
	 * @see http://sindice.com/developers/querylanguages
	 */
	public List<SindiceQueryResultEntry> queryAdvanced(String query) {
		return convertJSONtoNativeObject(queryAdvancedAsJSON(query));
	}
	
	private List<SindiceQueryResultEntry> convertJSONtoNativeObject(JSONArray queryResult) {
		List<SindiceQueryResultEntry> result = new ArrayList<SindiceQueryResultEntry>();
		int resultCount = queryResult.length();
		
		for (int i = 0; i < resultCount; i++) {
			try {
				JSONObject jsonEntry = queryResult.getJSONObject(0);
				SindiceQueryResultEntry entry = new SindiceQueryResultEntry();
				
				// Content
				entry.content = jsonEntry.getString("content");
				
				// Link / Document URI
				entry.link = URI.create(jsonEntry.getString("link"));
				
				// Formats
				JSONArray formatArray = jsonEntry.getJSONArray("formats");
				List<Format> formats = new ArrayList<Format>();
				for (int j = 0; j < formatArray.length(); j++) {
					String format = formatArray.getString(j);
					if ("RDF".equalsIgnoreCase(format)) {
						formats.add(Format.RDF);
					} else if ("RDFa".equalsIgnoreCase(format)) {
						formats.add(Format.RDFa);
					} else if ("MICROFORMAT".equalsIgnoreCase(format)) {
						formats.add(Format.Microformat);
					} else {
						formats.add(Format.Other);
					}
				}
				entry.formats = formats;
				
				// Title
				JSONArray titleArray = jsonEntry.getJSONArray("title");
				List<String> titles = new ArrayList<String>();
				for (int j = 0; j < titleArray.length(); j++) {
					titles.add(titleArray.getString(j));
				}
				entry.title = titles;
				
				// Date
				DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
				try {
					entry.updated = formatter.parse(jsonEntry.getString("updated"));
				} catch (ParseException pe) {
				}
				
				result.add(entry);
			} catch (JSONException e) {
				log.error(e.getMessage());
			}
		}
		
		return result;
	}

}