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
package net.sradonia.i18n.translations;

import javax.swing.Action;

import net.sradonia.i18n.StringBundle;
import net.sradonia.i18n.Translator.Translation;

public class ActionTranslation extends Translation<Action> {

	protected String shortDescriptionKey;
	protected String longDescriptionKey;

	public ActionTranslation(Action object, String key, Object... params) {
		this(object, key, null, null, params);
	}

	public ActionTranslation(Action object, String key, String shortDescriptionKey, Object... params) {
		this(object, key, shortDescriptionKey, null, params);
	}

	public ActionTranslation(Action object, String key, String shortDescriptionKey, String longDescriptionKey, Object... params) {
		super(object, key, params);
		this.shortDescriptionKey = shortDescriptionKey;
		this.longDescriptionKey = longDescriptionKey;
	}

	@Override
	public void translate(StringBundle bundle) {
		bundle.getMnemonicString(key, params).applyTo(object);
		if (shortDescriptionKey != null && !shortDescriptionKey.isEmpty())
			object.putValue(Action.SHORT_DESCRIPTION, bundle.getString(shortDescriptionKey, params));
		if (longDescriptionKey != null && !longDescriptionKey.isEmpty())
			object.putValue(Action.LONG_DESCRIPTION, bundle.getString(longDescriptionKey, params));
	}
}
