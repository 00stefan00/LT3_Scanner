package nl.circulairtriangles.scanner.network;

import android.os.AsyncTask;
import android.util.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The RESTRequest class simplifies the process of making RESTful requests to the web address of choice.
 */
public class RESTRequest extends AsyncTask<Void, Void, String>
{
	/** The enumeration of available HTTP request methods. */
	public enum Method { GET, POST, PUT };
	
	/** The enumeration of request's default accepted data types. */
	public enum HeaderAcceptedData
	{
		TEXT ("text/plain"), HTML ("text/html"), JSON ("application/json"), XML ("application/xml");
		
		private String headerAcceptedData;
		
		/**
		 * @param headerAcceptedData
		 */
		private HeaderAcceptedData(String headerAcceptedData)
		{
			this.headerAcceptedData = headerAcceptedData;
		}
		
		/**
		 * @return headerAcceptedData
		 */
		public String getHeaderAcceptedData()
		{
			return headerAcceptedData;
		}
	};
	
	/** The enumeration of errors that can be thrown by the RESTRequest. */
	public enum ExceptionCode
	{
		UNKNOWN_METHOD (1), INVALID_URL (2), INVALID_PARAMETERS (3), REQUEST_FAILED (4), REQUEST_ABORTED (5), NO_RESULT (6);
		
		private int exceptionCode;
		
		/**
		 * @param exceptionCode
		 */
		private ExceptionCode(int exceptionCode)
		{
			this.exceptionCode = exceptionCode;
		}

		/**
		 * @return exceptionType
		 */
		public String toString()
		{
			return "RESTRequest error: " + Integer.toString(exceptionCode);
		}
	}
	
	/** This variable indicates whether or not an asynchronous task is running. */
	protected boolean running;
	
	/** This variable indicates whether or not the httpRequest was manually aborted using the abort() method. */
	protected boolean manuallyAborted;
	
	/** The address an HTTP request will be sent to. */
	protected String address;
	
	/** The method with which the HTTP request is sent. */
	protected Method method;
	
	/** The accepted data type to set in the request's Accept header. */
	protected String headerAcceptedData;
	
	/** The ID to pass along with the event. This helps recognizing an event fired by a class that needs to handle multiple REST requests. */
	protected String ID;
	
	/** The parameters sent along with the request. */
	protected List<NameValuePair> parameters;
	
	/** The listener classes that need to be notified of a finished request. */
	protected ArrayList<RESTRequestListener> eventListeners;
	
	/** The HttpUriRequest is stored as class variable so it can be cancelled. */
	protected HttpUriRequest httpRequest;
	
	/**
	 * Overloads the RESTRequest(String address, String ID) constructor.
	 * 
	 * @param address
	 */
	public RESTRequest(String address)
	{
		this(address, "");
	}
	
	/**
	 * Overloads the RESTRequest(String address, Method method, String ID) constructor.
	 * 
	 * @param address,
	 * @param ID
	 */
	public RESTRequest(String address, String ID)
	{
		this(address, Method.GET, ID);
	}
	
	/**
	 * Overloads the RESTRequest(String address, Method method, String headerAcceptedData, String ID) constructor.
	 * 
	 * @param address
	 * @param method
	 * @param ID
	 */
	public RESTRequest(String address, Method method, String ID)
	{
		this(address, method, HeaderAcceptedData.JSON.getHeaderAcceptedData(), ID);
	}
	
	/**
	 * Constructs a new RESTRequest with the address, method, and accepted data type.
	 * 
	 * The passed ID will be sent along with the fired event to be able to recognize the specific
	 * event in a class that handles multiple REST requests.
	 * 
	 * @param address
	 * @param method
	 * @param headerAcceptedData
	 * @param ID
	 */
	public RESTRequest(String address, Method method, String headerAcceptedData, String ID)
	{
		manuallyAborted = false;
		
		this.address            = address;
		this.method             = method;
		this.headerAcceptedData = headerAcceptedData;
		this.ID                 = ID;
		
		parameters = new ArrayList<NameValuePair>();
		
		eventListeners = new ArrayList<RESTRequestListener>();
	}
	
	/**
	 * Tests if the passed string matches any of the ExceptionType's values
	 * 
	 * @return isExceptionType
	 */
	public static boolean isExceptionCode(String string)
	{
		for (ExceptionCode exceptionType : ExceptionCode.values())
		{
			if (exceptionType.toString().equals(string))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @return address
	 */
	public String getAddress()
	{
		return address;
	}

	/**
	 * @param address
	 */
	public void setAddress(String address)
	{
		this.address = address;
	}
	
	/**
	 * @return method
	 */
	public Method getMethod()
	{
		return method;
	}
	
	/**
	 * @param method
	 */
	public void setMethod(Method method)
	{
		this.method = method;
	}
	
	/**
	 * @return headerAcceptedData
	 */
	public String getHeaderAcceptedData()
	{
		return headerAcceptedData;
	}
	
	/**
	 * @param headerAcceptedData
	 */
	public void setHeaderAcceptedData(String headerAcceptedData)
	{
		this.headerAcceptedData = headerAcceptedData;
	}
	
	/**
	 * @return ID
	 */
	public String getID()
	{
		return ID;
	}

	/**
	 * @param
	 */
	public void setID(String ID)
	{
		this.ID = ID;
	}
	
	/**
	 * @param key
	 * @param value
	 */
	public void putString(String key, String value)
	{
		parameters.add(new BasicNameValuePair(key, encode(value)));
	}
	
	/**
	 * @param eventListener
	 */
	public void addEventListener(RESTRequestListener eventListener)
	{
		this.eventListeners.add(eventListener);
	}
	
	/**
	 * Return whether or not an asynchronous task is currently running
	 */
	public boolean isRunning()
	{
		return running;
	}
	
	/**
	 * Cancel the asynchronous task.
	 */
	public boolean cancel()
	{
		running = false;
		
		// Abort HTTP request
		if (httpRequest instanceof HttpUriRequest)
		{
			manuallyAborted = true;
			
			try
			{
				httpRequest.abort();
			}
			catch (UnsupportedOperationException e) { }
		}
		
		return super.cancel(true);
	}
	
	@Override
	protected String doInBackground(Void... voids)
	{
		running         = true;
		manuallyAborted = false;
		
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient();

		// Get the correct request method
		try
		{
			switch (method)
			{
				case GET:
					// Set URL and encode parameters
					httpRequest = new HttpGet(address + "?" + URLEncodedUtils.format(parameters, "utf-8"));
					break;
	
				case POST:
					httpRequest = new HttpPost(address);
					
					// Encode parameters as entity
					((HttpPost) httpRequest).setEntity(new UrlEncodedFormEntity(parameters));
					break;
					
				case PUT:
					httpRequest = new HttpPut(address);
					
					// Encode parameters as entity
					((HttpPut) httpRequest).setEntity(new UrlEncodedFormEntity(parameters));
					break;
					
				default:
					return ExceptionCode.UNKNOWN_METHOD.toString();
			}
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			return ExceptionCode.INVALID_URL.toString();
		}
		catch (UnsupportedEncodingException e)
		{
			return ExceptionCode.INVALID_PARAMETERS.toString();
		}
		
		// Indicate what data needs to be received
		httpRequest.setHeader("Accept", headerAcceptedData);

		InputStream inputStream = null;
		
		Scanner scanner = null;
		
		try
		{
			// Run request
			HttpResponse httpResponse = defaultHttpClient.execute(httpRequest);
			
			// Get content of response
			inputStream = httpResponse.getEntity().getContent();
			
			// Trick to read all data from a stream in one line: https://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
			scanner = new Scanner(inputStream).useDelimiter("\\A");

			String result = "";
			
			// Read lines into result
			while (scanner.hasNext())
			{
				result += scanner.next();
			}
			
			if (result.length() > 0)
			{
				return result;
			}
		}
		catch (IOException e)
		{
			if (!manuallyAborted)
			{
				return ExceptionCode.REQUEST_FAILED.toString();
			}
			
			return ExceptionCode.REQUEST_ABORTED.toString();
		}
		finally // Close opened utilities
		{
			if (scanner != null)
			{
				scanner.close();
			}
			
			if (inputStream != null)
			{
				try
				{
					inputStream.close();
				}
				catch (IOException e) { }
			}
		}
		
		return ExceptionCode.NO_RESULT.toString();
	}
	
	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		
		for (RESTRequestListener eventListener : eventListeners)
		{
			// Create new RESTRequestEvent to be handled by the event listener
			eventListener.RESTRequestOnPreExecute(new RESTRequestEvent(this, ID));
		}
	}
	
	@Override
	protected void onProgressUpdate(Void... voids)
	{
		super.onProgressUpdate(voids);
		
		for (RESTRequestListener eventListener : eventListeners)
		{
			// Create new RESTRequestEvent to be handled by the event listener
			eventListener.RESTRequestOnProgressUpdate(new RESTRequestEvent(this, ID));
		}
	}
	
	@Override
	protected void onPostExecute(String result)
	{
		running = false;
		
		for (RESTRequestListener eventListener : eventListeners)
		{
			// Create new RESTRequestEvent to be handled by the event listener
			eventListener.RESTRequestOnPostExecute(new RESTRequestEvent(this, result, ID));
		}
	}
	
	@Override
	public String toString()
	{
		return address + "?" + URLEncodedUtils.format(parameters, "utf-8");
	}

	private String encode(String value) {
		String returnSting = "";
		try {
			returnSting = Base64.encodeToString(value.getBytes("UTF-8"), Base64.NO_WRAP);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnSting;
	}
}