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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.visiom.carpc.asb.messagebus.async.ParallelWorker;
import de.visiom.carpc.can.MessageLoader;
import de.visiom.carpc.can.canbus.io.CANBusEventObserver;
import de.visiom.carpc.can.canbus.io.CANBusReader;
import de.visiom.carpc.can.entities.Message;
import de.visiom.carpc.can.entities.Signal;
import de.visiom.carpc.can.impl.canbus.CANBusSimulator;

public class TestCANBusReader extends ParallelWorker implements CANBusReader {

	private static final Logger LOG = LoggerFactory
			.getLogger(TestCANBusReader.class);

	private static Map<String, Double> dummyValues = new HashMap<String, Double>();

	static {
		dummyValues.put(ParameterNames.GESCHWINDIGKEIT, 0D);
		dummyValues.put(ParameterNames.LADEZUSTAND, 60D);
		dummyValues.put(ParameterNames.LONGACCELERATION, 0D);
		dummyValues.put(ParameterNames.BLINKER, 0D);
		dummyValues.put(ParameterNames.LICHT, 3D);
		dummyValues.put(ParameterNames.GANG, 1D);
		dummyValues.put(ParameterNames.ABS, 0D);
		dummyValues.put(ParameterNames.ESP, 0D);
		dummyValues.put(ParameterNames.FAHRERTUER, 1D);
		dummyValues.put(ParameterNames.BEIFAHRERTUER, 1D);
		dummyValues.put("fronthaube", 1D);
		dummyValues.put("heckklappe", 1D);
		dummyValues.put("tageskilometerstand", 20.8);
		dummyValues.put("gesamtkilometerstand", 2014D);
		dummyValues.put("energieverbrauch", 10D);
		dummyValues.put("aussentemperatur", 18D);
		dummyValues.put("fahrbereit", 0D);
		dummyValues.put("volumeUpPressed", 0D);
		dummyValues.put("volumeDownPressed", 0D);
		dummyValues.put(ParameterNames.SITZGURT, 0D);
		dummyValues.put(ParameterNames.AIRBAG, 0D);
		dummyValues.put("shutdown", 0D);
	}

	private List<Message> messages;

	private final Set<CANBusEventObserver> observers = new HashSet<CANBusEventObserver>();

	private MessageLoader messageLoader;

	private CANBusSimulator canBusSimulator;

	public void setMessageLoader(MessageLoader messageLoader) {
		this.messageLoader = messageLoader;
	}

	public void setCanBusSimulator(CANBusSimulator canBusSimulator) {
		this.canBusSimulator = canBusSimulator;
	}

	@Override
	public void initialize() {
		LOG.info("Initializing the can bus reader...");
		messages = messageLoader.loadMessages();
		LOG.info("{} messages were loaded", messages.size());
		new SpeedGenerator().start();
		new BlinkerGenerator().start();
	}

	@Override
	public long getExecutionInterval() {
		return 200;
	}

	@Override
	public void execute() {
		Map<Signal, Double> doubleValues = new HashMap<Signal, Double>();
		for (Message message : messages) {
			for (Signal signal : message.getSignals()) {
				Double dummyValue = dummyValues.get(signal.getParameterName());
				if (dummyValue != null) {
					doubleValues.put(signal, dummyValue);
				} else {
					doubleValues.put(signal, canBusSimulator.get(signal));
				}
			}
			for (CANBusEventObserver observer : observers) {
				observer.onValueChange(doubleValues);
			}
		}
	}

	class BlinkerGenerator extends Thread {
		@Override
		public void run() {
			while (true) {
				synchronized (this) {
					try {
						wait(300);
					} catch (InterruptedException e) {
						LOG.error("I was interrupted", e);
					}
				}
				dummyValues.put("abs", dummyValues.get("absdeaktivieren"));
				dummyValues.put("esp", dummyValues.get("espdeaktivieren"));
			}
		}
	}

	class SpeedGenerator extends Thread {

		boolean increment = true;

		@Override
		public void run() {
			while (true) {
				synchronized (this) {
					try {
						wait(300);
					} catch (InterruptedException e) {
						LOG.error("I was interrupted", e);
					}
				}

				if (increment) {
					dummyValues
							.put(ParameterNames.GESCHWINDIGKEIT, dummyValues
									.get(ParameterNames.GESCHWINDIGKEIT) + 1);
					dummyValues
							.put("energieverbrauch", dummyValues
									.get(ParameterNames.GESCHWINDIGKEIT) / 5.0);
					//dummyValues.put(ParameterNames.LADEZUSTAND,
					//		dummyValues.get(ParameterNames.LADEZUSTAND) - 0.5);
					// dummyValues.put("volumeDownPressed", 0D);
					// double newValue = dummyValues.get("volumeUpPressed") == 0D ? 1D : 0D;
					// dummyValues.put("volumeUpPressed", newValue);
				} else {
					dummyValues
							.put(ParameterNames.GESCHWINDIGKEIT, dummyValues
									.get(ParameterNames.GESCHWINDIGKEIT) - 1);
					dummyValues.put("energieverbrauch",
							dummyValues.get(ParameterNames.GESCHWINDIGKEIT)
									/ -10.0);
					// dummyValues.put(ParameterNames.LADEZUSTAND,
					//		dummyValues.get(ParameterNames.LADEZUSTAND) + 0.5);
					// double newValue = dummyValues.get("volumeDownPressed") == 0D ? 1D : 0D;
					// dummyValues.put("volumeDownPressed", newValue);
					// dummyValues.put("volumeUpPressed", 0D);
				}
				if (dummyValues.get(ParameterNames.GESCHWINDIGKEIT) == 120
						|| dummyValues.get(ParameterNames.GESCHWINDIGKEIT) == 0) {
					increment = !increment;
				}
				if (dummyValues.get(ParameterNames.GESCHWINDIGKEIT) == 0) {
					synchronized (this) {
						try {
							dummyValues.put(ParameterNames.GANG, 3D);
							dummyValues.put("blinker", 3D);
							wait(10000);
							dummyValues.put("blinker", 0D);
							dummyValues.put("fahrbereit", 1D);
							wait(20000);
							dummyValues.put(ParameterNames.GANG, 1D);
							dummyValues.put("fahrbereit", 0D);
						} catch (InterruptedException e) {
							LOG.error("I was interrupted", e);
						}
					}
				}
			}
		}
	}

	@Override
	public void addObserver(CANBusEventObserver observer) {
		observers.add(observer);
	}
}
