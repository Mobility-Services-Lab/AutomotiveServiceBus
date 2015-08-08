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
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.visiom.carpc.asb.messagebus.events.ValueChangeEvent;
import de.visiom.carpc.asb.messagebus.handlers.ValueChangeEventHandler;
import de.visiom.carpc.asb.rest.impl.factories.ValueResponseFactory;
import de.visiom.carpc.asb.rest.impl.sse.ParameterObservable;
import de.visiom.carpc.asb.servicemodel.parameters.Parameter;
import de.visiom.carpc.asb.servicemodel.valueobjects.ValueObject;

public class ValueUpdateHandler extends ValueChangeEventHandler {

    private static final Logger LOG = LoggerFactory
            .getLogger(ValueUpdateHandler.class);

    private final Map<String, Map<String, ParameterObservable>> observables = new HashMap<String, Map<String, ParameterObservable>>();

    public Observable getObservable(Parameter parameter) {
        synchronized (this) {
            return observables.get(parameter.getService().getName()).get(
                    parameter.getName());
        }
    }

    @Override
    public void onValueChangeEvent(ValueChangeEvent valueChangeEvent) {
        String serviceName = valueChangeEvent.getParameter().getService()
                .getName();
        String parameterName = valueChangeEvent.getParameter().getName();
        ValueObject valueType = valueChangeEvent.getValue();
        synchronized (this) {
            if (!observables.containsKey(serviceName)) {

                observables.put(serviceName,
                        new HashMap<String, ParameterObservable>());
            }
            if (!observables.get(serviceName).containsKey(parameterName)) {
                LOG.info("creating an observable for {}/{}", serviceName,
                        parameterName);
                observables.get(serviceName).put(parameterName,
                        new ParameterObservable());
            }
            if (observables.get(serviceName).get(parameterName)
                    .countObservers() > 0) {
                Parameter parameter = valueChangeEvent.getParameter();
                String responseStringValue = ValueResponseFactory
                        .createStringValueFromValueType(valueType, parameter);
                observables.get(serviceName).get(parameterName)
                        .setNewValue(responseStringValue);
            }
        }

    }
}
