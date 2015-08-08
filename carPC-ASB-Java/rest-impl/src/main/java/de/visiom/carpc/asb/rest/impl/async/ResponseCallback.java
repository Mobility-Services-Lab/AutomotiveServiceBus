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
package de.visiom.carpc.asb.rest.impl.async;

import javax.ws.rs.container.AsyncResponse;

import de.visiom.carpc.asb.messagebus.async.RequestCallback;
import de.visiom.carpc.asb.rest.impl.handlers.CommandResponseHandler;

public class ResponseCallback implements RequestCallback {

    private final AsyncResponse asyncResponse;

    private final CommandResponseHandler commandResponseHandler;

    public ResponseCallback(AsyncResponse asyncResponse,
            CommandResponseHandler commandResponseHandler) {
        this.asyncResponse = asyncResponse;
        this.commandResponseHandler = commandResponseHandler;
    }

    @Override
    public void execute(Long requestID) {
        commandResponseHandler.getResponseObservable(requestID).addObserver(
                new CommandObserver(asyncResponse));
    }
}