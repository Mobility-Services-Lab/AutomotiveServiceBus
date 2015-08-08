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
package de.visiom.carpc.can.impl.canbus.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.visiom.carpc.can.MessageLoader;
import de.visiom.carpc.can.entities.Message;
import de.visiom.carpc.can.entities.Signal;
import de.visiom.carpc.can.impl.canbus.converter.exceptions.UnknownIdentifierException;
import de.visiom.carpc.can.impl.canbus.converter.streams.BitInputStream;
import de.visiom.carpc.can.impl.canbus.converter.streams.BitOutputStream;

public class DefaultCANMessageConverter implements CANMessageConverter {

    private static final Logger LOG = LoggerFactory
            .getLogger(DefaultCANMessageConverter.class);

    private final Map<Integer, List<Signal>> signals = new HashMap<Integer, List<Signal>>();

    private BitInputStream bis;

    private MessageLoader messageLoader;

    public void init() {
        for (Message message : messageLoader.loadMessages()) {
            List<Signal> sortedSignals = new ArrayList<Signal>();
            sortedSignals.addAll(message.getSignals());
            Collections.sort(sortedSignals);
            signals.put(message.getIdentifier(), sortedSignals);
        }
    }

    public void setMessageLoader(MessageLoader messageLoader) {
        this.messageLoader = messageLoader;
    }

    @Override
    public Map<Signal, Double> bytesToIntegerValues(byte[] bytes, int identifier)
            throws UnknownIdentifierException {
        byte[] receivedBytes = bytes;
        if (!isIdentifierKnown(identifier)) {
            throw new UnknownIdentifierException(identifier);
        }
        Map<Signal, Double> result = new HashMap<Signal, Double>();
        List<Signal> currentSignals = signals.get(identifier);
        receivedBytes = reverseArray(receivedBytes);
        int absoluteEndPositionCurrentSignal = 0;
        int absoluteStartPositionCurrentSignal = 0;
        int currentAbsolutePosition = getAbsoluteBitStartPosition(
                Byte.SIZE - 1, Byte.SIZE - 1);
        bis = new BitInputStream(new ByteArrayInputStream(receivedBytes));
        int bitsToBeRead = 0;
        try {
            for (Signal currentSignal : currentSignals) {
                absoluteEndPositionCurrentSignal = getCurrentSignalAbsoluteEndPosition(currentSignal);
                absoluteStartPositionCurrentSignal = getAbsoluteBitStartPosition(
                        currentSignal.getStartBit(),
                        currentSignal.getStartByte());
                if (currentAbsolutePosition != absoluteEndPositionCurrentSignal) {
                    bitsToBeRead = currentAbsolutePosition
                            - absoluteEndPositionCurrentSignal;
                    bis.readBits(bitsToBeRead);
                    currentAbsolutePosition -= bitsToBeRead;
                }
                bitsToBeRead = currentAbsolutePosition
                        - absoluteStartPositionCurrentSignal + 1;
                long readValue = bis.readBits(bitsToBeRead);
                currentAbsolutePosition -= bitsToBeRead;
                double convertedValue;
                if (currentSignal.isSigned()) {
                    convertedValue = readValue;
                    convertedValue *= currentSignal.getStepSize();
                } else {
                    convertedValue = readValue;
                    convertedValue *= currentSignal.getStepSize();
                    convertedValue += currentSignal.getLowerBound();
                }
                result.put(currentSignal, convertedValue);
            }
        } catch (IOException ioe) {
            LOG.error(
                    "An exception has occured while converting the can message: ",
                    ioe);
        }
        bis.close();
        return result;
    }

    private int getCurrentSignalAbsoluteEndPosition(Signal currentSignal) {
        int startIndex = getAbsoluteBitStartPosition(
                currentSignal.getStartBit(), currentSignal.getStartByte());
        return startIndex + currentSignal.getLength() - 1;
    }

    protected boolean isIdentifierKnown(int identifier) {
        return signals.containsKey(identifier);
    }

    @Override
    public byte[] integerValuesToBytes(Map<Signal, Double> signalValues,
            int messageIdentifier) throws UnknownIdentifierException {
        if (!isIdentifierKnown(messageIdentifier)) {
            throw new UnknownIdentifierException(messageIdentifier);
        }
        List<Signal> currentSignals = signals.get(messageIdentifier);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BitOutputStream bitOutputStream = new BitOutputStream(
                byteArrayOutputStream);
        int absoluteEndPositionCurrentSignal = 0;
        int absoluteStartPositionCurrentSignal = 0;
        int currentAbsolutePosition = getAbsoluteBitStartPosition(
                Byte.SIZE - 1, Byte.SIZE - 1);
        int bitsToBeWritten = 0;
        for (Signal currentSignal : currentSignals) {
            absoluteEndPositionCurrentSignal = getCurrentSignalAbsoluteEndPosition(currentSignal);
            absoluteStartPositionCurrentSignal = getAbsoluteBitStartPosition(
                    currentSignal.getStartBit(), currentSignal.getStartByte());
            if (currentAbsolutePosition != absoluteEndPositionCurrentSignal) {
                bitsToBeWritten = currentAbsolutePosition
                        - absoluteEndPositionCurrentSignal;
                bitOutputStream.write(bitsToBeWritten, 0);
                currentAbsolutePosition -= bitsToBeWritten;
            }
            bitsToBeWritten = currentAbsolutePosition
                    - absoluteStartPositionCurrentSignal + 1;
            double convertedValue = 0;
            Double valueToSet = signalValues.get(currentSignal);
            if (valueToSet != null) {
                convertedValue = (valueToSet + Math.abs(currentSignal
                        .getLowerBound())) / currentSignal.getStepSize();
            }
            bitOutputStream.write(bitsToBeWritten, (int) convertedValue);
            currentAbsolutePosition -= bitsToBeWritten;
        }
        bitOutputStream.flush();
        bitOutputStream.close();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return reverseArray(bytes);
    }

    private byte[] reverseArray(byte[] bytes) {
        byte[] newArray = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            newArray[i] = bytes[bytes.length - 1 - i];
        }
        return newArray;
    }

    private int getAbsoluteBitStartPosition(int startBit, int startByte) {
        return startBit + startByte * Byte.SIZE;
    }

}
