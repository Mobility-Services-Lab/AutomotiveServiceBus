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
package de.visiom.carpc.asb.parametervalueregistry.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.visiom.carpc.asb.messagebus.events.ValueChangeEvent;
import de.visiom.carpc.asb.messagebus.handlers.ValueChangeEventHandler;
import de.visiom.carpc.asb.parametervalueregistry.ParameterValueRegistry;
import de.visiom.carpc.asb.parametervalueregistry.exceptions.UninitalizedValueException;
import de.visiom.carpc.asb.servicemodel.Service;
import de.visiom.carpc.asb.servicemodel.parameters.Parameter;
import de.visiom.carpc.asb.servicemodel.valueobjects.ValueObject;

public class ParameterValueRegistryImpl extends ValueChangeEventHandler
		implements ParameterValueRegistry {

	private static final Logger LOG = LoggerFactory
			.getLogger(ParameterValueRegistryImpl.class);

	private static final String VALUE_CHANGE_LOG_MASK = "[{}:{}] :: {}";

	private final Map<Parameter, ValueObject> values = new HashMap<Parameter, ValueObject>();

	@Override
	public ValueObject getValue(Parameter parameter)
			throws UninitalizedValueException {
		LOG.info("Looking for: " + parameter);
		LOG.info("Availabe parameters are:");
		for (Entry<Parameter, ValueObject> mapEntry : values.entrySet()) {
			LOG.info(mapEntry.getKey() + " ---- " + mapEntry.getValue());
		}
		ValueObject result = values.get(parameter);
		if (result == null) {
			throw new UninitalizedValueException(parameter);
		}
		return result;
	}

	@Override
	public void onValueChangeEvent(ValueChangeEvent valueChangeEvent) {
		ValueObject valueType = valueChangeEvent.getValue();
		Service service = valueChangeEvent.getParameter().getService();
		Parameter parameter = valueChangeEvent.getParameter();
		synchronized (this) {
			values.put(parameter, valueType);
			LOG.info(VALUE_CHANGE_LOG_MASK, service.getName(),
					parameter.getName(), valueType.toString());

		}
	}
}