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
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import de.visiom.carpc.asb.messagebus.async.RequestCallback;
import de.visiom.carpc.asb.messagebus.commands.Request;
import de.visiom.carpc.asb.messagebus.commands.Response;
import de.visiom.carpc.asb.messagebus.constants.EventConstants;
import de.visiom.carpc.asb.servicemodel.Service;

@RunWith(MockitoJUnitRunner.class)
public class CommandPublisherImplTest {

    @Mock
    private EventAdmin eventAdmin;

    @InjectMocks
    private CommandPublisherImpl commandPublisher;

    @Test
    public void testPublishRequestRequestStringArray() {
        final Request request = Mockito.mock(Request.class);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Event event = (Event) args[0];
                String serviceName = (String) event
                        .getProperty(EventConstants.SERVICE_NAME_PROPERTY_KEY);
                assertEquals("test1,test2", serviceName);
                assertEquals("visiom/commands/requests", event.getTopic());
                assertTrue(request == event
                        .getProperty(EventConstants.REQUEST_PROPERTY_KEY));
                return null;
            }
        }).when(eventAdmin).postEvent(any(Event.class));
        DummyService s1 = new DummyService();
        DummyService s2 = new DummyService();
        s1.setName("test1");
        s2.setName("test2");
        commandPublisher.publishRequest(request, s1, s2);
        verify(eventAdmin).postEvent(any(Event.class));
    }

    @Test
    public void testPublishResponse() {
        final Response response = Mockito.mock(Response.class);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Event event = (Event) args[0];
                String serviceName = (String) event
                        .getProperty(EventConstants.SERVICE_NAME_PROPERTY_KEY);
                assertEquals("TEST", serviceName);
                assertEquals("visiom/commands/responses", event.getTopic());
                assertTrue(response == event
                        .getProperty(EventConstants.RESPONSE_PROPERTY_KEY));
                assertEquals(0L,
                        event.getProperty(EventConstants.REQUEST_ID_KEY));
                return null;
            }
        }).when(eventAdmin).postEvent(any(Event.class));
        commandPublisher.publishResponse(response, 0L, new DummyService());
        verify(eventAdmin).postEvent(any(Event.class));
    }

    @Test
    public void testPublishRequestRequestRequestCallbackStringArray() {
        Request request = Mockito.mock(Request.class);
        Service service = Mockito.mock(Service.class);
        RequestCallback requestCallback = Mockito.mock(RequestCallback.class);
        commandPublisher.publishRequest(request, requestCallback, service);
        verify(requestCallback).execute(anyLong());
        verify(eventAdmin).postEvent(any(Event.class));
    }

}
