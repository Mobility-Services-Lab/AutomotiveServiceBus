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
package de.visiom.carpc.asb.rest.impl.entities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.visiom.carpc.asb.rest.impl.entities.adapters.ParameterAdapter;
import de.visiom.carpc.asb.rest.parameters.CurrentTargetNumericRestParameter;
import de.visiom.carpc.asb.rest.parameters.NumericRestParameter;
import de.visiom.carpc.asb.servicemodel.exceptions.IncompatibleValueException;
import de.visiom.carpc.asb.servicemodel.parameters.NumericParameter;
import de.visiom.carpc.asb.servicemodel.parameters.Parameter;
import de.visiom.carpc.asb.servicemodel.valueobjects.ValueObject;

@XmlRootElement(name = "restNumericParameter")
public class CurrentTargetNumericRestParameterImpl extends RestParameterImpl
        implements CurrentTargetNumericRestParameter {

    private transient NumericParameter currentReferenceParameter;

    private transient NumericParameter targetReferenceParameter;

    private final static String type = "currentTargetNumeric";

    @XmlAttribute
    @XmlJavaTypeAdapter(value = ParameterAdapter.class)
    public NumericParameter getCurrentReferenceParameter() {
        return currentReferenceParameter;
    }

    public void setCurrentReferenceParameter(NumericParameter referenceParameter) {
        this.currentReferenceParameter = referenceParameter;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(value = ParameterAdapter.class)
    public NumericParameter getTargetReferenceParameter() {
        return targetReferenceParameter;
    }

    public void setTargetReferenceParameter(
            NumericParameter targetReferenceParameter) {
        this.targetReferenceParameter = targetReferenceParameter;
    }

    @Override
    public Object createValue(
            Map<Parameter, ValueObject> referenceParameterValues) {
        return referenceParameterValues.get(currentReferenceParameter)
                .toString();
    }

    @Override
    public List<Parameter> getReferencedParameters() {
        return Arrays.asList(new Parameter[] { currentReferenceParameter });
    }

    @Override
    public Map<Parameter, ValueObject> getChanges(Object newValue)
            throws IncompatibleValueException {
        Map<Parameter, ValueObject> result = new HashMap<Parameter, ValueObject>();
        result.put(currentReferenceParameter,
                currentReferenceParameter.createValueObject(newValue));
        return result;
    }

    @Override
    public Parameter getSubscriptionParameter() {
        return currentReferenceParameter;
    }

    @Override
    public NumericRestParameter getCurrentValueParameter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NumericRestParameter getTargetValueParameter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub

    }

}
