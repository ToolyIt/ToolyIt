/**
 *
 */
package it.tooly.dctmclient.model;

import com.documentum.fc.client.impl.docbroker.DocbrokerMap;

import it.tooly.shared.model.AbstractModelObject;

/**
 * A Documentum docbroker
 *
 * @author M.E. de Boer
 *
 */
public class Docbroker extends AbstractModelObject implements IDocbroker {

	private IContentServer server;
	private int port;
	private int secureConnectMode;

	public Docbroker(IContentServer server, int port, int secureConnectMode) {
		super(getId(server, port), getName(server, port));
		this.server = server;
		this.port = port;
		this.secureConnectMode = secureConnectMode;
    }

	public Docbroker(IContentServer server, DocbrokerMap docbrokerMap, int index) {
		this(server, docbrokerMap.getPortNumber(index), docbrokerMap.getSecureConnectMode(index));
	}

	public static String getId(IContentServer server, int port) {
		return server.getId() + ":" + port;
	}

	public static String getName(IContentServer server, int port) {
		return server.getName() + ":" + port;
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
