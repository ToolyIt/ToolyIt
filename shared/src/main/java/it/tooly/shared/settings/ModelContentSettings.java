/**
 *
 */
package it.tooly.shared.settings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

/**
 * @author M.E. de Boer
 *
 */
public class ModelContentSettings {
	public static final String PROPERTIES_FILENAME = ModelContentSettings.class.getSimpleName() + ".properties";
	public static final String TMP_SUBFOLDER_PROPERTY = "tmp_subfolder";
	public static final String TMP_SUBFOLDER_DEFAULT = "ToolyItContent";

	private static Properties settings = null;

	private static synchronized void initSettings() {
		if (settings == null) {
			settings = new Properties();
			InputStream input = ModelContentSettings.class.getClassLoader().getResourceAsStream(PROPERTIES_FILENAME);
			try {
				settings.load(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getSetting(String name, String defaultValue) {
		initSettings();
		if (settings != null) {
			return settings.getProperty(name, defaultValue);
		} else {
			return null;
		}
	}

	public static File getContentDir(boolean createIfNotExists) {
		String subFolder = getSetting(TMP_SUBFOLDER_PROPERTY, TMP_SUBFOLDER_DEFAULT);
		File tmpDir = FileUtils.getTempDirectory();
		File contentDir = new File(tmpDir, subFolder);
		if (!contentDir.exists() && createIfNotExists) {
			contentDir.mkdir();
		}
		return contentDir;
	}
}
