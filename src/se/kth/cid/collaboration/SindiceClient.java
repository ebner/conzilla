/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.collaboration;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONArray;
import org.json.JSONException;

import se.kth.cid.util.Tracer;

/**
 * Client for accessing the semantic indexer at sindice.com. 
 * 
 * @author Hannes Ebner
 */
public class SindiceClient {
	
	public static void main(String[] argv) {
		SindiceClient client = new SindiceClient();
		
		// Query a URI
		JSONArray result = client.queryURIAsJSON(URI.create("http://www.conzilla.org/people/amb/Organic.Edunet/concept#b4f7c5115758067e4"));
		System.out.println(result);
		
		// Query a keyword
		System.out.println("Results for Organic.Edunet: " + client.queryKeywordAsJSON("Organic.Edunet").length());
		
		// Submit files
		List<URI> files = new ArrayList<URI>();
		//files.add(URI.create("http://collaborilla.conzilla.org/repository/people/amb/urn_path_/org/conzilla/people/amb/virginska/information.rdf"));
		//files.add(URI.create("http://collaborilla.conzilla.org/repository/people/amb/urn_path_/org/conzilla/people/amb/virginska/presentation.rdf"));
		client.submitRDFLocations(files);
	}
	
	private static String SINDICE_POST_URL = "http://sindice.com/general/parse";
	
	private static String SINDICE_QUERY_URL = "http://sindice.com/query/lookup";
	
	private static int SO_TIMEOUT = 10000; // ms 
	
	private HttpClient client;
	
	public SindiceClient() {
		client = new HttpClient();
	}
	
	public void submitRDFLocations(List<URI> files) {
		PostMethod method = new PostMethod(SINDICE_POST_URL);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
		method.getParams().setSoTimeout(SO_TIMEOUT);
	    
		String fileList = new String();
	    for (URI uri : files) {
			fileList += uri.toASCIIString() + "\n";
		}
	    
	    NameValuePair[] data = { new NameValuePair("url", fileList) };
	    method.setRequestBody(data);
	    
	    try {
			client.executeMethod(method);
		} catch (HttpException e) {
			Tracer.error(e.getMessage());
		} catch (IOException e) {
			Tracer.error(e.getMessage());
		} finally {
			method.releaseConnection();
		}
	}
	
	private JSONArray sendQueryForJSON(String queryName, String queryValue) {
		List<String> results = new ArrayList<String>();
		String queryResult = "";
		
		try {
			int i = 1;
			do {
				queryResult = sendPagedQuery(queryName, queryValue, "application/json", i++);
				results.add(queryResult);
			} while (new JSONArray(queryResult).length() == 10);
		} catch (JSONException e) {
			Tracer.error("Unable to create JSON from String: " + e.getMessage());
		}
		
		JSONArray jsonResult = new JSONArray();
		for (String part : results) {
			try {
				JSONArray partArray = new JSONArray(part);
				for (int i = 0; i < partArray.length(); i++) {
					jsonResult.put(partArray.get(i));
				}
			} catch (JSONException e) {
				Tracer.error("Unable to handle JSON array: " + e.getMessage());
			}
		}
		
		return jsonResult;
	}
	
	private String sendPagedQuery(String queryName, String queryValue, String mediaType, int page) {
		byte[] result = null;
		HttpMethod method = new GetMethod(SINDICE_QUERY_URL);
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
	    method.setFollowRedirects(true);
	    method.getParams().setSoTimeout(SO_TIMEOUT);
	    method.setRequestHeader("Accept", mediaType);
	    
	    method.setQueryString(new NameValuePair[] { 
	    		new NameValuePair(queryName, queryValue),
	    		new NameValuePair("page", Integer.toString(page))
	    });

	    try {
			int statusCode = client.executeMethod(method);
			if (statusCode < 200 || statusCode > 299) {
				Tracer.debug("Got status code " + statusCode + ", query was probably not successful");
			}
			result = method.getResponseBody();
		} catch (HttpException e) {
			Tracer.error(e.getMessage());
		} catch (IOException e) {
			Tracer.error(e.getMessage());
		} finally {
			method.releaseConnection();
		}
		
		return new String(result);
	}
	
	public JSONArray queryURIAsJSON(URI uri) {
		return sendQueryForJSON("uri", uri.toASCIIString());
	}
	
	public JSONArray queryKeywordAsJSON(String keyword) {
		return sendQueryForJSON("keyword", keyword);
	}
	
//	public JSONArray queryIFPAsJSON(URI property, String object) {
//		// TODO
//	}

}