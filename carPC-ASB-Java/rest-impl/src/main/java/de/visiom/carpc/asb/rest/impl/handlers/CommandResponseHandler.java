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
package de.visiom.carpc.asb.rest.impl.handlers;

import java.util.HashMap;
import java.util.Map;

import de.visiom.carpc.asb.messagebus.commands.GenericResponse;
import de.visiom.carpc.asb.messagebus.handlers.GenericResponseHandler;

public class CommandResponseHandler extends GenericResponseHandler {

    private final Map<Long, ResponseObservable> requestIdObservables = new HashMap<Long, ResponseObservable>();

    @Override
    public void onGenericResponse(GenericResponse genericResponse,
            Long requestID) {
        if (requestIdObservables.containsKey(requestID)) {
            requestIdObservables.get(requestID).responseHasArrived(
                    genericResponse.getStatus());
            requestIdObservables.remove(requestID);
        }
    }

    public synchronized ResponseObservable getResponseObservable(Long requestID) {
        ResponseObservable newObservable = new ResponseObservable();
        requestIdObservables.put(requestID, newObservable);
        return newObservable;
    }

}
