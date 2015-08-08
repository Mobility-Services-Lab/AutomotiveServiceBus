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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import de.visiom.carpc.asb.messagebus.constants.EventConstants;
import de.visiom.carpc.asb.messagebus.events.ValueChangeEvent;
import de.visiom.carpc.asb.servicemodel.parameters.Parameter;

@RunWith(MockitoJUnitRunner.class)
public class EventPublisherImplTest {

    @Mock
    private EventAdmin eventAdmin;

    @InjectMocks
    private EventPublisherImpl eventPublisher;

    @Test
    public void testPublishValueChange() {
        Parameter parameter = new DummyParameter();
        final ValueChangeEvent valueChangeEvent = ValueChangeEvent
                .createValueChangeEvent(parameter, null);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Event event = (Event) args[0];
                assertEquals("visiom/updates/"
                        + valueChangeEvent.getParameter().getService()
                                .getName(), event.getTopic());
                assertTrue(valueChangeEvent == event
                        .getProperty(EventConstants.VALUE_PROPERTY_KEY));
                assertEquals(
                        valueChangeEvent.getParameter().getName(),
                        event.getProperty(EventConstants.PARAMETER_NAME_PROPERTY_KEY));
                return null;
            }
        }).when(eventAdmin).postEvent(any(Event.class));
        eventPublisher.publishValueChange(valueChangeEvent);
        verify(eventAdmin).postEvent(any(Event.class));
    }

}
