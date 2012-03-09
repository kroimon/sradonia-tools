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
import java.io.InputStream;

/**
 * <p>
 * InputStream to demultiplex multiple data channels from one stream.
 * </p>
 * <p>
 * For packet documentation see {@link MultiChannelOutputStream}.
 * </p>
 * 
 * @see MultiChannelOutputStream
 * @author Stefan Rado
 */
public class MultiChannelInputStream {

	public class Packet {
		private int channel;
		private byte[] data;

		private Packet(int channel, byte[] data) {
			this.channel = channel;
			this.data = data;
		}

		public byte[] getData() {
			return data;
		}

		public int getType() {
			return channel;
		}

		@Override
		public String toString() {
			return channel + ": " + new String(data);
		}
	}

	private InputStream is;

	public MultiChannelInputStream(InputStream is) {
		this.is = is;
	}

	public Packet readPacket() throws IOException {
		synchronized (is) {
			byte[] header = new byte[10];
			if (is.read(header) == -1)
				return null;

			if (header[0] != 0x01) // SOH (Start of Heading)
				throw new IOException("Packet header didn't start with SOH (0x01)");
			if (header[9] != 0x02) // STX (Start of Text)
				throw new IOException("Packet header didn't end with STX (0x02)");

			int channel = (((header[1] & 0xff) << 24) | ((header[2] & 0xff) << 16) | ((header[3] & 0xff) << 8) | (header[4] & 0xff));

			int length = (((header[5] & 0xff) << 24) | ((header[6] & 0xff) << 16) | ((header[7] & 0xff) << 8) | (header[8] & 0xff));

			byte[] data = new byte[length];
			is.read(data);

			return new Packet(channel, data);
		}
	}

}
