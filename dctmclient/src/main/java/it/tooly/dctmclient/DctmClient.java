/**
 *
 */
package it.tooly.dctmclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfDocbaseMap;
import com.documentum.fc.client.IDfDocbrokerClient;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfServerMap;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.impl.docbroker.DocbrokerMap;
import com.documentum.fc.client.search.IDfPassThroughQuery;
import com.documentum.fc.client.search.IDfQueryManager;
import com.documentum.fc.client.search.IDfQueryProcessor;
import com.documentum.fc.client.search.IDfSearchService;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfRuntimeException;
import com.documentum.fc.common.IDfLoginInfo;

import it.tooly.dctmclient.model.ContentServer;
import it.tooly.dctmclient.model.Docbroker;
import it.tooly.dctmclient.model.IContentServer;
import it.tooly.dctmclient.model.IDocbroker;
import it.tooly.dctmclient.model.IRepository;
import it.tooly.dctmclient.model.IUserAccount;
import it.tooly.dctmclient.model.Repository;
import it.tooly.shared.model.util.ModelMap;


/**
 * DctmClient class. This contains a static instance of itself and is the
 * central class for getting information from Documentum.
 *
 * @author M.E. de Boer
 */
public class DctmClient {

	private static DctmClient instance = null;
	private final Logger logger;
	private IDfClientX clientx = null;
	private IDfClient client = null;
	private IDfDocbrokerClient docbrokerClient = null;

	/**
	 * Known content servers with the hostname as key
	 */
	private Map<String, ContentServer> hostsContentServers;

	/**
	 * Known docbrokers
	 */
	private ModelMap<Docbroker> docbrokerMap;

	/**
	 * Known repositories with the repository id as key
	 */
	private ModelMap<Repository> repoMap;

	/**
	 * Known content servers with the content server name as key
	 */
	private ModelMap<ContentServer> contentServerMap;

	/**
	 * Session managers per content server
	 */
	private Map<IContentServer, IDfSessionManager> serverSessMans;

	/**
	 * Sessions per content server
	 */
	private Map<IContentServer, IDfSession> serverSessions;

	public DctmClient() {
		this.logger = Logger.getLogger(DctmClient.class);
		this.init();
	}

	private void init() {
		this.clientx = new DfClientX();
		this.hostsContentServers = new HashMap<>();
		this.docbrokerMap = new ModelMap<>();
		this.repoMap = new ModelMap<>();
		this.contentServerMap = new ModelMap<>();
		this.serverSessMans = new HashMap<>();
		this.serverSessions = new HashMap<>();
	}

	public static synchronized DctmClient getInstance() {
		if (instance == null) {
			instance = new DctmClient();
		}
		return instance;
	}

	/**
	 * Initialize the DFC client if it's not already initialized
	 * @throws DfException
	 */
	private synchronized void initDFC() throws DfException {
		if (this.client == null) {
			this.client = this.clientx.getLocalClient();
			this.docbrokerClient = this.clientx.getDocbrokerClient();
		}
	}

	public IDfClient getClient() throws DfException {
		initDFC();
		return this.client;
	}

	/**
	 * Get a known server by its hostname from a cached table. If we don't know
	 * of this server yet, a new one will be created and cached.
	 *
	 * @param hostname
	 *            The full hostname of the server, such as server1.company.org
	 * @param name
	 *            The short name of the server, such as server1
	 * @param contentServer
	 *            True if the server is a content server
	 * @return An instance of IServer of IContentServer
	 * @throws DfException
	 */
	private ContentServer getCreateContentServerByHostname(String hostname, String name)
			throws DfException {
		if (hostname == null)
			return null;
		if (name == null) {
			name = StringUtils.substringBefore(hostname, ".");
		}
		ContentServer server = this.hostsContentServers.get(hostname);
		if (server == null) {
			server = new ContentServer(null, name, hostname, -1, null);
			this.hostsContentServers.put(hostname, server);
		}
		return server;
	}

	/**
	 * Get a map of docbrokers known to this Documentum client
	 *
	 * @return A {@link ModelMap<Docbroker>} of docbrokers
	 * @throws DfException
	 */
	public ModelMap<Docbroker> getDocbrokerMap() throws DfException {
		initDFC();
		logger.debug("Getting docbroker map");
		ModelMap<Docbroker> docbrokerMap = new ModelMap<>();
		DocbrokerMap dfDocbrokerMap = (DocbrokerMap) this.docbrokerClient.getDocbrokerMap();
		for (int x = 0; x < dfDocbrokerMap.getDocbrokerCount(); x++) {
			String attrName = dfDocbrokerMap.getAttr(x).getName();
			String attrVals = dfDocbrokerMap.getAllRepeatingStrings(attrName, ",");
			logger.debug("  [" + x + "] " + attrName + ": " + attrVals);
			ContentServer server = getCreateContentServerByHostname(dfDocbrokerMap.getHostName(x), null);
			Docbroker docbroker = new Docbroker(server, dfDocbrokerMap.getPortNumber(x),
					dfDocbrokerMap.getSecureConnectMode(x));
			docbrokerMap.put(docbroker);
		}
		return docbrokerMap;
	}

	/**
	 * Get a map of docbrokers on a specific host (known to this Documentum
	 * client)
	 *
	 * @param hostname
	 *            The hostname of the server
	 * @return A {@link ModelMap<Docbroker>} of docbrokers
	 * @throws DfException
	 */
	public ModelMap<Docbroker> getDocbrokerMap(String hostname) throws DfException {
		initDFC();
		logger.debug("Getting docbroker map for server " + hostname);
		ModelMap<Docbroker> docbrokerMap = new ModelMap<>();
		DocbrokerMap dfDocbrokerMap = (DocbrokerMap) this.docbrokerClient.getDocbrokerMap();
		for (int x = 0; x < dfDocbrokerMap.getDocbrokerCount(); x++) {
			String docbrokerHostname = dfDocbrokerMap.getHostName(x);
			if (docbrokerHostname == null || !docbrokerHostname.equalsIgnoreCase(hostname)) {
				// Continue if this docbroker is not on the requested host
				continue;
			}
			String attrName = dfDocbrokerMap.getAttr(x).getName();
			String attrVals = dfDocbrokerMap.getAllRepeatingStrings(attrName, ",");
			logger.debug("  [" + x + "] " + attrName + ": " + attrVals);
			ContentServer server = getCreateContentServerByHostname(hostname, null);
			Docbroker docbroker = createOrUpdateDocbroker(server, dfDocbrokerMap, x);
			docbrokerMap.put(docbroker);
		}
		return docbrokerMap;
	}

	private Docbroker createOrUpdateDocbroker(ContentServer contentServer, DocbrokerMap docbrokerMap, int index)
			throws DfException {
		String hostname = docbrokerMap.getHostName(index);
		if (this.hostsContentServers == null || this.hostsContentServers.isEmpty()) {
			this.getContentServerMap();
		}
		if (contentServer == null)
			contentServer = getCreateContentServerByHostname(hostname, null);
		if (contentServer == null)
			return null;
		int portNr = docbrokerMap.getPortNumber(index);
		String docbrokerId = Docbroker.getId(contentServer, portNr);
		Docbroker docbroker = this.docbrokerMap.get(docbrokerId);
		if (docbroker == null) {
			docbroker = new Docbroker(contentServer, docbrokerMap, index);
			this.docbrokerMap.put(docbroker);
		} else {
			docbroker.setServer(contentServer);
			docbroker.setPort(portNr);
			docbroker.setName(Docbroker.getName(contentServer, portNr));
			docbroker.setSecureConnectMode(docbrokerMap.getSecureConnectMode(index));
		}
		return docbroker;
	}

	/**
	 * Get a map of all known repositories
	 *
	 * @return A {@link ModelMap} of repositories.
	 * @throws DfException
	 */
	public ModelMap<Repository> getRepositoryMap() throws DfException {
		logger.debug("Getting repository map");
		initDFC();
		IDfDocbaseMap dbm = this.docbrokerClient.getDocbaseMap();
		ModelMap<Repository> repoMap = getRepositoryMapFromDocbaseMap(dbm);
		logger.debug("Found " + this.repoMap.size() + " repositories");
		return repoMap;
	}

	/**
	 * Get a map of repositories known to a specific docbroker
	 *
	 * @return A {@link ModelMap} of repositories.
	 * @throws DfException
	 */
	public ModelMap<Repository> getRepositoryMap(IDocbroker docbroker) throws DfException {
		logger.debug("Getting repository map for docbroker " + docbroker.getName());
		initDFC();
		IDfDocbaseMap dbm = this.docbrokerClient.getDocbaseMapFromSpecificDocbroker(null,
				docbroker.getServer().getHostname(), Integer.toString(docbroker.getPort()));
		ModelMap<Repository> repoMap = getRepositoryMapFromDocbaseMap(dbm);
		logger.debug("Found " + this.repoMap.size() + " repositories");
		return repoMap;
	}

	public ModelMap<Repository> getRepositoryMapFromDocbaseMap(IDfDocbaseMap docbaseMap) throws DfException {
		ModelMap<Repository> repoMap = new ModelMap<>();
		for (int rx = 0; rx < docbaseMap.getDocbaseCount(); rx++) {
			logger.debug(" - " + docbaseMap.getDocbaseName(rx) + " (" + docbaseMap.getDocbaseDescription(rx) + ")");
			Repository repo = createOrUpdateRepository(docbaseMap, rx);
			repoMap.put(repo);
		}
		return repoMap;
	}

	private Repository createOrUpdateRepository(IDfDocbaseMap docbaseMap, int index) throws DfException {
		Repository repo = this.repoMap.get(docbaseMap.getDocbaseId(index));
		if (repo == null) {
			repo = new Repository(docbaseMap.getDocbaseId(index), docbaseMap.getDocbaseName(index),
					docbaseMap.getDocbaseDescription(index));
			this.repoMap.put(repo);
		} else {
			repo.setName(docbaseMap.getDocbaseName(index));
			repo.setDescription(docbaseMap.getDocbaseDescription(index));
		}
		return repo;
	}

	/**
	 * @return A list of known, cached, content servers (values from the content
	 *         server map)
	 * @throws DfException
	 */
	public Collection<ContentServer> getContentServers() throws DfException {
		return this.contentServerMap.values();
	}

	/**
	 * @return The map of all content servers known to the Documentum client
	 * @throws DfException
	 */
	public ModelMap<ContentServer> getContentServerMap() throws DfException {
		initDFC();
		logger.debug("Getting content server map");
		ModelMap<ContentServer> contentServerMap = new ModelMap<>();
		IDfDocbaseMap docbaseMap = this.docbrokerClient.getDocbaseMap();
		for (int x = 0; x < docbaseMap.getDocbaseCount(); x++) {
			IDfServerMap dfServerMap = (IDfServerMap) docbaseMap.getServerMap(x);
			Repository repository = createOrUpdateRepository(docbaseMap, x);
			ModelMap<ContentServer> repoContentServerMap = getContentServerMapFromServerMap(repository, dfServerMap);
			contentServerMap.putAll(repoContentServerMap);
		}
		return contentServerMap;
	}

	/**
	 * Get content servers for a specific repository
	 *
	 * @param repository
	 *            - An {@link IRepository}
	 * @return A {@link ModelMap} with {@link ContentServer} as values
	 */
	public ModelMap<ContentServer> getContentServerMap(IRepository repository) throws DfException {
		initDFC();
		logger.debug("Getting content server map for repository " + repository.getName());
		IDfServerMap dfServerMap = (IDfServerMap) this.docbrokerClient.getServerMap(repository.getName());
		ModelMap<ContentServer> serverMap = new ModelMap<>();
		List<ContentServer> servers = new ArrayList<>();
		for (int sx = 0; sx < dfServerMap.getServerCount(); sx++) {
			ContentServer server;
			server = new ContentServer(repository, dfServerMap, sx);
			logger.debug(" - " + server);
			servers.add(server);
			this.hostsContentServers.put(server.getHostname(), server);
		}
		return serverMap;
	}

	/**
	 * Get content servers for a specific repository
	 *
	 * @param repositoryName
	 *            - Name of the repository
	 * @return A {@link ModelMap} with {@link ContentServer} as values
	 */
	public ModelMap<ContentServer> getContentServerMap(String repositoryName) throws DfException {
		initDFC();
		logger.debug("Getting content server map for repository " + repositoryName);
		IDfServerMap dfServerMap = (IDfServerMap) this.docbrokerClient.getServerMap(repositoryName);
		ModelMap<Repository> repoMap = getRepositoryMap();
		Repository repository = repoMap.get(repositoryName);
		if (repository == null) {
			logger.warn("Unknown repository " + repositoryName);
			return null;
		}
		ModelMap<ContentServer> serverMap = new ModelMap<>();
		for (int sx = 0; sx < dfServerMap.getServerCount(); sx++) {
			ContentServer server = createOrUpdateContentServer(repository, dfServerMap, sx);
			serverMap.put(server);
		}
		return serverMap;
	}

	public ModelMap<ContentServer> getContentServerMapFromServerMap(IRepository repository, IDfServerMap dfServerMap)
			throws DfException {
		ModelMap<ContentServer> serverMap = new ModelMap<>();
		for (int sx = 0; sx < dfServerMap.getServerCount(); sx++) {
			ContentServer server = createOrUpdateContentServer(repository, dfServerMap, sx);
			serverMap.put(server);
		}
		return serverMap;
	}

	private ContentServer createOrUpdateContentServer(IRepository repository, IDfServerMap dfServerMap, int index)
			throws DfException {
		ContentServer server = this.contentServerMap.get(dfServerMap.getServerName(index));
		if (server == null) {
			server = new ContentServer(repository, dfServerMap, index);
			this.contentServerMap.put(server);
		} else {
			server.update(dfServerMap.getHostName(index), dfServerMap.getClientProximity(index),
					dfServerMap.getLastStatus(index));
		}
		this.hostsContentServers.put(server.getHostname(), server);
		return server;
	}

	/**
	 * Create a Documentum login-info object using plain username/password
	 * strings
	 *
	 * @param username
	 * @param password
	 * @param domain
	 * @return A IDfLoginInfo interface
	 */
	public IDfLoginInfo createLoginInfo(String username, String password, String domain) {
		IDfLoginInfo loginInfo = this.clientx.getLoginInfo();
		loginInfo.setUser(username);
		loginInfo.setPassword(password);
		loginInfo.setDomain(domain);
		return loginInfo;
	}

	/**
	 * Create a Documentum login-info object using a IUserAccount
	 * @param account A useraccount object (implementing IUserAccount)
	 * @return A IDfLoginInfo interface
	 */
	public IDfLoginInfo createLoginInfo(IUserAccount account) {
		return createLoginInfo(account.getLoginName(), account.getPassword(), null);
	}

	/**
	 * Get a Documentum session manager for the given content server.
	 * This method calls {@link #getSessionManager(IContentServer, IUserAccount)} with a null account.
	 * @param cServer An IContentServer object specifying the content server for which to get the session manager.
	 * @return A IDfSessionManager interface. Note that this session manager may contain an identity, if this has earlier been set.
	 * @throws DfServiceException
	 */
	public synchronized IDfSessionManager getSessionManager(IContentServer cServer) throws DfServiceException {
		return getSessionManager(cServer, null);
	}

	/**
	 * Get a Documentum session manager for the given content server. Also, set
	 * an identity based on the given user account if this hasn't been done
	 * before.
	 *
	 * @param cServer
	 *            An IContentServer object specifying the content server for
	 *            which to get the session manager.
	 * @param account
	 *            The account for which to set a Documentum identity for the
	 *            session manager. If there already is a cached session manager
	 *            for this server and it already has an identity, this will NOT
	 *            replace it.
	 * @return
	 * @throws DfServiceException
	 */
	public synchronized IDfSessionManager getSessionManager(IContentServer cServer, IUserAccount account)
			throws DfServiceException {
		if (cServer==null) return null;
		IDfSessionManager serverSM = null;
		if (this.serverSessMans.containsKey(cServer)) {
			serverSM = this.serverSessMans.get(cServer);
		} else {
			serverSM = this.client.newSessionManager();
			this.serverSessMans.put(cServer, serverSM);
		}

		if (!serverSM.hasIdentity(cServer.getConnectionString()) && account!=null) {
			IDfLoginInfo loginInfo = createLoginInfo(account);
			serverSM.setIdentity(cServer.getConnectionString(), loginInfo);
		}
		return serverSM;
	}

	/**
	 * Get a Documentum session for the given content server.
	 * This calls {@link #getSession(IContentServer, IUserAccount)} with a null account. Therefore, this
	 * method only returns a session if credentials have been set for this server.
	 * @param cServer An IContentServer object
	 * @return An IDfSession interface, if a session exists or can be created
	 * @throws DfServiceException
	 */
	public synchronized IDfSession getSession(IContentServer cServer) throws DfServiceException {
		return this.getSession(cServer, null);
	}

	/**
	 * Get a Documentum session for the given content server.
	 * @param cServer An IContentServer object
	 * @param account An IUserAccount object
	 * @return An IDfSession interface, if a session exists or can be created
	 * @throws DfServiceException
	 */
	public synchronized IDfSession getSession(IContentServer cServer, IUserAccount account) throws DfServiceException {
		IDfSessionManager sesMan = getSessionManager(cServer, account);
		if (sesMan==null) return null;
		IDfSession session = sesMan.getSession(cServer.getConnectionString());
		this.serverSessions.put(cServer, session);
		return session;
	}

	/**
	 * Get a Documentum session for the given repository. The first found
	 * content server will be used to connect to.
	 *
	 * @param repository
	 *            An IRepository object
	 * @param account
	 *            An IUserAccount object
	 * @return An IDfSession interface, if a session exists or can be created
	 * @throws DfException
	 * @throws DfServiceException
	 */
	public synchronized IDfSession getSession(IRepository repository, IUserAccount account) throws DfException {
		ModelMap<ContentServer> servers = getContentServerMap(repository);
		if (servers == null || servers.isEmpty())
			return null;
		IDfSessionManager sesMan = getSessionManager(servers.get(0), account);
		if (sesMan == null)
			return null;
		IDfSession session = sesMan.getSession(servers.get(0).getConnectionString());
		this.serverSessions.put(servers.get(0), session);
		return session;
	}

	/**
	 * Release all current sessions for a repository
	 *
	 * @param repository
	 *            The repository for which to release the server sessions
	 * @param alsoDisconnect
	 *            {@code true} to also do a session disconnect
	 * @return The number sessions that have been released
	 * @throws DfException
	 */
	public synchronized int releaseSessions(IRepository repository, boolean alsoDisconnect) throws DfException {
		ModelMap<ContentServer> servers = getContentServerMap(repository);
		Set<IContentServer> disconnectedServers = new HashSet<>();

		int nrReleasedSessions = 0;
		try {
			for (ContentServer server : servers.values()) {
				IDfSession session = this.serverSessions.get(server);
				if (session == null) {
					logger.info("No session for server " + server);
					continue;
				}
				try {
					session.getSessionManager().release(session);
				} catch (DfRuntimeException re) {
					logger.warn("Could not release session", re);
				}
				nrReleasedSessions++;
				if (alsoDisconnect && session.isConnected()) {
					session.disconnect();
					disconnectedServers.add(server);
				}
			}
		} catch (DfException e) {
			logger.error("Error disconnecting session", e);
		} finally {
			for (IContentServer server : disconnectedServers) {
				this.serverSessions.remove(server);
			}
		}
		return nrReleasedSessions;
	}

	/**
	 * Release all current sessions
	 *
	 * @param alsoDisconnect
	 *            {@code true} to also do a session disconnect
	 * @return The number sessions that have been released
	 */
	public synchronized int releaseAllSessions(boolean alsoDisconnect) {
		Set<Entry<IContentServer, IDfSession>> serverSessions = this.serverSessions.entrySet();
		Set<IContentServer> disconnectedServers = new HashSet<>();
		int nrReleasedSessions = 0;
		try {
			for(Entry<IContentServer, IDfSession> serverSession:serverSessions) {
				IDfSession session = serverSession.getValue();
				try {
					session.getSessionManager().release(session);
				} catch (DfRuntimeException re) {
					logger.warn("Could not release session", re);
				}
				nrReleasedSessions++;
				if (alsoDisconnect && session.isConnected()) {
					session.disconnect();
					disconnectedServers.add(serverSession.getKey());
				}
			}
		} catch (DfException e) {
			logger.error("Error disconnecting session", e);
		} finally {
			for (IContentServer server:disconnectedServers) {
				this.serverSessions.remove(server);
			}
		}
		return nrReleasedSessions;
	}

	public IDfQuery getQuery(String dql) {
		IDfQuery query = this.clientx.getQuery();
		query.setDQL(dql);
		return query;
	}

	/**
	 * Get a query processor with all content servers as sources
	 * @param sessMan
	 * @param dql
	 * @return
	 * @throws DfException
	 */
	public IDfQueryProcessor getQueryProcessor(IDfSessionManager sessMan, String dql)
			throws DfException {
		initDFC();
		IDfSearchService searchSrv = this.client.newSearchService(sessMan, null);
		IDfQueryManager qMan = searchSrv.newQueryMgr();
		IDfPassThroughQuery pTQ = qMan.newPassThroughQuery(dql);
		ModelMap<ContentServer> csMap = this.getContentServerMap();
		for (IContentServer cs : csMap.values()) {
			pTQ.addSelectedSource(cs.getConnectionString());
		}
		IDfQueryProcessor qP = searchSrv.newQueryProcessor(pTQ, false);
		return qP;
	}

}
