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
package de.visiom.carpc.asb.messagebus.constants;

/**
 * Message Bus related string constants
 * 
 * @author david
 * 
 */
public final class EventConstants {

    /**
     * Don't instantiate this helper class.
     */
    private EventConstants() {
    }

    public static final String SERVICE_NAME_PROPERTY_KEY = "serviceName";

    public static final String PARAMETER_NAME_PROPERTY_KEY = "parameterName";

    public static final String VALUE_PROPERTY_KEY = "valueChange";

    public static final String REQUEST_PROPERTY_KEY = "request";

    public static final String RESPONSE_PROPERTY_KEY = "response";

    public static final String REQUEST_ID_KEY = "requestID";

    public static final String TIMESTAMP_PROPERTY_KEY = "timestamp";

    public static final String TL_TOPIC = "visiom";

    public static final String TOPIC_SEPARATOR = "/";

    public static final String COMMANDS_SUB_TOPIC = TL_TOPIC + TOPIC_SEPARATOR
            + "commands";

    public static final String REQUESTS_TOPIC = COMMANDS_SUB_TOPIC
            + TOPIC_SEPARATOR + "requests";

    public static final String RESPONSES_TOPIC = COMMANDS_SUB_TOPIC
            + TOPIC_SEPARATOR + "responses";

    public static final String UPDATES_TOPIC = TL_TOPIC + TOPIC_SEPARATOR
            + "updates" + TOPIC_SEPARATOR;

}
