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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This {@link ThreadFactory} implementation takes a Thread either from a given parent ThreadFactory instance or a newly created
 * {@link SimpleThreadFactory} and renames it.
 * 
 * @author Stefan Rado
 */
public class RenamingThreadFactory implements ThreadFactory {

	private ThreadFactory threadFactory;
	private String name;
	private AtomicInteger count;

	public RenamingThreadFactory(ThreadFactory threadFactory, String name) {
		this.threadFactory = threadFactory;
		this.name = name;
		count = new AtomicInteger(1);
	}

	public RenamingThreadFactory(String name) {
		this(new SimpleThreadFactory(), name);
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread newThread = threadFactory.newThread(r);
		newThread.setName(name + count.getAndIncrement());
		return newThread;
	}

}
