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
package de.visiom.carpc.can.impl.valuestore;

import de.visiom.carpc.asb.servicemodel.valueobjects.NumberValueObject;
import de.visiom.carpc.asb.servicemodel.valueobjects.StateValueObject;
import de.visiom.carpc.asb.servicemodel.valueobjects.SwitchValueObject;
import de.visiom.carpc.asb.servicemodel.valueobjects.SwitchValueObject.Switch;
import de.visiom.carpc.asb.servicemodel.valueobjects.ValueObject;
import de.visiom.carpc.can.entities.ParameterReference;
import de.visiom.carpc.can.entities.StateMapping;
import de.visiom.carpc.can.exceptions.InvalidValueException;

public class ValueObjectToNumericValueConverter {

    public static Double convert(ValueObject valueObject,
            ParameterReference parameter)
            throws InvalidValueException {
        Double result = null;
        if (valueObject instanceof NumberValueObject) {
            result = ((NumberValueObject) valueObject).getValue().doubleValue();
        } else if (valueObject instanceof StateValueObject) {
            String state = ((StateValueObject) valueObject).getValue();
            for (StateMapping sm : parameter.getStateMappings()) {
                if (sm.getState().equals(state)) {
                    result = sm.getInternalValue();
                }
            }
        } else if (valueObject instanceof SwitchValueObject) {
            Switch switchType = ((SwitchValueObject) valueObject).getValue();
            if (switchType.equals(Switch.ON)) {
                result = 1D;
            } else {
                result = 0D;
            }
        }
        if (result == null) {
            throw new InvalidValueException(valueObject, parameter);
        }
        return result;
    }

}
