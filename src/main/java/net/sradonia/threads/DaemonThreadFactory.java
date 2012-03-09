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
package net.sradonia.threads;

import java.util.concurrent.ThreadFactory;

/**
 * This {@link ThreadFactory} implementation takes a Thread either from a given parent ThreadFactory instance or a newly created
 * {@link SimpleThreadFactory} and sets it to run as a daemon.
 * 
 * @author Stefan Rado
 */
public class DaemonThreadFactory implements ThreadFactory {

	private ThreadFactory threadFactory;
	private boolean daemon;

	public DaemonThreadFactory(ThreadFactory threadFactory, boolean daemon) {
		this.threadFactory = threadFactory;
		this.daemon = daemon;
	}

	public DaemonThreadFactory(ThreadFactory threadFactory) {
		this(threadFactory, true);
	}

	public DaemonThreadFactory(boolean daemon) {
		this(new SimpleThreadFactory(), daemon);
	}

	public DaemonThreadFactory() {
		this(new SimpleThreadFactory(), true);
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread newThread = threadFactory.newThread(r);
		newThread.setDaemon(daemon);
		return newThread;
	}
}
