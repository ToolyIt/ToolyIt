/**
 *
 */
package it.tooly.shared.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import it.tooly.shared.common.ToolyException;

/**
 * @author ISC09616
 *
 */
public class AbstractModelContentObject extends AbstractModelObject implements IModelContentObject {
	byte[] content = null;

	public AbstractModelContentObject(String id) {
		super(id);
	}

	public AbstractModelContentObject(String id, String name) {
		super(id, name);
	}

	@Override
	public boolean hasContent() {
		return content != null && content.length > 0;
	}

	@Override
	public InputStream getContent() throws ToolyException {
		return new ByteArrayInputStream(this.content);
	}

	@Override
	public void setContent(byte[] content) throws ToolyException {
		this.content = content;
	}

}
