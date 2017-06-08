package it.tooly.shared.common;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;

/**
 * The Class FileUtils.
 */
public class FileUtils {

	/** The logger instance. */
	private final static Logger LOGGER = Logger.getLogger(FileUtils.class);

	/**
	 * Returns a List with the files with specified suffix and before a certain
	 * time somewhere under a directory.
	 *
	 * @param directory
	 *            the directory
	 * @param suffix
	 *            the suffix, which is the file extension or the full file name.
	 *            Checked with
	 * @param recursive
	 *            if recursive
	 * @param lastModifiedBefore
	 *            last modified time in milliseconds. Files before lastModified
	 *            will be returned. Specify <code>0</code> to ignore.
	 * @return the files with suffix {@link String#endsWith(String)}. Check is
	 *         made case insensitive. Specify <code>null</code> to ignore.
	 */
	public List<File> getFilesWithSuffix(final File directory, final String suffix, final boolean recursive, final long lastModifiedBefore) {
		List<File> files = new ArrayList<File>();
		getFilesWithSuffix(files, directory, suffix.toLowerCase(), recursive, lastModifiedBefore);
		return files;
	}

	/**
	 * Fills a List with the files with specified suffix somewhere under a
	 * directory.
	 *
	 * @param files
	 *            the file list
	 * @param directory
	 *            the directory
	 * @param suffix
	 *            the suffix, which is the file extension or the full file name.
	 *            Checked with
	 * @param recursive
	 *            if recursive
	 * @param lastModifiedBefore
	 *            last modified time in milliseconds. File before lastModified
	 *            will be returned. Specify <code>0</code> to ignore
	 * @return the files with suffix {@link String#endsWith(String)}. Check is
	 *         done lower cased. Specify <code>null</code> to ignore.
	 */
	private void getFilesWithSuffix(final List<File> files, final File directory, final String suffix, final boolean recursive, final long lastModifiedBefore) {
		File[] foundFiles = directory.listFiles();
		if (foundFiles != null) {
			for (File foundFile : foundFiles) {
				if (recursive && foundFile.isDirectory()) {
					getFilesWithSuffix(files, foundFile, suffix, recursive, lastModifiedBefore);
				} else if ((suffix == null || foundFile.getName().toLowerCase().endsWith(suffix)) && (lastModifiedBefore == 0 || lastModifiedBefore >= foundFile.lastModified())) {
					files.add(foundFile);
				}
			}
		}
	}

	/**
	 * Returns the objectId from the file name.
	 *
	 * @param fileName
	 *            the file name
	 * @return the object id
	 */
	public static String getObjectIdFromFileName(final String fileName) {
		String objectId = null;
		int index = fileName.lastIndexOf(File.separator) + 1;
		if (index > 0) {
			objectId = fileName.substring(index, index + 16);
		}
		LOGGER.debug("ObjectId from fileName " + fileName + " = '" + objectId + "'");
		return objectId;
	}

	/**
	 * Copy file to location.
	 *
	 * @param srcFile
	 *            the src file
	 * @param targetFolder
	 *            the target folder
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String copyFileToLocation(final File srcFile, final File targetFolder) throws IOException {
		String resultPath = null;
		File targetFile = new File(targetFolder + File.separator + srcFile.getName());

		// read the file
		InputStream in = new FileInputStream(srcFile);

		// For Overwrite the file.
		OutputStream out = new FileOutputStream(targetFile);

		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
		LOGGER.debug("File copied.");

		return resultPath;
	}

	/**
	 * Gets the host name.
	 *
	 * @return the host name
	 */
	public static String getHostName() {
		String hostName = null;
		try {
			InetAddress addr = InetAddress.getLocalHost();
			hostName = addr.getHostName();
		} catch (UnknownHostException e) {
			LOGGER.error(e);
		}
		return hostName;
	}

	// /**
	// * Checks if is open.
	// *
	// * @param file the file
	// * @return true, if is open
	// * @throws FileSystemException the file system exception
	// */
	// public static boolean isOpen(final File file) throws FileSystemException {
	// final FileSystemManager vfs = VFS.getManager();
	// final FileObject checkFile = vfs.toFileObject(file);
	//
	// boolean result = checkFile.isContentOpen();
	// LOGGER.debug("File is " + (result ? "open" : "closed"));
	// return result;
	// }

	/**
	 * In the case of an exception, return false. This exception is masked an
	 * indicated that the sourcefile is not a valid pdf.
	 *
	 * @param file
	 *            the file
	 * @return true, if successful
	 */
	public static boolean obtainLock(final File file) {
		boolean result = false;
		FileLock lock = null;
		try {

			FileChannel channel = new FileInputStream(file).getChannel();
			lock = channel.tryLock();

			if (lock == null) {
				return false;
			}
			if (lock.isValid() && lock.isShared()) {
				return true;
			}
			return false;

		} catch (Exception e) {
			LOGGER.warn(e.getMessage());
			result = false;
		} finally {
			try {
				if (lock != null) {
					lock.release();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * Gets the file extension.
	 *
	 * @param fileName
	 *            the file name
	 * @return the file extension
	 */
	public static String getFileExtension(final String fileName) {
		if (fileName != null && fileName.trim().length() > 0) {
			String ext = fileName.lastIndexOf(".") > -1 ? fileName.substring(fileName.lastIndexOf(".") + 1) : null;
			return ext;
		}
		return null;
	}

	/**
	 * Gets the file name no extension.
	 *
	 * @param fileName
	 *            the file name
	 * @return the file name no extension
	 */
	public static String getFileNameNoExtension(final String fileName) {
		if (fileName != null) {
			String ext = fileName.lastIndexOf(".") > -1 ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
			return ext;
		}
		return null;
	}

	/**
	 * Find files.
	 *
	 * @param path
	 *            the path
	 * @param filter
	 *            the filter with wildcards '?' and '*' (like in DOS/Windows)
	 * @return the list
	 */
	public static List<File> findFiles(final String path, final String filter) {

		FileFilter fileFilter = new WildcardFileFilter(filter, IOCase.SYSTEM);

		return findFiles(path, fileFilter, true);
	}

	/**
	 * Find first file.
	 *
	 * @param path
	 *            the path
	 * @param filter
	 *            the filter
	 * @return the file
	 */
	public static File findFirstFile(final String path, final String filter) {
		FileFilter fileFilter = new WildcardFileFilter(filter, IOCase.SYSTEM);
		List<File> files = findFiles(path, fileFilter, true);
		if (files != null && files.size() > 0) {
			return files.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Finds files that match the given RegEx pattern (E.g.
	 * 'C:/Temp/Folder/file.*.jpg').
	 *
	 * @param fileNameRegEx
	 *            the RegEx full path file name pattern (E.g.
	 *            'C:/Temp/Folder/file.*.jpg').
	 * @return the list
	 */
	public static List<String> findFilesByRegEx(final String fileNameRegEx) {
		List<String> result = new ArrayList<String>();

		if (fileNameRegEx != null && fileNameRegEx.trim().length() > 0) {
			File templateFile = new File(fileNameRegEx);
			if (templateFile.exists()) {
				result.add(templateFile.getAbsolutePath());
			} else {
				File parentFolder = templateFile.getParentFile();
				if (parentFolder != null && parentFolder.exists()) {
					final String fileName = templateFile.getName();
					System.out.print("  Scaning for files like '" + fileNameRegEx + "'... ");

					List<File> foundFiles = FileUtils.findFiles(parentFolder.getAbsolutePath(), new FileFilter() {

						public boolean accept(final File f) {
							return StringUtils.matchesPattern(f.getAbsolutePath(), fileName);
						}
					}, true);

					Collections.sort(foundFiles);

					for (File file : foundFiles) {
						result.add(file.getAbsolutePath());
					}

					System.out.println(foundFiles.size() + " found");

				} else {
					System.err.println("ERROR: Unable to determine parent folder for file '" + fileNameRegEx + "'");

				}
			}

		}
		return result;
	}

	/**
	 * Find files.
	 *
	 * @param path
	 *            the path
	 * @param fileFilter
	 *            the file filter
	 * @param parseSubfolders
	 *            the parse subfolders
	 * @return the list
	 */
	public static List<File> findFiles(final String path, final FileFilter fileFilter, final boolean parseSubfolders) {
		return findFiles(path, fileFilter, false, parseSubfolders);
	}

	/**
	 * Find files.
	 *
	 * @param path
	 *            the path
	 * @param fileFilter
	 *            the file filter
	 * @param returnSubfolders
	 *            Return folders in the result
	 * @param parseSubfolders
	 *            Iterate through subfolders
	 * @return the list
	 */
	public static List<File> findFiles(final String path, final FileFilter fileFilter, final boolean returnSubfolders, final boolean parseSubfolders) {

		List<File> result = new ArrayList<File>();

		File filePath = new File(path);

		File[] files = filePath.listFiles(fileFilter);
		if (files != null) {
			for (File file : files) {
				if (!file.isDirectory()) {
					result.add(file);
				}
			}
		}

		if (returnSubfolders || parseSubfolders) {
			File[] subFolders = filePath.listFiles(new FileFilter() {
				public boolean accept(final File pathname) {
					return pathname.isDirectory();
				}
			});

			if (subFolders != null) {
				// Add subfolders to the result
				if (returnSubfolders) {
					for (File subFolder : subFolders) {
						result.add(subFolder);
					}
				}

				// Add the content of subfolders to the result
				if (parseSubfolders) {
					for (File subFolder : subFolders) {
						result.addAll(findFiles(subFolder.getAbsolutePath(), fileFilter, returnSubfolders));
					}
				}
			}
		}

		return result;
	}

	/**
	 * Find folders.
	 *
	 * @param path
	 *            the path
	 * @param folderFilter
	 *            the folder filter
	 * @param parseSubfolders
	 *            the parse subfolders
	 * @return the list
	 */
	public static List<File> findFolders(final String path, final String folderFilter, final boolean parseSubfolders) {

		FileFilter filter = new WildcardFileFilter(folderFilter, IOCase.SYSTEM);

		return findFolders(path, filter, parseSubfolders);
	}

	/**
	 * Find files.
	 *
	 * @param path
	 *            the path
	 * @param folderFilter
	 *            the folder filter
	 * @param parseSubfolders
	 *            the parse subfolders
	 * @return the list
	 */
	public static List<File> findFolders(final String path, final FileFilter folderFilter, final boolean parseSubfolders) {

		List<File> result = new ArrayList<File>();

		File filePath = new File(path);

		File[] subFolders = filePath.listFiles(folderFilter);
		for (File file : subFolders) {
			if (file.isDirectory()) {
				result.add(file);
			}
		}

		// Add the content of subfolders to the result
		if (parseSubfolders) {
			for (File subFolder : subFolders) {
				result.addAll(findFolders(subFolder.getAbsolutePath(), folderFilter, true));
			}
		}

		return result;
	}

	/**
	 * Loads a file into the byte array;.
	 *
	 * @param filename
	 *            The name of the file to be lodaded
	 * @return Content of the file as a byte array
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static byte[] loadFile(final String filename) throws IOException {

		FileInputStream fis = new FileInputStream(filename);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		copyStream(fis, baos);

		return baos.toByteArray();
	}

	/**
	 * Saves the data from the input stream into the file.
	 *
	 * @param filename
	 *            The name of the file where the data will be stored
	 * @param inputStream
	 *            Input stream to read the data from.
	 *            <p>
	 *            <i>NOTE: <b>The input stream instance is not clossed. It
	 *            should be closed manually after this method call.</b></i>
	 *            </p>
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static File saveFile(final String filename, final InputStream inputStream) throws IOException {
		File file = new File(filename);
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
		try {
			copyStream(inputStream, bos);
			return file;
		} finally {
			inputStream.close();
			bos.flush();
			bos.close();
		}
	}

	/**
	 * Saves the data from the byte array into the file.
	 *
	 * @param filename
	 *            The name of the file where the data will be stored
	 * @param byteArray
	 *            The data represented as an byte array
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static File saveFile(final String filename, final byte[] byteArray) throws IOException {
		return saveFile(filename, new ByteArrayInputStream(byteArray));
	}

	/**
	 * Saves the given string into the file.
	 *
	 * @param filename
	 *            The name of the file where the string will be stored
	 * @param data
	 *            the data
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static File saveFile(final String filename, final String data) throws IOException {
		return saveFile(filename, data.getBytes());
	}

	/**
	 * Loads each line from the given file into the List.
	 *
	 * @param fileName
	 *            the file name
	 * @return the list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static List<String> loadFileIntoList(final String fileName) throws IOException {

		List<String> result = new ArrayList<String>();

		BufferedReader isReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));

		StringBuffer buff = new StringBuffer();

		String line = isReader.readLine();
		while (line != null) {
			result.add(line);
			line = isReader.readLine();
		}

		return result;
	}

	/**
	 * Loads each line from the given file into the Map where both key and value
	 * are the same (the line).
	 *
	 * @param fileName
	 *            the file name
	 * @return the map
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static Map<String, String> loadFileIntoMap(final String fileName) throws IOException {
		Map<String, String> result = new HashMap<String, String>();

		List<String> lines = loadFileIntoList(fileName);
		if (lines != null && lines.size() > 0) {
			for (String line : lines) {
				result.put(line, line);
			}
		}
		return result;
	}

	/**
	 * Copies all the bytes from the input stream (source) into the output
	 * stream (destination).
	 *
	 * @param inputStream
	 *            The source stream from which the data will be copied into the
	 *            destination stream
	 * @param outputStream
	 *            The destination stream where the data will be copied from the
	 *            source input stream
	 * @return The number of bytes that were copied
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static long copyStream(final InputStream inputStream, final OutputStream outputStream) throws IOException {
		return copyStream(inputStream, outputStream, 0, Long.MAX_VALUE);
	}

	/**
	 * Copies the given number of bytes (starting at a given offset) from the
	 * input stream (source) into the output stream (destination).
	 *
	 * @param inputStream
	 *            The source stream from which the data will be copied into the
	 *            destination stream
	 * @param outputStream
	 *            The destination stream where the data will be copied from the
	 *            source input stream
	 * @param offset
	 *            An offset in the input stream from which to start copying.
	 * @param length
	 *            number of bytes to be copied from the input stream
	 * @return Number of bytes that were actually copied
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static long copyStream(final InputStream inputStream, final OutputStream outputStream, final long offset, final long length) throws IOException {
		byte[] buff = new byte[1024];
		int len = 0;
		long total = 0;
		inputStream.skip(offset);
		len = inputStream.read(buff);
		while (len > 0 && total < length) {
			outputStream.write(buff, 0, len);
			total += len;
			len = inputStream.read(buff);
		}
		return total;
	}

	/**
	 * Reads all bytes from the input stream and generates a byte array of it.
	 *
	 * @param inputStream
	 *            The source stream to copy the bytes from.
	 * @return Byte array with all bytes copied from the input source
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static byte[] copyStreamToByteArray(final InputStream inputStream) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		copyStream(inputStream, baos);
		return baos.toByteArray();
	}

	/**
	 * Ensure folder.
	 *
	 * @param folderPath
	 *            the folder path
	 * @return the file
	 */
	public static File ensureFolder(final String folderPath) {
		return ensureFolder(new File(folderPath));
	}

	/**
	 * Ensure folder.
	 *
	 * @param folder
	 *            the folder
	 * @return the file
	 */
	public static File ensureFolder(final File folder) {
		folder.mkdirs();

		return folder;
	}

	/**
	 * Creates the temporary file.
	 *
	 * @param extension
	 *            the extension
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static File createTemporaryFile(final String extension) throws IOException {
		return File.createTempFile("temp_", extension);
	}

	/**
	 * Creates the temporary file name.
	 *
	 * @param extension
	 *            the extension
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String createTemporaryFileName(final String extension) throws IOException {
		return File.createTempFile("temp_", extension).getAbsolutePath();
	}

	/**
	 * Load file to list.
	 *
	 * @param filename
	 *            the filename
	 * @return the list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static List<String> loadFileToList(final String filename) throws IOException {
		List<String> result = new ArrayList<String>();

		BufferedReader isReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

		String line = isReader.readLine();
		while (line != null) {
			result.add(line);
			line = isReader.readLine();
		}
		return result;
	}

	/**
	 * Save file.
	 *
	 * @param filename
	 *            the filename
	 * @param list
	 *            the list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void saveFile(final String filename, final List<String> list) throws IOException {
		BufferedWriter osWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)));

		for (String line : list) {
			osWriter.append(line).append("\n");
		}

		osWriter.flush();
		osWriter.close();
	}

	/**
	 * Given the string that is Base64 encoded, this method will first decode it
	 * (to binary) and then save it to a binary file.
	 *
	 * @param filename
	 *            The name of the file where the string will be stored
	 * @param base64EncodedString
	 *            the base64 encoded string
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static File saveBase64StringToFile(final String filename, final String base64EncodedString) throws IOException {
		byte[] byteArray = Base64.decode(base64EncodedString.getBytes());
		return saveFile(filename, byteArray);
	}

	public static void savePasswordToFile(File file, String password) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException {
		OutputStream fileOut = new FileOutputStream(file);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		CipherOutputStream cOut = new CipherOutputStream(fileOut, cipher);
		try {
			cOut.write(password.getBytes());
		} finally {
			cOut.close();
		}
	}

	public static String getPasswordFromFile(File file) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException {
		InputStream fileIn = new FileInputStream(file);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		CipherInputStream cIn = new CipherInputStream(fileIn, cipher);
		byte[] bytes = new byte[] {};
		try {
			int bytesRead = cIn.read(bytes);
		} finally {
			cIn.close();
		}
		return new String(bytes);
	}

	/**
	 * Escape file name.
	 *
	 * @param fileName
	 *            the file name
	 * @return the string
	 */
	public static String escapeFileName(final String fileName) {
		if (fileName != null) {
			String result = fileName.replaceAll("\\\\", "-").replaceAll("/", "-").replaceAll("\\t", " ").replaceAll("\\n", " ").replaceAll("\\r", " ").replaceAll("\\:", ";")
					.replaceAll("\\\"", ";");
			return result;
		}
		return null;
	}

	/**
	 * Creates an ZIP archive out of given files. The zip archive will be flat
	 * i.e. all files will be in the root of the archive.
	 *
	 * @param zipFile
	 *            the zip file
	 * @param sourceFiles
	 *            the source files
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void zipFiles(File zipFile, File... sourceFiles) throws IOException {

		BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(zipFile));
		ZipOutputStream zos = new ZipOutputStream(fos);

		if (sourceFiles != null && sourceFiles.length > 0) {
			for (File sourceFile : sourceFiles) {
				if (!sourceFile.isDirectory()) {
					ZipEntry ze = new ZipEntry(sourceFile.getName());
					zos.putNextEntry(ze);

					FileInputStream fis = new FileInputStream(sourceFile);
					copyStream(fis, zos);
					fis.close();
					zos.closeEntry();
				}
			}
		}
		zos.close();
		fos.close();

	}

	/**
	 * Zip the contents of a folder, possibly including sub folders
	 * (recursively).
	 *
	 * @param zipFile
	 *            the zip file
	 * @param folder
	 *            the folder
	 * @param zipSubfolders
	 *            the zip subfolders
	 * @param preserveFolderTree
	 *            Preserve the paths in the zip file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void zipFolder(File zipFile, File folder, boolean zipSubfolders, boolean preserveFolderTree) throws IOException {

		if (folder.exists() && folder.canRead()) {

			String rootPath = folder.getAbsolutePath();

			List<File> sourceFiles = findFiles(folder.getAbsolutePath(), null, false, zipSubfolders);

			BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(zipFile));
			ZipOutputStream zos = new ZipOutputStream(fos);

			if (sourceFiles != null && sourceFiles.size() > 0) {
				for (File sourceFile : sourceFiles) {
					if (!sourceFile.isDirectory()) {

						String zippeddFilePath = null;
						if (preserveFolderTree) {
							zippeddFilePath = sourceFile.getAbsolutePath().substring(rootPath.length() + 1);
						} else {
							zippeddFilePath = sourceFile.getName();
						}
						ZipEntry ze = new ZipEntry(zippeddFilePath);
						zos.putNextEntry(ze);

						FileInputStream fis = new FileInputStream(sourceFile);
						copyStream(fis, zos);
						fis.close();
						zos.closeEntry();
					}
				}
			}
			zos.close();
			fos.close();
			// LOGGER.debug("ZIP archive created!");
		}
	}

	/**
	 * Delete folder.
	 *
	 * @param folder
	 *            the folder
	 * @param deepDelete
	 *            the deep delete
	 * @return true, if successful
	 */
	public static boolean deleteFolder(File folder, boolean deepDelete) {
		if (folder.exists() && folder.canWrite()) {

			File[] subFolders = folder.listFiles(new FileFilter() {
				public boolean accept(File f) {
					return f.isDirectory();
				}
			});

			// If there are subfolders
			if (subFolders.length > 0) {

				// Return with FALSE if 'deepDelete' is FALSE
				if (!deepDelete) {
					return false;
				}

				// Otherwise (deepDelete is TRUE) delete subfolders reqursively
				for (File subFolder : subFolders) {
					deleteFolder(subFolder, deepDelete);
				}
			}

			// Find all files in given folder
			File[] files = folder.listFiles(new FileFilter() {
				public boolean accept(File f) {
					return true;
				}
			});

			// And delete them
			for (File file : files) {
				file.delete();
			}

			// Finally, delete the folder itself
			folder.delete();

			return true;
		}

		return false;

	}
}
