package it.tooly.dctmclient.model;

import it.tooly.shared.model.AbstractModelObject;
import it.tooly.shared.model.IModelObject;

public class Server extends AbstractModelObject implements IServer {

	/**
	 * Constructor for a server.
	 *
	 * @param id
	 * @param name
	 * @param hostname
	 *            - The full hostname
	 */
	public Server(String id, String name, String hostname) {
		super(id, name);
		init(hostname);
	}

	/**
	 * Constructor for a server. The name will also be used as the id (see
	 * {@link IModelObject})
	 *
	 * @param name
	 * @param hostname
	 *            - The full hostname
	 */
	public Server(String name, String hostname) {
		super(name, name);
		init(hostname);
	}

	private void init(String hostname) {
		this.addStringAttribute("hostname", hostname);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see opensource.dctm.model.IServer#getHostname()
	 */
	public String getHostname() {
		return (String) getAttrValue("hostname");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see opensource.dctm.model.IServer#setHostname()
	 */
	public void setHostname(String hostname) {
		setAttrValue("hostname", hostname);
	}

}
