package it.tooly.dctmclient.model;

import com.documentum.fc.common.DfException;

import it.tooly.shared.model.AbstractModelObject;
import it.tooly.shared.model.IModelObject;

public class Repository extends AbstractModelObject implements IModelObject, IRepository {

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
		addStringAttribute("description", description);
	}

	public String getDescription() {
		return (String) getAttrValue("description");
	}

	public void setDescription(String description) {
		setAttrValue("description", description);
	}
}
