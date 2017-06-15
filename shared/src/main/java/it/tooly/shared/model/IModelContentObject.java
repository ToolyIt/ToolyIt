/**
 *
 */
package it.tooly.shared.model;

import java.io.InputStream;

import it.tooly.shared.common.ToolyException;

/**
 * An extension of {@link IModelObject}, adding the option to let the object
 * have some kind of content.
 *
 * @author M.E. de Boer
 *
 */
public interface IModelContentObject extends IModelObject {

	/**
	 *
	 * @return true if this object has content
	 */
	public boolean hasContent();

	/**
	 * Get the content of this object
	 *
	 * @return An array of bytes
	 * @throws ToolyException
	 */
	public InputStream getContent() throws ToolyException;

	/**
	 * Set the content of this object
	 *
	 * @throws ToolyException
	 */
	public void setContent(byte[] content) throws ToolyException;
}
