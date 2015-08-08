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
package de.visiom.carpc.asb.rest.impl.sse;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventOutputWrapper implements Observer {

    private final static Logger LOG = LoggerFactory
            .getLogger(EventOutputWrapper.class);

    private final EventOutput eventOutput;

    public EventOutputWrapper(Observable observable, EventOutput eventOutput) {
        observable.addObserver(this);
        this.eventOutput = eventOutput;
    }

    public EventOutput getEventOutput() {
        return eventOutput;
    }

    @Override
    public void update(Observable observable, Object newValue) {
        LOG.info("Building an outbound event...");
        final OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
        eventBuilder.data(String.class, newValue + "\n\n");
        final OutboundEvent event = eventBuilder.build();
        try {
            LOG.info("Writing the event...");
            eventOutput.write(event);
        } catch (IOException e) {
            try {
                eventOutput.close();
            } catch (IOException ioClose) {
                LOG.error("Error when closing the event output: {}", ioClose);
            } finally {
                observable.deleteObserver(this);
            }
        }
    }
}