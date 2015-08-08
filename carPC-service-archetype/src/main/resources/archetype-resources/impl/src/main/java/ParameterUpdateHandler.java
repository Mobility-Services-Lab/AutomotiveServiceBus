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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.visiom.carpc.asb.messagebus.events.ValueChangeEvent;
import de.visiom.carpc.asb.messagebus.handlers.ValueChangeEventHandler;
import de.visiom.carpc.asb.servicemodel.valueobjects.NumberValueObject;
import de.visiom.carpc.asb.servicemodel.parameters.Parameter;

public class ParameterUpdateHandler extends ValueChangeEventHandler {

    private static final Logger LOG = LoggerFactory
            .getLogger(ParameterUpdateHandler.class);
    
    @Override
    public void onValueChangeEvent(ValueChangeEvent valueChangeEvent) {
        NumberValueObject numberValueObject = (NumberValueObject) valueChangeEvent
                .getValue();
        Parameter parameter = valueChangeEvent.getParameter();
        Double value = numberValueObject.getValue().doubleValue();
        LOG.info("Received an update for {}/{}: {}", parameter.getName(), 
                parameter.getService().getName(), value);
    }
}
