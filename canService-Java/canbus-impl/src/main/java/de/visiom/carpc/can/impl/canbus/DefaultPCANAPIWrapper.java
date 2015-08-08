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
package de.visiom.carpc.can.impl.canbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import peak.can.basic.PCANBasic;
import peak.can.basic.TPCANBaudrate;
import peak.can.basic.TPCANHandle;
import peak.can.basic.TPCANMsg;
import peak.can.basic.TPCANStatus;
import peak.can.basic.TPCANType;

public class DefaultPCANAPIWrapper implements PCANAPIWrapper {

    private static final Logger LOG = LoggerFactory
            .getLogger(DefaultPCANAPIWrapper.class);

    public void init() {
        if (pcan.initializeAPI()) {
            LOG.info("CAN API was successfully initialized");
            TPCANStatus initStatus = pcan.Initialize(TPCANHandle.PCAN_USBBUS1,
                    TPCANBaudrate.PCAN_BAUD_500K, TPCANType.PCAN_TYPE_DNG_SJA,
                    0, (short) 0);
            initialized = initStatus.equals(TPCANStatus.PCAN_ERROR_OK);
            LOG.info("Initialization status of the CAN interface: {}",
                    initStatus);

        } else {
            LOG.error("Unable to initialize the API");
            initialized = false;
        }
    }

    private final PCANBasic pcan = new PCANBasic();

    private boolean initialized;

    /* (non-Javadoc)
     * @see de.visiom.carpc.can.canbus.PCANAPIWrapper#read(peak.can.basic.TPCANMsg)
     */
    @Override
    public TPCANStatus read(TPCANMsg message) {
        return pcan.Read(TPCANHandle.PCAN_USBBUS1, message, null);
    }

    /* (non-Javadoc)
     * @see de.visiom.carpc.can.canbus.PCANAPIWrapper#write(peak.can.basic.TPCANMsg)
     */
    @Override
    public TPCANStatus write(TPCANMsg message) {
        return pcan.Write(TPCANHandle.PCAN_USBBUS1, message);
    }

    /* (non-Javadoc)
     * @see de.visiom.carpc.can.canbus.PCANAPIWrapper#isInitialized()
     */
    @Override
    public boolean isInitialized() {
        return initialized;
    }

}
