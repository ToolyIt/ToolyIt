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
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfDocbaseMap;
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

	/**
	 * Known content servers with the hostname as key
	 */
	private Map<String, ContentServer> hostsContentServers;

	/**
	 * Known content servers per repository with the repository name as key
	 */
	private Map<String, List<ContentServer>> reposContentServers;

	/**
	 * Known repositories with the (short) repository name as key
	 */
	private ModelMap<Repository> repoMap;

	/**
	 * Known content servers with the (short) name as key
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
		this.reposContentServers = new HashMap<>();
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
			this.client = clientx.getLocalClient();
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

	public ModelMap<Docbroker> getDocbrokerMap() throws DfException {
		initDFC();
		ModelMap<Docbroker> docbrokerMap = new ModelMap<>();
		DocbrokerMap dfDocbrokerMap = (DocbrokerMap) this.client.getDocbrokerMap();
		logger.debug("DOCBROKERS");
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

	public ModelMap<Repository> getRepositoryMap() {
		return this.repoMap;
	}

	/**
	 * Load the repositories from the DFC configuration. This will also update
	 * the content server map.
	 *
	 * @return A {@link ModelMap} of repositories.
	 * @throws DfException
	 */
	public ModelMap<Repository> loadRepositoryMap() throws DfException {
		logger.debug("Loading repository map");
		initDFC();
		IDfDocbaseMap dbm = this.client.getDocbaseMap();
		for (int rx = 0; rx < dbm.getDocbaseCount(); rx++) {
			logger.debug(" - " + dbm.getDocbaseName(rx) + " (" + dbm.getDocbaseDescription(rx) + ")");
			Repository repo;
			repo = new Repository(dbm.getDocbaseId(rx), dbm.getDocbaseName(rx),
					dbm.getDocbaseDescription(rx));
			this.repoMap.put(dbm.getDocbaseName(rx), repo);
		}
		logger.debug("Found " + this.repoMap.size() + " repositories");
		return this.repoMap;
	}

	/**
	 * @return A list of known content servers (values from the content server map)
	 */
	public Collection<ContentServer> getContentServers() {
		return this.contentServerMap.values();
	}

	/**
	 * @return The map of known content servers
	 */
	public ModelMap<ContentServer> getContentServerMap() {
		return this.contentServerMap;
	}

	/**
	 * Get all content servers for the known repositories. This won't return
	 * anything if there are no kown repositories.
	 *
	 * @return A {@link Collection} of {@link ContentServer} objects.
	 * @throws DfException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public Collection<ContentServer> loadAllContentServers() throws DfException, InterruptedException, ExecutionException {
		initDFC();
		Collection<ContentServer> servers = new ArrayList<>();
		Collection<Repository> repoList = getRepositoryMap().values();
		for (Repository repo : repoList) {
			ModelMap<ContentServer> csMap = loadContentServerMap(repo);
			servers.addAll(csMap.values());
		}
		return servers;
	}

	/**
	 * Load all content servers, cache and return them
	 * @return A {@link ModelMap} with {@link ContentServer} as values
	 */
	public ModelMap<ContentServer> loadContentServerMap(IRepository repository) throws DfException {
		initDFC();
		IDfServerMap dfServerMap = (IDfServerMap) this.client.getServerMap(repository.getName());
		if (this.contentServerMap != null) {
			// Clear servers from known serverlist first
			for (Entry<String, ContentServer> csEntry : this.contentServerMap.entrySet()) {
				this.hostsContentServers.remove(csEntry.getValue().getHostname());
			}
		}
		this.contentServerMap = new ModelMap<>();
		logger.debug("DCTMCLIENT - CONTENT SERVERS FOR REPOSITORY " + repository.getName());
		List<ContentServer> servers = new ArrayList<>();
		for (int sx = 0; sx < dfServerMap.getServerCount(); sx++) {
			ContentServer server;
			server = new ContentServer(repository, dfServerMap, sx);
			logger.debug(" - " + server);
			servers.add(server);
			this.contentServerMap.put(server);
			this.hostsContentServers.put(server.getHostname(), server);
		}
		this.reposContentServers.put(repository.getName(), servers);
		return this.contentServerMap;
	}

	/**
	 * Create a Documentum login-info object using plain username/password strings
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
		List<ContentServer> servers;
		if (this.reposContentServers == null || this.reposContentServers.isEmpty()) {
			ModelMap<ContentServer> serverMap = loadContentServerMap(repository);
			servers = new ArrayList<>(serverMap.values());
		} else {
			servers = this.reposContentServers.get(repository.getName());
		}
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
	 */
	public synchronized int releaseSessions(IRepository repository, boolean alsoDisconnect) {
		List<ContentServer> servers = this.reposContentServers.get(repository.getName());
		Set<IContentServer> disconnectedServers = new HashSet<>();

		int nrReleasedSessions = 0;
		try {
			for (ContentServer server : servers) {
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
