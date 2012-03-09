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
package net.sradonia.i18n;

import java.text.MessageFormat;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sradonia.gui.MnemonicString;

/**
 * {@link ResourceBundle} wrapper with additional constructors and {@link #getString(String, Object[])} parameter substitution.
 * 
 * @author Stefan Rado
 */
public final class StringBundle {
	private static final Log log = LogFactory.getLog(StringBundle.class);

	private static final String DEFAULT_BASENAME = "resources";

	private ResourceBundle bundle;
	private String prefix;

	/*
	 * simple constructor
	 */
	public StringBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	/*
	 * class constructors --> package constructors
	 */
	public StringBundle(Class<?> clazz) {
		this(clazz.getPackage());
		setPrefix(clazz);
	}

	public StringBundle(Class<?> clazz, Locale locale) {
		this(clazz.getPackage(), locale);
		setPrefix(clazz);
	}

	public StringBundle(Class<?> clazz, Locale locale, ClassLoader loader) {
		this(clazz.getPackage(), locale, loader);
		setPrefix(clazz);
	}

	public StringBundle(Class<?> clazz, String baseName) {
		this(clazz.getPackage(), baseName);
		setPrefix(clazz);
	}

	public StringBundle(Class<?> clazz, String baseName, Locale locale) {
		this(clazz.getPackage(), baseName, locale);
		setPrefix(clazz);
	}

	public StringBundle(Class<?> clazz, String baseName, Locale locale, ClassLoader loader) {
		this(clazz.getPackage(), baseName, locale, loader);
		setPrefix(clazz);
	}

	/*
	 * package constructors -> string constructors
	 */

	public StringBundle(Package pack) {
		this(pack, DEFAULT_BASENAME);
	}

	public StringBundle(Package pack, Locale locale) {
		this(pack, DEFAULT_BASENAME, locale);
	}

	public StringBundle(Package pack, Locale locale, ClassLoader loader) {
		this(pack, DEFAULT_BASENAME, locale, loader);
	}

	public StringBundle(Package pack, String baseName) {
		this(pack.getName() + "." + baseName);
	}

	public StringBundle(Package pack, String baseName, Locale locale) {
		this(pack.getName() + "." + baseName, locale);
	}

	public StringBundle(Package pack, String baseName, Locale locale, ClassLoader loader) {
		this(pack.getName() + "." + baseName, locale, loader);
	}

	/*
	 * string constructors
	 */
	public StringBundle(String baseName) {
		bundle = ResourceBundle.getBundle(baseName);
	}

	public StringBundle(String baseName, Locale locale) {
		bundle = ResourceBundle.getBundle(baseName, locale);
	}

	public StringBundle(String baseName, Locale locale, ClassLoader loader) {
		bundle = ResourceBundle.getBundle(baseName, locale, loader);
	}

	/**
	 * Sets the prefix which will automatically be added to keys.
	 * 
	 * @param prefix
	 *            the new prefix
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Sets the prefix which will automatically be added to keys. The new prefix is the simple name of the given name with a dot at the end.
	 * 
	 * @param clazz
	 *            the class whose name will be used as the prefix
	 */
	public void setPrefix(Class<?> clazz) {
		setPrefix(clazz.getSimpleName() + ".");
	}

	/**
	 * @return the prefix which will automatically be added to keys
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Helper method which adds the prefix to keys if there is one set.
	 * 
	 * @param key
	 *            the key to prefix
	 * @return the prefixed key
	 */
	private String getPrefixedKey(String key) {
		if (prefix == null || prefix.length() == 0)
			return key;
		else
			return prefix + key;
	}

	/**
	 * @see ResourceBundle#getLocale()
	 */
	public Locale getLocale() {
		return bundle.getLocale();
	}

	/**
	 * @see ResourceBundle#getKeys()
	 */
	public Enumeration<String> getKeys() {
		return bundle.getKeys();
	}

	/**
	 * @see ResourceBundle#containsKey(String)
	 */
	public boolean containsKey(String key) {
		return bundle.containsKey(getPrefixedKey(key));
	}

	/**
	 * Replaces {x} with the x-th element of the params array.
	 * 
	 * @param value
	 *            the value
	 * @param params
	 *            the params
	 * @return the resulting value
	 */
	private String replaceParams(String value, Object... params) {
		if (params == null || params.length == 0) {
			return value;
		} else {
			if (params.length == 1 && params[0] instanceof Collection<?>)
				params = ((Collection<?>) params[0]).toArray();
			return new MessageFormat(value, bundle.getLocale()).format(params, new StringBuffer(), null).toString();
		}
	}

	/**
	 * Fetches a String for the given key. Occurences of {x} will be replaced by the x-th element of the params array.
	 * 
	 * @see ResourceBundle#getString(String)
	 * @param key
	 *            the key
	 * @param params
	 *            optional parameters
	 * @return the value corresponding to the key
	 */
	public String getString(String key, Object... params) {
		try {
			return replaceParams(bundle.getString(getPrefixedKey(key)), params);
		} catch (MissingResourceException e1) {
			try {
				// fallback one: try unprefixed key
				return replaceParams(bundle.getString(key), params);
			} catch (MissingResourceException e2) {
				// fallback two: return the key
				if (log.isWarnEnabled()) {
					StringBuilder warn = new StringBuilder("missing resource: ");
					if (prefix != null && prefix.length() > 0)
						warn.append('(').append(prefix).append(')');
					warn.append(key);

					log.warn(warn);
				}
				return key;
			}
		}
	}

	/**
	 * Fetches the first character of the key's value.
	 * 
	 * @param key
	 *            the key
	 * @return the first character of the key's value.
	 */
	public char getChar(String key) {
		return getChar(key, 0);
	}

	/**
	 * Fetches one character of the key's value. If the String is shorter than the requested position, the last possible character will be returned.
	 * 
	 * @param key
	 *            the key
	 * @param position
	 *            the position of the character in the key's value
	 * @return the first character of the value
	 */
	public char getChar(String key, int position) {
		try {
			String s = getString(key);
			return s.charAt(Math.min(position, s.length() - 1));
		} catch (Exception e) {
			return key.charAt(0);
		}
	}

	/**
	 * Convenient method to fetch a readily parsed {@link MnemonicString}.
	 * 
	 * @param key
	 *            the key
	 * @param params
	 *            optional parameters
	 * @return a readily parsed mnemonic string
	 */
	public MnemonicString getMnemonicString(String key, Object... params) {
		return new MnemonicString(getString(key, params));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof StringBundle) && bundle.equals(((StringBundle) obj).bundle) && prefix.equals(((StringBundle) obj).prefix);
	}
}
