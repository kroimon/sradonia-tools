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

import javax.swing.AbstractButton;

import net.sradonia.i18n.StringBundle;
import net.sradonia.i18n.Translator.Translation;

public class ButtonTranslation extends Translation<AbstractButton> {

	protected String toolTipKey;

	public ButtonTranslation(AbstractButton object, String key, Object... params) {
		this(object, key, null, params);
	}

	public ButtonTranslation(AbstractButton object, String key, String toolTipKey, Object... params) {
		super(object, key, params);
		this.toolTipKey = toolTipKey;
	}

	@Override
	public void translate(StringBundle bundle) {
		bundle.getMnemonicString(key, params).applyTo(object);
		if (toolTipKey != null && !toolTipKey.isEmpty())
			object.setToolTipText(bundle.getString(toolTipKey, params));
	}

}
