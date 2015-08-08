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
package de.visiom.carpc.asb.serviceregistry.impl.entities;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.visiom.carpc.asb.servicemodel.parameters.StringParameter;
import de.visiom.carpc.asb.servicemodel.valueobjects.StringValueObject;
import de.visiom.carpc.asb.servicemodel.valueobjects.ValueObject;

@XmlRootElement(name = "stringParameter")
public final class StringParameterImpl extends ParameterImpl implements
        StringParameter {

    private static final List<Class<? extends ValueObject>> COMPATIBLE_VALUE_TYPES;

    static {
        COMPATIBLE_VALUE_TYPES = new LinkedList<Class<? extends ValueObject>>();
        COMPATIBLE_VALUE_TYPES.add(StringValueObject.class);
    }

    @Override
    @XmlTransient
    public List<Class<? extends ValueObject>> getCompatibleValueObjects() {
        return COMPATIBLE_VALUE_TYPES;
    }

    @Override
    public ValueObject createValueObject(Object object) {
        return StringValueObject.valueOf(object);
    }

}
