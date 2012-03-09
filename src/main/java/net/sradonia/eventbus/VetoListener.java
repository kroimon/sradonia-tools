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
 * This interface allows classes to veto the delivery of certain events.
 * </p>
 * 
 * @author Stefan Rado
 */
public interface VetoListener {

	/**
	 * <p>
	 * Method called by an {@link EventBus} instance to decide whether to deliver an event or not.
	 * </p>
	 * 
	 * <p>
	 * If this method returns <code>true</code>, the delivery process will be stopped immediately and no other veto listener or event subscriber will
	 * be called with this event.
	 * </p>
	 * 
	 * @param topic
	 *            the topic the event was published under
	 * @param event
	 *            the event object
	 * @return <code>true</code> if the event should be vetoed, <code>false</code> otherwise
	 */
	public boolean shouldVeto(String topic, Object event);

}
