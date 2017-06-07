/**
 *
 */
package it.tooly.dctmclient.model;

import java.util.List;
import java.util.Map;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfServerMap;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfValue;
import com.documentum.fc.impl.util.RegistryPasswordUtils;
import com.documentum.operations.common.DfBase64FormatException;

import it.tooly.dctmclient.util.DctmUtils;

/**
 * The model object for a Documentum Content Server
 * @author M.E. de Boer
 */
public class ContentServer extends DctmObject implements IContentServer, IRepositoryObject {

	final static String A_CONCURRENT_SESSIONS = "concurrent_sessions";
	final static String DQL_GET_SESSIONS="execute show_sessions";

	private String hostname;
	private String connectionString;
	private int proximity;
	private String lastStatus;
	private ConnectionState connectionState;
	private int maxSessionCount;
	private IDfId serverConfigId;
	private SessionsCount sessionsCount = null;

	public ContentServer(IRepository repository, IDfServerMap dfServerMap, int index) throws DfException {
		super(repository, dfServerMap.getServerName(index), dfServerMap.getServerName(index));
		this.hostname = dfServerMap.getHostName(index);
		this.connectionString = null;
		this.proximity = dfServerMap.getClientProximity(index);
		this.lastStatus = dfServerMap.getLastStatus(index);
		this.connectionState = ConnectionState.DISCONNECTED;
		this.typedObject = dfServerMap;
		this.maxSessionCount = -1;
		this.serverConfigId = DfId.DF_NULLID;
		setConnectionString();
	}

	public ContentServer(IRepository repository, String name, String hostname, int proximity, String lastStatus) {
		super(repository, name, name);

		this.hostname = hostname;
		this.connectionString = null;
		this.proximity = proximity;
		this.lastStatus = lastStatus;
		this.connectionState = ConnectionState.DISCONNECTED;
		this.typedObject = null;
		this.maxSessionCount = -1;
		this.serverConfigId = DfId.DF_NULLID;
		setConnectionString();
	}

	private void setConnectionString() {
		// Build the connection string for this server instance
		StringBuffer connName = new StringBuffer();
		connName.append(this.repository==null?"":this.repository.getName());
		connName.append(".");
		connName.append(this.getName());
		connName.append("@");
		connName.append(this.getHostname());
		this.connectionString = connName.toString();
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getProximity() {
		return this.proximity;
	}

	public void setProximity(int proximity) {
		this.proximity = proximity;
	}

	/**
	 * @return the lastStatus
	 */
	public String getLastStatus() {
		return lastStatus;
	}

	/**
	 * @param lastStatus the lastStatus to set
	 */
	public void setLastStatus(String lastStatus) {
		this.lastStatus = lastStatus;
	}

	public Map<String, IDfValue> getAttributes() {
		int attrCount;
		Map<String, IDfValue> attrsValues;
		try {
			attrCount = this.typedObject.getAttrCount();
			attrsValues = new java.util.LinkedHashMap<>(attrCount);
			for (int x=0; x<attrCount; x++) {
				attrsValues.put(this.typedObject.getAttr(x).getName(), this.typedObject.getValueAt(x));
			}
		} catch (DfException e) {
			e.printStackTrace();
			return new java.util.LinkedHashMap<String, IDfValue>();
		}

		return attrsValues;
	}

	public String getAttribute(String propertyName) {
		try {
			return this.typedObject.getValue(propertyName).asString();
		} catch (DfException e) {
			e.printStackTrace();
			return "";
		}
	}

	public int getMaxSessionCount() {
		return this.maxSessionCount;
	}

	public void setMaxSessionCount(int maxSessionCount) {
		this.maxSessionCount = maxSessionCount;
	}

	public String getConnectionString() {
		return this.connectionString;
	}

	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}

	/**
	 * @return The connection state
	 */
	public ConnectionState getConnectionState() {
		return this.connectionState;
	}

	/**
	 * @param connected the connected to set
	 */
	public void setConnectionState(ConnectionState connectionState) {
		this.connectionState = connectionState;
	}

	public synchronized void init(IDfSessionManager sm) throws DfException {
		retrieveDetailsFromRepository(sm);
	}

	public SessionsCount getSessionsCount() {
		return this.sessionsCount;
	}

	/**
	 * @return the serverConfigId
	 */
	public IDfId getServerConfigId() {
		return serverConfigId;
	}

	/**
	 * @param serverConfigId the serverConfigId to set
	 */
	public void setServerConfigId(IDfId serverConfigId) {
		this.serverConfigId = serverConfigId;
	}

	public synchronized SessionsCount updateSessionCount(IDfSessionManager sm) throws DfException {
		Map<String, String> countSessionsMap;
		IDfSession session = null;
		try {
			session = sm.getSession(this.getConnectionString());
			List<Map<String, String>> qRes = DctmUtils.executeQueryReturnListOfMaps(session, SessionsCount.DQL_COUNT_SESSIONS);
			countSessionsMap = qRes.get(0);
			int hot = 	Integer.parseInt(countSessionsMap.get(SessionsCount.A_HOT_LIST_SIZE));
			int warm = 	Integer.parseInt(countSessionsMap.get(SessionsCount.A_WARM_LIST_SIZE));
			int cold = 	Integer.parseInt(countSessionsMap.get(SessionsCount.A_COLD_LIST_SIZE));
			int max = 	Integer.parseInt(countSessionsMap.get(A_CONCURRENT_SESSIONS));
			this.setMaxSessionCount(max);
			if (this.sessionsCount==null) {
				this.sessionsCount = new SessionsCount(max, hot, warm, cold);
			} else {
				this.sessionsCount.setAll(max, hot, warm, cold);
			}
		} finally {
			if (session != null)
				sm.release(session);
		}
		return this.sessionsCount;
	}

	public void retrieveDetailsFromRepository(IDfSessionManager sm) throws DfException {
		IDfPersistentObject serverConfObj;
		IDfSession session = null;
		try {
			session = sm.getSession(this.getConnectionString());
			serverConfObj = session
					.getObjectByQualification("dm_server_config where object_name = '" + this.getName() + "'");
		} finally {
			if (session != null)
				sm.release(session);
		}
		this.serverConfigId = serverConfObj.getObjectId();
		this.setMaxSessionCount(serverConfObj.getInt(A_CONCURRENT_SESSIONS));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		//Construction the string representation
		//of the filter
		StringBuffer stringObject = new StringBuffer();
		stringObject.append(getName());
		stringObject.append(" (Host:");
		stringObject.append(getHostname());
		stringObject.append(")");
		return stringObject.toString();
	}

	/**
	 * Decrypts the password. Decrypts the passed-in password using DFC
	 * facilities. Returns password "as is" if it's not encrypted.
	 *
	 * @return password to decrypt.
	 * @throws DfException
	 *             in case there is a problem decrypting the password
	 */
	private String decryptPassword(String passwordToDecrypt) throws DfException
	{
		String actualPassword = "";

		//Decrypt the password if it's encrypted
		try {
			actualPassword = RegistryPasswordUtils.decrypt(passwordToDecrypt);
		} catch (DfException e) {
			//Check if formatting exception was caught
			if(e.getCause() instanceof DfBase64FormatException)
			{
				//Assume that the password is not encrypted.
				actualPassword = passwordToDecrypt;
			}else{
				throw e;
			}
		}
		return actualPassword;
	}

}
