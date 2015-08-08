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
package de.visiom.carpc.can.impl.canbus.io;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.visiom.carpc.can.canbus.io.CANBusWriter;
import de.visiom.carpc.can.entities.Signal;
import de.visiom.carpc.can.impl.canbus.CANBusSimulator;

public class TestCANBusWriter implements CANBusWriter {

    private static final Logger LOG = LoggerFactory
            .getLogger(TestCANBusWriter.class);

    private CANBusSimulator canBusSimulator;

    public void setCanBusSimulator(CANBusSimulator canBusSimulator) {
        this.canBusSimulator = canBusSimulator;
    }

    @Override
    public boolean setSignals(int messageIdentifier,
            Map<Signal, Double> signalValues) {
        for (Map.Entry<Signal, Double> signalEntry : signalValues.entrySet()) {
            canBusSimulator.set(signalEntry.getKey(), signalEntry.getValue());
        }
        return true;
    }

}
