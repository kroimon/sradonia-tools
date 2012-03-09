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
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import net.sradonia.servers.Server;
import net.sradonia.threads.DaemonThreadFactory;
import net.sradonia.threads.RenamingThreadFactory;

/**
 * Simple TCP Server with possibility to start/stop and restart.
 * 
 * It's running either as a deamon or as a normal thread.
 * 
 * When a new connection is established a newly created {@link TcpServerServerSocket}-Instance will call the associated {@link TcpServerListener} to
 * handle the connection. The TCP connection will remain open after the handler has finished until the explicit call of the
 * {@link TcpServerServerSocket#close()} method.
 * 
 * When the port is set to 0 when the server is started, any available port will be used which can be found via the {@link #getPort()} method.
 * 
 * @author Stefan Rado
 */
public class TcpServer extends Server<TcpServerListener> {

	protected TcpServerServerSocket socket;
	protected boolean keepAlive = true;

	/**
	 * Creates a new <code>TcpServer</code> instance with the given port and listener and sets the threadmode to daemon.
	 * 
	 * @param port
	 *            the TCP port this server will listen on
	 * @param listener
	 *            the Listener handling the events
	 */
	public TcpServer(int port, TcpServerListener listener) {
		this(port, listener, true);
	}

	/**
	 * Creates a new <code>TcpServer</code> instance with the given port and listener and sets the threadmode.
	 * 
	 * @param port
	 *            the TCP port this server will listen on
	 * @param listener
	 *            the Listener handling the events
	 */
	public TcpServer(int port, TcpServerListener listener, boolean daemon) {
		setPort(port);
		setListener(listener);
		setDaemon(daemon);
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	/**
	 * Starts/Stops the server.
	 * 
	 * @param on
	 *            true=start / false=stop
	 */
	@Override
	public void setRunning(boolean on) {
		super.setRunning(on);
		if (thread.isInterrupted() && socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}

	protected class ConnectionHandler implements Runnable {
		TcpServer parent;
		TcpServerSocket connection;

		public ConnectionHandler(TcpServer parent, TcpServerSocket connection) {
			this.parent = parent;
			this.connection = connection;
		}

		public void run() {
			try {
				parent.callOnConnect(connection);
			} finally {
				if (!parent.isKeepAlive()) {
					try {
						connection.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	protected class TcpServerServerSocket extends ServerSocket {

		protected TcpServer parent;

		public TcpServerServerSocket(TcpServer parent, int port) throws IOException {
			super(port);
			this.parent = parent;
		}

		@Override
		public TcpServerSocket accept() throws IOException {
			if (isClosed())
				throw new SocketException("Socket is closed");
			if (!isBound())
				throw new SocketException("Socket is not bound yet");
			TcpServerSocket s = new TcpServerSocket(parent);
			implAccept(s);
			return s;
		}
	}

	@Override
	protected void runServer() {
		ExecutorService threadPool = null;
		try {
			socket = new TcpServerServerSocket(this, port);
			port = socket.getLocalPort();
			socket.setSoTimeout(0); // wait forever

			callOnServerStarted();

			ThreadFactory factory = new DaemonThreadFactory(new RenamingThreadFactory(Thread.currentThread().getName() + "-HandlerThread-"), daemon);
			threadPool = Executors.newCachedThreadPool(factory);
			while (!Thread.currentThread().isInterrupted()) {
				try {
					threadPool.execute(new ConnectionHandler(this, socket.accept()));
				} catch (IOException e) {
					if (!Thread.currentThread().isInterrupted())
						callOnIOException(null, e, "Error while receiving");
				}
			}
		} catch (BindException e) {
			thread = null;
			callOnIOException(null, e, "Can't bind port");
		} catch (IOException e) {
			thread = null;
			callOnIOException(null, e, "Couldn't open ServerSocket on port " + port + "!");
		} finally {
			if (threadPool != null)
				threadPool.shutdown();

			callOnServerStopped();
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

	protected void callOnConnect(TcpServerSocket connection) {
		try {
			listener.onConnect(this, connection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void callOnDisconnect(TcpServerSocket connection) {
		try {
			listener.onDisconnect(this, connection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void callOnIOException(TcpServerSocket connection, IOException e, String msg) {
		try {
			listener.onIOException(this, connection, e, msg);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Opens a new socket, connects it to the given address and port and returns the new {@link TcpServerServerSocket} associated with this server
	 * instance.
	 * 
	 * @param address
	 *            the address to connect to
	 * @param port
	 *            the port to connect to
	 * @return the newly opened connection
	 * @throws IOException
	 *             if an I/O error occurs when creating the socket
	 */
	public TcpServerSocket connect(InetAddress address, int port) throws IOException {
		return new TcpServerSocket(this, address, port);
	}
}
