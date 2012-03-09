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

import java.util.WeakHashMap;

public class Translator {

	public static abstract class Translation<T> {
		protected final T object;
		protected final String key;
		protected final Object[] params;

		public Translation(T object, String key, Object... params) {
			this.object = object;
			this.key = key;
			this.params = params;
		}

		public void translate(Translator t) {
			translate(t.getStringBundle());
		}

		public abstract void translate(StringBundle bundle);
	}

	private StringBundle bundle;
	private final WeakHashMap<Object, Translation<?>> translations;

	public Translator(StringBundle bundle) {
		this.bundle = bundle;
		translations = new WeakHashMap<Object, Translation<?>>();
	}

	public StringBundle getStringBundle() {
		return bundle;
	}

	public void setStringBundle(StringBundle bundle) {
		this.bundle = bundle;
		updateTranslations();
	}

	@SuppressWarnings("unchecked")
	public <T> Translation<T> getTranslation(T object) {
		return (Translation<T>) translations.get(object);
	}

	public void setTranslation(Translation<?> translation) {
		translations.put(translation.object, translation);
		translation.translate(bundle);
	}

	public void removeTranslation(Object translatedObject) {
		translations.remove(translatedObject);
	}

	public void updateTranslations() {
		for (Translation<?> translation : translations.values())
			translation.translate(bundle);
	}

}
