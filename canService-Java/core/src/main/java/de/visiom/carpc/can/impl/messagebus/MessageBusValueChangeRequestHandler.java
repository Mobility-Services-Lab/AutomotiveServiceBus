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
package de.visiom.carpc.can.impl.messagebus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.visiom.carpc.asb.messagebus.CommandPublisher;
import de.visiom.carpc.asb.messagebus.commands.GenericResponse;
import de.visiom.carpc.asb.messagebus.commands.Response;
import de.visiom.carpc.asb.messagebus.commands.ValueChangeRequest;
import de.visiom.carpc.asb.messagebus.handlers.ValueChangeRequestHandler;
import de.visiom.carpc.can.MessageBusToCANBusForwarder;

public class MessageBusValueChangeRequestHandler extends
        ValueChangeRequestHandler {

    private static final Logger LOG = LoggerFactory
            .getLogger(MessageBusValueChangeRequestHandler.class);

    private static final String RECEIVE_LOG_MESSAGE = "Received a value change request for {}. Dispatching the request to the ValueStore...";

    private CommandPublisher commandPublisher;

    private MessageBusToCANBusForwarder messageForwarder;

    public void setCommandPublisher(CommandPublisher commandPublisher) {
        this.commandPublisher = commandPublisher;
    }

    public void setMessageForwarder(MessageBusToCANBusForwarder messageForwarder) {
        this.messageForwarder = messageForwarder;
    }

    @Override
    public void onValueChangeRequest(ValueChangeRequest request, Long requestID) {
        LOG.info(RECEIVE_LOG_MESSAGE, request.getParameter());
        boolean wasSuccessfull = messageForwarder.setValue(
                request.getParameter(), request.getValue(), requestID);
        int responseStatus = getResponseStatus(wasSuccessfull);
        Response response = GenericResponse
                .createGenericResponse(responseStatus);
        commandPublisher.publishResponse(response, requestID, request
                .getParameter().getService());
    }

    private int getResponseStatus(boolean wasSuccessfull) {
        if (wasSuccessfull) {
            return GenericResponse.STATUS_OK;
        } else {
            return GenericResponse.STATUS_ERROR;
        }
    }
}