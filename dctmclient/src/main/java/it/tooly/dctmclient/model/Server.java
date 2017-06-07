package it.tooly.dctmclient.model;

public class Server extends AbstractObject implements IServer {

	protected String hostname;

	/**
	 * Constructor for a server. The name will also be used as the id (see {@link IModelObject})
	 * @param name
	 * @param hostname
	 */
	public Server(String name, String hostname) {
		super(name, name);
		this.hostname = hostname;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see opensource.dctm.model.IServer#getHostname()
	 */
	public String getHostname() {
		return this.hostname;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see opensource.dctm.model.IServer#setHostname()
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

}
