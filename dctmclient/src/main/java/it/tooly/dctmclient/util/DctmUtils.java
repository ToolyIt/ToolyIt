package it.tooly.dctmclient.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfAuditTrailManager;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfList;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfAttr;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfLoginInfo;
import com.documentum.fc.common.IDfTime;
import com.documentum.fc.common.IDfValue;

import it.tooly.shared.util.FileUtils;
import it.tooly.shared.util.StringUtils;

/**
 * The Class DctmUtils.
 *
 * @author Darko Sarkanovic
 * @author Matthijs de Boer
 */
public class DctmUtils {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(DctmUtils.class);

	/**
	 * The Interface ICollectionInitiable.
	 */
	public interface ICollectionInitiable {

		/**
		 * Inits the from collection.
		 *
		 * @param collection
		 *            the collection
		 * @throws DfException
		 *             the df exception
		 */
		void initFromCollection(IDfCollection collection) throws DfException;
	}

	/** The Constant DATE_FORMAT_DOCUMENTUM. */
	public static final SimpleDateFormat DATE_FORMAT_DOCUMENTUM = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	/** The Constant KNOWN_DATE_FORMATS. */
	private static final String[] KNOWN_DATE_FORMATS = { "dd-MM-yyyy hh:mm:ss", "dd-MM-yyyy", "yyyyMMdd HHmmss" };

	/** The Constant STRING_TO_INT_IGNORED_CHARACTERS. */
	private static final String[] STRING_TO_INT_IGNORED_CHARACTERS = { "\\.", "-", ",.*" };

	/** The Constant STRING_TO_DOUBLE_IGNORED_CHARACTERS. */
	private static final String[] STRING_TO_DOUBLE_IGNORED_CHARACTERS = { "\\.", "-" };

	/**
	 * Append object attribute repeating value.
	 *
	 * @param obj
	 *            the obj
	 * @param attrName
	 *            the attr name
	 * @param value
	 *            the value
	 * @param truncLengthIfLonger
	 *            the trunc length if longer
	 * @throws DfException
	 *             the df exception
	 */
	protected static void appendObjectAttributeRepeatingValue(final IDfTypedObject obj, final String attrName, final Object value, final boolean truncLengthIfLonger)
			throws DfException {

		int attrType = obj.getAttrDataType(attrName);

		switch (attrType) {
		case 0: // '\0'
			obj.appendBoolean(attrName, getValueAsBoolean(value));
			break;

		case 1: // '\001'
			obj.appendInt(attrName, getValueAsInt(value));
			break;

		case 2: // '\002'
			if (truncLengthIfLonger) {
				IDfAttr attrInfo = getAttributeTypeDefinition(obj, attrName);
				int maxLength = attrInfo.getLength();
				obj.appendString(attrName, getValueAsString(value, maxLength));
			} else {
				obj.appendString(attrName, getValueAsString(value));
			}
			break;

		case 3: // '\003'
			obj.appendId(attrName, getValueAsDfId(value));
			break;

		case 4: // '\004'
			obj.appendTime(attrName, getValueAsDfTime(value));
			break;

		case 5: // '\005'
			obj.appendDouble(attrName, getValueAsDouble(value));
			break;
		}
	}

	/**
	 * Close collection.
	 *
	 * @param collection
	 *            the collection
	 */
	public static void closeCollection(final IDfCollection collection) {
		if (collection != null) {
			try {
				collection.close();
			} catch (DfException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Connect to docbase.
	 *
	 * @param docbaseName
	 *            the docbase name
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return the i df session
	 * @throws DfException
	 *             the df exception
	 */
	public static IDfSession connectToDocbase(final String docbaseName, final String username, final String password) throws DfException {
		IDfClient dfClient = DfClient.getLocalClient();
		if (dfClient == null) {
			throw new DfException("Exception initialising session manager. dfClient is null.");
		}
		IDfLoginInfo li = new DfLoginInfo();
		li.setUser(username);
		li.setPassword(password);

		IDfSessionManager sessionManager = dfClient.newSessionManager();
		sessionManager.setIdentity(docbaseName, li);
		return sessionManager.getSession(docbaseName);
	}

	/**
	 * Converts idf value to a java object that is suitable for the value (e.g.
	 * String, Integer, Double or Date).
	 *
	 * @param dfValue
	 *            the df value
	 * @return the object
	 */
	public static Object convertIdfValueToJavaObject(final IDfValue dfValue) {
		if (dfValue != null) {
			if (dfValue.getDataType() == IDfValue.DF_STRING) {
				return dfValue.asString();
			} else if (dfValue.getDataType() == IDfValue.DF_DOUBLE) {
				return new Double(dfValue.asDouble());
			} else if (dfValue.getDataType() == IDfValue.DF_ID) {
				return dfValue.asId().getId();
			} else if (dfValue.getDataType() == IDfValue.DF_INTEGER) {
				return new Integer(dfValue.asInteger());
			} else if (dfValue.getDataType() == IDfValue.DF_BOOLEAN) {
				return new Boolean(dfValue.asBoolean());
			} else if (dfValue.getDataType() == IDfValue.DF_TIME) {
				return dfValue.asTime().getDate();
			} else if (dfValue.getDataType() == IDfValue.DF_UNDEFINED) {
				return dfValue.asString();
			}
		}
		return null;
	}

	/**
	 * Convert a list of PersistentObjects to a list of SysObjects. Same as
	 * {@link #convertPersistent2SysObjects(List, boolean)} but this method
	 * doesn't throw an Exception.
	 *
	 * @param poList
	 *            the po list
	 * @return the list
	 * @see {@link DctmUtils#convertPersistent2SysObjects(List, boolean)}
	 */
	public static List<IDfSysObject> convertPersistent2SysObjects(List<IDfPersistentObject> poList) {
		try {
			return convertPersistent2SysObjects(poList, false);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	/**
	 * Convert a list of PersistentObjects to a list of SysObjects. This only
	 * works if the runtime type of the objects in the list actually are
	 * {@link IDfSysObject}.
	 *
	 * @param poList
	 *            - The list of {@link IDfPersistentObject} objects
	 * @param failOnWrongType
	 *            - {@code boolean}. If true, the operation fails if an object
	 *            in the given list is not of the type IDfSysObject. If false,
	 *            these objects will be skipped, potentially returning a smaller
	 *            list.
	 * @return A list with the same objects (or a subset), as IDfSysObject
	 * @throws Exception
	 *             When the given list contains one ore more objects not of the
	 *             type {@link IDfSysObject} and {@code failOnWrongType} is
	 *             {@literal true}.
	 */
	public static List<IDfSysObject> convertPersistent2SysObjects(List<IDfPersistentObject> poList, boolean failOnWrongType) throws Exception {
		List<IDfSysObject> soList = new ArrayList<>(poList.size());
		for (IDfPersistentObject obj : poList) {
			if (obj instanceof IDfSysObject) {
				soList.add((IDfSysObject) obj);
			} else if (failOnWrongType) {
				throw new Exception("One or more objects are not of type IDfSysObject");
			}
		}
		return soList;
	}

	/**
	 * Creates the folder path.
	 *
	 * @param dfSession
	 *            the df session
	 * @param folderPath
	 *            the folder path
	 * @param folderObjectType
	 *            the folder object type
	 * @return the string
	 * @throws DfException
	 *             the df exception
	 */
	public static String createFolderPath(IDfSession dfSession, String folderPath, String folderObjectType) throws DfException {
		return createFolderPath(dfSession, folderPath, folderObjectType, null, null);
	}

	/**
	 * Creates the folder path.
	 *
	 * @param dfSession
	 *            the df session
	 * @param folderPath
	 *            the folder path
	 * @param folderObjectType
	 *            the folder object type
	 * @param aclName
	 *            the acl name
	 * @param aclDomain
	 *            the acl domain
	 * @return the string
	 * @throws DfException
	 *             the df exception
	 */
	public static String createFolderPath(IDfSession dfSession, String folderPath, String folderObjectType, String aclName, String aclDomain) throws DfException {
		if (StringUtils.isNotBlank(folderPath)) {

			if (!folderPath.startsWith("/")) {
				throw new DfException("Bad folder path '" + folderPath + "'! Folder path MUST start with slash (/).");
			}
			if (folderPath.endsWith("/")) {
				throw new DfException("Bad folder path '" + folderPath + "'! Folder path MUST NOT end with slash (/).");
			}

			// Check if the path already exists
			String folderId = executeQueryReturnFirstString(dfSession, "SELECT r_object_id FROM dm_folder WHERE any r_folder_path = '" + folderPath + "'");

			// If folder path exists than return
			if (StringUtils.isNotBlank(folderId)) {
				return folderId;
			}

			String parentPath = folderPath.substring(0, folderPath.lastIndexOf("/"));
			if (parentPath != null && parentPath.length() > 1) {
				if (createFolderPath(dfSession, parentPath, folderObjectType, aclName, aclDomain) != null) {

					String folderName = folderPath.substring(folderPath.lastIndexOf("/") + 1);

					if (StringUtils.isNotBlank(folderName)) {

						StringBuffer dql = new StringBuffer();
						dql.append("CREATE ").append(folderObjectType).append(" OBJECT ");
						dql.append("SET object_name='").append(folderName).append("' ");

						if (StringUtils.isNotBlank(aclName)) {
							dql.append("SET acl_name='").append(aclName).append("' ");

							if (StringUtils.isNotBlank(aclDomain)) {
								dql.append("SET acl_domain='").append(aclDomain).append("' ");
							} else {
								dql.append("SET acl_domain='dm_dbo' ");

							}
						}

						dql.append("LINK '").append(parentPath).append("' ");

						return executeQueryReturnFirstString(dfSession, dql.toString());

					}
				}
			}

		}

		return null;
	}

	/**
	 * Document exists in docbase.
	 *
	 * @param dfSession
	 *            the df session
	 * @param objectType
	 *            the object type
	 * @param folderPath
	 *            the folder path
	 * @param objectName
	 *            the object name
	 * @return true, if successful
	 * @throws DfException
	 *             the df exception
	 */
	public static boolean documentExists(final IDfSession dfSession, final String objectType, final String folderPath, final String objectName) throws DfException {
		String dql = "SELECT r_object_id FROM " + objectType + " WHERE object_name = '" + DctmUtils.encodeForDql(objectName)
				+ "'  AND ANY i_folder_id IN (SELECT r_object_id FROM dm_folder WHERE ANY r_folder_path='" + DctmUtils.encodeForDql(folderPath) + "')";

		String objectId = DctmUtils.executeQueryReturnFirstString(dfSession, dql);
		return objectId != null && !DfId.DF_NULLID_STR.equalsIgnoreCase(objectId);
	}

	/**
	 * Escapew single quote to double single quote for use in DQL. Already
	 * existing double single quotes are preserved.
	 *
	 * @param string
	 *            the string
	 * @return the string
	 */
	public static String encodeForDql(final String string) {
		if (string != null) {
			String newStr = string;
			newStr = newStr.replaceAll("''", "-------QuotQuot------");
			newStr = newStr.replaceAll("'", "''");
			newStr = newStr.replaceAll("-------QuotQuot------", "''");

			return newStr;
		}
		return null;
	}

	/**
	 * Ensure documentum folder path convention.<br>
	 * Replaces all "\\" to "/"
	 *
	 * @param path
	 *            the path
	 * @return the string
	 */
	public static String ensureDocumentumFolderPathConvention(final String path) {
		if (path != null) {
			String result = path.replaceAll("\\\\", "/");
			if (result.endsWith("/")) {
				result = result.substring(0, result.length() - 1);
			}
			return result;
		}
		return null;
	}

	/**
	 * Ensure name convention.
	 *
	 * @param value
	 *            the value
	 * @return the object
	 */
	public static String ensureObjectNameConvention(final String value) {
		if (StringUtils.isNotEmpty(value)) {
			String newValue = value.trim();
			newValue = newValue.replaceAll("/", " & ");

			// Truncate if longer than 255
			newValue = StringUtils.ensureUTF8StringLength(newValue, 255);
			return newValue.trim();
		}
		return value;
	}

	/**
	 * Execute non-reqult query (e.g. Create, Update DELETE etc.)
	 *
	 * @param session
	 *            the session
	 * @param dql
	 *            the dql
	 * @return the int
	 * @throws DfException
	 *             the df exception
	 */
	public static String executeNonResultsQuery(final IDfSession session, final String dql) throws DfException {
		IDfCollection collection = null;
		try {
			IDfQuery dfQuery = new DfQuery(dql);
			collection = dfQuery.execute(session, IDfQuery.DF_QUERY);
			if (collection != null && collection.next()) {
				return collection.getString(collection.getAttr(0).getName());
			}

			return null;
		} finally {
			if (collection != null) {
				try {
					collection.close();
				} catch (DfException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Executes DQL query.
	 *
	 * @param session
	 *            the session
	 * @param dql
	 *            the dql
	 * @return the i df collection
	 * @throws DfException
	 *             the df exception
	 */
	public static IDfCollection executeQuery(final IDfSession session, final String dql) throws DfException {
		IDfQuery dfQuery = new DfQuery(dql);
		return dfQuery.execute(session, IDfQuery.DF_QUERY);
	}

	/**
	 * Executes DQL query.
	 *
	 * @param session
	 *            the session
	 * @param dql
	 *            the dql
	 * @param queryType
	 *            the query type (e.g. IDfQuery.DF_QUERY)
	 * @return the i df collection
	 * @throws DfException
	 *             the df exception
	 */
	public static IDfCollection executeQuery(final IDfSession session, final String dql, final int queryType) throws DfException {
		IDfQuery dfQuery = new DfQuery(dql);
		return dfQuery.execute(session, queryType);
	}

	/**
	 * Execute query without resulting {@link IDfCollection}.
	 *
	 * @param session
	 *            the session
	 * @param string
	 *            the string
	 * @throws DfException
	 *             the df exception
	 */
	public static void executeQueryNoResult(final IDfSession session, final String string) throws DfException {
		String dql = "";
		IDfCollection collection = null;
		try {
			collection = executeQuery(session, dql);
		} finally {
			if (collection != null) {
				collection.close();
			}
		}
	}

	/**
	 * Execute query return object ids.
	 *
	 * @param dfSession
	 *            the df session
	 * @param dql
	 *            the dql
	 * @return the list
	 * @throws DfException
	 *             the df exception
	 */
	public static List<IDfId> executeQueryReturnDfIds(IDfSession dfSession, String dql) throws DfException {
		List<IDfId> result = new ArrayList<IDfId>();
		IDfCollection collection = null;
		try {
			collection = executeQuery(dfSession, dql);
			if (collection != null) {
				while (collection.next()) {
					IDfId objectId = collection.getId("r_object_id");
					if (objectId != null && !DfId.DF_NULLID.equals(objectId)) {
						result.add(objectId);
					}
				}
			}
			return result;
		} finally {
			if (collection != null) {
				collection.close();
			}
		}
	}

	/**
	 * Execute query return objects.
	 *
	 * @param dfSession
	 *            the Documentum session
	 * @param dql
	 *            the dql query
	 * @return A list of IDfPersistentObjects
	 * @throws DfException
	 *             the df exception
	 */
	public static List<IDfPersistentObject> executeQueryReturnDfObjects(IDfSession dfSession, String dql) throws DfException {
		return executeQueryReturnDfObjects(dfSession, dql, IDfPersistentObject.class);
	}

	/**
	 * Execute query return objects.
	 *
	 * @param <T>
	 *            the generic type
	 * @param dfSession
	 *            the Documentum session
	 * @param dql
	 *            - The dql query
	 * @param objType
	 *            - The type of objects to return in the list
	 * @return the list
	 * @throws DfException
	 *             the df exception
	 */
	public static <T extends IDfTypedObject> List<T> executeQueryReturnDfObjects(IDfSession dfSession, String dql, Class<T> objType) throws DfException {
		List<T> result = new ArrayList<>();
		IDfCollection collection = null;
		try {
			collection = executeQuery(dfSession, dql);
			if (collection == null) {
				return null;
			}
			while (collection.next()) {
				IDfId objectId = collection.getId("r_object_id");
				if (objectId != null && !DfId.DF_NULLID.equals(objectId)) {
					IDfPersistentObject pObj = dfSession.getObject(objectId);
					if (objType.isInstance(pObj)) {
						result.add(objType.cast(pObj));
					} else {
						LOGGER.info("Not returning object " + objectId.getId() + " because it's not a " + objType.getSimpleName());
					}
				}
			}
			return result;
		} finally {
			if (collection != null) {
				collection.close();
			}
		}
	}

	/**
	 * Executes given DQL query and returns a result in form of a Map where
	 * first column represents a key and second column represents a value.
	 *
	 * @param session
	 *            the session
	 * @param dql
	 *            the dql
	 * @return the map
	 * @throws DfException
	 *             the df exception
	 */
	public static Map<String, String> executeQueryReturnKeyValueMap(final IDfSession session, final String dql) throws DfException {
		Map<String, String> result = new HashMap<String, String>();

		IDfCollection collection = null;
		try {
			collection = executeQuery(session, dql);
			if (collection != null) {
				while (collection.next()) {
					String keyAttrName = collection.getAttr(0).getName();
					String valueAttrName = collection.getAttr(1).getName();

					if (collection.getAttr(1).isRepeating()) {
						result.put(collection.getString(keyAttrName), collection.getAllRepeatingStrings(valueAttrName, ","));
					} else {
						result.put(collection.getString(keyAttrName), collection.getString(valueAttrName));
					}
				}
			}
			return result;
		} finally {
			if (collection != null) {
				collection.close();
			}
		}
	}

	/**
	 * Execute query return list of maps where each map represents columns of
	 * one row.
	 *
	 * @param session
	 *            the session
	 * @param dql
	 *            the dql
	 * @return the list
	 * @throws DfException
	 *             the df exception
	 */
	public static List<Map<String, String>> executeQueryReturnListOfMaps(final IDfSession session, final String dql) throws DfException {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		IDfCollection collection = null;
		try {
			collection = executeQuery(session, dql);
			if (collection != null) {

				while (collection.next()) {
					Map<String, String> rowMap = getMapOfPropertiesFromCollection(collection);
					result.add(rowMap);
				}
			}
			return result;
		} finally {
			if (collection != null) {
				collection.close();
			}
		}
	}

	/**
	 * Voer een DQL query uit en geef het resultaat terug als lijst met daarin
	 * een map van attributen met waardes.
	 *
	 * @param s
	 *            de sessie
	 * @param dql
	 *            de query welke uitgevoerd moet worden (update/delete/select)
	 * @param batchSize
	 *            the maximum number of rows that can be returned to the server
	 *            in each call to the underlying RDBMS. See
	 *            {@link DfQuery#setBatchSize}. Only used when > 0.
	 * @return een lijst van resultaten indien een select is uitgevoerd
	 * @throws DfException
	 *             the df exception
	 */
	public static List<Map<String, List<IDfValue>>> executeQueryReturnListOfMapsDfValue(IDfSession s, String dql, int batchSize) throws DfException {
		IDfQuery query = new DfQuery(dql);
		if (batchSize > 0)
			query.setBatchSize(batchSize);
		IDfCollection collection = null;
		List<Map<String, List<IDfValue>>> rows = new ArrayList<Map<String, List<IDfValue>>>();
		try {
			collection = query.execute(s, IDfQuery.DF_EXEC_QUERY);
			while (collection.next()) {
				int nrOfCols = collection.getAttrCount();
				Map<String, List<IDfValue>> columns = new LinkedHashMap<String, List<IDfValue>>(nrOfCols);
				for (int x = 0; x < nrOfCols; x++) {
					List<IDfValue> colValues = new ArrayList<IDfValue>();
					String attrName = collection.getAttr(x).getName();
					int nrValues = collection.getValueCount(attrName);
					for (int y = 0; y < nrValues; y++) {
						IDfValue val = collection.getRepeatingValue(attrName, y);
						colValues.add(val);
					}
					columns.put(attrName, colValues);
				}
				rows.add(columns);
			}
		} finally {
			if (collection != null) {
				collection.close();
			}
		}
		return rows;
	}

	/**
	 * Executes DQL query and returns a Map of Maps.
	 *
	 * @param session
	 *            the session
	 * @param dql
	 *            the dql
	 * @param keyAttribute
	 *            the key attribute
	 * @return the list
	 * @throws DfException
	 *             the df exception
	 */
	public static Map<String, Map<String, String>> executeQueryReturnMaps(final IDfSession session, final String dql, String keyAttribute) throws DfException {
		Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();

		IDfCollection collection = null;
		try {
			collection = executeQuery(session, dql);
			if (collection != null) {

				while (collection.next()) {
					Map<String, String> rowMap = getMapOfPropertiesFromCollection(collection);
					result.put(rowMap.get(keyAttribute), rowMap);
				}
			}
			return result;
		} finally {
			if (collection != null) {
				collection.close();
			}
		}
	}

	/**
	 * Execute query return objects.
	 *
	 * @param <E>
	 *            the element type
	 * @param dfSession
	 *            the df session
	 * @param dql
	 *            the dql
	 * @param classDefintion
	 *            the class defintion
	 * @return the list
	 * @throws DfException
	 *             the df exception
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 */
	public static <E extends ICollectionInitiable> List<E> executeQueryReturnObjects(IDfSession dfSession, String dql, Class<E> classDefintion) throws DfException,
			InstantiationException, IllegalAccessException {

		List<E> result = new ArrayList<E>();

		IDfCollection collection = null;
		try {
			collection = executeQuery(dfSession, dql);
			if (collection != null) {
				while (collection.next()) {

					E eObj = classDefintion.newInstance();
					eObj.initFromCollection(collection);
					result.add(eObj);
				}
			}
			return result;
		} finally {
			if (collection != null) {
				collection.close();
			}
		}
	}

	/**
	 * Execute query return printable string arrays.
	 *
	 * @param session
	 *            the session
	 * @param dql
	 *            the dql
	 * @param maxColumnWidth
	 *            the max column width
	 * @return the list
	 * @throws DfException
	 *             the df exception
	 */
	public static List<String[]> executeQueryReturnPrintableStringArrays(final IDfSession session, final String dql, int maxColumnWidth) throws DfException {
		List<String[]> result = new ArrayList<String[]>();
		String[] columnNames = null;
		int[] columnLengths = null;

		IDfCollection collection = null;
		boolean isFirst = true;
		try {
			collection = executeQuery(session, dql);
			if (collection != null) {

				while (collection.next()) {
					if (isFirst) {
						columnNames = new String[collection.getAttrCount()];
						columnLengths = new int[collection.getAttrCount()];

						for (int i = 0; i < collection.getAttrCount(); i++) {
							String value = collection.getAttr(i).getName();
							columnNames[i] = value;

							// Calculate the max column width based on current value
							if (value.length() > columnLengths[i]) {
								if (value.length() > maxColumnWidth && maxColumnWidth > 0) {
									columnLengths[i] = maxColumnWidth;
								} else {
									columnLengths[i] = value.length();
								}
							}
						}
						result.add(columnNames);
						isFirst = false;
					}

					String[] values = new String[collection.getAttrCount()];
					for (int i = 0; i < collection.getAttrCount(); i++) {
						String attrName = collection.getAttr(i).getName();
						String value = null;
						if (collection.getAttr(i).isRepeating()) {
							value = collection.getAllRepeatingStrings(attrName, ",");
						} else {
							value = collection.getString(attrName);
						}
						values[i] = value;

						// Calculate the max column width based on current value
						if (value.length() > columnLengths[i]) {
							if (value.length() > maxColumnWidth && maxColumnWidth > 0) {
								columnLengths[i] = maxColumnWidth;
							} else {
								columnLengths[i] = value.length();
							}
						}

					}
					result.add(values);
				}

				// Lineup the column values by appending spaces to the values

				for (String[] row : result) {
					for (int i = 0; i < row.length; i++) {
						row[i] = rightAlignString(row[i], columnLengths[i]);
					}
				}

			}
			return result;
		} finally {
			if (collection != null) {
				collection.close();
			}
		}
	}

	/**
	 * Executes DQL query and returns the value of a first returned column.
	 *
	 * @param session
	 *            the session
	 * @param dql
	 *            the dql
	 * @return the singleton value
	 * @throws DfException
	 *             the df exception
	 * @deprecated Use {@link #executeQueryReturnFirstString} or
	 *             {@link #executeQueryReturnFirstValue}.
	 */
	public static String executeQueryReturnSingleton(final IDfSession session, final String dql) throws DfException {
		return executeQueryReturnFirstString(session, dql);
	}

	/**
	 * Executes DQL query and returns the value in the first column of the first
	 * returned row.
	 *
	 * @param session
	 *            the session
	 * @param dql
	 *            the dql
	 * @return the value, as a String
	 * @throws DfException
	 *             the df exception
	 */
	public static String executeQueryReturnFirstString(final IDfSession session, final String dql) throws DfException {
		IDfValue firstVal = executeQueryReturnFirstDfValue(session, dql);

		if (firstVal == null)
			return null;
		return firstVal.asString();
	}

	/**
	 * Executes DQL query and returns the value in the first column of the first
	 * returned row.
	 *
	 * @param session
	 *            the session
	 * @param dql
	 *            the dql
	 * @return the value, which could be a Boolean, Integer, Double, Date or
	 *         String
	 * @throws DfException
	 *             the df exception
	 */
	public static Object executeQueryReturnFirstValue(final IDfSession session, final String dql) throws DfException {
		IDfValue firstVal = executeQueryReturnFirstDfValue(session, dql);

		if (firstVal == null)
			return null;

		switch (firstVal.getDataType()) {
		case IDfValue.DF_BOOLEAN:
			return firstVal.asBoolean();
		case IDfValue.DF_INTEGER:
			return firstVal.asInteger();
		case IDfValue.DF_DOUBLE:
			return firstVal.asDouble();
		case IDfValue.DF_TIME:
			return firstVal.asTime().getDate();
		default:
			return firstVal.asString();
		}
	}

	/**
	 * Executes DQL query and returns the value in the first column of the first
	 * returned row.
	 *
	 * @param session
	 *            the session
	 * @param dql
	 *            the dql
	 * @return the value, as a IDfValue
	 * @throws DfException
	 *             the df exception
	 */
	public static IDfValue executeQueryReturnFirstDfValue(final IDfSession session, final String dql) throws DfException {
		IDfCollection collection = null;
		IDfValue firstVal = null;
		try {
			collection = executeQuery(session, dql);
			if (collection != null && collection.next()) {
				firstVal = collection.getValueAt(0);
			}
		} finally {
			closeCollection(collection);
		}
		return firstVal;
	}

	/**
	 * Executes DQL query and returns a list of String arrays.
	 *
	 * @param session
	 *            the session
	 * @param dql
	 *            the dql
	 * @return the list
	 * @throws DfException
	 *             the df exception
	 */
	public static List<String[]> executeQueryReturnStringArrays(final IDfSession session, final String dql) throws DfException {
		List<String[]> result = new ArrayList<String[]>();
		IDfCollection collection = null;
		try {
			collection = executeQuery(session, dql);
			if (collection != null) {
				while (collection.next()) {
					String[] values = new String[collection.getAttrCount()];
					for (int i = 0; i < collection.getAttrCount(); i++) {
						String attrName = collection.getAttr(i).getName();
						if (collection.getAttr(i).isRepeating()) {
							values[i] = collection.getAllRepeatingStrings(attrName, ",");
						} else {
							values[i] = collection.getString(attrName);
						}
					}
					result.add(values);
				}
			}
			return result;
		} finally {
			if (collection != null) {
				collection.close();
			}
		}
	}

	/**
	 * Executes DQL query and returns a list of values of a first returned
	 * column.
	 *
	 * @param session
	 *            the session
	 * @param dql
	 *            the dql
	 * @return the list
	 * @throws DfException
	 *             the df exception
	 */
	public static List<String> executeQueryReturnStrings(final IDfSession session, final String dql) throws DfException {
		List<String> result = new ArrayList<String>();
		IDfCollection collection = null;
		try {
			collection = executeQuery(session, dql);
			if (collection != null) {
				while (collection.next()) {
					result.add(collection.getString(collection.getAttr(0).getName()));
				}
			}
			return result;
		} finally {
			if (collection != null) {
				collection.close();
			}
		}
	}

	/**
	 * Execute server method.
	 *
	 * @param dfSession
	 *            the df session
	 * @param methodName
	 *            the method name
	 * @param executeAsync
	 *            the execute async
	 * @param parametersMap
	 *            the parameters map
	 * @throws DfException
	 *             the df exception
	 */
	public static void executeServerMethod(final IDfSession dfSession, final String methodName, final boolean executeAsync, Map<String, String> parametersMap) throws DfException {

		if (parametersMap == null) {
			parametersMap = new HashMap<String, String>();
		}
		putToMapIfNotPresent(parametersMap, "docbase_name", dfSession.getDocbaseName());
		putToMapIfNotPresent(parametersMap, "user_name", dfSession.getDocbaseOwnerName());

		IDfQuery query = new DfQuery();
		String methodCallDql = "EXECUTE do_method WITH METHOD='" + methodName + "', ARGUMENTS = ' " + mapToArgumentsString(parametersMap) + " ';";

		query.setDQL(methodCallDql);
		IDfCollection collection = null;

		try {
			collection = query.execute(dfSession, IDfQuery.DF_EXECREAD_QUERY);

			if (collection.next()) {
				int result = collection.getInt("result");
				int methodReturnVal = collection.getInt("method_return_val");
				boolean launchFailed = collection.getBoolean("launch_failed");
				boolean timedOut = collection.getBoolean("timed_out");
				String systemError = collection.getString("os_system_error");

				if (result != 0 || methodReturnVal != 0 || launchFailed || timedOut) {
					throw new DfException("Exception calling server method '" + methodName + "', result = " + result + ", methodReturnVal = " + methodReturnVal
							+ ", launch_failed = " + launchFailed + ", timedOut = " + timedOut + ", systemError = " + systemError);
				}
			}

		} finally {
			if (collection != null) {
				collection.close();
			}
		}
	}

	/**
	 * Fetch persist object list with session.
	 *
	 * @param session
	 *            - The session to use to get the objects from the repository
	 * @param objectList
	 *            - The list of persistent objects to re-fetch
	 * @param throwException
	 *            - Set to true to throw a DfException if it occurs or to false
	 *            to ignore it (which means you might get an empty list)
	 * @return A list of persistent with the given session
	 * @throws DfException
	 *             the df exception
	 */
	public static List<IDfPersistentObject> fetchPersistObjectListWithSession(IDfSession session, List<IDfPersistentObject> objectList, boolean throwException) throws DfException {
		List<IDfPersistentObject> newList = new ArrayList<>(objectList.size());
		for (IDfPersistentObject obj : objectList) {
			try {
				IDfPersistentObject fetchedObject = session.getObject(obj.getObjectId());
				newList.add(fetchedObject);
			} catch (DfException e) {
				if (throwException) {
					throw e;
				} else {
					LOGGER.error("Error while getting an object", e);
				}
			}
		}
		return newList;
	}

	/**
	 * Fetch sys object list with session.
	 *
	 * @param session
	 *            - The session to use to get the objects from the repository
	 * @param objectList
	 *            - The list of sysobjects to re-fetch
	 * @return A list of sysobjects with the given session
	 */
	public static List<IDfSysObject> fetchSysObjectListWithSession(IDfSession session, List<IDfSysObject> objectList) {
		List<IDfSysObject> newList = null;
		try {
			newList = fetchSysObjectListWithSession(session, objectList, false);
		} catch (DfException e) {
			LOGGER.error("Unexpected exception from nested method", e);
		}
		return newList;
	}

	/**
	 * Fetch sys object list with session.
	 *
	 * @param session
	 *            - The session to use to get the objects from the repository
	 * @param objectList
	 *            - The list of sysobjects to re-fetch
	 * @param throwException
	 *            - Set to true to throw a DfException if it occurs or to false
	 *            to ignore it (which means you might get an empty list)
	 * @return A list of sysobjects with the given session
	 * @throws DfException
	 *             the df exception
	 */
	public static List<IDfSysObject> fetchSysObjectListWithSession(IDfSession session, List<IDfSysObject> objectList, boolean throwException) throws DfException {
		List<IDfSysObject> newList = new ArrayList<>(objectList.size());
		for (IDfSysObject obj : objectList) {
			try {
				IDfSysObject fetchedObject = (IDfSysObject) session.getObject(obj.getObjectId());
				newList.add(fetchedObject);
			} catch (DfException e) {
				if (throwException) {
					throw e;
				} else {
					LOGGER.error("Error while getting an object", e);
				}
			}
		}
		return newList;
	}

	/**
	 * Gets the atribute type.
	 *
	 * @param typedObject
	 *            the typed object
	 * @param attrName
	 *            the attr name
	 * @return the atribute type
	 * @throws DfException
	 *             the df exception
	 */
	public static IDfAttr getAttributeTypeDefinition(final IDfTypedObject typedObject, final String attrName) throws DfException {
		if (typedObject != null && attrName != null && typedObject.hasAttr(attrName)) {
			for (int i = 0; i < typedObject.getAttrCount(); i++) {
				IDfAttr dfAttr = typedObject.getAttr(i);
				if (dfAttr.getName().endsWith(attrName)) {
					return dfAttr;
				}
			}
		}

		return null;
	}

	/**
	 * Get a list of child objects.
	 *
	 * @param object
	 *            the object
	 * @param relationName
	 *            the relation name
	 * @return the child objects
	 * @throws DfException
	 *             the df exception
	 * @see #getRelatedObjects(IDfPersistentObject, String, boolean)
	 */
	public static List<IDfPersistentObject> getChildObjects(IDfPersistentObject object, String relationName) throws DfException {
		return getRelatedObjects(object, relationName, true);
	}

	/**
	 * Gets the content.
	 *
	 * @param sysObject
	 *            the sys object
	 * @return the content
	 * @throws DfException
	 *             the df exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static File getContent(IDfSysObject sysObject) throws DfException, IOException {
		String filepath = sysObject.getFile(File.createTempFile("content", "").getAbsolutePath());
		return new File(filepath);
	}

	/**
	 * Gets the content as byte array.
	 *
	 * @param sysObject
	 *            the sys object
	 * @return the content as byte array
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws DfException
	 *             the df exception
	 */
	public static byte[] getContentAsByteArray(IDfSysObject sysObject) throws IOException, DfException {
		if (sysObject != null && sysObject.getLong("r_content_size") > 0) {
			return FileUtils.copyStreamToByteArray(sysObject.getContent());
		}

		return null;
	}

	/**
	 * Gets the content as string.
	 *
	 * @param sysObject
	 *            the sys object
	 * @return the content as string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws DfException
	 *             the df exception
	 */
	public static String getContentAsString(IDfSysObject sysObject) throws IOException, DfException {
		byte[] bytes = getContentAsByteArray(sysObject);
		if (bytes != null && bytes.length > 0) {
			return new String(bytes, "UTF-8");
		}
		return null;
	}

	/**
	 * Gets the document renditions.
	 *
	 * @param sysObject
	 *            the sys object
	 * @return the document renditions
	 * @throws DfException
	 *             the df exception
	 */
	public static List<String> getDocumentRenditions(final IDfSysObject sysObject) throws DfException {
		List<String> result = new ArrayList<String>();
		if (sysObject != null) {
			IDfCollection dfCollection = null;
			try {
				dfCollection = sysObject.getRenditions(null);
				while (dfCollection.next()) {
					result.add(dfCollection.getString("full_format"));
				}
			} finally {
				if (dfCollection != null) {
					dfCollection.close();
				}
			}
		}
		return result;
	}

	/**
	 * Gets the folder contents.
	 *
	 * @param dfSession
	 *            the df session
	 * @param folderId
	 *            the folder id
	 * @param objectType
	 *            the object type
	 * @return the folder contents
	 * @throws DfException
	 *             the df exception
	 */
	public static List<IDfSysObject> getFolderContents(final IDfSession dfSession, final String folderId, final String objectType) throws DfException {
		String dql = "SELECT r_object_id FROM " + (objectType != null && objectType.trim().length() > 0 ? objectType : "dm_sysobject") + " WHERE any i_folder_id = '" + folderId
				+ "'";

		List<String> objectIds = executeQueryReturnStrings(dfSession, dql);

		List<IDfSysObject> result = new ArrayList<IDfSysObject>();
		for (String objectId : objectIds) {
			result.add((IDfSysObject) dfSession.getObject(new DfId(objectId)));
		}
		return result;
	}

	/**
	 * Gets the map of properties from collection.
	 *
	 * @param collection
	 *            the collection
	 * @return the map of properties from collection
	 * @throws DfException
	 *             the df exception
	 */
	public static Map<String, String> getMapOfPropertiesFromCollection(IDfCollection collection) throws DfException {
		Map<String, String> result = new HashMap<String, String>();

		for (int i = 0; i < collection.getAttrCount(); i++) {
			String attrName = collection.getAttr(i).getName();
			if (collection.getAttr(i).isRepeating()) {
				result.put(attrName, collection.getAllRepeatingStrings(attrName, ","));
			} else {
				result.put(attrName, collection.getString(attrName));
			}
		}

		return result;
	}

	public static IDfPersistentObject getObjectByAttr(IDfSession session, String dctmObjectType, String attrName, String attrValue) throws DfException {
		List<IDfPersistentObject> objects = executeQueryReturnDfObjects(session, "select r_object_id from " + dctmObjectType + " where " + attrName + " = '" + attrValue + "'");
		if (objects == null || objects.isEmpty())
			return null;
		if (objects.size() > 1)
			LOGGER.info("Query returned more than 1 result. Returning first object.");
		return objects.get(0);
	}

	/**
	 * Returns all folders that the given object is linked to.
	 *
	 * @param obj
	 *            - The IDfSysObject to be examined
	 * @return List<IDfFolder> containing IDfFolder objects
	 * @throws DfException
	 *             the df exception
	 */
	public static List<IDfFolder> getObjectFolders(IDfSysObject obj) throws DfException {
		List<IDfFolder> folders = new LinkedList<>();
		for (int x = 0; x < obj.getFolderIdCount(); x++) {
			IDfFolder folder = obj.getObjectSession().getFolderBySpecification(obj.getFolderId(x).toString());
			if (folder != null)
				folders.add(folder);
		}
		return folders;
	}

	/**
	 * Returns all links (folder ID) that the given object is linked to.
	 *
	 * @param object
	 *            IDfSysObject to be checked
	 * @return List of folder IDs
	 * @throws DfException
	 *             if an error occurs
	 */
	public static List<String> getObjectLinks(final IDfSysObject object) throws DfException {
		List<String> links = new ArrayList<String>();
		for (int i = 0; i < object.getFolderIdCount(); i++) {
			links.add(object.getFolderId(i).getId());
		}
		return links;
	}

	/**
	 * Get a list of objects by their ids.
	 *
	 * @param session
	 *            - The Documentum session
	 * @param ids
	 *            - The objects ids
	 * @return A list of persistent objects
	 * @throws DfException
	 *             the df exception
	 * @see {@link IDfSession#getObject(IDfId)}
	 */
	public static List<IDfPersistentObject> getObjects(IDfSession session, List<IDfId> ids) throws DfException {
		List<IDfPersistentObject> objects = new ArrayList<>();
		for (IDfId id : ids) {
			IDfPersistentObject obj = session.getObject(id);
			objects.add(obj);
		}
		return objects;
	}

	/**
	 * Gets the sys objects.
	 *
	 * @param session
	 *            - The Documentum session
	 * @param ids
	 *            - The objects ids
	 * @return A list of sys objects
	 * @throws DfException
	 *             the df exception
	 */
	public static List<IDfSysObject> getSysObjects(IDfSession session, List<IDfId> ids) throws DfException {
		List<IDfSysObject> objects = new ArrayList<>();
		for (IDfId id : ids) {
			IDfPersistentObject obj = session.getObject(id);
			if (obj instanceof IDfSysObject) {
				objects.add((IDfSysObject) obj);
			}
		}
		return objects;
	}

	/**
	 * Get a list of parent objects.
	 *
	 * @param object
	 *            the object
	 * @param relationName
	 *            the relation name
	 * @return the parent objects
	 * @throws DfException
	 *             the df exception
	 * @see #getRelatedObjects(IDfPersistentObject, String, boolean)
	 */
	public static List<IDfPersistentObject> getParentObjects(IDfPersistentObject object, String relationName) throws DfException {
		return getRelatedObjects(object, relationName, false);
	}

	/**
	 * Get a list of related objects.
	 *
	 * @param object
	 *            - The object to get its relatives from
	 * @param relationName
	 *            - The name of the relation type
	 * @param getChildren
	 *            - true = Get children, false = Get parents
	 * @return ArrayList of IDfPersistentObject
	 * @throws DfException
	 *             the df exception
	 */
	public static List<IDfPersistentObject> getRelatedObjects(IDfPersistentObject object, String relationName, boolean getChildren) throws DfException {
		List<IDfPersistentObject> relatives = new ArrayList<>();
		IDfCollection relations;
		if (getChildren) {
			relations = object.getChildRelatives(relationName);
		} else {
			relations = object.getParentRelatives(relationName);
		}
		try {
			while (relations.next()) {
				IDfId relativeId = relations.getId(getChildren ? "child_id" : "parent_id");
				if (relativeId != null) {
					IDfPersistentObject relative = object.getSession().getObject(relativeId);
					if (relative != null) {
						relatives.add(relative);
					}
				}
			}
		} finally {
			DctmUtils.closeCollection(relations);
		}
		return relatives;
	}

	/**
	 * Returns the values of a repeating attribute as comma-separated string.
	 *
	 * @param sysObject
	 *            the sys object
	 * @param attributeName
	 *            the attribute name
	 * @return the repeating values as string list
	 * @throws DfException
	 *             the df exception
	 */
	public static String getRepeatingValuesAsString(final IDfSysObject sysObject, final String attributeName) throws DfException {
		StringBuffer result = new StringBuffer();
		if (sysObject != null && attributeName != null && sysObject.hasAttr(attributeName) && sysObject.isAttrRepeating(attributeName)) {
			for (int i = 0; i < sysObject.getValueCount(attributeName); i++) {
				result.append(sysObject.getRepeatingString(attributeName, i)).append(", ");
			}
		}

		return result.toString();
	}

	/**
	 * Gets the repeating values as string array.
	 *
	 * @param dfCollection
	 *            the df collection
	 * @param attributeName
	 *            the attribute name
	 * @return the repeating values as string array
	 * @throws DfException
	 *             the df exception
	 */
	public static String[] getRepeatingValuesAsStringArray(IDfCollection dfCollection, String attributeName) throws DfException {
		return getRepeatingValuesAsStringList(dfCollection, attributeName).toArray(new String[0]);
	}

	/**
	 * Returns the values of a repeating attribute as string array.
	 *
	 * @param sysObject
	 *            the sys object
	 * @param attributeName
	 *            the attribute name
	 * @return the repeating values as string array
	 * @throws DfException
	 *             the df exception
	 */
	public static String[] getRepeatingValuesAsStringArray(final IDfPersistentObject sysObject, final String attributeName) throws DfException {
		return getRepeatingValuesAsStringList(sysObject, attributeName).toArray(new String[] {});
	}

	/**
	 * Gets the repeating values as string list.
	 *
	 * @param dfCollection
	 *            the df collection
	 * @param attributeName
	 *            the attribute name
	 * @return the repeating values as string list
	 * @throws DfException
	 *             the df exception
	 */
	public static List<String> getRepeatingValuesAsStringList(IDfCollection dfCollection, String attributeName) throws DfException {
		List<String> result = new ArrayList<String>();

		if (dfCollection != null && attributeName != null && dfCollection.hasAttr(attributeName) && dfCollection.isAttrRepeating(attributeName)) {
			for (int i = 0; i < dfCollection.getValueCount(attributeName); i++) {
				result.add(dfCollection.getRepeatingString(attributeName, i));
			}
		}

		return result;
	}

	/**
	 * Returns the values of a repeating attribute as string list.
	 *
	 * @param sysObject
	 *            the sys object
	 * @param attributeName
	 *            the attribute name
	 * @return the repeating values as string list
	 * @throws DfException
	 *             the df exception
	 */
	public static List<String> getRepeatingValuesAsStringList(final IDfPersistentObject sysObject, final String attributeName) throws DfException {
		List<String> result = new ArrayList<String>();

		if (sysObject != null && attributeName != null && sysObject.hasAttr(attributeName) && sysObject.isAttrRepeating(attributeName)) {
			for (int i = 0; i < sysObject.getValueCount(attributeName); i++) {
				result.add(sysObject.getRepeatingString(attributeName, i));
			}
		}

		return result;
	}

	/**
	 * Gets the user privileges.
	 *
	 * @param session
	 *            the session
	 * @param userName
	 *            the user name
	 * @return the user privileges
	 * @throws DfException
	 *             the df exception
	 */
	public static int getUserPrivileges(IDfSession session, String userName) throws DfException {
		String userVal = org.apache.commons.lang.StringUtils.isBlank(userName) ? "USER" : ("'" + userName + "'");
		Integer privVal = (Integer) executeQueryReturnFirstValue(session, "select user_privileges from dm_user where user_name = " + userVal);
		int returnVal = privVal == null ? -1 : privVal.intValue();
		return returnVal;
	}

	/**
	 * Gets the value as boolean.
	 *
	 * @param value
	 *            the value
	 * @return the value as boolean
	 */
	public static boolean getValueAsBoolean(final Object value) {
		if (value != null) {
			if (value instanceof Boolean) {
				return (Boolean) value;
			} else if (value instanceof String) {
				String str = ((String) value).trim().toLowerCase();
				return str.trim().length() > 0 && ("true".equals(str) || "t".equals(str) || "1".equals(str) || "ja".equals(str) || "j".equals(str));
			} else if (value instanceof List) {
				List list = (List) value;
				if (list.size() > 0 && list.get(0) != null) {
					Object listValue = list.get(0);
					return getValueAsBoolean(listValue);
				}
			}
		}

		return false;
	}

	/**
	 * Gets the value as df id.
	 *
	 * @param value
	 *            the value
	 * @return the value as df id
	 */
	public static IDfId getValueAsDfId(final Object value) {
		if (value != null) {
			if (value instanceof IDfId) {
				return (IDfId) value;
			} else if (value instanceof String) {
				String str = ((String) value).trim().toLowerCase();
				return new DfId(str);
			} else if (value instanceof List) {
				List list = (List) value;
				if (list.size() > 0 && list.get(0) != null) {
					Object listValue = list.get(0);
					return getValueAsDfId(listValue);
				}
			}
		}

		return null;
	}

	/**
	 * Gets the value as df time.
	 *
	 * @param value
	 *            the value
	 * @return the value as df time
	 */
	public static IDfTime getValueAsDfTime(final Object value) {
		if (value != null) {
			if (value instanceof Date) {
				return new DfTime((Date) value);
			} else if (value instanceof String) {
				String str = ((String) value).trim().toLowerCase();
				Date date = parseDate(str, KNOWN_DATE_FORMATS);
				if (date != null) {
					return new DfTime(date);
				}
			} else if (value instanceof List) {
				List list = (List) value;
				if (list.size() > 0 && list.get(0) != null) {
					Object listValue = list.get(0);
					return getValueAsDfTime(listValue);
				}
			}
		}

		return null;
	}

	/**
	 * Gets the value as double.
	 *
	 * @param value
	 *            the value
	 * @return the value as double
	 */
	public static double getValueAsDouble(final Object value) {
		if (value != null) {
			if (value instanceof Double) {
				return (Double) value;
			} else if (value instanceof String) {
				String str = ((String) value).trim().toLowerCase();
				return StringUtils.parseDouble(str, -100, STRING_TO_DOUBLE_IGNORED_CHARACTERS);
				// return Double.parseDouble(str);
			} else if (value instanceof List) {
				List list = (List) value;
				if (list.size() > 0 && list.get(0) != null) {
					Object listValue = list.get(0);
					return getValueAsDouble(listValue);
				}
			}
		}

		return 0;
	}

	/**
	 * Gets the value as int.
	 *
	 * @param value
	 *            the value
	 * @return the value as int
	 */
	public static int getValueAsInt(final Object value) {
		if (value != null) {
			if (value instanceof Integer) {
				return (Integer) value;
			} else if (value instanceof String) {
				String str = ((String) value).trim().toLowerCase().replaceAll("\\%", "");
				return StringUtils.parseInt(str, 0, STRING_TO_INT_IGNORED_CHARACTERS);
				// return Integer.parseInt(str);

			} else if (value instanceof List) {
				List list = (List) value;
				if (list.size() > 0 && list.get(0) != null) {
					Object listValue = list.get(0);
					return getValueAsInt(listValue);
				}
			}
		}

		return 0;
	}

	/**
	 * Gets the value as string.
	 *
	 * @param value
	 *            the value
	 * @return the value as string
	 */
	public static String getValueAsString(final Object value) {
		return getValueAsString(value, 0);
	}

	/**
	 * Gets the value as string.
	 *
	 * @param value
	 *            the value
	 * @param maxLength
	 *            the max length
	 * @return the value as string
	 */
	public static String getValueAsString(final Object value, final int maxLength) {
		String result = null;
		if (value != null) {
			if (value instanceof String) {
				result = (String) value;
			} else if (value instanceof List) {
				List list = (List) value;
				if (list.size() > 0 && list.get(0) != null) {
					Object listValue = list.get(0);
					result = getValueAsString(listValue);
				}
			} else {
				result = value.toString();
			}
		}

		result = StringUtils.ensureUTF8StringLength(result, maxLength);

		return result;
	}

	/**
	 * Checks if audit event is registered for type.
	 *
	 * @param dfSession
	 *            the df session
	 * @param objectType
	 *            the object type
	 * @param auditEvent
	 *            the audit event
	 * @return true, if is audit event registered for type
	 * @throws DfException
	 *             the df exception
	 */
	public static boolean isAuditEventRegisteredForType(IDfSession dfSession, String objectType, String auditEvent) throws DfException {
		IDfAuditTrailManager auditTrailManager = dfSession.getAuditTrailManager();

		return auditTrailManager.isEventAuditedForType(objectType, auditEvent, null, null, null);

	}

	/**
	 * Load a text file into list. Each element of a list represents one row of
	 * text from the loaded file
	 *
	 * @param fileName
	 *            the f
	 * @return the list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static List<String> loadFileIntoList(final String fileName) throws IOException {
		String thisLine;
		final List<String> s = new ArrayList<String>();
		BufferedReader br = null;

		br = new BufferedReader(new FileReader(fileName));

		while ((thisLine = br.readLine()) != null) {
			s.add(thisLine.trim());
		}

		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return s;
	}

	/**
	 * Map to arguments string.
	 *
	 * @param map
	 *            the map
	 * @return the string
	 */
	private static String mapToArgumentsString(final Map map) {
		StringBuilder result = new StringBuilder();

		if (map != null) {
			for (Object paramName : map.keySet()) {
				result.append("-").append(paramName).append(" ").append(map.get(paramName)).append(" ");
			}
		}

		return result.toString();
	}

	/**
	 * Safely parses a date with given date formats.
	 *
	 * @param dateStr
	 *            the date str
	 * @param formats
	 *            the formats
	 * @return the date
	 */
	public static Date parseDate(final String dateStr, final String... formats) {
		if (dateStr != null && dateStr.trim().length() > 0 && formats != null && formats.length > 0) {
			for (String format : formats) {
				try {
					Date result = parseDateEx(dateStr, format);
					if (result != null) {
						return result;
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
				}
			}
		}
		return null;
	}

	/**
	 * Parses the date.
	 *
	 * @param dateStr
	 *            the date str
	 * @param format
	 *            the format
	 * @return the date
	 * @throws ParseException
	 *             the parse exception
	 */
	protected static Date parseDateEx(final String dateStr, final String format) throws ParseException {
		if (dateStr != null && dateStr.trim().length() > 0 && format != null && format.trim().length() > 0) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
			return simpleDateFormat.parse(dateStr);
		}

		return null;
	}

	/**
	 * Prepare a given string for use as a value in a DQL query.
	 * <p>
	 * Replaces single quotes with two single quotes
	 * </p>
	 *
	 * @param string
	 *            the string
	 * @return the string
	 */
	public static String prepareStringForDql(final String string) {
		if (string != null) {
			return string.replaceAll("'", "''");
		}
		return null;
	}

	/**
	 * Prints the object.
	 *
	 * @param sysObject
	 *            the sys object
	 * @throws DfException
	 *             the df exception
	 */
	public static void printObject(final IDfSysObject sysObject) throws DfException {
		if (sysObject != null) {
			System.out.println("r_object_id: " + sysObject.getObjectId().getId());
			System.out.println("object_name: " + sysObject.getObjectName());
			System.out.println("t_object_type: " + sysObject.getTypeName());

			for (int i = 0; i < sysObject.getAttrCount(); i++) {
				IDfAttr dfAttr = sysObject.getAttr(i);
				System.out.println(dfAttr.getName() + ": " + sysObject.getString(dfAttr.getName()));
			}
		}
	}

	/**
	 * Put to map if not present.
	 *
	 * @param map
	 *            the map
	 * @param paramName
	 *            the param name
	 * @param value
	 *            the value
	 */
	private static void putToMapIfNotPresent(final Map map, final String paramName, final Object value) {
		if (map != null && paramName != null && value != null && !map.containsKey(paramName)) {
			map.put(paramName, value);
		}
	}

	/**
	 * Register audit event on object type.
	 *
	 * @param dfSession
	 *            the df session
	 * @param objectType
	 *            the object type
	 * @param auditEvent
	 *            the audit event
	 * @param auditAttributes
	 *            the audit attributes
	 * @param auditSubTypes
	 *            the audit sub types
	 * @param signAuditEntries
	 *            the sign audit entries
	 * @param authenticate
	 *            the authenticate
	 * @param description
	 *            the description
	 * @throws DfException
	 *             the df exception
	 */
	public static void registerAuditEventForType(IDfSession dfSession, String objectType, String auditEvent, String[] auditAttributes, boolean auditSubTypes,
			boolean signAuditEntries, int authenticate, String description) throws DfException {

		IDfAuditTrailManager auditTrailManager = dfSession.getAuditTrailManager();

		auditTrailManager.registerEventForType(objectType, //
				auditEvent, //
				auditSubTypes, //
				null, // controllingApp
				null, // policyId
				null, // stateName
				signAuditEntries, //
				authenticate, //
				description,//
				new DfList(auditAttributes));

	}

	/**
	 * Release session.
	 *
	 * @param session
	 *            the session
	 */
	public static void releaseSession(final IDfSession session) {
		if (session != null) {
			session.getSessionManager().release(session);
		}
	}

	/**
	 * Removes the double quotes.
	 *
	 * @param string
	 *            the string
	 * @return the string
	 */
	public static String removeDoubleQuotes(final String string) {
		if (string != null) {
			return string.replaceAll("\"", "");
		}
		return null;
	}

	/**
	 * Right align string.
	 *
	 * @param string
	 *            the string
	 * @param length
	 *            the length
	 * @return the string
	 */
	private static String rightAlignString(String string, Integer length) {
		return String.format("%1$-" + length + "s", string);
	}

	/**
	 * Sets the object attribute.
	 *
	 * @param obj
	 *            the obj
	 * @param attrName
	 *            the attr name
	 * @param value
	 *            the value
	 * @param truncLengthIfLonger
	 *            the trunc length if longer
	 * @throws DfException
	 *             the df exception
	 */
	public static void setObjectAttribute(final IDfTypedObject obj, final String attrName, final Object value, final boolean truncLengthIfLonger) throws DfException {
		if (obj != null && attrName != null) {
			try {
				if (!obj.isAttrRepeating(attrName)) {
					setObjectAttributeSingleValue(obj, attrName, value, truncLengthIfLonger);
				} else {
					obj.removeAll(attrName);

					if (value != null) {
						if (value instanceof Collection) {
							Collection list = (Collection) value;
							for (Object listValue : list) {
								appendObjectAttributeRepeatingValue(obj, attrName, listValue, truncLengthIfLonger);
							}
						} else if (value instanceof Object[]) {
							Object[] array = (Object[]) value;
							for (Object arrayValue : array) {
								appendObjectAttributeRepeatingValue(obj, attrName, arrayValue, truncLengthIfLonger);
							}
						} else if (value instanceof Iterable) {
							Iterable iterable = (Iterable) value;
							for (Object listValue : iterable) {
								appendObjectAttributeRepeatingValue(obj, attrName, listValue, truncLengthIfLonger);
							}
						} else {
							appendObjectAttributeRepeatingValue(obj, attrName, value, truncLengthIfLonger);
						}

					} else {
						setObjectAttributeSingleValue(obj, attrName, (String) null, truncLengthIfLonger);
					}

				}

			} catch (Exception e) {
				throw new DfException("Error setting value '" + (value != null ? value.toString() : "NULL") + "' to attribute '" + attrName + "'" + e.getMessage(), e);
			}
		}
	}

	/**
	 * Sets the object attributes.
	 *
	 * @param obj
	 *            the obj
	 * @param attributes
	 *            the attributes
	 * @param truncLengthIfLonger
	 *            the trunc length if longer
	 * @throws DfException
	 *             the df exception
	 */
	public static void setObjectAttributes(final IDfTypedObject obj, final Map<String, Object> attributes, final boolean truncLengthIfLonger) throws DfException {
		if (obj != null && attributes != null) {
			for (String attrName : attributes.keySet()) {
				if (!attrName.startsWith("_")) {
					setObjectAttribute(obj, attrName, attributes.get(attrName), truncLengthIfLonger);
				}
			}
		}
	}

	/**
	 * Sets the object attribute single value.
	 *
	 * @param obj
	 *            the obj
	 * @param attrName
	 *            the attr name
	 * @param value
	 *            the value
	 * @param truncLengthIfLonger
	 *            the trunc length if longer
	 * @throws DfException
	 *             the df exception
	 */
	protected static void setObjectAttributeSingleValue(final IDfTypedObject obj, final String attrName, final Object value, final boolean truncLengthIfLonger) throws DfException {
		int attrType = obj.getAttrDataType(attrName);
		switch (attrType) {
		case 0: // Boolean
			obj.setBoolean(attrName, getValueAsBoolean(value));
			break;

		case 1: // Integer
			obj.setInt(attrName, getValueAsInt(value));
			break;

		case 2: // String
			if (truncLengthIfLonger) {
				IDfAttr attrInfo = getAttributeTypeDefinition(obj, attrName);
				int maxLength = attrInfo.getLength();
				obj.setString(attrName, getValueAsString(value, maxLength));
			} else {
				obj.setString(attrName, getValueAsString(value));
			}
			break;

		case 3: // Id
			obj.setId(attrName, getValueAsDfId(value));
			break;

		case 4: // Time
			obj.setTime(attrName, getValueAsDfTime(value));
			break;

		case 5: // Double
			obj.setDouble(attrName, getValueAsDouble(value));
			break;
		}
	}

	/**
	 * Unlinks object from given folder.
	 *
	 * @param object
	 *            the object
	 * @param path
	 *            the path
	 * @throws DfException
	 *             the df exception
	 */
	public static void unlinkObject(final IDfSysObject object, final String path) throws DfException {
		try {
			object.unlink(path);
		} catch (DfException e) {
			// TODO: handle exception
		}
	}

	/**
	 * Unlinks object from all folders.
	 *
	 * @param object
	 *            the object
	 * @throws DfException
	 *             the df exception
	 */
	public static void unlinkObjectFromAll(final IDfSysObject object) throws DfException {
		List<String> links = getObjectLinks(object);
		for (String path : links) {
			unlinkObject(object, path);
		}
	}

	/**
	 * Unregisters (if registered) given audit event for given object type .
	 *
	 * @param dfSession
	 *            the df session
	 * @param objectType
	 *            the object type
	 * @param auditEvent
	 *            the audit event
	 * @throws DfException
	 *             the df exception
	 */
	public static void unregisterAuditEventForType(IDfSession dfSession, String objectType, String auditEvent) throws DfException {
		IDfAuditTrailManager auditTrailManager = dfSession.getAuditTrailManager();

		if (auditTrailManager.isEventAuditedForType(objectType, auditEvent, null, null, null)) {
			auditTrailManager.unregisterEventForType(objectType, auditEvent, null, null, null);
		}

	}

	/**
	 * Creates the job request object (dm_job_request).
	 *
	 * @param dfSession
	 *            the df session
	 * @param requestName
	 *            the request name
	 * @param jobName
	 *            the job name
	 * @param methodName
	 *            the method name
	 * @param argumentsKeys
	 *            the arguments keys
	 * @param argumentsValues
	 *            the arguments values
	 * @param priority
	 *            the priority
	 * @return the string
	 * @throws DfException
	 *             the df exception
	 */
	public static String createJobRequest(IDfSession dfSession, String requestName, String jobName, String methodName, String[] argumentsKeys, String[] argumentsValues,
			int priority) throws DfException {

		IDfSysObject jobRequest = (IDfSysObject) dfSession.newObject("dm_job_request");
		jobRequest.setObjectName(requestName);
		jobRequest.setString("job_name", jobName);
		jobRequest.setString("method_name", methodName);
		jobRequest.setInt("priority", priority);
		jobRequest.setBoolean("request_completed", false);

		if (argumentsKeys != null && argumentsKeys.length > 0 && argumentsValues != null && argumentsValues.length > 0 && argumentsKeys.length == argumentsValues.length) {
			for (String key : argumentsKeys) {
				jobRequest.appendString("arguments_keys", key);
			}

			for (String value : argumentsValues) {
				jobRequest.appendString("arguments_values", value);
			}
		}

		jobRequest.save();
		jobRequest.fetch(null);

		return jobRequest.getObjectId().getId();

	}

	/**
	 * Gets the content type for given file extension.
	 *
	 * @param dfSession
	 *            the df session
	 * @param fileExtension
	 *            the file extension
	 * @return the content type for file extension
	 * @throws DfException
	 *             the df exception
	 */
	public static String getContentTypeForFileExtension(IDfSession dfSession, String fileExtension, String defaultContentType) throws DfException {
		String result = null;

		if (fileExtension != null && fileExtension.trim().length() > 0) {
			result = DctmUtils.executeQueryReturnFirstString(dfSession, "SELECT name FROM dm_format WHERE dos_extension = '" + fileExtension.toLowerCase() + "'");
		}

		if (result == null || result.trim().length() == 0) {
			result = defaultContentType;
		}

		return result;
	}

	/**
	 * Gets the content type for given mime type.
	 *
	 * @param dfSession
	 *            the df session
	 * @param mimeType
	 *            the mime type
	 * @return the content type for mime type
	 * @throws DfException
	 *             the df exception
	 */
	public static String getContentTypeForMIMEType(IDfSession dfSession, String mimeType, String defaultContentType) throws DfException {
		String result = null;

		if (mimeType != null && mimeType.trim().length() > 0) {
			result = DctmUtils
					.executeQueryReturnFirstString(dfSession, "SELECT name FROM dm_format WHERE mime_type = '" + mimeType.toLowerCase() + "' OR name ='" + mimeType + "'");
		}

		if (result == null || result.trim().length() == 0) {
			result = defaultContentType;
		}

		return result;
	}

	/**
	 * Hidden constructor of a utility class.
	 */
	private DctmUtils() {

	}
}
