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
package de.visiom.carpc.can.impl.valuestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import de.visiom.carpc.asb.messagebus.EventPublisher;
import de.visiom.carpc.asb.messagebus.events.ValueChangeEvent;
import de.visiom.carpc.asb.servicemodel.Service;
import de.visiom.carpc.asb.servicemodel.exceptions.IncompatibleValueException;
import de.visiom.carpc.asb.servicemodel.exceptions.NoSuchParameterException;
import de.visiom.carpc.asb.servicemodel.parameters.Parameter;
import de.visiom.carpc.asb.servicemodel.parameters.StateParameter;
import de.visiom.carpc.asb.servicemodel.valueobjects.StateValueObject;
import de.visiom.carpc.asb.servicemodel.valueobjects.ValueObject;
import de.visiom.carpc.asb.serviceregistry.ServiceRegistry;
import de.visiom.carpc.asb.serviceregistry.exceptions.NoSuchServiceException;
import de.visiom.carpc.can.MessageBusToCANBusForwarder;
import de.visiom.carpc.can.MessageLoader;
import de.visiom.carpc.can.canbus.io.CANBusEventObserver;
import de.visiom.carpc.can.canbus.io.CANBusReader;
import de.visiom.carpc.can.canbus.io.CANBusWriter;
import de.visiom.carpc.can.entities.Message;
import de.visiom.carpc.can.entities.ParameterReference;
import de.visiom.carpc.can.entities.Signal;
import de.visiom.carpc.can.entities.StateMapping;
import de.visiom.carpc.can.exceptions.InvalidValueException;

public class ValueStore implements MessageBusToCANBusForwarder,
        CANBusEventObserver {

    private static final String PROCESSING_ERROR = "Error while processing a write request from the CAN bus: {}";

    private static final String NO_VALID_STATE_MAPPING_ERROR = "Parameter {} does not define a valid state mapping for the value {}!";

    private MessageLoader messageLoader;

    public void setMessageLoader(MessageLoader messageLoader) {
        this.messageLoader = messageLoader;
    }

    private BiMap<ParameterReference, Parameter> parameterRefsToParameters;

    private BiMap<Parameter, ParameterReference> parameterToParameterRefs;

    private final Map<ParameterReference, Double> parameterValues = new HashMap<ParameterReference, Double>();

    private EventPublisher eventPublisher;

    private CANBusWriter canBusWriter;

    private CANBusReader canBusReader;

    private ServiceRegistry serviceRegistry;

    public void setEventPublisher(EventPublisher updatePublisher) {
        this.eventPublisher = updatePublisher;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setCanBusWriter(CANBusWriter canBusWriter) {
        this.canBusWriter = canBusWriter;
    }

    public void setCanBusReader(CANBusReader canBusReader) {
        this.canBusReader = canBusReader;
    }

    private static final Logger LOG = LoggerFactory.getLogger(ValueStore.class);

    public void init() {
        parameterRefsToParameters = HashBiMap.create();
        List<Message> messages = messageLoader.loadMessages();
        LOG.info("Found {} messages", messages.size());
        for (Message message : messages) {
            LOG.info("Processing message {} with {} signals",
                    message.getName(), message.getSignals().size());
            ParameterReference currentParameterReference;
            Parameter currentParameter;
            Service currentService;
            for (Signal signal : message.getSignals()) {
                currentParameterReference = signal.getParameterReference();
                try {
                    currentService = serviceRegistry.getService(signal
                            .getParameterReference().getServiceName());
                    currentParameter = currentService
                            .getParameter(currentParameterReference.getName());
                    parameterRefsToParameters.put(currentParameterReference,
                            currentParameter);
                    if (signal.getParameterName().equals("temperaturvorgabe")) {
                        initParameterValue(signal.getParameterReference(), 22D);
                    } else if (signal.getParameterName().equals("gang")) {
                        initParameterValue(signal.getParameterReference(), 3D);
                    } else if (signal.getParameterName().equals("fahrertür")) {
                        initParameterValue(signal.getParameterReference(), 1D);
                    } else if (signal.getParameterName().equals("beifahrertür")) {
                        initParameterValue(signal.getParameterReference(), 1D);
                    } else if (signal.getParameterName().equals("fronthaube")) {
                        initParameterValue(signal.getParameterReference(), 1D);
                    } else if (signal.getParameterName().equals("heckklappe")) {
                        initParameterValue(signal.getParameterReference(), 1D);
                    } else if (signal.getParameterName().equals("sitzgurt")) {
                        initParameterValue(signal.getParameterReference(), 1D);
                    } else {
                        initParameterValue(signal.getParameterReference(), 0D);
                    }
                    LOG.info(
                            "Added signal {} with reference to the parameter {}/{}",
                            signal.getName(),
                            currentParameterReference.getServiceName(),
                            currentParameterReference.getName());
                } catch (NoSuchParameterException e) {
                    logNonExistingParameter(currentParameterReference, signal);
                } catch (NoSuchServiceException e) {
                    logNonExistingParameter(currentParameterReference, signal);
                }
            }
        }
        parameterToParameterRefs = parameterRefsToParameters.inverse();
        canBusReader.addObserver(this);
    }

    private void logNonExistingParameter(ParameterReference parameterReference,
            Signal signal) {
        LOG.error(
                "The parameter {}/{} referenced by the signal {} does not exist in the parameter registry and will be ignored!",
                parameterReference.getServiceName(),
                parameterReference.getName(), signal.getName());
    }

    private void initParameterValue(ParameterReference parameterReference,
            Double defaultValue) {
        parameterValues.put(parameterReference, defaultValue);
        Parameter parameter = parameterRefsToParameters.get(parameterReference);
        ValueObject valueObject;
        try {
            valueObject = createValueObjectForNumericalValue(defaultValue,
                    parameter, parameterReference);
            eventPublisher.publishValueChange(ValueChangeEvent
                    .createValueChangeEvent(parameter, valueObject));
        } catch (IncompatibleValueException e) {
            LOG.error("Could not initialize {} with {}", parameter.getName(),
                    defaultValue);
        }
    }

    @Override
    public boolean setValue(Parameter parameter, ValueObject valueObject,
            Long requestID) {
        LOG.info("Processing a change request for {} with new value {}...",
                parameter, valueObject);
        ParameterReference parameterReference = parameterToParameterRefs
                .get(parameter);
        Double numericValue;
        boolean wasSuccessfull = false;
        try {
            numericValue = ValueObjectToNumericValueConverter.convert(
                    valueObject, parameterReference);
            if (parameterValues.get(parameterReference).equals(numericValue)) {
                return true;
            }
            Map<Signal, Double> signalValues = prepareWriteSignalsMap(
                    parameterReference, numericValue);
            wasSuccessfull = canBusWriter.setSignals(parameterReference
                    .getWriteSignal().getMessage().getIdentifier(),
                    signalValues);
            if (wasSuccessfull) {
                LOG.info("Value was successfully changed. Writing the change to the message bus...");
                eventPublisher.publishValueChange(ValueChangeEvent
                        .createValueChangeEvent(parameter, valueObject));
            }
        } catch (InvalidValueException e) {
            LOG.info("Could not convert the value {} to a numerical value!",
                    valueObject);
            wasSuccessfull = false;
        }
        return wasSuccessfull;
    }

    private Map<Signal, Double> prepareWriteSignalsMap(
            ParameterReference changedParameter, Double parameterNewNumericValue) {
        Signal changedSignal = changedParameter.getWriteSignal();
        Map<Signal, Double> signalValues = new HashMap<Signal, Double>();
        Message message = changedSignal.getMessage();
        if (!changedParameter.isReadable()) {
            // we can't read this parameter from the can bus, so we have to
            // store the new value as the current value.
            parameterValues.put(changedParameter, parameterNewNumericValue);
            // if the parameter can be read, we will store it as soon as the
            // bus calls us with the new value
        }
        signalValues.put(changedSignal, parameterNewNumericValue);
        for (Signal currentSignal : message.getSignals()) {
            Double currentValue = null;
            if (!currentSignal.equals(changedSignal)) {
                currentValue = parameterValues.get(currentSignal
                        .getParameterReference());
                signalValues.put(currentSignal, currentValue);
            }
        }
        return signalValues;
    }

    private void publishValueChangeFromCANBus(
            ParameterReference changedParameterReference, Double newNumericValue) {
        if (parameterRefsToParameters.containsKey(changedParameterReference)) {
            Parameter changedParameter = parameterRefsToParameters
                    .get(changedParameterReference);
            LOG.info("creating value type for {}:{}",
                    changedParameter.getName(), newNumericValue);
            try {
                ValueObject valueObject = createValueObjectForNumericalValue(
                        newNumericValue, changedParameter,
                        changedParameterReference);
                eventPublisher.publishValueChange(ValueChangeEvent
                        .createValueChangeEvent(changedParameter, valueObject));
            } catch (IncompatibleValueException e) {
                LOG.error(PROCESSING_ERROR, e);
            }

        }
    }

    private ValueObject createValueObjectForNumericalValue(
            double numericalValue, Parameter parameter,
            ParameterReference parameterReference)
            throws IncompatibleValueException {
        if (parameter instanceof StateParameter) {
            return getStateValueObjectForNumericValue(numericalValue,
                    parameterReference);
        } else {
            return parameter.createValueObject(numericalValue);
        }
    }

    private ValueObject getStateValueObjectForNumericValue(Double newValue,
            ParameterReference parameter) {
        for (StateMapping stateMapping : parameter.getStateMappings()) {
            if (stateMapping.getInternalValue().equals(newValue)) {
                return StateValueObject.valueOf(stateMapping.getState());
            }
        }
        LOG.error(NO_VALID_STATE_MAPPING_ERROR, parameter.getName(), newValue);
        return null;
    }

    @Override
    public void onValueChange(Map<Signal, Double> signalValues) {
        ParameterReference currentParameterReference;
        Double newValue = null;
        for (Map.Entry<Signal, Double> entry : signalValues.entrySet()) {
            currentParameterReference = entry.getKey().getParameterReference();
            newValue = entry.getValue();
            Double currentValue = parameterValues
                    .get(currentParameterReference);
            if (currentValue == null || !currentValue.equals(newValue)
                    && newValue != null) {
                parameterValues.put(currentParameterReference, newValue);
                publishValueChangeFromCANBus(currentParameterReference,
                        newValue);
            }
        }
    }
}
