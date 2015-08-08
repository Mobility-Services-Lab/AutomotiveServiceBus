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
package de.visiom.carpc.asb.messagebus.async;

import de.visiom.carpc.asb.messagebus.CommandPublisher;
import de.visiom.carpc.asb.messagebus.commands.Response;

/**
 * A callback interface which can be passed to
 * {@link CommandPublisher#publishRequest(de.visiom.carpc.asb.messagebus.commands.Request, RequestCallback, String...)
 * publishRequest} in order to execute custom code before the request will be
 * enqueued. Since the identifier of the new request is passed as a parameter,
 * you can use this method to prepare for the arrival of the {@link Response}.
 * 
 * @author david
 * 
 */
public interface RequestCallback {

    /**
     * When you pass an instance of this interface to
     * {@link CommandPublisher#publishRequest(de.visiom.carpc.asb.messagebus.commands.Request, RequestCallback, String...)
     * publishRequest}, this method will be executed before the new request is
     * enqueued.
     * 
     * @param requestID
     *            The identifier of the new request.
     */
    void execute(Long requestID);

}
