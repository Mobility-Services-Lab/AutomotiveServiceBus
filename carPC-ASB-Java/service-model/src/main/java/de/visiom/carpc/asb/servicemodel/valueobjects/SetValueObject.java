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
package de.visiom.carpc.asb.servicemodel.valueobjects;

import java.util.Map;

import de.visiom.carpc.asb.servicemodel.parameters.Parameter;

public class SetValueObject implements ValueObject {

    private final Map<Parameter, ValueObject> parameterValues;

    protected SetValueObject(Map<Parameter, ValueObject> parameterValues) {
        this.parameterValues = parameterValues;
    }

    public static SetValueObject valueOf(
            Map<Parameter, ValueObject> parameterValues) {
        return new SetValueObject(parameterValues);
    }

    public Map<Parameter, ValueObject> getValue() {
        return parameterValues;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((parameterValues == null) ? 0 : parameterValues.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SetValueObject)) {
            return false;
        }
        SetValueObject other = (SetValueObject) obj;
        if (parameterValues == null) {
            if (other.parameterValues != null) {
                return false;
            }
        } else if (!parameterValues.equals(other.parameterValues)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SetValueObject [parameterValues=" + parameterValues + "]";
    }

}
