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
package de.visiom.carpc.asb.messagebus;

import de.visiom.carpc.asb.messagebus.async.RequestCallback;
import de.visiom.carpc.asb.messagebus.commands.Request;
import de.visiom.carpc.asb.messagebus.commands.Response;
import de.visiom.carpc.asb.servicemodel.Service;

/**
 * A service which can be used to publish commands, i.e. requests and responses
 * on the message bus.
 * 
 * @author david
 * 
 */
public interface CommandPublisher {

    /**
     * Publishes a {@link Request} on the message bus. A request can be directed
     * to one or more services. The message bus will assign an unique identifier
     * to the request which will also be assigned to the response. If you need
     * to know the identifier before the event request will actually be put into
     * the message queue (for example to prepare your classes for the arrival of
     * the response), you can use
     * {@link #publishRequest(Request, RequestCallback, String...)
     * publishRequest} which takes a callback and will provide it with the
     * identifier before the request is enqueued.
     * 
     * @param request
     *            The request which should be published
     * @param services
     *            The services which should receive the request
     * @return The ID of the request which can be used to identify the
     *         corresponding {@link Response}
     */
    Long publishRequest(Request request, Service... services);

    /**
     * Publishes a {@link Request} on the message bus. A request can be directed
     * to one or more services. The message bus will assign an unique identifier
     * to the request which will also be assigned to the response. This method
     * takes a callback which will be provided with the new identifier and can
     * be used to execute custom code before the request is enqueued.
     * 
     * @param request
     *            The request which should be published
     * @param requestCallback
     *            A callback which will be provided with the new identifier and
     *            will be executed before the request is enqueued
     * @param services
     *            The services which should receive the request
     * @return The ID of the request which can be used to identify the
     *         corresponding {@link Response}
     */
    Long publishRequest(Request request, RequestCallback requestCallback,
            Service... services);

    /**
     * Publishes a {@link Response} to a {@link Request} on the message bus.
     * 
     * @param response
     *            The response which should be published
     * @param requestID
     *            The identifier of the original request
     * @param service
     *            The service which publishes the response
     */
    void publishResponse(Response response, Long requestID, Service service);

}
