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

import java.util.Locale;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The EventBus core class.
 * 
 * @author Stefan Rado
 */
public class EventBus {
	private static Log log = LogFactory.getLog(EventBus.class);

	protected static HashMap<Object, EventBus> buses = new HashMap<Object, EventBus>();

	protected Set<VetoListener> vetoListeners;
	protected Map<Class<?>, Set<VetoListener>> vetoListenersForClass;
	protected Map<Class<?>, Set<VetoListener>> vetoListenersForExactClass;
	protected Map<Pattern, Set<VetoListener>> vetoListenersForTopic;
	protected Map<String, Set<VetoListener>> vetoListenersForExactTopic;

	protected Set<EventSubscriber> subscribers;
	protected Map<Class<?>, Set<EventSubscriber>> subscribersForClass;
	protected Map<Class<?>, Set<EventSubscriber>> subscribersForExactClass;
	protected Map<Pattern, Set<EventSubscriber>> subscribersForTopic;
	protected Map<String, Set<EventSubscriber>> subscribersForExactTopic;

	/**
	 * <p>
	 * Searches for an existing EventBus instance. If none is found, a new one will be created automatically.
	 * </p>
	 * 
	 * <p>
	 * The name is case-insensitive. It will be transformed to lower case before usage.
	 * </p>
	 * 
	 * @param name
	 *            the name of the eventbus
	 * @return the searched eventbus instance
	 */
	public static EventBus getEventBus(Object name) {
		if (name instanceof String) {
			name = ((String) name).toLowerCase(Locale.ENGLISH);
		}
		EventBus eb = buses.get(name);
		if (eb == null) {
			eb = new EventBus();
			log.info("created EventBus: " + name);
			buses.put(name, eb);
		}
		return eb;
	}

	/**
	 * <p>
	 * Creates a private EventBus instance which can't be retrieved later via the {@link #getEventBus(Object)} method.
	 * </p>
	 * 
	 * @return a newly created private eventbus instance
	 */
	public static EventBus getEventBus() {
		EventBus eb = new EventBus();
		log.info("created private EventBus: " + eb);
		return eb;
	}

	/**
	 * Private constructor. To construct a new EventBus just use the static {@link #getEventBus(String)} method with an unused name or the
	 * {@link #getEventBus()} method to obtain a private and unnamed instance.
	 */
	private EventBus() {
	}

	/**
	 * <p>
	 * Subscribes the subscriber to <b>all</b> events published on this EventBus instance.
	 * </p>
	 * 
	 * <p>
	 * <b>This method should normally not be used until it really becomes necessary as it may produce a lot of needless subscriber calls!</b>
	 * </p>
	 * 
	 * @param subscriber
	 *            the event subscriber to add
	 */
	public void subscribe(EventSubscriber subscriber) {
		if (subscribers == null)
			subscribers = new LinkedHashSet<EventSubscriber>();
		synchronized (subscribers) {
			subscribers.add(subscriber);
		}
		if (log.isInfoEnabled())
			log.info("added subscriber: " + subscriber);
	}

	/**
	 * Subscribes the subscriber to all events published on this EventBus instance which are instance of or subclass of the specified event class.
	 * 
	 * @param clazz
	 *            the event class to subscribe to
	 * @param subscriber
	 *            the event subscriber to add
	 */
	public void subscribe(Class<?> clazz, EventSubscriber subscriber) {
		if (subscribersForClass == null)
			subscribersForClass = new LinkedHashMap<Class<?>, Set<EventSubscriber>>();
		synchronized (subscribersForClass) {
			Set<EventSubscriber> set = subscribersForClass.get(clazz);
			if (set == null) {
				set = new LinkedHashSet<EventSubscriber>();
				subscribersForClass.put(clazz, set);
			}
			set.add(subscriber);
		}
		if (log.isInfoEnabled())
			log.info("added subscriber to class [" + clazz + "]: " + subscriber);
	}

	/**
	 * Subscribes the subscriber to all events published on this EventBus instance which are instance of the specified event class. The subscriber
	 * will not get any events that is subclass of the subscribed event class.
	 * 
	 * @param clazz
	 *            the event class to subscribe to
	 * @param subscriber
	 *            the event subscriber to add
	 */
	public void subscribeExactly(Class<?> clazz, EventSubscriber subscriber) {
		if (subscribersForExactClass == null)
			subscribersForExactClass = new LinkedHashMap<Class<?>, Set<EventSubscriber>>();
		synchronized (subscribersForExactClass) {
			Set<EventSubscriber> set = subscribersForExactClass.get(clazz);
			if (set == null) {
				set = new LinkedHashSet<EventSubscriber>();
				subscribersForExactClass.put(clazz, set);
			}
			set.add(subscriber);
		}
		if (log.isInfoEnabled())
			log.info("added subscriber exactly to class [" + clazz + "]: " + subscriber);
	}

	/**
	 * <p>
	 * Subscribes the subscriber to all events which are published under topics matching the given regular expression.
	 * </p>
	 * 
	 * <p>
	 * A call to this method is equal to calling {@link #subscribe(Pattern, EventSubscriber)} with <code>Pattern.compile(topic)</code>.
	 * </p>
	 * 
	 * @param topic
	 *            regular expression used to determine wanted topics
	 * @param subscriber
	 *            the event subscriber to add
	 */
	public void subscribe(String topic, EventSubscriber subscriber) {
		subscribe(Pattern.compile(topic), subscriber);
	}

	/**
	 * Subscribes the subscriber to all events which are published under topics matching the given pattern.
	 * 
	 * @param topic
	 *            regular expression used to determine wanted topics
	 * @param subscriber
	 *            the event subscriber to add
	 */
	public void subscribe(Pattern topic, EventSubscriber subscriber) {
		if (subscribersForTopic == null)
			subscribersForTopic = new LinkedHashMap<Pattern, Set<EventSubscriber>>();
		synchronized (subscribersForTopic) {
			Set<EventSubscriber> set = subscribersForTopic.get(topic);
			if (set == null) {
				set = new LinkedHashSet<EventSubscriber>();
				subscribersForTopic.put(topic, set);
			}
			set.add(subscriber);
		}
		if (log.isInfoEnabled())
			log.info("added subscriber to topic [" + topic + "]: " + subscriber);
	}

	/**
	 * <p>
	 * Subscribes the subscriber to all events which are published using exactly the given topic.
	 * </p>
	 * 
	 * <p>
	 * The topics are compared using the {@link String#equals(Object)}</code> method.
	 * </p>
	 * 
	 * @param topic
	 *            regular expression used to determine wanted topics
	 * @param subscriber
	 *            the event subscriber to add
	 */
	public void subscribeExactly(String topic, EventSubscriber subscriber) {
		if (subscribersForExactTopic == null)
			subscribersForExactTopic = new LinkedHashMap<String, Set<EventSubscriber>>();
		synchronized (subscribersForExactTopic) {
			Set<EventSubscriber> set = subscribersForExactTopic.get(topic);
			if (set == null) {
				set = new LinkedHashSet<EventSubscriber>();
				subscribersForExactTopic.put(topic, set);
			}
			set.add(subscriber);
		}
		if (log.isInfoEnabled())
			log.info("added subscriber exactly to topic [" + topic + "]: " + subscriber);
	}

	/**
	 * <p>
	 * Subscribes the veto listener to <b>all</b> events published on this EventBus instance.
	 * </p>
	 * 
	 * <p>
	 * <b>This method should normally not be used until it really becomes necessary as it may produce a lot of needless veto listener calls!</b>
	 * </p>
	 * 
	 * @param listener
	 *            the veto listener to add
	 */
	public void subscribe(VetoListener listener) {
		if (vetoListeners == null) {
			vetoListeners = new LinkedHashSet<VetoListener>();
		}
		synchronized (vetoListeners) {
			vetoListeners.add(listener);
		}
		if (log.isInfoEnabled())
			log.info("added veto listener: " + listener);
	}

	/**
	 * Subscribes the veto listener to all events published on this EventBus instance which are instance of or subclass of the specified event class.
	 * 
	 * @param clazz
	 *            the event class to subscribe to
	 * @param listener
	 *            the veto listener to add
	 */
	public void subscribe(Class<?> clazz, VetoListener listener) {
		if (vetoListenersForClass == null)
			vetoListenersForClass = new LinkedHashMap<Class<?>, Set<VetoListener>>();
		synchronized (vetoListenersForClass) {
			Set<VetoListener> set = vetoListenersForClass.get(clazz);
			if (set == null) {
				set = new LinkedHashSet<VetoListener>();
				vetoListenersForClass.put(clazz, set);
			}
			set.add(listener);
		}
		if (log.isInfoEnabled())
			log.info("added veto listener to class [" + clazz + "]: " + listener);
	}

	/**
	 * Subscribes the veto listener to all events published on this EventBus instance which are instance of the specified event class. The veto
	 * listener will not get any events that is subclass of the subscribed event class.
	 * 
	 * @param clazz
	 *            the event class to subscribe to
	 * @param listener
	 *            the veto listener to add
	 */
	public void subscribeExactly(Class<?> clazz, VetoListener listener) {
		if (vetoListenersForExactClass == null)
			vetoListenersForExactClass = new LinkedHashMap<Class<?>, Set<VetoListener>>();
		synchronized (vetoListenersForExactClass) {
			Set<VetoListener> set = vetoListenersForExactClass.get(clazz);
			if (set == null) {
				set = new LinkedHashSet<VetoListener>();
				vetoListenersForExactClass.put(clazz, set);
			}
			set.add(listener);
		}
		if (log.isInfoEnabled())
			log.info("added veto listener exactly to class [" + clazz + "]: " + listener);
	}

	/**
	 * <p>
	 * Subscribes the veto listener to all events which are published under topics matching the given regular expression.
	 * </p>
	 * 
	 * <p>
	 * A call to this method is equal to calling {@link #subscribe(Pattern, VetoListener)} with <code>Pattern.compile(topic)</code>.
	 * </p>
	 * 
	 * @param topic
	 *            regular expression used to determine wanted topics
	 * @param listener
	 *            the veto listener to add
	 */
	public void subscribe(String topic, VetoListener listener) {
		subscribe(Pattern.compile(topic), listener);
	}

	/**
	 * Subscribes the veto listener to all events which are published under topics matching the given pattern.
	 * 
	 * @param topic
	 *            regular expression used to determine wanted topics
	 * @param listener
	 *            the veto listener to add
	 */
	public void subscribe(Pattern topic, VetoListener listener) {
		if (vetoListenersForTopic == null)
			vetoListenersForTopic = new LinkedHashMap<Pattern, Set<VetoListener>>();
		synchronized (vetoListenersForTopic) {
			Set<VetoListener> set = vetoListenersForTopic.get(topic);
			if (set == null) {
				set = new LinkedHashSet<VetoListener>();
				vetoListenersForTopic.put(topic, set);
			}
			set.add(listener);
		}
		if (log.isInfoEnabled())
			log.info("added veto listener to topic [" + topic + "]: " + listener);
	}

	/**
	 * <p>
	 * Subscribes the veto listener to all events which are published using exactly the given topic.
	 * </p>
	 * 
	 * <p>
	 * The topics are compared using the {@link String#equals(Object)}</code> method.
	 * </p>
	 * 
	 * @param topic
	 *            regular expression used to determine wanted topics
	 * @param listener
	 *            the veto listener to add
	 */
	public void subscribeExactly(String topic, VetoListener listener) {
		if (vetoListenersForExactTopic == null)
			vetoListenersForExactTopic = new LinkedHashMap<String, Set<VetoListener>>();
		synchronized (vetoListenersForExactTopic) {
			Set<VetoListener> set = vetoListenersForExactTopic.get(topic);
			if (set == null) {
				set = new LinkedHashSet<VetoListener>();
				vetoListenersForExactTopic.put(topic, set);
			}
			set.add(listener);
		}
		if (log.isInfoEnabled())
			log.info("added veto listener exactly to topic [" + topic + "]: " + listener);
	}

	/**
	 * <p>
	 * Publishes an event on the bus.
	 * </p>
	 * <p>
	 * A <code>null</code> topic will be used, so a call to this method is equal to calling {@link #publish(String, Object) publish(null, event)}.
	 * </p>
	 * 
	 * @param event
	 *            the event to publish
	 * @return <code>true</code> if the event has been published successfully, <code>false</code> if it has been vetoed
	 */
	public boolean publish(Object event) {
		return publish(null, event);
	}

	/**
	 * <p>
	 * Publishes an event on the bus.
	 * </p>
	 * 
	 * @param topic
	 *            the topic of the event
	 * @param event
	 *            the event object
	 * @return <code>true</code> if the event has been published successfully, <code>false</code> if it has been vetoed
	 */
	public boolean publish(String topic, Object event) {
		if (event == null)
			throw new IllegalArgumentException("can't publish null event!");

		if (log.isInfoEnabled())
			log.info("publishing {topic=" + topic + ", event=" + event + "}");

		Class<?> eventClass = event.getClass();

		// check VetoListeners
		LinkedHashSet<VetoListener> vetoListeners = new LinkedHashSet<VetoListener>();
		if (this.vetoListeners != null) {
			synchronized (this.vetoListeners) {
				vetoListeners.addAll(this.vetoListeners);
			}
		}
		vetoListeners.addAll(getVetoListenersForClass(eventClass));
		if (topic != null)
			vetoListeners.addAll(getVetoListenersForTopic(topic));

		for (VetoListener vetoListener : vetoListeners) {
			try {
				if (vetoListener.shouldVeto(topic, event)) {
					if (log.isInfoEnabled())
						log.info(vetoListener + " vetoed event {topic=" + topic + ", event=" + event + "}");
					return false;
				}
			} catch (RuntimeException e) {
				if (log.isErrorEnabled())
					log.error(vetoListener + " threw an exception while checking for veto of event {topic=" + topic + ", event=" + event + "}", e);
				throw e;
			}
		}

		// publish
		Set<EventSubscriber> subscribers = new LinkedHashSet<EventSubscriber>();
		if (this.subscribers != null) {
			synchronized (this.subscribers) {
				subscribers.addAll(this.subscribers);
			}
		}
		subscribers.addAll(getSubscribersForClass(eventClass));
		if (topic != null)
			subscribers.addAll(getSubscribersForTopic(topic));

		for (EventSubscriber subscriber : subscribers) {
			try {
				subscriber.onEvent(topic, event);
			} catch (RuntimeException e) {
				if (log.isErrorEnabled())
					log.error(subscriber + " threw an exception while handling event {topic=" + topic + ", event=" + event + "}", e);
				throw e;
			}
		}

		return true;
	}

	/**
	 * @param topic
	 *            the topic for which to collect subscribers
	 * @return a collection of matching subscribers
	 */
	protected Set<EventSubscriber> getSubscribersForTopic(String topic) {
		Set<EventSubscriber> subscribers = new LinkedHashSet<EventSubscriber>();
		if (subscribersForExactTopic != null) {
			synchronized (subscribersForExactTopic) {
				Set<EventSubscriber> set = subscribersForExactTopic.get(topic);
				if (set != null)
					subscribers.addAll(set);
			}
		}
		if (subscribersForTopic != null) {
			synchronized (subscribersForTopic) {
				for (Pattern p : subscribersForTopic.keySet()) {
					if (p.matcher(topic).matches())
						subscribers.addAll(subscribersForTopic.get(p));
				}
			}
		}
		return subscribers;
	}

	/**
	 * @param clazz
	 *            the class for which to collect subscribers
	 * @return a collection of matching subscribers
	 */
	protected Set<EventSubscriber> getSubscribersForClass(Class<?> clazz) {
		Set<EventSubscriber> subscribers = new LinkedHashSet<EventSubscriber>();
		if (subscribersForExactClass != null) {
			synchronized (subscribersForExactClass) {
				Set<EventSubscriber> set = subscribersForExactClass.get(clazz);
				if (set != null)
					subscribers.addAll(set);
			}
		}
		if (subscribersForClass != null) {
			synchronized (subscribersForClass) {
				for (Class<?> c : subscribersForClass.keySet()) {
					if (c.isAssignableFrom(clazz)) {
						subscribers.addAll(subscribersForClass.get(c));
					}
				}
			}
		}
		return subscribers;
	}

	/**
	 * @param topic
	 *            the topic for which to collect veto listeners
	 * @return a collection of matching veto listeners
	 */
	protected Set<VetoListener> getVetoListenersForTopic(String topic) {
		Set<VetoListener> listeners = new LinkedHashSet<VetoListener>();
		if (vetoListenersForExactTopic != null) {
			synchronized (vetoListenersForExactTopic) {
				Set<VetoListener> set = vetoListenersForExactTopic.get(topic);
				if (set != null)
					listeners.addAll(set);
			}
		}
		if (vetoListenersForTopic != null) {
			synchronized (vetoListenersForTopic) {
				for (Pattern p : vetoListenersForTopic.keySet()) {
					if (p.matcher(topic).matches())
						listeners.addAll(vetoListenersForTopic.get(p));
				}
			}
		}
		return listeners;
	}

	/**
	 * @param clazz
	 *            the class for which to collect veto listeners
	 * @return a collection of matching veto listeners
	 */
	protected Set<VetoListener> getVetoListenersForClass(Class<?> clazz) {
		LinkedHashSet<VetoListener> listeners = new LinkedHashSet<VetoListener>();
		if (vetoListenersForExactClass != null) {
			synchronized (vetoListenersForExactClass) {
				Set<VetoListener> set = vetoListenersForExactClass.get(clazz);
				if (set != null)
					listeners.addAll(set);
			}
		}
		if (vetoListenersForClass != null) {
			synchronized (vetoListenersForClass) {
				for (Class<?> c : vetoListenersForClass.keySet()) {
					if (c.isAssignableFrom(clazz)) {
						listeners.addAll(vetoListenersForClass.get(c));
					}
				}
			}
		}
		return listeners;
	}
}
