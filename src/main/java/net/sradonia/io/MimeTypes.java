/*******************************************************************************
 * sradonia tools
 * Copyright (C) 2012 Stefan Rado
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/
package net.sradonia.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides support for searching a MIME-Type for a given file extension. The MIME-Types can be read from a file. When there is no
 * MIME-Type for the given extension in the database, a default value will be returned.
 * 
 * @author Stefan Rado
 */
public class MimeTypes {

	protected String defaultMimeType = null;
	protected Hashtable<String, String> extensions = new Hashtable<String, String>();

	/**
	 * Creates a new instance without loading any database files
	 */
	public MimeTypes() {
	}

	/**
	 * Creates a new instance and calls the {@link #loadMimeTypes(String)} function.
	 * 
	 * @param mimeTypeFile
	 *            a valid database file
	 * @throws FileNotFoundException
	 *             when the file couldn't be found
	 */
	public MimeTypes(String mimeTypeFile) throws FileNotFoundException {
		loadMimeTypes(mimeTypeFile);
	}

	/**
	 * Creates a new instance and calls the {@link #loadMimeTypes(File)} function.
	 * 
	 * @param mimeTypeFile
	 *            a valid database file
	 * @throws FileNotFoundException
	 *             when the file couldn't be found
	 */
	public MimeTypes(File mimeTypeFile) throws FileNotFoundException {
		loadMimeTypes(mimeTypeFile);
	}

	/**
	 * Loads the MIME-Types from the given file.
	 * 
	 * @param mimeTypeFile
	 *            a valid database file
	 * @throws FileNotFoundException
	 *             when the file couldn't be found
	 */
	public void loadMimeTypes(String mimeTypeFile) throws FileNotFoundException {
		loadMimeTypes(new File(mimeTypeFile));
	}

	/**
	 * Loads the MIME-Types from the given file.
	 * 
	 * @param mimeTypeFile
	 *            a valid database file
	 * @throws FileNotFoundException
	 *             when the file couldn't be found
	 */
	public void loadMimeTypes(File mimeTypeFile) throws FileNotFoundException {
		if (mimeTypeFile.canRead() && mimeTypeFile.isFile()) {
			LineInput in;
			try {
				in = new LineInput(new FileInputStream(mimeTypeFile), 0, "ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				in = new LineInput(new FileInputStream(mimeTypeFile), 0);
			}
			try {
				String line;
				while ((line = in.readLine()) != null) {
					if (line.length() == 0 || line.trim().charAt(0) == '#')
						continue;
					Matcher regex = Pattern.compile("^(\\S+)\\s+(.+)$").matcher(line);
					if (regex.matches()) {
						String[] exts = regex.group(2).split("\\s");
						for (String ext : exts)
							extensions.put(ext, regex.group(1));
					}
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Sets the default MIME-Type which should be returned when the MIME-Type searched is not in the database.
	 * 
	 * @param defaultMimeType
	 *            the new default MIME-Type
	 */
	public void setDefaultMimeType(String defaultMimeType) {
		this.defaultMimeType = defaultMimeType;
	}

	/**
	 * Returns the default MIME-Type which should be returned when the MIME-Type searched is not in the database.
	 * 
	 * @return the default MIME-Type
	 */
	public String getDefaultMimeType() {
		return defaultMimeType;
	}

	/**
	 * Returns the MIME-Type for the given extension or the defaultMimeType if not found.
	 * 
	 * @param extension
	 *            the extension which should be searched
	 * @return the associated MIME-Type
	 */
	public String getMimeType(String extension) {
		String mt = extensions.get(extension);
		if (mt == null)
			return defaultMimeType;
		return mt;
	}

	/**
	 * Returns an array containing all the loaded extensions.
	 * 
	 * @return an array containing the loaded extensions
	 */
	public String[] getExtensions() {
		return extensions.keySet().toArray(new String[0]);
	}

	/**
	 * Maps a MIME-Type to the given extension.
	 * 
	 * @param extension
	 *            the extension
	 * @param mimeType
	 *            the associated MIME-Type
	 * @return the old MIME-Type associated with the extension or null if it did not have one.
	 */
	public String add(String extension, String mimeType) {
		return extensions.put(extension, mimeType);
	}

	/**
	 * Clears the internal database.
	 */
	public void clear() {
		extensions.clear();
	}
}
