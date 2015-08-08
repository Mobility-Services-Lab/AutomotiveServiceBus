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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Range;

import de.visiom.carpc.asb.servicemodel.exceptions.IncompatibleValueException;
import de.visiom.carpc.asb.servicemodel.parameters.NumericParameter;
import de.visiom.carpc.asb.servicemodel.valueobjects.NumberValueObject;
import de.visiom.carpc.asb.servicemodel.valueobjects.ValueObject;

@XmlRootElement(name = "numericParameter")
public final class NumericParameterImpl extends ParameterImpl implements
        NumericParameter {

    private Range<BigDecimal> range;

    private String unitSymbol;

    private static final Logger LOG = LoggerFactory
            .getLogger(NumericParameterImpl.class);

    private static final List<Class<? extends ValueObject>> COMPATIBLE_VALUE_TYPES;

    static {
        COMPATIBLE_VALUE_TYPES = new LinkedList<Class<? extends ValueObject>>();
        COMPATIBLE_VALUE_TYPES.add(NumberValueObject.class);
    }

    @Override
    @XmlAttribute(required = true)
    public Range<BigDecimal> getRange() {
        return range;
    }

    @Override
    public void setRange(Range<BigDecimal> range) {
        this.range = range;
    }

    @Override
    @XmlAttribute
    public String getUnitSymbol() {
        return unitSymbol;
    }

    public void setUnitSymbol(String unitSymbol) {
        this.unitSymbol = unitSymbol;
    }

    @Override
    @XmlTransient
    public List<Class<? extends ValueObject>> getCompatibleValueObjects() {
        return COMPATIBLE_VALUE_TYPES;
    }

    @Override
    public ValueObject createValueObject(Object object)
            throws IncompatibleValueException {
        Number number;
        if (object instanceof Number) {
            number = (Number) object;
        } else {
            try {
                number = NumberFormat.getInstance().parse(object.toString());
            } catch (ParseException e) {
                throw new IncompatibleValueException(object, this);
            }
        }
        if (!isValueInRange(number)) {
            LOG.info(
                    "The valid range is {}..{}, given value {} for {} is out of range!",
                    getRange().lowerEndpoint().toString(), getRange()
                            .upperEndpoint().toString(), number.toString(),
                    getName());
            throw new IncompatibleValueException(object, this);
        }
        return NumberValueObject.valueOf(number);
    }

    private boolean isValueInRange(Number number) {
        Double lowerEndpoint = getRange().lowerEndpoint().doubleValue();
        Double upperEndpoint = getRange().upperEndpoint().doubleValue();
        return number.doubleValue() >= lowerEndpoint
                && number.doubleValue() <= upperEndpoint;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((range == null) ? 0 : range.hashCode());
        result = prime * result
                + ((unitSymbol == null) ? 0 : unitSymbol.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof NumericParameterImpl)) {
            return false;
        }
        NumericParameterImpl other = (NumericParameterImpl) obj;
        if (range == null) {
            if (other.range != null) {
                return false;
            }
        } else if (!range.equals(other.range)) {
            return false;
        }
        if (unitSymbol == null) {
            if (other.unitSymbol != null) {
                return false;
            }
        } else if (!unitSymbol.equals(other.unitSymbol)) {
            return false;
        }
        return true;
    }

}
