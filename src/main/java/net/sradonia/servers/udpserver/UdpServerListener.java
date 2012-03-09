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
package net.sradonia.servers.udpserver;

import java.io.IOException;
import java.net.DatagramPacket;

/**
 * The listener interface for receiving events of a running {@link UdpServer} instance. The class interested in processing these events implements
 * this interface and registers itself to the {@link UdpServer} either via the constructor or via the {@link UdpServer#setListener(UdpServerListener)}
 * method.
 * 
 * @author Stefan Rado
 */
public interface UdpServerListener {

	/**
	 * Is called when the {@link UdpServer} starts listening.
	 * 
	 * @param src
	 *            the source of the event
	 */
	public void onServerStarted(UdpServer src);

	/**
	 * Is called when the {@link UdpServer} stops listening.
	 * 
	 * @param src
	 *            the source of the event
	 */
	public void onServerStopped(UdpServer src);

	/**
	 * Is called when a UDP packet is received.
	 * 
	 * @param src
	 *            the source of the event
	 * @param packet
	 *            the received packet
	 */
	public void onReceivedPacket(UdpServer src, DatagramPacket packet);

	/**
	 * Is called when a UDP packet has been sent.
	 * 
	 * @param src
	 *            the source of the event
	 * @param packet
	 *            the sent packed
	 */
	public void onSentPacket(UdpServer src, DatagramPacket packet, boolean overSocket);

	/**
	 * Is called when an {@link IOException} occured during the run of the server. Additional infos can be found in the <tt>info</tt> argument.
	 * 
	 * @param src
	 *            the source of the event
	 * @param e
	 *            the reason for the event
	 * @param info
	 *            an additional message describing the situation when the exception was thrown
	 */
	public void onIOException(UdpServer src, IOException e, String info);
}
