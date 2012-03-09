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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class EventBusTest {

	public EventBus eventBus;
	public boolean called;
	public final EventSubscriber eventSubscriber;
	public final VetoListener vetoListener;

	public EventBusTest() {
		eventSubscriber = new EventSubscriber() {
			public void onEvent(String topic, Object event) {
				assertEquals(topic, "topic");
				assertEquals(event, "event");
				called = true;
			}
		};

		vetoListener = new VetoListener() {
			public boolean shouldVeto(String topic, Object event) {
				assertEquals(topic, "topic");
				if (event.equals("no veto"))
					return true;
				else if (event.equals("veto"))
					return false;
				else {
					fail("illegal event");
					return false;
				}
			}
		};
	}

	@Before
	public void setUp() {
		eventBus = EventBus.getEventBus();
		called = false;
	}

	@Test
	public void testGetEventBusObject() {
		final Object name = new Object();

		EventBus first = EventBus.getEventBus(name);
		EventBus second = EventBus.getEventBus(name);

		assertNotNull(first);
		assertNotSame(first, eventBus);
		assertSame(first, second);
	}

	@Test
	public void testSubscribeForAll() {
		EventBus eb = EventBus.getEventBus();
		eb.subscribe(eventSubscriber);

		assertTrue(eb.publish("topic", "event"));
		assertTrue(called);
	}

	@Test
	public void testSubscribeForClass() {
		EventBus eb = EventBus.getEventBus();
		eb.subscribe(CharSequence.class, eventSubscriber);

		assertTrue(eb.publish("topic", "event"));
		assertTrue(called);
	}

	@Test
	public void testSubscribeForExactClass1() {
		EventBus eb = EventBus.getEventBus();
		eb.subscribeExactly(CharSequence.class, eventSubscriber);

		assertTrue(eb.publish("topic", "event"));
		assertFalse(called);
	}

	@Test
	public void testSubscribeForExactClass2() {
		EventBus eb = EventBus.getEventBus();
		eb.subscribeExactly(String.class, eventSubscriber);

		assertTrue(eb.publish("topic", "event"));
		assertTrue(called);
	}

	@Test
	public void testSubscribeForTopic() {
		EventBus eb = EventBus.getEventBus();
		eb.subscribe("topic", eventSubscriber);

		assertTrue(eb.publish("topic", "event"));
		assertTrue(called);
	}

	@Test
	public void testSubscribePatternEventSubscriber() {
		fail("Not yet implemented");
	}

	@Test
	public void testSubscribeExactlyStringEventSubscriber() {
		fail("Not yet implemented");
	}

	@Test
	public void testSubscribeVetoListener() {
		EventBus eb = EventBus.getEventBus();
		eb.subscribe(new VetoListener() {
			public boolean shouldVeto(String topic, Object event) {
				assertEquals(topic, "topic");
				assertEquals(event, "event");
				called = true;
				return false;
			}
		});
		assertFalse(eb.publish("topic", "event"));
		assertTrue(called);
	}

	@Test
	public void testSubscribeClassOfQVetoListener() {
		fail("Not yet implemented");
	}

	@Test
	public void testSubscribeExactlyClassOfQVetoListener() {
		fail("Not yet implemented");
	}

	@Test
	public void testSubscribeStringVetoListener() {
		fail("Not yet implemented");
	}

	@Test
	public void testSubscribePatternVetoListener() {
		fail("Not yet implemented");
	}

	@Test
	public void testSubscribeExactlyStringVetoListener() {
		fail("Not yet implemented");
	}

	@Test
	public void testPublishObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testPublishStringObject() {
		fail("Not yet implemented");
	}

}
