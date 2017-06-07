package it.tooly.dctmclient.model;

import com.documentum.fc.common.IDfAttr;
import com.documentum.fc.common.IDfValue;

public interface IDctmObject extends IRepositoryObject {
	public IDfAttr getAttr(int index);
	public IDfValue getAttrValue(int index);
	public String getAttrStringValue(int index);
}
