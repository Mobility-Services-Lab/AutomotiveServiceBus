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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.visiom.carpc.asb.servicemodel.Service;
import de.visiom.carpc.asb.servicemodel.exceptions.NoSuchParameterException;
import de.visiom.carpc.asb.servicemodel.parameters.Parameter;

@XmlRootElement(name = "service")
public class ServiceImpl implements Service {

    private long id;

    private String name;

    private String description;

    private final Map<String, Integer> servicePositionCache = new HashMap<String, Integer>();

    @Override
    @XmlAttribute
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    @XmlElementWrapper(name = "parameters")
    @XmlAnyElement(lax = true)
    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    private List<Parameter> parameters = new ArrayList<Parameter>();

    private List<Parameter> preferences = new ArrayList<Parameter>();

    @XmlRootElement(name = "services")
    public static class ServiceList {

        private final List<ServiceImpl> values = new LinkedList<ServiceImpl>();

        @XmlAnyElement(lax = true)
        public List<ServiceImpl> getValues() {
            return values;
        }

        public void add(ServiceImpl service) {
            values.add(service);
        }

    }

    @Override
    @XmlElementWrapper(name = "preferences")
    @XmlAnyElement(lax = true)
    public List<Parameter> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<Parameter> preferences) {
        this.preferences = preferences;
    }

    @Override
    public Parameter getParameter(String parameterName)
            throws NoSuchParameterException {
        Integer position = servicePositionCache.get(parameterName);
        if (position != null) {
            return parameters.get(position);
        }
        Iterator<Parameter> parameterIterator = parameters.iterator();
        Parameter currentParameter;
        for (int i = 0; parameterIterator.hasNext(); i++) {
            currentParameter = parameterIterator.next();
            if (currentParameter.getName().equals(parameterName)) {
                servicePositionCache.put(parameterName, i);
                return currentParameter;
            }
        }
        throw new NoSuchParameterException(getName(), parameterName);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ServiceImpl)) {
            return false;
        }
        ServiceImpl other = (ServiceImpl) obj;
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

}
