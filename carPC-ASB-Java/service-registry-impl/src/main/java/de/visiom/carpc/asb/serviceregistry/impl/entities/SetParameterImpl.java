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
import java.util.Map;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.visiom.carpc.asb.servicemodel.exceptions.IncompatibleValueException;
import de.visiom.carpc.asb.servicemodel.exceptions.NoSuchParameterException;
import de.visiom.carpc.asb.servicemodel.parameters.Parameter;
import de.visiom.carpc.asb.servicemodel.parameters.SetParameter;
import de.visiom.carpc.asb.servicemodel.valueobjects.SetValueObject;
import de.visiom.carpc.asb.servicemodel.valueobjects.ValueObject;

@XmlRootElement(name = "setParameter")
public class SetParameterImpl extends ParameterImpl implements SetParameter {

	private List<Parameter> parameters;
	
	@Override
	@XmlElementWrapper(name = "parameters")
	@XmlAnyElement(lax = true)
	public List<Parameter> getParameters() {
		if (getService() != null) {
			for (Parameter parameter : this.parameters) {
				((ParameterImpl) parameter).setService(getService());
			}
		}
		return parameters;
	}

	@Override
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	private static final List<Class<? extends ValueObject>> COMPATIBLE_VALUE_TYPES;

	static {
		COMPATIBLE_VALUE_TYPES = new LinkedList<Class<? extends ValueObject>>();
		COMPATIBLE_VALUE_TYPES.add(SetValueObject.class);
	}

	@Override
	public List<Class<? extends ValueObject>> getCompatibleValueObjects() {
		return COMPATIBLE_VALUE_TYPES;
	}

	@Override
	public ValueObject createValueObject(Object object)
			throws IncompatibleValueException {
		if (object instanceof Map<?, ?>) {
			Map<Parameter, ValueObject> parameterValues = (Map<Parameter, ValueObject>) object;
			for (Map.Entry<Parameter, ValueObject> entry : parameterValues
					.entrySet()) {
				if (!parameters.contains(entry.getKey())) {
					throw new IncompatibleValueException(parameterValues, this);
				}
			}
			return SetValueObject
					.valueOf((Map<Parameter, ValueObject>) parameterValues);
		}
		throw new IncompatibleValueException(object, this);
	}

	@Override
	public Parameter getParameter(String name) throws NoSuchParameterException {
		List<Parameter> parameters = getParameters();
		for (Parameter parameter : parameters) {
			if (parameter.getName().equals(name)) {
				return parameter;
			}
		}
		throw new NoSuchParameterException(getService().getName(), getName()
				+ "/" + name);
	}
}
