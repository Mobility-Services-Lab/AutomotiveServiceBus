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
package de.visiom.carpc.asb.rest.impl.entities;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.visiom.carpc.asb.rest.impl.entities.adapters.ParameterAdapter;
import de.visiom.carpc.asb.rest.parameters.RestParameter;
import de.visiom.carpc.asb.rest.parameters.SetRestParameter;
import de.visiom.carpc.asb.servicemodel.exceptions.IncompatibleValueException;
import de.visiom.carpc.asb.servicemodel.exceptions.NoSuchParameterException;
import de.visiom.carpc.asb.servicemodel.parameters.NumericParameter;
import de.visiom.carpc.asb.servicemodel.parameters.Parameter;
import de.visiom.carpc.asb.servicemodel.parameters.SetParameter;
import de.visiom.carpc.asb.servicemodel.parameters.StateParameter;
import de.visiom.carpc.asb.servicemodel.valueobjects.SetValueObject;
import de.visiom.carpc.asb.servicemodel.valueobjects.ValueObject;

@XmlRootElement(name = "restSetParameter")
public class RestSetParameterImpl extends RestParameterImpl implements
		SetRestParameter {

	private transient SetParameter referenceParameter;

	private List<RestParameter> parameters;

	private final String type = "set";

	private static final Logger LOG = LoggerFactory
			.getLogger(RestSetParameterImpl.class);

	@XmlAttribute
	@XmlJavaTypeAdapter(value = ParameterAdapter.class)
	public SetParameter getReferenceParameter() {
		return referenceParameter;
	}

	public void setReferenceParameter(SetParameter referenceParameter) {
		this.referenceParameter = referenceParameter;
	}

	@Override
	public void initialize() {
		// parameters = referenceParameter.getParameters();
	}

	@Override
	public Object createValue(
			Map<Parameter, ValueObject> referenceParameterValues) {
		SetValueObject setValueObject = (SetValueObject) referenceParameterValues
				.get(referenceParameter);
		Map<String, String> valuesToReturn = new HashMap<String, String>();
		for (Map.Entry<Parameter, ValueObject> entry : setValueObject
				.getValue().entrySet()) {
			valuesToReturn.put(entry.getKey().getName(), entry.getValue()
					.toString());
		}
		return new Gson().toJson(valuesToReturn);
		// return referenceParameterValues.get(referenceParameter).toString();
	}

	@Override
	public List<Parameter> getReferencedParameters() {
		return Arrays.asList(new Parameter[] { referenceParameter });
	}

	@Override
	public Map<Parameter, ValueObject> getChanges(Object newValue)
			throws IncompatibleValueException {
		String valueString = (String) newValue;
		Type type = new TypeToken<Map<String, String>>() {
		}.getType();
		Map<String, String> map = new Gson().fromJson(valueString, type);
		Map<Parameter, ValueObject> newValueObjects = new HashMap<Parameter, ValueObject>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			Parameter currentParameter;
			try {
				currentParameter = referenceParameter.getParameter(entry
						.getKey());
				newValueObjects.put(currentParameter,
						currentParameter.createValueObject(entry.getValue()));
			} catch (NoSuchParameterException e) {
				LOG.error(
						"Parameter {} was referenced in REST query, but it is not a part of the referenced SetParameter {}!",
						entry.getKey(), referenceParameter);
			}
		}

		Map<Parameter, ValueObject> result = new HashMap<Parameter, ValueObject>();
		result.put(referenceParameter,
				referenceParameter.createValueObject(newValueObjects));
		return result;
	}

	@Override
	public Parameter getSubscriptionParameter() {
		return referenceParameter;
	}

	@Override
	@XmlElementWrapper(name = "parameters")
	@XmlAnyElement(lax = true)
	public List<RestParameter> getParameters() {
		return parameters;
	}
}
