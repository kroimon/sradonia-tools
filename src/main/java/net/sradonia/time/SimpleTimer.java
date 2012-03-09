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
package net.sradonia.time;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple Timer class to execute {@link Runnable}s after a specified delay. The timer may either be set to be a daemon thread, which means that it
 * will be killed when the last other non-deamon thread terminates, or it may run as a non-daemon thread which guarantees the execution even if all
 * other threads terminated.
 * 
 * @author Stefan
 */
public class SimpleTimer {

	protected static AtomicInteger threadcount = new AtomicInteger(0);
	protected Thread thread;

	protected Runnable task;
	protected long delay;
	protected boolean daemon;

	protected class Task extends Thread {

		// use own interrupt flag to avoid flag-clearing by the called task
		private boolean interrupted = false;

		@Override
		public void interrupt() {
			interrupted = true;
			super.interrupt();
		}

		public void run() {
			do {
				try {
					sleep(delay);
				} catch (InterruptedException e) {
					break;
				}
				try {
					task.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} while (!interrupted);
		}
	}

	/**
	 * Creates a new timer which will run the given task after the specified delay, either as a daemon or as a non-daemon thread.
	 * 
	 * @param task
	 *            the {@link Runnable} to execute
	 * @param delay
	 *            the delay before it is run
	 * @param daemon
	 *            daemon status of the timer thread
	 */
	public SimpleTimer(Runnable task, long delay, boolean daemon) {
		setTask(task);
		setDelay(delay);
		setDaemon(daemon);
	}

	/**
	 * Creates a new timer which will run the given task after the specified delay. The timer thread will be created in non-daemon mode.
	 * 
	 * @param task
	 *            the {@link Runnable} to execute
	 * @param delay
	 *            the delay before it is run
	 */
	public SimpleTimer(Runnable task, long delay) {
		this(task, delay, false);
	}

	/**
	 * Returns the current running state of the timer
	 * 
	 * @return the running state of the timer
	 */
	public boolean isRunning() {
		return (thread != null && thread.isAlive());
	}

	/**
	 * Start/Stops the timer.
	 * 
	 * @param running
	 *            the new running state
	 */
	public void setRunning(boolean running) {
		if (!isRunning() && running) {
			thread = new Task();
			thread.setName("SimpleTimer-" + threadcount.incrementAndGet());
			thread.setDaemon(daemon);
			thread.start();
		} else if (isRunning() && !running) {
			thread.interrupt();
		}
	}

	public Runnable getTask() {
		return task;
	}

	public void setTask(Runnable task) {
		if (task == null)
			throw new IllegalArgumentException("task can't be null!");
		this.task = task;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		if (delay <= 0)
			throw new IllegalArgumentException("delay has to be > 0");
		this.delay = delay;
	}

	public boolean isDaemon() {
		return daemon;
	}

	public void setDaemon(boolean daemon) {
		if (!isRunning())
			this.daemon = daemon;
	}

}
