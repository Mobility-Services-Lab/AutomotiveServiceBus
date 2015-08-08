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
package de.visiom.carpc.asb.serviceregistry.impl.entities;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.visiom.carpc.asb.servicemodel.exceptions.IncompatibleValueException;
import de.visiom.carpc.asb.servicemodel.parameters.StateParameter;
import de.visiom.carpc.asb.servicemodel.valueobjects.StateValueObject;
import de.visiom.carpc.asb.servicemodel.valueobjects.ValueObject;

@XmlRootElement(name = "stateParameter")
public final class StateParameterImpl extends ParameterImpl implements
		StateParameter {

	private List<String> states;

	private static final Logger LOG = LoggerFactory
			.getLogger(StateParameterImpl.class);

	@Override
	@XmlElementWrapper(name = "states")
	@XmlElement(name = "state")
	public List<String> getStates() {
		return states;
	}

	@Override
	public void setStates(List<String> states) {
		this.states = states;
	}

	private static final List<Class<? extends ValueObject>> COMPATIBLE_VALUE_TYPES;

	static {
		COMPATIBLE_VALUE_TYPES = new LinkedList<Class<? extends ValueObject>>();
		COMPATIBLE_VALUE_TYPES.add(StateValueObject.class);
	}

	@Override
	public List<Class<? extends ValueObject>> getCompatibleValueObjects() {
		return COMPATIBLE_VALUE_TYPES;
	}

	@Override
	public ValueObject createValueObject(Object object)
			throws IncompatibleValueException {
		String toStringValue = object.toString();
		LOG.info("The parameter {} has {} states!", this.getName(),
				states.size());
		for (String state : states) {
			LOG.info("Checking whether {} matches the state {}...",
					toStringValue, state);
			if (toStringValue.equals(state)) {
				return StateValueObject.valueOf(state);
			}
		}
		throw new IncompatibleValueException(object, this);
	}
}
