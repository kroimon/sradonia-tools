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

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Fast LineInput InputStream. This buffered InputStream provides methods for reading lines of bytes. The lines can be converted to String or
 * character arrays either using the default encoding or a user supplied encoding.
 * 
 * Buffering and data copying are highly optimized, making this an ideal class for protocols that mix character encoding lines with arbitrary byte
 * data (eg HTTP).
 * 
 * The buffer size is also the maximum line length in bytes and/or characters. If the byte length of a line is less than the max, but the character
 * length is greater, than then trailing characters are lost.
 * 
 * Line termination is forgiving and accepts CR, LF, CRLF or EOF. Line input uses the mark/reset mechanism, so any marks set prior to a readLine call
 * are lost.
 * 
 * @author Greg Wilkins (gregw)
 */
public class LineInput extends FilterInputStream {
	private byte buf[];
	private ByteBuffer byteBuffer;
	private InputStreamReader reader;
	private int mark = -1; // reset marker
	private int pos; // Start marker
	private int avail; // Available back marker, may be byte limited
	private int contents; // Absolute back marker of buffer
	private int byteLimit = -1;
	private boolean newByteLimit;
	private LineBuffer lineBuffer;
	private String encoding;
	private boolean eof = false;
	private boolean lastCr = false;
	private boolean seenCrLf = false;

	private final static int defaultBufferSize = 8192;

	private final static int LF = 10;
	private final static int CR = 13;

	/**
	 * Creates a new LineInput with the specified underlying InputStream source and the default buffer size.
	 * 
	 * @param in
	 *            The underlying input stream
	 */
	public LineInput(InputStream in) {
		this(in, defaultBufferSize);
	}

	/**
	 * Creates a new LineInput with the specified underlying InputStream source and the given buffer size.
	 * 
	 * @param in
	 *            The underlying input stream
	 * @param bufferSize
	 *            The buffer size and maximum line length
	 */
	public LineInput(InputStream in, int bufferSize) {
		super(in);
		if (bufferSize <= 0)
			bufferSize = defaultBufferSize;
		buf = new byte[bufferSize];
		byteBuffer = new ByteBuffer(buf);
		lineBuffer = new LineBuffer(bufferSize);
		try {
			reader = new InputStreamReader(byteBuffer, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			reader = new InputStreamReader(byteBuffer);
		}
	}

	/**
	 * Creates a new LineInput with the specified underlying InputStream source, the given buffer size and the character encoding to use for readLine
	 * methods.
	 * 
	 * @param in
	 *            The underlying input stream
	 * @param bufferSize
	 *            The buffer size and maximum line length
	 * @param encoding
	 *            The character encoding to use for readLine methods
	 * @throws UnsupportedEncodingException
	 */
	public LineInput(InputStream in, int bufferSize, String encoding) throws UnsupportedEncodingException {
		super(in);
		if (bufferSize <= 0)
			bufferSize = defaultBufferSize;
		buf = new byte[bufferSize];
		byteBuffer = new ByteBuffer(buf);
		lineBuffer = new LineBuffer(bufferSize);
		reader = new InputStreamReader(byteBuffer, encoding);
		this.encoding = encoding;
	}

	public InputStream getInputStream() {
		return in;
	}

	/**
	 * Set the byte limit. If set, only this number of bytes are read before EOF.
	 * 
	 * @param bytes
	 *            Limit number of bytes, or -1 for no limit
	 */
	public void setByteLimit(int bytes) {
		byteLimit = bytes;

		if (bytes >= 0) {
			newByteLimit = true;
			byteLimit -= contents - pos;
			if (byteLimit < 0) {
				avail += byteLimit;
				byteLimit = 0;
			}
		} else {
			newByteLimit = false;
			avail = contents;
			eof = false;
		}
	}

	/**
	 * Get the byte limit.
	 * 
	 * @return Number of bytes until EOF is returned or -1 for no limit
	 */
	public int getByteLimit() {
		if (byteLimit < 0)
			return byteLimit;
		return byteLimit + avail - pos;
	}

	/**
	 * Read a line ended by CR, LF or CRLF. The default or supplied encoding is used to convert bytes to characters.
	 * 
	 * @return The line as a String or null for EOF
	 * @exception IOException
	 */
	public synchronized String readLine() throws IOException {
		int len = fillLine(buf.length);

		if (len < 0)
			return null;

		String s = null;
		if (encoding == null) {
			s = new String(buf, mark, len);
		} else {
			try {
				s = new String(buf, mark, len, encoding);
			} catch (UnsupportedEncodingException e) {
				// log.warn(LogSupport.EXCEPTION,e);
			}
		}
		mark = -1;

		return s;
	}

	/**
	 * Read a line ended by CR, LF or CRLF. The default or supplied encoding is used to convert bytes to characters.
	 * 
	 * @param c
	 *            Character buffer to place the line into
	 * @param off
	 *            Offset into the buffer
	 * @param len
	 *            Maximum length of line
	 * @return The length of the line or -1 for EOF
	 * @throws IOException
	 */
	public int readLine(char[] c, int off, int len) throws IOException {
		int blen = fillLine(len);

		if (blen < 0)
			return -1;
		if (blen == 0)
			return 0;

		byteBuffer.setStream(mark, blen);

		int read = 0;
		while (read < len && reader.ready()) {
			int r = reader.read(c, off + read, len - read);
			if (r <= 0)
				break;
			read += r;
		}

		mark = -1;

		return read;
	}

	/**
	 * Read a line ended by CR, LF or CRLF.
	 * 
	 * @param b
	 *            Byte array to place the line into
	 * @param off
	 *            Offset into the buffer
	 * @param len
	 *            Maximum length of line
	 * @return The length of the line or -1 for EOF
	 * @throws IOException
	 */
	public int readLine(byte[] b, int off, int len) throws IOException {
		len = fillLine(len);

		if (len < 0)
			return -1;
		if (len == 0)
			return 0;

		System.arraycopy(buf, mark, b, off, len);
		mark = -1;

		return len;
	}

	/**
	 * Read a Line ended by CR, LF or CRLF. Read a line into a shared LineBuffer instance. The LineBuffer is resused between calls and should not be
	 * held by the caller. The default or supplied encoding is used to convert bytes to characters.
	 * 
	 * @return LineBuffer instance or null for EOF
	 * @throws IOException
	 */
	public LineBuffer readLineBuffer() throws IOException {
		return readLineBuffer(buf.length);
	}

	/**
	 * Read a Line ended by CR, LF or CRLF. Read a line into a shared LineBuffer instance. The LineBuffer is resused between calls and should not be
	 * held by the caller. The default or supplied encoding is used to convert bytes to characters.
	 * 
	 * @param len
	 *            Maximum length of a line, or 0 for default
	 * @return LineBuffer instance or null for EOF
	 * @throws IOException
	 */
	public LineBuffer readLineBuffer(int len) throws IOException {
		len = fillLine(len > 0 ? len : buf.length);

		if (len < 0)
			return null;

		if (len == 0) {
			lineBuffer.size = 0;
			return lineBuffer;
		}

		byteBuffer.setStream(mark, len);

		lineBuffer.size = 0;
		int read = 0;
		while (read < len && reader.ready()) {
			int r = reader.read(lineBuffer.buffer, read, len - read);
			if (r <= 0)
				break;
			read += r;
		}
		lineBuffer.size = read;
		mark = -1;

		return lineBuffer;
	}

	public synchronized int read() throws IOException {
		int b;
		if (pos >= avail)
			fill();
		if (pos >= avail)
			b = -1;
		else
			b = buf[pos++] & 255;

		return b;
	}

	public synchronized int read(byte b[], int off, int len) throws IOException {
		int avail = this.avail - pos;
		if (avail <= 0) {
			fill();
			avail = avail - pos;
		}

		if (avail <= 0) {
			len = -1;
		} else {
			len = (avail < len) ? avail : len;
			System.arraycopy(buf, pos, b, off, len);
			pos += len;
		}

		return len;
	}

	public long skip(long n) throws IOException {
		int avail = this.avail - pos;
		if (avail <= 0) {
			fill();
			avail = avail - pos;
		}

		if (avail <= 0) {
			n = 0;
		} else {
			n = (avail < n) ? avail : n;
			pos += n;
		}

		return n;
	}

	public synchronized int available() throws IOException {
		int in_stream = in.available();
		if (byteLimit >= 0 && in_stream > byteLimit)
			in_stream = byteLimit;

		return avail - pos + in_stream;
	}

	public synchronized void mark(int limit) throws IllegalArgumentException {
		if (limit > buf.length) {
			byte[] new_buf = new byte[limit];
			System.arraycopy(buf, pos, new_buf, pos, avail - pos);
			buf = new_buf;
			if (byteBuffer != null)
				byteBuffer.setBuffer(buf);
		}
		mark = pos;
	}

	public synchronized void reset() throws IOException {
		if (mark < 0)
			throw new IOException("Resetting to invalid mark");
		pos = mark;
		mark = -1;
	}

	public boolean markSupported() {
		return true;
	}

	private void fill() throws IOException {
		// if the mark is in the middle of the buffer
		if (mark > 0) {
			// moved saved bytes to start of buffer
			int saved = contents - mark;
			System.arraycopy(buf, mark, buf, 0, saved);
			pos -= mark;
			avail -= mark;
			contents = saved;
			mark = 0;
		} else if (mark < 0 && pos > 0) {
			// move remaining bytes to start of buffer
			int saved = contents - pos;
			System.arraycopy(buf, pos, buf, 0, saved);
			avail -= pos;
			contents = saved;
			pos = 0;
		} else if (mark == 0 && pos > 0 && contents == buf.length) {
			// Discard the mark as we need the space.
			mark = -1;
			fill();
			return;
		}

		// Get ready to top up the buffer
		int n = 0;
		eof = false;

		// Handle byte limited EOF
		if (byteLimit == 0) {
			eof = true;
		}
		// else loop until something is read.
		else
			while (!eof && n == 0 && buf.length > contents) {
				// try to read as much as will fit.
				int space = buf.length - contents;

				n = in.read(buf, contents, space);

				if (n <= 0) {
					// If no bytes - we could be NBIO, so we want to avoid
					// a busy loop.
					if (n == 0) {
						// Yield to give a chance for some bytes to turn up
						Thread.yield();

						// Do a byte read as that is blocking
						int b = in.read();
						if (b >= 0) {
							n = 1;
							buf[contents++] = (byte) b;
						} else {
							eof = true;
						}
					} else {
						eof = true;
					}
				} else {
					contents += n;
				}
				avail = contents;

				// If we have a byte limit
				if (byteLimit > 0) {
					// adjust the bytes available
					if (contents - pos >= byteLimit)
						avail = byteLimit + pos;

					if (n > byteLimit)
						byteLimit = 0;
					else if (n >= 0)
						byteLimit -= n;
					else if (n == -1)
						throw new IOException("Premature EOF");
				}
			}

		// If we have some characters and the last read was a CR and
		// the first char is a LF, skip it
		if (avail - pos > 0 && lastCr && buf[pos] == LF) {
			seenCrLf = true;
			pos++;
			if (mark >= 0)
				mark++;
			lastCr = false;

			// If the byte limit has just been imposed, dont count
			// LF as content.
			if (byteLimit >= 0 && newByteLimit) {
				if (avail < contents)
					avail++;
				else
					byteLimit++;
			}
			// If we ate all that ws filled, fill some more
			if (pos == avail)
				fill();
		}
		newByteLimit = false;
	}

	private int fillLine(int maxLen) throws IOException {
		mark = pos;

		if (pos >= avail)
			fill();
		if (pos >= avail)
			return -1;

		byte b;
		boolean cr = lastCr;
		boolean lf = false;
		lastCr = false;
		int len = 0;

		LineLoop: while (pos <= avail) {
			// if we have gone past the end of the buffer
			while (pos == avail) {
				// If EOF or no more space in the buffer,
				// return a line.
				if (eof || (mark == 0 && contents == buf.length)) {
					lastCr = !eof && buf[avail - 1] == CR;

					cr = true;
					lf = true;
					break LineLoop;
				}

				// If we have a CR and no more characters are available
				if (cr && in.available() == 0 && !seenCrLf) {
					lastCr = true;
					cr = true;
					lf = true;
					break LineLoop;
				} else {
					// Else just wait for more...
					pos = mark;
					fill();
					pos = len;
					cr = false;
				}
			}

			// Get the byte
			b = buf[pos++];

			switch (b) {
			case LF:
				if (cr) {
					seenCrLf = true;
				}
				lf = true;
				break LineLoop;

			case CR:
				if (cr) {
					// Double CR
					if (pos > 1) {
						pos--;
						break LineLoop;
					}
				}
				cr = true;
				break;

			default:
				if (cr) {
					if (pos == 1) {
						cr = false;
					} else {
						pos--;
						break LineLoop;
					}
				}

				len++;
				if (len == maxLen) {
					// look for EOL
					if (mark != 0 && pos + 2 >= avail && avail < buf.length)
						fill();

					if (pos < avail && buf[pos] == CR) {
						cr = true;
						pos++;
					}
					if (pos < avail && buf[pos] == LF) {
						lf = true;
						pos++;
					}

					if (!cr && !lf) {
						// fake EOL
						lf = true;
						cr = true;
					}
					break LineLoop;
				}

				break;
			}
		}

		if (!cr && !lf && len == 0)
			len = -1;

		return len;
	}

	private static class ByteBuffer extends ByteArrayInputStream {
		ByteBuffer(byte[] buffer) {
			super(buffer);
		}

		void setBuffer(byte[] buffer) {
			buf = buffer;
		}

		void setStream(int offset, int length) {
			pos = offset;
			count = offset + length;
			mark = -1;
		}
	}

	/**
	 * Reusable LineBuffer. Externalized LineBuffer for fast line parsing.
	 */
	public static class LineBuffer {
		public char[] buffer;
		public int size;

		public LineBuffer(int maxLineLength) {
			buffer = new char[maxLineLength];
		}

		public String toString() {
			return new String(buffer, 0, size);
		}
	}

	public void destroy() {
		buf = null;
		byteBuffer = null;
		reader = null;
		lineBuffer = null;
		encoding = null;
	}
}
