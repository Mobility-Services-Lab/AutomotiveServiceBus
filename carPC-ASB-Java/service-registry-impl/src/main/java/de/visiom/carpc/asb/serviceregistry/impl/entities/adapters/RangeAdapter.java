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
package de.visiom.carpc.asb.serviceregistry.impl.entities.adapters;

import java.math.BigDecimal;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.google.common.collect.Range;

public class RangeAdapter extends XmlAdapter<String, Range<BigDecimal>> {

    @Override
    public String marshal(Range<BigDecimal> v) throws Exception {
        return v.toString();
    }

    @Override
    public Range<BigDecimal> unmarshal(String v) throws Exception {
        boolean leftInfinity = false, rightInfinity = false;
        String firstNumberString = v.substring(0, v.indexOf(".."));
        String secondNumberString = v.substring(v.indexOf("..") + 2);
        BigDecimal firstNumber = null, secondNumber = null;
        if (firstNumberString.equals("infty")) {
            leftInfinity = true;
        } else {
            firstNumber = BigDecimal.valueOf(Double
                    .parseDouble(firstNumberString));
        }
        if (secondNumberString.equals("infty")) {
            rightInfinity = true;
        } else {
            secondNumber = BigDecimal.valueOf(Double
                    .parseDouble(secondNumberString));
        }
        Range<BigDecimal> result = null;
        if (!leftInfinity && !rightInfinity) {
            result = Range.open(firstNumber, secondNumber);
        } else if (leftInfinity && rightInfinity) {
            result = Range.all();
        } else if (leftInfinity && !rightInfinity) {
            result = Range.lessThan(secondNumber);
        } else {
            result = Range.greaterThan(firstNumber);
        }
        return result;
    }
}
