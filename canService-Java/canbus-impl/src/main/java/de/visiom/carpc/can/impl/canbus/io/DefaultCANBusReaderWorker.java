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

import de.visiom.carpc.asb.messagebus.async.ParallelWorker;
import de.visiom.carpc.can.canbus.io.CANBusEventObserver;
import de.visiom.carpc.can.canbus.io.CANBusReader;
import de.visiom.carpc.can.impl.canbus.PCANAPIWrapper;
import de.visiom.carpc.can.impl.canbus.converter.CANMessageConverter;

public class DefaultCANBusReaderWorker extends ParallelWorker implements
        CANBusReader {

    private DefaultCANBusReader reader;

    private PCANAPIWrapper pcanApiWrapper;

    private CANMessageConverter converter;

    public void setPcanApiWrapper(PCANAPIWrapper pcanApiWrapper) {
        this.pcanApiWrapper = pcanApiWrapper;
    }

    public void setConverter(CANMessageConverter converter) {
        this.converter = converter;
    }

    @Override
    public void initialize() {
        reader = new DefaultCANBusReader();
        reader.setConverter(converter);
        reader.setPcanApiWrapper(pcanApiWrapper);
    }

    @Override
    public long getExecutionInterval() {
        return 500;
    }

    @Override
    public void execute() {
        reader.executeIteration();
    }

    @Override
    public void addObserver(CANBusEventObserver observer) {
        reader.addObserver(observer);
    }
}
