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

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import com.google.common.collect.Range;

import de.visiom.carpc.asb.serviceregistry.impl.entities.adapters.RangeAdapter;

public class RangeAdapterTest {

    private final RangeAdapter rangeAdapter = new RangeAdapter();

    @Test
    public void testUnmarshalString() throws Exception {
        String string = "0..100";
        Range<BigDecimal> range = rangeAdapter.unmarshal(string);
        assertEquals(Range.open(new BigDecimal(0), new BigDecimal(100)), range);
        string = "-30..30";
        range = rangeAdapter.unmarshal(string);
        assertEquals(Range.open(new BigDecimal(-30), new BigDecimal(30)), range);
        string = "1..9";
        range = rangeAdapter.unmarshal(string);
        assertEquals(Range.open(new BigDecimal(1), new BigDecimal(9)), range);
    }

}
