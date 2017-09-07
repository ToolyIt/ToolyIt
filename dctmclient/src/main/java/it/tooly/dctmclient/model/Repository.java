package it.tooly.dctmclient.model;

import com.documentum.fc.common.DfException;

import it.tooly.shared.model.AbstractModelObject;
import it.tooly.shared.model.IModelObject;

public class Repository extends AbstractModelObject implements IModelObject, IRepository {
	public static final String ATTR_DESCRIPTION = "description";

	/**
	 * Repository constructor. This also loads the content server map for this
	 * repository.
	 *
	 * @param id
	 *            docbase ID
	 * @param name
	 *            docbase name
	 * @param description
	 *            docbase description
	 * @throws DfException
	 */
	public Repository(String id, String name, String description) throws DfException {
		super(id, name);
		addStringAttribute(ATTR_DESCRIPTION, description);
	}

	public String getDescription() {
		return (String) getAttrValue(ATTR_DESCRIPTION);
	}

	public void setDescription(String description) {
		setAttrValue(ATTR_DESCRIPTION, description);
	}
}
