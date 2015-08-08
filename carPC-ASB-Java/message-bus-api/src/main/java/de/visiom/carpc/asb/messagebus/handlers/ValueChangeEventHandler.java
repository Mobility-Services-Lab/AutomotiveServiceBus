/*
 * Copyright 2015 Technische Universität München
 *
 * Author:
 * David Soto Setzke
 *
 *
 * This file is part of the Automotive Service Bus v1.1 which was
 * forked from the research project Visio.M:
 *
 * 	 http://www.visiom-automobile.de/home/
 *
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * 	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package de.visiom.carpc.asb.messagebus.handlers;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import de.visiom.carpc.asb.messagebus.constants.EventConstants;
import de.visiom.carpc.asb.messagebus.events.ValueChangeEvent;

/**
 * Implement this class and register it as a EventHandler OSGi service to handle
 * value change events.
 * 
 * @author david
 * 
 */
public abstract class ValueChangeEventHandler implements EventHandler {

    @Override
    public final void handleEvent(final Event event) {
        Object eventObject = event
                .getProperty(EventConstants.VALUE_PROPERTY_KEY);
        if (!(eventObject instanceof ValueChangeEvent)) {
            return;
        }
        onValueChangeEvent((ValueChangeEvent) eventObject);
    }

    /**
     * Called when the handler receives a value change event.
     * 
     * @param valueChangeEvent
     *            The event that was received
     */
    public abstract void onValueChangeEvent(
            final ValueChangeEvent valueChangeEvent);

}
