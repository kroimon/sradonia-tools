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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author Stefan Rado
 */
public class BCoder {

	static public void encode(Object ob, OutputStream os) throws Exception {
		if (ob instanceof Integer) {
			os.write('i');
			os.write(((Integer) ob).toString().getBytes());
			os.write('e');

		} else if (ob instanceof ArrayList<?>) {
			os.write('l');
			Object[] ao = ((ArrayList<?>) ob).toArray();
			for (int i = 0; i < ao.length; i++) {
				encode(ao[i], os);
			}
			os.write('e');

		} else if (ob instanceof LinkedHashMap<?, ?>) {
			os.write('d');
			LinkedHashMap<?, ?> h = (LinkedHashMap<?, ?>) ob;
			Object keys[] = h.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				Object r = keys[i];
				encode(((String) r).getBytes(), os);
				encode(h.get(r), os);
			}
			os.write('e');

		} else if (ob instanceof byte[]) {
			byte[] s = (byte[]) ob;
			os.write(new Integer(s.length).toString().getBytes());
			os.write(':');
			os.write(s);

		} else {
			throw new IllegalArgumentException("Object can't be bencoded!");
		}
	}

	static public Object decode(InputStream is) throws Exception {
		int c = is.read();

		if (c == 'e') {
			// End of something
			return null;

		} else if (c == 'i') {
			// Integer
			StringBuilder sb = new StringBuilder();
			while ((c = is.read()) != 'e')
				sb.append((char) c);
			try {
				return new Integer(Integer.parseInt(sb.toString()));
			} catch (NumberFormatException e) {
				return new Integer(0);
			}

		} else if ((c >= '0') && (c <= '9')) {
			// String
			StringBuilder sb = new StringBuilder();
			sb.append((char) c);
			while ((c = is.read()) != ':')
				sb.append((char) c);

			try {
				byte[] data = new byte[Integer.parseInt(sb.toString())];
				for (int i = 0; i < data.length; i++)
					data[i] = (byte) (is.read());
				return data;
			} catch (NumberFormatException e) {
				return new byte[0];
			}

		} else if (c == 'l') {
			// List
			ArrayList<Object> v = new ArrayList<Object>();
			Object o;
			while (true) {
				o = decode(is);
				if (o == null)
					return v;
				else
					v.add(o);
			}

		} else if (c == 'd') {
			// Dictionary
			LinkedHashMap<Object, Object> h = new LinkedHashMap<Object, Object>();
			while (true) {
				Object key = decode(is);
				if (key == null)
					return h;
				else
					h.put(key, decode(is));
			}

		} else {
			// Something else
			return null;
		}
	}
}
