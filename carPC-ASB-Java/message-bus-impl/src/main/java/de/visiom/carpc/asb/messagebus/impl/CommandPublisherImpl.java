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

import de.visiom.carpc.asb.messagebus.CommandPublisher;
import de.visiom.carpc.asb.messagebus.async.RequestCallback;
import de.visiom.carpc.asb.messagebus.commands.Request;
import de.visiom.carpc.asb.messagebus.commands.Response;
import de.visiom.carpc.asb.messagebus.constants.EventConstants;
import de.visiom.carpc.asb.servicemodel.Service;

public final class CommandPublisherImpl implements CommandPublisher {

    private EventAdmin eventAdmin;

    public void setEventAdmin(EventAdmin eventAdmin) {
        this.eventAdmin = eventAdmin;
    }

    private static final Long minimumID = -5000L;

    private static final Long maximumID = 5000L;

    private Long currentRequestID = minimumID - 1;

    @Override
    public Long publishRequest(Request request, Service... services) {
        return publishRequest(request, null, services);
    }

    @Override
    public void publishResponse(Response response, Long requestID,
            Service service) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(EventConstants.RESPONSE_PROPERTY_KEY, response);
        properties.put(EventConstants.SERVICE_NAME_PROPERTY_KEY,
                service.getName());
        properties.put(EventConstants.REQUEST_ID_KEY, requestID);
        Event eventToSend = new Event(EventConstants.RESPONSES_TOPIC,
                properties);
        eventAdmin.postEvent(eventToSend);
    }

    @Override
    public Long publishRequest(Request request,
            RequestCallback requestCallback, Service... services) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(EventConstants.REQUEST_PROPERTY_KEY, request);
        StringBuilder serviceNamesBuilder = new StringBuilder();
        boolean first = true;
        for (Service service : services) {
            if (!first) {
                serviceNamesBuilder.append(",");
            }
            serviceNamesBuilder.append(service.getName());
            first = false;
        }
        properties.put(EventConstants.SERVICE_NAME_PROPERTY_KEY,
                serviceNamesBuilder.toString());
        // Assign an identifier to the request so that responses can refer to
        // a specific request
        Long requestID = generateRequestID();
        properties.put(EventConstants.REQUEST_ID_KEY, requestID);
        Event eventToSend = new Event(EventConstants.REQUESTS_TOPIC, properties);
        if (requestCallback != null) {
            requestCallback.execute(requestID);
        }
        eventAdmin.postEvent(eventToSend);
        return requestID;
    }

    // needs to be thread-safe because multiple requests could be sent at the
    // same time
    private synchronized Long generateRequestID() {
        if (currentRequestID.equals(maximumID)) {
            currentRequestID = minimumID - 1;
        }
        currentRequestID++;
        return currentRequestID;
    }

}
