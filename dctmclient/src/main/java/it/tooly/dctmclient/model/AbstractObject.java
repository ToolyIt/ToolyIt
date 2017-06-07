/*******************************************************************************
 * Copyright (c) 2005, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package it.tooly.dctmclient.model;

public abstract class AbstractObject {
	protected String id;
	protected String name;

	public AbstractObject(String id) {
		this.id = id;
		this.name = "";
	}

	public AbstractObject(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * @return {@code false}, as this is not an observable object
	 */
	public boolean isObservable() {
		return false;
	}

	public String getId() {
		return this.id;
	}

	protected void setId(String newId) {
		this.id = newId;
	}

	public boolean hasNullId() {
		return IModelObject.NULL_ID.equals(this.id);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public int compareTo(IModelObject o) {
		return this.getId().compareTo(o.getId());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name + " (" + id + ")";
	}

}
