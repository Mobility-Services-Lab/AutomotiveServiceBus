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

import peak.can.basic.TPCANMsg;
import peak.can.basic.TPCANStatus;
import de.visiom.carpc.can.canbus.io.CANBusWriter;
import de.visiom.carpc.can.entities.Signal;
import de.visiom.carpc.can.impl.canbus.PCANAPIWrapper;
import de.visiom.carpc.can.impl.canbus.converter.CANMessageConverter;
import de.visiom.carpc.can.impl.canbus.converter.exceptions.UnknownIdentifierException;

public class DefaultCANBusWriter implements CANBusWriter {

    private static final Logger LOG = LoggerFactory
            .getLogger(DefaultCANBusWriter.class);

    private PCANAPIWrapper pcanApiWrapper;

    private CANMessageConverter converter;

    private int errorCounter = 0;

    private static final String ERROR_LOG_MESSAGE = "An error has occured while sending a can message: {}";

    public void setConverter(CANMessageConverter converter) {
        this.converter = converter;
    }

    public void setPcanApiWrapper(PCANAPIWrapper pcanApiWrapper) {
        this.pcanApiWrapper = pcanApiWrapper;
    }

    @Override
    public boolean setSignals(int messageIdentifier,
            Map<Signal, Double> signalValues) {
        TPCANStatus status = null;
        boolean wasSuccessfull = false;
        try {
            byte[] bytesToSend = converter.integerValuesToBytes(signalValues,
                    messageIdentifier);
            TPCANMsg canMessage = prepareMessage(bytesToSend, messageIdentifier);
            status = pcanApiWrapper.write(canMessage);
            wasSuccessfull = status == TPCANStatus.PCAN_ERROR_OK;
            if (!wasSuccessfull && errorCounter < 5) {
                LOG.error(ERROR_LOG_MESSAGE, status);
                errorCounter++;
            } else if (wasSuccessfull && errorCounter != 0) {
                errorCounter = 0;
            }
        } catch (UnknownIdentifierException e) {
            LOG.info("Error while trying to forward values to the CAN bus:", e);
        }
        return wasSuccessfull;
    }

    private TPCANMsg prepareMessage(byte[] bytesToSend, int messageIdentifier) {
        TPCANMsg canMessage = new TPCANMsg();
        canMessage.setData(bytesToSend, (byte) bytesToSend.length);
        canMessage.setLength((byte) bytesToSend.length);
        canMessage.setType(TPCANMsg.MSGTYPE_STANDARD);
        canMessage.setID(messageIdentifier);
        return canMessage;
    }

}
