package nl.circulairtriangles.scanner.network;

import java.util.EventObject;

public class RESTRequestEvent extends EventObject
{
	/** Serial */
	private static final long serialVersionUID = 1L;

	/** The ID to identify the request by. This is of use when handling multiple REST requests in a single class. */
	protected String ID;
	
	/** The result of the RESTRequest */
	protected String result;
	
	/**
	 * @param source
	 * @param ID
	 */
	public RESTRequestEvent(Object source, String ID)
	{
		this(source, "", ID);
	}
	
	/**
	 * @param source
	 * @param result
	 * @param ID
	 */
	public RESTRequestEvent(Object source, String result, String ID)
	{
		super(source);
		
		this.result = result;
		this.ID     = ID;
	}
	
	/**
	 * @return result
	 */
	public String getResult()
	{
		return result;
	}
	
	/**
	 * @return ID
	 */
	public String getID()
	{
		return ID;
	}
}
