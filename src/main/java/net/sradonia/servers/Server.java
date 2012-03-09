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
package net.sradonia.servers;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract Server implementation.
 * 
 * @author Stefan Rado
 * @param <ListenerClass>
 *            the class which can be set as listener
 */
public abstract class Server<ListenerClass> {

	protected int port;
	protected boolean daemon;
	protected ListenerClass listener;

	protected Thread thread;
	protected static AtomicInteger threadcount = new AtomicInteger(0);

	/**
	 * Gets the port the server listens on. Reveals nothing about the current running state.
	 * 
	 * @return the port the server listens on
	 * @see #setPort(int)
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * Sets the port the server listens on. Will return without any changes when the server is currently running.
	 * 
	 * @param port
	 *            the new port in the valid range of 0-65535
	 * @see #getPort()
	 */
	public void setPort(int port) {
		if (isRunning())
			return;
		if (port < 0 || port > 65535)
			throw new IllegalArgumentException("Illegal port number");
		this.port = port;
	}

	/**
	 * Returns whether the serverthread should be started as a daemon or not.
	 * 
	 * @return true when the serverthread should be stared as a daemon
	 * @see #setDaemon(boolean)
	 */
	public boolean isDaemon() {
		return daemon;
	}

	/**
	 * Sets whether the serverthread should be started as a daemon or not.
	 * 
	 * @param daemon
	 *            true when the serverthread should be started as a daemon
	 * @see #isDaemon()
	 */
	public void setDaemon(boolean daemon) {
		if (!isRunning())
			this.daemon = daemon;
	}

	/**
	 * Gets the event listener.
	 * 
	 * @return the current listener
	 * @see #setListener(ListenerClass)
	 */
	public ListenerClass getListener() {
		return this.listener;
	}

	/**
	 * Sets the event listener. The new listener can't be null.
	 * 
	 * @param listener
	 *            the new listener
	 * @see #getListener()
	 */
	public void setListener(ListenerClass listener) {
		if (listener == null)
			throw new IllegalArgumentException("listener can't be null");
		this.listener = listener;
	}

	/**
	 * Returns the current running state.
	 * 
	 * @return true when the server is online
	 * @see #setRunning(boolean)
	 */
	public boolean isRunning() {
		return (thread != null && thread.isAlive());
	}

	/**
	 * Starts/Stops the server.
	 * 
	 * @param on
	 *            true=start / false=stop
	 * @see #isRunning()
	 */
	public void setRunning(boolean on) {
		if (on && !isRunning()) {
			thread = new Thread() {
				public void run() {
					runServer();
				}
			};
			thread.setName(this.getClass().getSimpleName() + "-" + threadcount.incrementAndGet());
			thread.setDaemon(daemon);
			thread.start();
		} else if (!on && isRunning()) {
			thread.interrupt();
		}
	}

	/**
	 * This method is called by a separate server thread. It opens the implementation-specific Sockets, handles the incoming connections and passes
	 * them to the listener.
	 */
	protected abstract void runServer();

}
