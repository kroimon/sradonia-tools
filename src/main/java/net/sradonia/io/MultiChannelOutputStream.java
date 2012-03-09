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

import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>
 * OutputStream to multiplex multiple data channels onto one stream. Data will be send in packets containing the channel id.
 * </p>
 * 
 * <p>
 * The packet format:
 * <table>
 * <tr>
 * <th>Offset</th>
 * <th>Length</th>
 * <th>Content</th>
 * </tr>
 * <tr>
 * <td>0</td>
 * <td>1 byte</td>
 * <td>SOH (Start of Heading) - 0x01</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>4 bytes</td>
 * <td>Channel number (integer)</td>
 * </tr>
 * <tr>
 * <td>5</td>
 * <td>4 bytes</td>
 * <td>Length x of the data field in bytes (integer)</td>
 * </tr>
 * <tr>
 * <td>9</td>
 * <td>1 byte</td>
 * <td>STX (Start of Text) - 0x02</td>
 * </tr>
 * <tr>
 * <td>10</td>
 * <td>x bytes</td>
 * <td>The wrapped data</td>
 * </tr>
 * </table>
 * </p>
 * 
 * <p>
 * The packets can be read with an {@link MultiChannelInputStream}
 * </p>
 * 
 * @see MultiChannelInputStream
 * @author Stefan Rado
 */
public class MultiChannelOutputStream extends OutputStream {

	private final OutputStream os;
	private int defaultChannel;

	public MultiChannelOutputStream(OutputStream os) {
		this(os, 0);
	}

	public MultiChannelOutputStream(OutputStream os, int defaultChannel) {
		this.os = os;
		this.defaultChannel = defaultChannel;
	}

	public int getDefaultChannel() {
		return defaultChannel;
	}

	public void setDefaultChannel(byte defaultChannel) {
		this.defaultChannel = defaultChannel;
	}

	private void writeHeader(int channel, int len) throws IOException {
		os.write(0x01); // SOH (Start of Heading)
		writeIntegerBytes(channel); // Channel
		writeIntegerBytes(len); // Length
		os.write(0x02); // STX (Start of Text)
	}

	private void writeIntegerBytes(int i) throws IOException {
		os.write((i >>> 24) & 0xff);
		os.write((i >>> 16) & 0xff);
		os.write((i >>> 8) & 0xff);
		os.write((i) & 0xff);
	}

	@Override
	public void write(int b) throws IOException {
		write(defaultChannel, b);
	}

	public void write(int channel, int b) throws IOException {
		synchronized (os) {
			writeHeader(channel, 1);
			os.write((byte) b);
		}
	}

	public void write(int channel, byte[] b) throws IOException {
		write(channel, b, 0, b.length);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		write(defaultChannel, b, off, len);
	}

	public void write(int channel, byte[] b, int off, int len) throws IOException {
		synchronized (os) {
			writeHeader(channel, len);
			os.write(b, off, len);
		}
	}

	@Override
	public void flush() throws IOException {
		synchronized (os) {
			os.flush();
		}
	}

	@Override
	public void close() throws IOException {
		synchronized (os) {
			os.close();
		}
	}

}
