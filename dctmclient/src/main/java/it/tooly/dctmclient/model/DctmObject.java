package it.tooly.dctmclient.model;

import org.apache.log4j.Logger;

import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfValue;
import com.documentum.fc.common.IDfAttr;
import com.documentum.fc.common.IDfValue;

public class DctmObject extends AbstractObject implements IDctmObject {
	private static final Logger LOGGER = Logger.getLogger(DctmObject.class);
	protected IRepository repository;
	protected IDfTypedObject typedObject;
	
	protected DctmObject(String id) {
		super(id);
		this.repository = null;
	}

	protected DctmObject(IRepository repo, String id, String name) {
		super(id, name);
		this.repository = repo;
	}
	
	protected DctmObject(IRepository repo, String id, IDfTypedObject typedObject) throws DfException {
		super(id, typedObject.getValueAt(0).asString());
		this.repository = repo;
	}

	public IRepository getRepository() {
		return this.repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	/**
	 * @return The typedObject
	 */
	public IDfTypedObject getTypedObject() {
		return typedObject;
	}

	/**
	 * @param typedObject The typedObject to set
	 */
	public void setTypedObject(IDfTypedObject typedObject) {
		this.typedObject = typedObject;
	}
	
	public IDfValue getAttrValue(int index) {
		IDfValue value;
		try {
			value = this.typedObject==null?null:this.typedObject.getValueAt(index);
		} catch (DfException e) {
			LOGGER.error("Could not get value from typed object", e);
			value = new DfValue("-?-");
		}
		return value;
	}

	@Override
	public IDfAttr getAttr(int index) {
		IDfAttr attr;
		try {
			attr = this.typedObject==null?null:this.typedObject.getAttr(index);
		} catch (DfException e) {
			LOGGER.error("Could not get value from typed object", e);
			attr = null;
		}
		return attr;
	}

	@Override
	public String getAttrStringValue(int index) {
		return getAttrValue(index).asString();
	}

}
