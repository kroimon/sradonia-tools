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

import java.awt.Dialog;
import java.awt.Frame;
import java.util.Locale;

import javax.swing.AbstractButton;
import javax.swing.Action;

import net.sradonia.i18n.translations.*;

public class SimpleTranslator extends Translator {

	public SimpleTranslator(StringBundle bundle) {
		super(bundle);
	}

	protected SimpleTranslator(Class<?> clazz) {
		super(new StringBundle(clazz));
	}

	protected SimpleTranslator(Class<?> clazz, Locale locale) {
		super(new StringBundle(clazz, locale));
	}

	public <T extends Action> T translate(T action, String key, Object... params) {
		setTranslation(new ActionTranslation(action, key, params));
		return action;
	}

	public <T extends AbstractButton> T translate(T button, String key, Object... params) {
		setTranslation(new ButtonTranslation(button, key, params));
		return button;
	}

	public <T extends Frame> T translate(T frame, String key, Object... params) {
		setTranslation(new FrameTranslation(frame, key, params));
		return frame;
	}

	public <T extends Dialog> T translate(T dialog, String key, Object... params) {
		setTranslation(new DialogTranslation(dialog, key, params));
		return dialog;
	}
}
