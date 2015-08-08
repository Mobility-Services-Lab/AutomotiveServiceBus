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
package de.visiom.carpc.can.canbus.io;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import peak.can.basic.TPCANMsg;
import peak.can.basic.TPCANStatus;
import de.visiom.carpc.can.entities.Signal;
import de.visiom.carpc.can.impl.canbus.PCANAPIWrapper;
import de.visiom.carpc.can.impl.canbus.converter.CANMessageConverter;
import de.visiom.carpc.can.impl.canbus.converter.exceptions.UnknownIdentifierException;
import de.visiom.carpc.can.impl.canbus.io.DefaultCANBusReader;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCANBusReaderTest {

    private DefaultCANBusReader reader;

    @Mock
    private CANBusEventObserver observer;

    @Mock
    private PCANAPIWrapper wrapper;

    @Mock
    private CANMessageConverter converter;

    @Before
    public void setUp() {
        reader = new DefaultCANBusReader();
        reader.addObserver(observer);
        reader.setPcanApiWrapper(wrapper);
        reader.setConverter(converter);
    }

    protected void setUpWrapper(final byte[] bytes) {
        doAnswer(new Answer<TPCANStatus>() {
            @Override
            public TPCANStatus answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                TPCANMsg message = (TPCANMsg) args[0];
                message.setID(1);
                message.setData(bytes, (byte) bytes.length);
                return TPCANStatus.PCAN_ERROR_OK;
            }
        }).when(wrapper).read(any(TPCANMsg.class));
    }

    protected void setUpConverter() {
        try {
            doAnswer(new Answer<Map<Signal, Double>>() {
                @Override
                public Map<Signal, Double> answer(InvocationOnMock invocation) {
                    // Object[] args = invocation.getArguments();
                    // byte[] bytes = (byte[]) args[0];
                    // byte identifier = (Byte) args[1];
                    Map<Signal, Double> values = new HashMap<Signal, Double>();
                    values.put(new Signal(), 0D);
                    return values;
                }
            }).when(converter)
                    .bytesToIntegerValues(any(byte[].class), anyInt());
        } catch (UnknownIdentifierException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDoesInitialChangePropagation() {
        setUpWrapper(new byte[] { 1, 2, 3, 4 });
        setUpConverter();
        reader.executeIteration();
        verify(observer).onValueChange(any(Map.class));
    }

    @Test
    public void doesNotPropagateUnchangedValues() {
        setUpWrapper(new byte[] { 1, 2, 3, 4 });
        setUpConverter();
        for (int i = 0; i < 5; i++) {
            reader.executeIteration();
        }
        verify(observer).onValueChange(any(Map.class));
    }

    @Test
    public void doesPropagateChangedValues() {
        setUpWrapper(new byte[] { 1, 2, 3, 4 });
        setUpConverter();
        for (int i = 0; i < 5; i++) {
            reader.executeIteration();
        }
        setUpWrapper(new byte[] { 4, 3, 2, 1 });
        reader.executeIteration();
        verify(observer, times(2)).onValueChange(any(Map.class));
    }
}
