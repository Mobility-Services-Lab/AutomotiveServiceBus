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
package de.visiom.carpc.asb.rest.impl.factories;

import java.util.HashMap;
import java.util.Map;

public final class LinkMapFactory {

    private static final String VALUE_KEY = "value";

    private static final String SUBSCRIPTION_KEY = "subscription";

    private static final String PARAMETERS_PATH = "parameters";

    private static final String SUBSCRIPTIONS_PATH = "subscription";

    private static final String SEPARATOR = "/";

    private LinkMapFactory() {
    }

    public static Map<String, String> createParametersLinkMap(
            String serviceName, String parameterName) {
        Map<String, String> links = new HashMap<String, String>();
        links.put(VALUE_KEY, serviceName + SEPARATOR + PARAMETERS_PATH
                + SEPARATOR + parameterName);
        links.put(SUBSCRIPTION_KEY, serviceName + SEPARATOR + PARAMETERS_PATH
                + SEPARATOR + parameterName + SEPARATOR + SUBSCRIPTIONS_PATH);
        return links;
    }

}
