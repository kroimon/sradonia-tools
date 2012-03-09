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
package net.sradonia.servers.tcpserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Special {@link Socket} derivative to link it to the generating {@link TcpServer}.
 * 
 * @author Stefan Rado
 */
public class TcpServerSocket extends Socket {

	private TcpServer parent;
	private boolean calledOnDisconnect;

	public TcpServerSocket(TcpServer parent) {
		this.parent = parent;
	}

	public TcpServerSocket(TcpServer parent, InetAddress address, int port) throws IOException {
		super(address, port);
		this.parent = parent;
	}

	public TcpServerSocket(TcpServer parent, String host, int port) throws UnknownHostException, IOException {
		super(host, port);
		this.parent = parent;
	}

	public void close(boolean wait) throws IOException {
		if (wait)
			close(200);
		else
			close();
	}

	public void close(int wait) throws IOException {
		if (wait > 0)
			try {
				Thread.sleep(wait);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		close();
	}

	@Override
	public void close() throws IOException {
		super.close();
		if (!calledOnDisconnect) {
			parent.callOnDisconnect(this);
			calledOnDisconnect = true;
		}
	}

	public TcpServer getTcpServer() {
		return parent;
	}

}
