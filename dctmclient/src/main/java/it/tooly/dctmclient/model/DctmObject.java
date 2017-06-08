package it.tooly.dctmclient.model;

import java.util.Date;

import org.apache.log4j.Logger;

import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfValue;
import com.documentum.fc.common.IDfAttr;
import com.documentum.fc.common.IDfValue;

import it.tooly.shared.model.AbstractModelObject;

public class DctmObject extends AbstractModelObject implements IDctmObject {
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
		addAttrsFromTypedObject(typedObject);
	}

	private void addAttrsFromTypedObject(IDfTypedObject typedObject) throws DfException {
		for (int x = 0; x < typedObject.getAttrCount(); x++) {
			IDfAttr attr = typedObject.getAttr(x);
			IDfValue val = typedObject.getValueAt(x);
			int dataType = val.getDataType();
			switch (dataType) {
			case IDfValue.DF_BOOLEAN:
				addAttribute(attr.getName(), Boolean.class, val.asBoolean());
			case IDfValue.DF_INTEGER:
				addAttribute(attr.getName(), Integer.class, val.asInteger());
			case IDfValue.DF_ID:
				addAttribute(attr.getName(), String.class, val.asId().toString());
			case IDfValue.DF_TIME:
				addAttribute(attr.getName(), Date.class, val.asTime().getDate());
			case IDfValue.DF_DOUBLE:
				addAttribute(attr.getName(), Double.class, val.asDouble());
			case IDfValue.DF_STRING:

			default:
				addAttribute(attr.getName(), String.class, val.asString());
			}

		}
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
