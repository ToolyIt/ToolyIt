package it.tooly.dctmclient.model;

import com.documentum.fc.common.DfException;

public class Repository extends AbstractObject implements IModelObject, IRepository {
	
	private String description;
	
	/**
	 * Repository constructor. This also loads the content server map for this repository.
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
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
