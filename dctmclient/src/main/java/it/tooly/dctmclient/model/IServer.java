package it.tooly.dctmclient.model;

import it.tooly.shared.model.IModelObject;

public interface IServer extends IModelObject {

	/**
	 * Returns server host name.
	 * 
	 * @return host name of this server
	 */
	String getHostname();
	
	/**
	 * Set the server host name.
	 * 
	 * @param hostname The new hostname
	 */
	void setHostname(String hostname);
}
