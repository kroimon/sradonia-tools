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
package net.sradonia.eventbus;

/**
 * <p>
 * This interface marks classes that can subscribe for events using an {@link EventBus}.
 * </p>
 * 
 * <p>
 * Another way is using annotations in the {@link net.sradonia.eventbus.annotations} package.
 * </p>
 * 
 * @author Stefan Rado
 */
public interface EventSubscriber {

	/**
	 * Method called by an {@link EventBus} instance to deliver subscribed events.
	 * 
	 * @param topic
	 *            the topic the event was published under
	 * @param event
	 *            the event object
	 */
	public void onEvent(String topic, Object event);

}
