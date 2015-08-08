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
package de.visiom.carpc.asb.parametervalueregistry.exceptions;

import de.visiom.carpc.asb.parametervalueregistry.ParameterValueRegistry;
import de.visiom.carpc.asb.servicemodel.parameters.Parameter;

/**
 * Thrown when the {@link ParameterValueRegistry} can't return the value for a
 * specific parameter. That usually means that the parameters service did not
 * yet publish a value change event for it.
 * 
 * @author david
 * 
 */
public final class UninitalizedValueException extends Exception {

    private static final long serialVersionUID = -8355100003957712546L;

    private static final String MESSAGE = "The parameter %s/%s has not been initialized yet!";

    public UninitalizedValueException(Parameter parameter) {
        super(String.format(MESSAGE, parameter.getService().getName(),
                parameter.getName()));
    }
}
