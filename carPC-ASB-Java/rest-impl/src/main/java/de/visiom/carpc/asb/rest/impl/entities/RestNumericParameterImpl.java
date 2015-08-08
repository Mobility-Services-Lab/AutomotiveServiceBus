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
import de.visiom.carpc.asb.rest.parameters.NumericRestParameter;
import de.visiom.carpc.asb.servicemodel.exceptions.IncompatibleValueException;
import de.visiom.carpc.asb.servicemodel.parameters.NumericParameter;
import de.visiom.carpc.asb.servicemodel.parameters.Parameter;
import de.visiom.carpc.asb.servicemodel.valueobjects.ValueObject;

@XmlRootElement(name = "restNumericParameter")
public class RestNumericParameterImpl extends RestParameterImpl implements
        NumericRestParameter {

    private transient NumericParameter referenceParameter;

    private final String type = "numeric";

    private Double lowerBound;

    private Double upperBound;

    private Double stepSize;

    private Integer intervals;

    private String unitSymbol;

    @XmlAttribute
    @XmlJavaTypeAdapter(value = ParameterAdapter.class)
    public NumericParameter getReferenceParameter() {
        return referenceParameter;
    }

    public void setReferenceParameter(NumericParameter referenceParameter) {
        this.referenceParameter = referenceParameter;
    }

    @Override
    public void initialize() {
        if (referenceParameter == null) {
            throw new NullPointerException("Reference parameter for "
                    + this.getPath() + " was not found!");
        }
        lowerBound = referenceParameter.getRange().lowerEndpoint()
                .doubleValue();
        upperBound = referenceParameter.getRange().upperEndpoint()
                .doubleValue();
        unitSymbol = referenceParameter.getUnitSymbol();
    }

    @Override
    public Double getLowerBound() {
        return lowerBound;
    }

    @Override
    public Double getUpperBound() {
        return upperBound;
    }

    @Override
    @XmlAttribute
    public Double getStepSize() {
        return stepSize;
    }

    public void setStepSize(Double stepSize) {
        this.stepSize = stepSize;
    }

    @Override
    @XmlAttribute
    public Integer getIntervals() {
        return intervals;
    }

    public void setIntervals(Integer intervals) {
        this.intervals = intervals;
    }

    @Override
    public String getUnitSymbol() {
        return unitSymbol;
    }

    @Override
    public Object createValue(
            Map<Parameter, ValueObject> referenceParameterValues) {
        return referenceParameterValues.get(referenceParameter).toString();
    }

    @Override
    public List<Parameter> getReferencedParameters() {
        return Arrays.asList(new Parameter[] { referenceParameter });
    }

    @Override
    public Map<Parameter, ValueObject> getChanges(Object newValue)
            throws IncompatibleValueException {
        Map<Parameter, ValueObject> result = new HashMap<Parameter, ValueObject>();
        result.put(referenceParameter,
                referenceParameter.createValueObject(newValue));
        return result;
    }

    @Override
    public Parameter getSubscriptionParameter() {
        return referenceParameter;
    }

}
