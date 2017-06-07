/**
 * 
 */
package it.tooly.dctmclient.model;

/**
 * A Documentum docbroker
 * 
 * @author M.E. de Boer
 *
 */
public interface IDocbroker extends IModelObject{

	public IServer getServer();
	
	public int getPort();

	public int getSecureConnectMode();

}
