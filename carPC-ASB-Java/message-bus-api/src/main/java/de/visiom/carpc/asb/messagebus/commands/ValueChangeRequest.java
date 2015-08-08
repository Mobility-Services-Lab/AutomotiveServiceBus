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
package de.visiom.carpc.asb.messagebus.commands;

import de.visiom.carpc.asb.servicemodel.parameters.Parameter;
import de.visiom.carpc.asb.servicemodel.valueobjects.ValueObject;

/**
 * This class represents a request that asks a service to change one of its
 * parameters.
 * 
 * @author david
 * 
 */
public class ValueChangeRequest implements Request {

    private final Parameter parameter;

    private final ValueObject value;

    protected ValueChangeRequest(Parameter parameter, ValueObject value) {
        this.parameter = parameter;
        this.value = value;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public ValueObject getValue() {
        return value;
    }

    /**
     * 
     * @param parameter
     *            The parameter that should be changed
     * @param value
     *            The new value of the parameter
     * @return A {@link ValueChangeRequest} object
     */
    public static ValueChangeRequest createValueChangeRequest(
            Parameter parameter, ValueObject value) {
        return new ValueChangeRequest(parameter, value);
    }

}
