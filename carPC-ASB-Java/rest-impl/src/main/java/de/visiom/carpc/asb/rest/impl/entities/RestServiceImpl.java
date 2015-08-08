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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.visiom.carpc.asb.rest.parameters.RestParameter;
import de.visiom.carpc.asb.rest.parameters.RestService;
import de.visiom.carpc.asb.servicemodel.exceptions.NoSuchParameterException;

@XmlRootElement(name = "service")
public class RestServiceImpl implements RestService {

    private static final Logger LOG = LoggerFactory
            .getLogger(RestServiceImpl.class);

    private List<RestParameter> parameters;

    private final transient Map<String, Integer> servicePositionCache = new HashMap<String, Integer>();

    private String name;

    private transient String path;

    private transient int ranking;

    @Override
    @XmlElementWrapper(name = "parameters")
    @XmlAnyElement(lax = true)
    public List<RestParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<RestParameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @XmlAttribute
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    @XmlAttribute
    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    @Override
    public RestParameter getParameter(String parameterPath)
            throws NoSuchParameterException {
        Integer position = servicePositionCache.get(parameterPath);
        if (position != null) {
            return parameters.get(position);
        }
        Iterator<RestParameter> parameterIterator = parameters.iterator();
        RestParameter currentParameter;
        for (int i = 0; parameterIterator.hasNext(); i++) {
            currentParameter = parameterIterator.next();
            if (currentParameter.getPath().equals(parameterPath)) {
                servicePositionCache.put(parameterPath, i);
                return currentParameter;
            }
        }
        throw new NoSuchParameterException(getName(), parameterPath);
    }

    @XmlRootElement(name = "services")
    public static class RestServiceList {

        private final List<RestServiceImpl> values = new LinkedList<RestServiceImpl>();

        @XmlAnyElement(lax = true)
        public List<RestServiceImpl> getValues() {
            return values;
        }

        public void add(RestServiceImpl service) {
            values.add(service);
        }

    }

}
