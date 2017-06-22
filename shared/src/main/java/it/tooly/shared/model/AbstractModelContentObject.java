/**
 *
 */
package it.tooly.shared.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import it.tooly.shared.common.ToolyException;
import it.tooly.shared.settings.ModelContentSettings;

/**
 * @author M.E. de Boer
 *
 */
public class AbstractModelContentObject extends AbstractModelObject implements IModelContentObject {
	private long lockedByThread;
	private String contentType;

	public AbstractModelContentObject(String id) {
		super(id);
		initLocalVars();
	}

	public AbstractModelContentObject(String id, String name) {
		super(id, name);
		initLocalVars();
	}

	protected void initLocalVars() {
		this.lockedByThread = -1;
		this.contentType = null;
	}

	@Override
	public boolean hasContent() {
		File contentFile = getContentFile();
		return contentFile != null && contentFile.exists() && FileUtils.sizeOf(contentFile) > 0;
	}

	private File getContentFile() {
		File contentDir = ModelContentSettings.getContentDir(true);
		return new File(contentDir, this.getId() + "_" + this.hashCode());
	}

	@Override
	public InputStream getContent() throws ToolyException {
		File contentFile = getContentFile();
		FileInputStream fis = null;
		if (contentFile != null && contentFile.exists()) {
			try {
				fis = new FileInputStream(contentFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return fis;
	}

	@Override
	public void setContent(byte[] content) throws ToolyException {
		File contentFile = getContentFile();
		if (contentFile == null) {
			throw new ToolyException("Unable to set content because could not get or create a content file");
		}
		if (!contentFile.exists()) {
			try {
				contentFile.createNewFile();
			} catch (IOException e) {
				throw new ToolyException(
						"Unable to set content because could not create content file " + contentFile.getName(), e);
			}
		}
		if (!contentFile.canWrite()) {
			throw new ToolyException(
					"Unable to set content because could not write to content file" + contentFile.getName());
		}
		try {
			FileUtils.writeByteArrayToFile(contentFile, content);
		} catch (IOException e) {
			throw new ToolyException(
					"Unable to set content because could not write to content file" + contentFile.getName(), e);
		}
		this.getContentType();
	}

	public synchronized boolean lock() {
		if (this.lockedByThread == -1 || this.lockedByThread == Thread.currentThread().getId()) {
			this.lockedByThread = Thread.currentThread().getId();
			return true;
		} else {
			return false;
		}
	}

	public synchronized boolean unlock() {
		if (this.lockedByThread != -1 && this.lockedByThread == Thread.currentThread().getId()) {
			this.lockedByThread = -1;
			return true;
		} else {
			return false;
		}
	}

	public String getLockOwner() {
		return Long.toString(this.lockedByThread);
	}

	public String getContentType() throws ToolyException {
		if (this.contentType == null && this.hasContent()) {
			File contentFile = this.getContentFile();
			Path filePath = FileSystems.getDefault().getPath(contentFile.getAbsolutePath());
			try {
				this.contentType = Files.probeContentType(filePath);
			} catch (IOException e) {
				throw new ToolyException("Could not get content type", e);
			}
		}
		return this.contentType;
	}
}
