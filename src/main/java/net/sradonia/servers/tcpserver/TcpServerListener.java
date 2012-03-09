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

/**
 * The listener interface for receiving events of a running {@link TcpServer} instance. The class interested in processing these events implements
 * this interface and registers itself to the {@link TcpServer} either via the constructor or via the {@link TcpServer#setListener(TcpServerListener)}
 * method.
 * 
 * @author Stefan Rado
 */
public interface TcpServerListener {

	/**
	 * Is called when the {@link TcpServer} starts listening.
	 * 
	 * @param src
	 *            the source of the event
	 */
	public void onServerStarted(TcpServer src);

	/**
	 * Is called when the {@link TcpServer} stops listening.
	 * 
	 * @param src
	 *            the source of the event
	 */
	public void onServerStopped(TcpServer src);

	/**
	 * Is called when the Listener should handle a new connection.
	 * 
	 * @param src
	 *            the source of the event
	 * @param connection
	 *            the newly connected {@link TcpServerConnection}
	 */
	public void onConnect(TcpServer src, TcpServerSocket connection);

	/**
	 * Is called when the connection is closed.
	 * 
	 * @param src
	 *            the source of the event
	 * @param connection
	 *            the closed {@link TcpServerConnection}
	 */
	public void onDisconnect(TcpServer src, TcpServerSocket connection);

	/**
	 * Is called when an {@link IOException} occured during the run of the server. Additional infos can be found in the <tt>info</tt> argument.
	 * 
	 * @param src
	 *            the source of the event
	 * @param connection
	 *            the {@link TcpServerConnection}, if given. If null, the exception was raised by the server.
	 * @param e
	 *            the reason for the event
	 * @param info
	 *            an additional message describing the situation when the exception was thrown
	 */
	public void onIOException(TcpServer src, TcpServerSocket connection, IOException e, String info);
}
