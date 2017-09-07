package it.tooly.dctmclient.model;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.IDfAttr;
import com.documentum.fc.common.IDfValue;

public interface IDctmObject extends IRepositoryObject {
	public IDfAttr getAttr(int index);
	public IDfValue getAttrValue(int index);
	public String getAttrStringValue(int index);

	/**
	 * @return The typedObject
	 */
	public IDfTypedObject getTypedObject();

	/**
	 * @return The sysObject
	 */
	public IDfSysObject getSysObject();
}
