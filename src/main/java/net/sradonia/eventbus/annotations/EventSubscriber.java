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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to specify that a method is intended to receive certain events from an {@link EventBus}. To register these annotated methods for an
 * object you have to invoke the {@link AnnotationProcessor#process(Object)} method with the object.
 * 
 * @author Stefan Rado
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventSubscriber {

	/**
	 * Whether to subscribe to events on a class- or topic-based way.
	 * 
	 * If this value is set to {@link SubscriptionType#CLASS} the {@link #eventClass()} parameter will be used to determine which event class should
	 * be used to subscribe to.
	 * 
	 * If this value is set to {@link SubscriptionType#TOPIC} the {@link #eventTopic() parameter will be used to determine which event topic should be
	 * used to subscribe to.
	 */
	SubscriptionType type() default SubscriptionType.CLASS;

	/**
	 * The event class to subscribe to.
	 * 
	 * If no value is specified the {@link AnnotationProcessor} tries to find the event class looking at the parameters of the annotated method.
	 * 
	 * Used only if {@link #type()} is set to {@link SubscriptionType#CLASS}.
	 */
	Class<?> eventClass() default UseClassOfAnnotatedMethodParameter.class;

	/**
	 * The topic to subscribe to.
	 * 
	 * If no value is specified the {@link AnnotationProcessor} will throw an {@link IllegalArgumentException}.
	 * 
	 * Used only if {@link #type()} is set to {@link SubscriptionType#TOPIC}.
	 */
	String eventTopic() default "";

	/**
	 * The {@link EventBus}'s name to subscribe to.
	 * 
	 * Defaults to <code>"default"</code>.
	 */
	String eventBus() default "default";

	/**
	 * Whether to use one of the subscribeExactly() or subscribe() methods.
	 * 
	 * Default is <code>false</code>.
	 */
	boolean exact() default false;
}
