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

import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 * <p>
 * An abstract {@link Action} that publishes performed {@link ActionEvent events} to an event bus.
 * </p>
 * <p>
 * The bus and the topic can be specified by implementing/overriding the <code>getEventXXX()</code> methods.
 * </p>
 * 
 * @author Stefan Rado
 */
abstract public class AbstractEventBusAction extends AbstractAction {
	private static final long serialVersionUID = 3890297390092501629L;

	public AbstractEventBusAction() {
	}

	public AbstractEventBusAction(String name) {
		super(name);
	}

	public AbstractEventBusAction(String name, Icon icon) {
		super(name, icon);
	}

	public String getEventTopic() {
		return null;
	}

	abstract public EventBus getEventBus();

	public void actionPerformed(ActionEvent e) {
		getEventBus().publish(getEventTopic(), e);
	}

}
