package nl.circulairtriangles.scanner.network;

public interface RESTRequestListener
{
	void RESTRequestOnPreExecute(RESTRequestEvent event);
	
	void RESTRequestOnProgressUpdate(RESTRequestEvent event);
	
	void RESTRequestOnPostExecute(RESTRequestEvent event);
}
