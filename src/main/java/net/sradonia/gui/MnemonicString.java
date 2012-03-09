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
package net.sradonia.gui;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JLabel;

/**
 * <p>
 * A helper class that takes a {@link String} with the mnemonic escape character '&' and offers methods to set the text and the marked mnemonic to a
 * {@link AbstractButton button}.
 * </p>
 * <p>
 * The {@link #MnemonicString(String) constructor} takes a String of the form "Hello &World" and can set "Hello World" as text and 'W' as the mnemonic
 * for a button via the {@link #applyTo(AbstractButton)} method.
 * </p>
 * 
 * @author Stefan Rado
 */
public class MnemonicString {

	private final static char MNEMONIC_PREFIX = '&';

	private String text;
	private int mnemonicIndex;
	private int mnemonic;

	/**
	 * <p>
	 * Takes a String in the form "Hello &World" and parses it.
	 * </p>
	 * <p>
	 * The text "Hello World" and the mnemonic 'W' can be set to a component using one of the <code>setToXXX</code> methods.
	 * </p>
	 * 
	 * @param mnemonicString
	 *            the String containing the mnemonic (prefixed with '&')
	 */
	public MnemonicString(String mnemonicString) {
		mnemonicIndex = -1;
		mnemonic = 0;

		if (mnemonicString != null) {
			int len = mnemonicString.length();

			int idx = -2;
			char mnemonicChar = 0;
			do {
				idx = mnemonicString.indexOf(MNEMONIC_PREFIX, idx + 2);
			} while (idx < len - 1 && (mnemonicChar = mnemonicString.charAt(idx + 1)) == MNEMONIC_PREFIX);

			if (idx != -1 && idx < len - 1) {
				mnemonicIndex = idx;
				mnemonic = (int) mnemonicChar;
				if (mnemonic >= 'a' && mnemonic <= 'z')
					mnemonic -= ('a' - 'A');

				StringBuffer sb = new StringBuffer(len - 1);
				sb.append(mnemonicString.substring(0, mnemonicIndex));
				sb.append(mnemonicString.substring(mnemonicIndex + 1, len));
				text = sb.toString();
			} else {
				text = mnemonicString;
			}

			text = text.replace(new String(new char[] { MNEMONIC_PREFIX, MNEMONIC_PREFIX }), new String(new char[] { MNEMONIC_PREFIX }));
		} else {
			text = new String();
		}
	}

	/**
	 * @return the text without the mnemonic prefix
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return the index of the mnemonic character
	 */
	public int getMnemonicIndex() {
		return mnemonicIndex;
	}

	/**
	 * @return the mnemonic
	 */
	public int getMnemonic() {
		return mnemonic;
	}

	/**
	 * @return the mnemonic character
	 */
	public char getMnemonicChar() {
		return (char) mnemonic;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append('[').append(text).append(", ");
		if (mnemonicIndex == -1)
			sb.append("no mnemonic");
		else
			sb.append('\'').append(getMnemonicChar()).append("' @ ").append(mnemonicIndex);
		sb.append(']');
		return sb.toString();
	}

	/**
	 * Sets the button's text and mnemonic.
	 * 
	 * @param button
	 *            the button
	 * @see AbstractButton#setText(String)
	 * @see AbstractButton#setMnemonic(int)
	 * @see AbstractButton#setDisplayedMnemonicIndex(int)
	 */
	public void applyTo(AbstractButton button) {
		button.setText(text);
		if (mnemonicIndex != -1) {
			button.setMnemonic(mnemonic);
			button.setDisplayedMnemonicIndex(mnemonicIndex);
		}
	}

	/**
	 * Sets the label's text and mnemonic.
	 * 
	 * @param label
	 *            the label
	 * @see JLabel#setText(String)
	 * @see JLabel#setDisplayedMnemonic(int)
	 * @see JLabel#setDisplayedMnemonicIndex(int)
	 */
	public void applyTo(JLabel label) {
		label.setText(text);
		if (mnemonicIndex != -1) {
			label.setDisplayedMnemonic(mnemonic);
			label.setDisplayedMnemonicIndex(mnemonicIndex);
		}
	}

	/**
	 * Sets the label's name and mnemonic values.
	 * 
	 * @param action
	 *            the action
	 * @see Action#putValue(String, Object)
	 * @see Action#NAME
	 * @see Action#MNEMONIC_KEY
	 * @see Action#DISPLAYED_MNEMONIC_INDEX_KEY
	 */
	public void applyTo(Action action) {
		action.putValue(Action.NAME, text);
		action.putValue(Action.MNEMONIC_KEY, mnemonic);
		action.putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, mnemonicIndex);
	}

}
