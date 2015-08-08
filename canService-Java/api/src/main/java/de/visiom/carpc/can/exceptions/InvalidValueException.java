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
package de.visiom.carpc.can.exceptions;

import de.visiom.carpc.asb.servicemodel.valueobjects.ValueObject;
import de.visiom.carpc.can.entities.ParameterReference;

public class InvalidValueException extends Exception {

    private static final long serialVersionUID = -1115973427463131148L;

    private static final String MESSAGE = "The value %s is not valid for the parameter %s";

    public InvalidValueException(ValueObject valueObject,
            ParameterReference parameter) {
        super(String
                .format(MESSAGE, valueObject.toString(), parameter.toString()));
    }
}
