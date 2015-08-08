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
package de.visiom.carpc.asb.messagebus.impl;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import de.visiom.carpc.asb.messagebus.EventPublisher;
import de.visiom.carpc.asb.messagebus.constants.EventConstants;
import de.visiom.carpc.asb.messagebus.events.ValueChangeEvent;

public final class EventPublisherImpl implements EventPublisher {

    private EventAdmin eventAdmin;

    public void setEventAdmin(EventAdmin eventAdmin) {
        this.eventAdmin = eventAdmin;
    }

    @Override
    public void publishValueChange(ValueChangeEvent valueChangeEvent) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(EventConstants.VALUE_PROPERTY_KEY, valueChangeEvent);
        properties.put(EventConstants.PARAMETER_NAME_PROPERTY_KEY,
                valueChangeEvent.getParameter().getName());
        properties.put(EventConstants.SERVICE_NAME_PROPERTY_KEY,
                valueChangeEvent.getParameter().getService().getName());
        StringBuilder sb = new StringBuilder(EventConstants.UPDATES_TOPIC)
                .append(valueChangeEvent.getParameter().getService().getName());
        Event eventToSend = new Event(sb.toString(), properties);
        eventAdmin.postEvent(eventToSend);
    }
}
