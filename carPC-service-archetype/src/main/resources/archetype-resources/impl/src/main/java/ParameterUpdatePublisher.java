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
#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import de.visiom.carpc.asb.messagebus.EventPublisher;
import de.visiom.carpc.asb.messagebus.async.ParallelWorker;
import de.visiom.carpc.asb.messagebus.events.ValueChangeEvent;
import de.visiom.carpc.asb.parametervalueregistry.exceptions.UninitalizedValueException;
import de.visiom.carpc.asb.servicemodel.Service;
import de.visiom.carpc.asb.servicemodel.exceptions.NoSuchParameterException;
import de.visiom.carpc.asb.servicemodel.parameters.NumericParameter;
import de.visiom.carpc.asb.servicemodel.valueobjects.NumberValueObject;
import de.visiom.carpc.asb.servicemodel.valueobjects.ValueObject;
import de.visiom.carpc.asb.serviceregistry.ServiceRegistry;
import de.visiom.carpc.asb.serviceregistry.exceptions.NoSuchServiceException;

public class ParameterUpdatePublisher extends ParallelWorker {

    private NumericParameter testParameter;

    private EventPublisher eventPublisher;

    private ServiceRegistry serviceRegistry;

    private int currentValue;

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void initialize() throws NoSuchParameterException,
            NoSuchServiceException, UninitalizedValueException {
        Service testService = serviceRegistry.getService("${serviceName}");
        testParameter = (NumericParameter) testService.getParameter("${parameterName}");
        resetCurrentValue();
    }

    @Override
    public long getExecutionInterval() {
        return 5000;
    }

    @Override
    public void execute() {
        ValueObject valueObject = NumberValueObject.valueOf(currentValue++);
        ValueChangeEvent valueChangeEvent = ValueChangeEvent
                .createValueChangeEvent(testParameter, valueObject);
        eventPublisher.publishValueChange(valueChangeEvent);
        if (currentValueIsTooBig()) {
            resetCurrentValue();
        }
    }

    public boolean currentValueIsTooBig() {
        return currentValue >= testParameter.getRange().upperEndpoint()
                .intValue();
    }

    public void resetCurrentValue() {
        currentValue = testParameter.getRange().lowerEndpoint().intValue();
    }
}