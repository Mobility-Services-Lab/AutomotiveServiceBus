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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.visiom.carpc.can.MessageLoader;
import de.visiom.carpc.can.entities.Message;
import de.visiom.carpc.can.entities.Signal;

public class CANBusSimulator {

    private static final Logger LOG = LoggerFactory
            .getLogger(CANBusSimulator.class);

    private List<Message> messages;

    private MessageLoader messageLoader;

    private final Map<Signal, Double> signalValue = new HashMap<Signal, Double>();

    public void setMessageLoader(MessageLoader messageLoader) {
        this.messageLoader = messageLoader;
    }

    public void init() {
        LOG.info("initializing the simulator...");
        messages = messageLoader.loadMessages();
        for (Message message : messages) {
            for (Signal signal : message.getSignals()) {
                signalValue.put(signal, 0D);
            }
        }
    }

    public void set(Signal key, Double value) {
        signalValue.put(key, value);
    }

    public Double get(Signal key) {
        return signalValue.get(key);
    }

}
