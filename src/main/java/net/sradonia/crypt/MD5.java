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
package net.sradonia.crypt;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * @author Stefan Rado
 */
public class MD5 {
	private MessageDigest md;
	private Random rnd;

	private final static char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public MD5() throws NoSuchAlgorithmException {
		md = MessageDigest.getInstance("MD5");
	}

	public void update(byte input) {
		md.update(input);
	}

	public void update(byte[] input) {
		md.update(input);
	}

	public void update(byte[] input, int offset, int len) {
		md.update(input, offset, len);
	}

	public void update(String input) {
		md.update(input.getBytes());
	}

	public byte[] digest() {
		return md.digest();
	}

	public byte[] digest(byte input) {
		md.update(input);
		return md.digest();
	}

	public byte[] digest(byte[] input) {
		return md.digest(input);
	}

	public int digest(byte[] buf, int offset, int len) throws DigestException {
		return md.digest(buf, offset, len);
	}

	public byte[] digest(String input) {
		md.update(input.getBytes());
		return md.digest();
	}

	public String digestString() {
		return digest2string(digest());
	}

	public String digestString(byte input) {
		return digest2string(digest(input));
	}

	public String digestString(byte[] input) {
		return digest2string(digest(input));
	}

	public String digestString(String input) {
		return digest2string(digest(input));
	}

	public void reset() {
		md.reset();
	}

	/**
	 * Encodes a 128 bit (16 bytes) MD5 into a 32 character hexadecimal String.
	 * 
	 * @param binaryDigest
	 *            Array containing the digest
	 * @return Encoded MD5, or null if encoding failed
	 */
	public static String digest2string(byte[] binaryDigest) {
		if (binaryDigest.length != 16)
			return null;
		StringBuffer sb = new StringBuffer(32);
		for (int i = 0; i < 16; i++) {
			sb.append(HEX_CHARS[(binaryDigest[i] >> 4) & 0xf]);
			sb.append(HEX_CHARS[binaryDigest[i] & 0xf]);
		}
		return sb.toString();
	}

	public String randomDigest() {
		if (rnd == null)
			rnd = new Random();
		return digestString("" + rnd.nextLong());
	}
}
