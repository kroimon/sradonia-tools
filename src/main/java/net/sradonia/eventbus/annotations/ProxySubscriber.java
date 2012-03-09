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
package net.sradonia.eventbus.annotations;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sradonia.eventbus.EventSubscriber;
import net.sradonia.eventbus.annotations.AnnotationProcessor.MethodType;

/**
 * A proxy class used for the invokation of annotated event subscriber methods.
 * 
 * @author Stefan Rado
 */
class ProxySubscriber implements EventSubscriber {
	private static final Log log = LogFactory.getLog(ProxySubscriber.class);

	private Object object;
	private Method method;
	private MethodType methodType;

	public ProxySubscriber(Object object, Method method, MethodType methodType) {
		this.object = object;
		this.method = method;
		this.methodType = methodType;
		if (log.isDebugEnabled()) {
			log.debug("instantiated new ProxySubscriber for method " + method + " in object " + object);
		}
	}

	public void onEvent(String topic, Object event) {
		Object[] params = null;
		if (methodType == MethodType.EVENT_ONLY) {
			params = new Object[] { event };
		} else if (methodType == MethodType.TOPIC_AND_EVENT) {
			params = new Object[] { topic, event };
		}

		try {
			method.invoke(object, params);
		} catch (Exception e) {
			log.error("error invoking event subscriber method " + method + " in object " + object);
		}
	}

}
