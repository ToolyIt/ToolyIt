package it.tooly.dctmclient.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfValue;
import com.documentum.fc.common.IDfAttr;
import com.documentum.fc.common.IDfValue;

import it.tooly.dctmclient.DctmClient;
import it.tooly.shared.common.ToolyException;
import it.tooly.shared.model.AbstractModelContentObject;

public class DctmObject extends AbstractModelContentObject implements IDctmObject {
	private static final Logger LOGGER = Logger.getLogger(DctmObject.class);
	protected IRepository repository;
	protected IDfTypedObject typedObject;

	public DctmObject(String id) {
		super(id);
		this.repository = null;
	}

	public DctmObject(IRepository repo, String id, String name) {
		super(id, name);
		this.repository = repo;
	}

	public DctmObject(IDfTypedObject typedObject) throws DfException {
		super(typedObject.getObjectId().getId());
		if (typedObject.getObjectId().isNull()) {
			if (typedObject.hasAttr("r_object_id")) {
				this.id = typedObject.getString("r_object_id");
			} else if (typedObject.hasAttr("id")) {
				this.id = typedObject.getString("id");
			}
		}
		addAttrsFromTypedObject(typedObject);
		if (typedObject.hasAttr("object_name")) {
			this.setName(typedObject.getString("object_name"));
		} else if (typedObject.hasAttr("name")) {
			this.setName(typedObject.getString("name"));
		}
		long docbaseId = typedObject.getObjectId().getNumericDocbaseId();
		this.repository = DctmClient.getInstance().getRepositoryMap().get(docbaseId);
	}

	public DctmObject(IRepository repo, String id, IDfTypedObject typedObject) throws DfException {
		super(id != null ? id : typedObject.getObjectId().getId(), typedObject.getValueAt(0).asString());
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
				addAttribute(attr.getName(), AttrType.BOOLEAN, val.asBoolean());
				break;
			case IDfValue.DF_INTEGER:
				addAttribute(attr.getName(), AttrType.INTEGER, val.asInteger());
				break;
			case IDfValue.DF_ID:
				addAttribute(attr.getName(), AttrType.STRING, val.asId().toString());
				break;
			case IDfValue.DF_TIME:
				addAttribute(attr.getName(), AttrType.DATE, val.asTime().getDate());
				break;
			case IDfValue.DF_DOUBLE:
				addAttribute(attr.getName(), AttrType.DOUBLE, val.asDouble());
				break;
			case IDfValue.DF_STRING:

			default:
				addAttribute(attr.getName(), AttrType.STRING, val.asString());
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
	 * Return the internal Documentum object as a sysobject.
	 *
	 * @return The object, but only if this is a IDfSysObject, otherwise returns
	 *         null
	 */
	public IDfSysObject getSysObject() {
		if (typedObject instanceof IDfSysObject)
			return (IDfSysObject) typedObject;
		else
			return null;
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

	/*
	 * (non-Javadoc)
	 *
	 * @see it.tooly.shared.model.AbstractModelContentObject#hasContent()
	 */
	@Override
	public boolean hasContent() {
		if (typedObject != null && typedObject instanceof IDfSysObject) {
			try {
				return ((IDfSysObject) typedObject).getContentSize() > 0;
			} catch (DfException e) {
				LOGGER.warn("Error getting content size of object", e);
			}
		}
		return super.hasContent();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see it.tooly.shared.model.AbstractModelContentObject#getContent()
	 */
	@Override
	public InputStream getContent() throws ToolyException {
		if (typedObject != null && typedObject instanceof IDfSysObject) {
			try {
				return ((IDfSysObject) typedObject).getContent();
			} catch (DfException e) {
				throw new ToolyException("Could not get object content", e);
			}
		}
		return super.getContent();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see it.tooly.shared.model.AbstractModelContentObject#setContent(byte[])
	 */
	@Override
	public void setContent(byte[] content) throws ToolyException {
		if (typedObject != null && typedObject instanceof IDfSysObject) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				bos.write(content);
			} catch (IOException ioe) {
				throw new ToolyException(ioe);
			}
			try {
				((IDfSysObject) typedObject).setContent(bos);
			} catch (DfException e) {
				throw new ToolyException("Could not get object content", e);
			}
		}
	}

}
