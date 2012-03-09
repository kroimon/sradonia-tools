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

import net.sradonia.eventbus.EventBus;
import net.sradonia.eventbus.annotations.SubscriptionType;

/**
 * Processes annotations for an object instance.
 * 
 * Example:
 * 
 * <pre>
 * public class AnnotationTest {
 * 	public EventBusTest() {
 * 		AnnotationProcessor.process(this);
 * 	}
 * 
 * 	&#064;EventSubscriber()
 * 	public void onStart(String topic, StartEvent e) {
 * 		// do something
 * 	}
 * 
 * 	&#064;EventSubscriber()
 * 	public void onStop(StopEvent e) {
 * 		// do something
 * 	}
 * }
 * </pre>
 * 
 * @author Stefan Rado
 */
public class AnnotationProcessor {
	private static Log log = LogFactory.getLog(AnnotationProcessor.class);

	enum MethodType {
		EVENT_ONLY, TOPIC_AND_EVENT
	}

	private static class MethodSignature {
		public MethodType type;
		public Class<?> eventParamClass;
	}

	/**
	 * Searches for all {@link EventSubscriber} annotated methods of the given object and registers them using {@link ProxySubscriber}s.
	 * 
	 * @param object
	 *            the object to process
	 */
	public static void process(Object object) {
		if (log.isInfoEnabled())
			log.info("processing " + object);
		for (Method method : object.getClass().getMethods()) {
			EventSubscriber annotation = method.getAnnotation(EventSubscriber.class);
			if (annotation != null) {
				processMethod(object, method, annotation);
			}
		}
	}

	private static void processMethod(Object object, Method method, EventSubscriber annotation) {
		if (log.isDebugEnabled())
			log.debug("processing method " + method);
		EventBus eventBus = EventBus.getEventBus(annotation.eventBus());
		boolean exact = annotation.exact();

		MethodSignature methodSig = checkSubscriptionMethod(object, method);

		SubscriptionType subscriptionType = annotation.type();
		if (subscriptionType == SubscriptionType.CLASS) {
			Class<?> eventClass = annotation.eventClass();

			if (eventClass.equals(UseClassOfAnnotatedMethodParameter.class)) {
				eventClass = methodSig.eventParamClass;
			} else if (!methodSig.eventParamClass.isAssignableFrom(eventClass)) {
				throw new IllegalArgumentException("can't annotate method " + method + " in object " + object
						+ ": method's event parameter doesn't match eventClass");
			}

			if (exact) {
				eventBus.subscribeExactly(eventClass, new ProxySubscriber(object, method, methodSig.type));
			} else {
				eventBus.subscribe(eventClass, new ProxySubscriber(object, method, methodSig.type));
			}

		} else if (subscriptionType == SubscriptionType.TOPIC) {
			String eventTopic = annotation.eventTopic();

			if (eventTopic.length() == 0) {
				throw new IllegalArgumentException("can't subscribe to null topic in method " + method + " in object " + object);
			}

			if (!methodSig.eventParamClass.equals(Object.class)) {
				throw new IllegalArgumentException("can't annotate method " + method + " in object " + object
						+ ": method's event parameter has to be of type Object");
			}

			if (exact) {
				eventBus.subscribeExactly(eventTopic, new ProxySubscriber(object, method, methodSig.type));
			} else {
				eventBus.subscribe(eventTopic, new ProxySubscriber(object, method, methodSig.type));
			}

		}
	}

	private static MethodSignature checkSubscriptionMethod(Object object, Method method) {
		MethodSignature methodSig = new MethodSignature();
		Class<?>[] methodParams = method.getParameterTypes();

		if (methodParams.length < 1) {
			throw new IllegalArgumentException("can't annotate method " + method + " in object " + object + ": no parameter method");

		} else if (methodParams.length == 1) {
			methodSig.type = MethodType.EVENT_ONLY;
			methodSig.eventParamClass = methodParams[0];

		} else if (methodParams.length == 2) {
			if (!methodParams[0].isAssignableFrom(String.class)) {
				throw new IllegalArgumentException("can't annotate method " + method + " in object " + object
						+ ": first parameter has to be of type String");
			}
			methodSig.type = MethodType.TOPIC_AND_EVENT;
			methodSig.eventParamClass = methodParams[1];

		} else {
			throw new IllegalArgumentException("can't annotate method " + method + " in object " + object + ": parameter count has to be 1 or 2");
		}

		return methodSig;
	}
}
