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

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;

/**
 * <p>
 * An abstract {@link Action} that publishes performed {@link ActionEvent events} to an event bus.
 * </p>
 * 
 * <p>
 * The bus can be specified via one of the constructors. To use a topic one has to override the {@link #getEventTopic()} method.
 * </p>
 * 
 * @author Stefan Rado
 */
public class EventBusAction extends AbstractEventBusAction {
	private static final long serialVersionUID = -6010035631182862134L;

	private EventBus eventBus;

	public EventBusAction(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public EventBusAction(EventBus eventBus, String name, Icon icon) {
		super(name, icon);
		this.eventBus = eventBus;
	}

	public EventBusAction(EventBus eventBus, String name) {
		super(name);
		this.eventBus = eventBus;
	}

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}
}
