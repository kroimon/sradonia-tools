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

import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;

import net.sradonia.servers.Server;
import net.sradonia.threads.DaemonThreadFactory;
import net.sradonia.threads.RenamingThreadFactory;

/**
 * Simple UDP Server with possibility to start/stop and restart.
 * 
 * It's running either as a deamon or as a normal thread. When a packet is received the associated {@link UdpServerListener} will be called to handle
 * it. When the port is set to 0 when the server is started, any available port will be used which can later be found via {@link #getPort()}.
 * 
 * @author Stefan Rado
 */
public class UdpServer extends Server<UdpServerListener> {

	protected int maxPacketSize = 2048;

	protected DatagramSocket socket;

	/**
	 * Creates a new <code>DatagramServer</code> instance with the given port and listener and sets the threadmode to daemon.
	 * 
	 * @param port
	 *            the UDP port this server will listen on
	 * @param listener
	 *            the Listener handling the events
	 */
	public UdpServer(int port, UdpServerListener listener) {
		this(port, listener, true);
	}

	/**
	 * Creates a new <code>DatagramServer</code> instance with the given port and listener and sets the threadmode.
	 * 
	 * @param port
	 *            the UDP port this server will listen on
	 * @param listener
	 *            the Listener handling the events
	 * @param daemon
	 *            whether the serverthread should be started as a daemon or not
	 * @see #setPort(int)
	 * @see #setListener(UdpServerListener)
	 * @see #setDaemon(boolean)
	 */
	public UdpServer(int port, UdpServerListener listener, boolean daemon) {
		setPort(port);
		setListener(listener);
		setDaemon(daemon);
	}

	/**
	 * Starts/Stops the server.
	 * 
	 * @param on
	 *            true=start / false=stop
	 * @see #isRunning()
	 */
	public void setRunning(boolean on) {
		super.setRunning(on);
		if (thread != null && thread.isInterrupted() && socket != null)
			socket.close();
	}

	/**
	 * Returns the maximum size of packets in bytes that will be received. Packets with a larger size will be truncated to this size.
	 * 
	 * @return the maximum size of received packets
	 */
	public int getMaxPacketSize() {
		return maxPacketSize;
	}

	/**
	 * Sets the maximum size (in bytes) of packets being received. Packets with a larger size will be truncated to this size. This value can be set to
	 * a value between 1 and 65507 (bytes). The default value is 2048.
	 * 
	 * @param maxPacketSize
	 *            the new maximum packet size.
	 */
	public void setMaxPacketSize(int maxPacketSize) {
		if (maxPacketSize < 1 || maxPacketSize > 65507)
			throw new IndexOutOfBoundsException("maxPacketSize has to be between 1 and 65507");
		this.maxPacketSize = maxPacketSize;
	}

	protected class PacketHandler implements Runnable {
		UdpServer parent;
		DatagramPacket packet;

		public PacketHandler(UdpServer parent, DatagramPacket packet) {
			this.parent = parent;
			this.packet = packet;
		}

		public void run() {
			byte[] data = new byte[packet.getLength()];
			System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
			packet.setData(data);
			parent.callOnReceivedPacket(packet);
		}
	}

	@Override
	protected void runServer() {
		try {
			if (port != 0)
				socket = new DatagramSocket(port);
			else
				socket = new DatagramSocket();
			port = socket.getLocalPort();

			callOnServerStarted();

			ExecutorService threadPool = Executors.newCachedThreadPool(new DaemonThreadFactory(new RenamingThreadFactory(Thread.currentThread()
					.getName() + "-HandlerThread-"), daemon));
			DatagramPacket packet;
			while (!Thread.currentThread().isInterrupted()) {
				try {
					packet = new DatagramPacket(new byte[maxPacketSize], maxPacketSize);
					socket.receive(packet);
					threadPool.execute(new PacketHandler(this, packet));
				} catch (IOException e) {
					if (!Thread.currentThread().isInterrupted())
						callOnIOException(e, "Error while receiving");
				}
			}
			threadPool.shutdown();

			callOnServerStopped();

		} catch (BindException e) {
			thread = null;
			callOnIOException(e, "Can't bind port");
		} catch (IOException e) {
			thread = null;
			callOnIOException(e, "Couldn't open socket on port " + port + "!");
		}
	}

	/* Listener callers */
	protected void callOnServerStarted() {
		try {
			listener.onServerStarted(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void callOnServerStopped() {
		try {
			listener.onServerStopped(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void callOnIOException(IOException e, String info) {
		try {
			listener.onIOException(this, e, info);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	protected void callOnReceivedPacket(DatagramPacket packet) {
		try {
			listener.onReceivedPacket(this, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void callOnSentPacket(DatagramPacket packet, boolean overSocket) {
		try {
			listener.onSentPacket(this, packet, overSocket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends a UDP packet with the given byte array to the given port on the given host.
	 * 
	 * @param host
	 *            the target host
	 * @param port
	 *            the target UDP port
	 * @param data
	 *            the byte array to send
	 */
	public void sendPacket(InetAddress host, int port, byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, host, port);
		try {
			if (isRunning() && socket != null) {
				socket.send(packet);
				callOnSentPacket(packet, true);
			} else {
				DatagramSocket fallbackSocket = new DatagramSocket();
				fallbackSocket.send(packet);
				fallbackSocket.close();
				callOnSentPacket(packet, false);
			}
		} catch (IOException e) {
			callOnIOException(e, "Error while sending: " + new String(data));
		}
	}

	/**
	 * Sends a UDP packet with the given String to the given port on the given host.
	 * 
	 * @param host
	 *            the target host
	 * @param port
	 *            the target UDP port
	 * @param msg
	 *            the String to send
	 */
	public void sendPacket(InetAddress host, int port, String msg) {
		sendPacket(host, port, msg.getBytes());
	}

	/**
	 * Sends a UDP packet with the given byte array to the broadcast address <code>255.255.255.255</code>.
	 * 
	 * @param port
	 *            the target port
	 * @param data
	 *            the byte array to send
	 */
	synchronized public void sendBroadcastPacket(int port, byte[] data) {
		try {
			sendPacket(InetAddress.getByName("255.255.255.255"), port, data);
		} catch (UnknownHostException e) {
		}
	}

	/**
	 * Sends a UDP packet with the given String to the broadcast address <code>255.255.255.255</code>.
	 * 
	 * @param port
	 *            the target port
	 * @param msg
	 *            the String to send
	 */
	synchronized public void sendBroadcastPacket(int port, String msg) {
		sendBroadcastPacket(port, msg.getBytes());
	}

}
