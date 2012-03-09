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

/**
 * A resizable byte array. Modified for own purposes by Stefan Rado
 * 
 * @author Originally by <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 * @author modified by Stefan Rado
 */
public final class ByteArrayBuffer {

	private byte[] buffer;
	private int len;

	public ByteArrayBuffer(int capacity) {
		if (capacity < 0) {
			throw new IllegalArgumentException("Buffer capacity may not be negative");
		}
		this.buffer = new byte[capacity];
	}

	private void expand(int newlen) {
		byte newbuffer[] = new byte[Math.max(this.buffer.length * 2, newlen)];
		System.arraycopy(this.buffer, 0, newbuffer, 0, this.len);
		this.buffer = newbuffer;
	}

	public void append(final byte[] b, int off, int len) {
		if (b == null || len == 0) {
			return;
		}
		if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) < 0) || ((off + len) > b.length)) {
			throw new IndexOutOfBoundsException();
		}
		int newlen = this.len + len;
		if (newlen > this.buffer.length) {
			expand(newlen);
		}
		System.arraycopy(b, off, this.buffer, this.len, len);
		this.len = newlen;
	}

	public void append(final byte[] b) {
		append(b, 0, b.length);
	}

	public void append(int b) {
		int newlen = this.len + 1;
		if (newlen > this.buffer.length) {
			expand(newlen);
		}
		this.buffer[this.len] = (byte) b;
		this.len = newlen;
	}

	public void append(final char[] b, int off, int len) {
		if (b == null) {
			return;
		}
		if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) < 0) || ((off + len) > b.length)) {
			throw new IndexOutOfBoundsException();
		}
		if (len == 0) {
			return;
		}
		int oldlen = this.len;
		int newlen = oldlen + len;
		if (newlen > this.buffer.length) {
			expand(newlen);
		}
		for (int i1 = off, i2 = oldlen; i2 < newlen; i1++, i2++) {
			this.buffer[i2] = (byte) b[i1];
		}
		this.len = newlen;
	}

	public void clear() {
		this.len = 0;
	}

	public byte[] toByteArray() {
		byte[] b = new byte[this.len];
		if (this.len > 0) {
			System.arraycopy(this.buffer, 0, b, 0, this.len);
		}
		return b;
	}

	public int byteAt(int i) {
		return this.buffer[i];
	}

	public int capacity() {
		return this.buffer.length;
	}

	public int length() {
		return this.len;
	}

	public void setLength(int len) {
		if (len < 0 || len > this.buffer.length) {
			throw new IndexOutOfBoundsException();
		}
		this.len = len;
	}

	public boolean isEmpty() {
		return this.len == 0;
	}

	public boolean isFull() {
		return this.len == this.buffer.length;
	}

}
