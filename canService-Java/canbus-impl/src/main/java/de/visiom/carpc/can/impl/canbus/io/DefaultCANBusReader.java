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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import peak.can.basic.TPCANMsg;
import peak.can.basic.TPCANStatus;
import de.visiom.carpc.can.canbus.io.CANBusEventObserver;
import de.visiom.carpc.can.canbus.io.CANBusReader;
import de.visiom.carpc.can.entities.Signal;
import de.visiom.carpc.can.impl.canbus.PCANAPIWrapper;
import de.visiom.carpc.can.impl.canbus.converter.CANMessageConverter;
import de.visiom.carpc.can.impl.canbus.converter.exceptions.UnknownIdentifierException;

public class DefaultCANBusReader implements CANBusReader {

    private static final Logger LOG = LoggerFactory
            .getLogger(DefaultCANBusReader.class);

    private PCANAPIWrapper pcanApiWrapper;

    private final TPCANMsg message = new TPCANMsg();

    private final Map<Integer, Integer> messageHashCodes = new HashMap<Integer, Integer>();

    private final Set<CANBusEventObserver> observers = new HashSet<CANBusEventObserver>();

    private int currentID = 0;

    private int errorCounter = 0;

    private static final int MAX_NUMBER_OF_SUBSEQUENT_ERRORS = 5;

    private CANMessageConverter converter;

    public void setPcanApiWrapper(PCANAPIWrapper pcanApiWrapper) {
        this.pcanApiWrapper = pcanApiWrapper;
    }

    public void setConverter(CANMessageConverter converter) {
        this.converter = converter;
    }

    public void executeIteration() {
        for (int i = 0; i < 30; i++) {
            TPCANStatus state = pcanApiWrapper.read(message);
            if (state.equals(TPCANStatus.PCAN_ERROR_OK)) {
                processMessage();
            } else if (errorCounter < MAX_NUMBER_OF_SUBSEQUENT_ERRORS) {
                LOG.error(
                        "An error has occured while reading a can message: {}",
                        state);
                errorCounter++;
            }
        }
    }

    private void processMessage() {
        byte[] currentPayload = message.getData();
        currentID = message.getID();
        int hashCodeOfReadMessage = Arrays.hashCode(currentPayload);
        if (doesMessageContainNewData(currentID, hashCodeOfReadMessage)) {
            try {
                Map<Signal, Double> values = converter.bytesToIntegerValues(
                        currentPayload, currentID);
                propagateValueChanges(hashCodeOfReadMessage, values);
            } catch (UnknownIdentifierException e) {
                LOG.error("Could not convert signals for {}", currentID, e);
            }
        }
        errorCounter = 0;
    }

    private boolean doesMessageContainNewData(int messageID, int messageHashCode) {
        return !messageHashCodes.containsKey(messageID)
                || messageHashCode != messageHashCodes.get(currentID)
                        .intValue();
    }

    private void propagateValueChanges(int hashCodeOfReadMessage,
            Map<Signal, Double> values) {
        messageHashCodes.put(currentID, hashCodeOfReadMessage);
        for (CANBusEventObserver observer : observers) {
            observer.onValueChange(values);
        }
    }

    @Override
    public void addObserver(CANBusEventObserver observer) {
        observers.add(observer);
    }
}
