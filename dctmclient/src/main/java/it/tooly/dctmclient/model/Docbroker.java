/**
 *
 */
package it.tooly.dctmclient.model;

import it.tooly.dctmclient.model.util.ModelMap;

/**
 * A Documentum docbroker
 *
 * @author M.E. de Boer
 *
 */
public class Docbroker extends DctmObject implements IDocbroker {

	private IContentServer server;
	private int port;
	private int secureConnectMode;
	private ModelMap<IRepository> repoMap;

	public Docbroker(IContentServer server, int port, int secureConnectMode) {
		super(server.getRepository(), server.getId() + port, server.getId() + port);
		this.server = server;
    }

	public int getRepositoryCount() {
		return repoMap.size();
	}

	public ModelMap<IRepository> getRepositoryMap() {
		return this.repoMap;
	}

	public IContentServer getServer() {
		return this.server;
	}

	public void setServer(ContentServer server) {
		this.server = server;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the secureConnectMode
	 */
	public int getSecureConnectMode() {
		return this.secureConnectMode;
	}

	/**
	 * @param secureConnectMode
	 *            the secureConnectMode to set
	 */
	public void setSecureConnectMode(int secureConnectMode) {
		this.secureConnectMode = secureConnectMode;
	}

}
