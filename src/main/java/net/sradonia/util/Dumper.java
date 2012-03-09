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
package net.sradonia.util;

public final class Dumper {

	private static final char HEX_CHARS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private Dumper() {
	}

	public static String dump(byte[] data) {
		return dump(data, 0, data.length);
	}

	public static String dump(byte[] data, int offset, int count) {
		if (offset > data.length - 1)
			throw new IndexOutOfBoundsException("offset");

		int length = Math.min(offset + count, data.length);

		StringBuffer sb = new StringBuffer((length - offset) * 3 + 1);
		sb.append('[');
		for (int i = offset; i < length; i++) {
			sb.append(HEX_CHARS[(data[i] >> 4) & 0xF]);
			sb.append(HEX_CHARS[data[i] & 0xF]);
			if (i < length - 1)
				sb.append(' ');
		}
		sb.append(']');
		return sb.toString();
	}

}
