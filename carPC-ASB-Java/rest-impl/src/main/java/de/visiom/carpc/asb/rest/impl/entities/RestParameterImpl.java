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

import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.visiom.carpc.asb.rest.parameters.RestParameter;
import de.visiom.carpc.asb.rest.parameters.RestService;

@XmlRootElement(name = "restParameter")
public abstract class RestParameterImpl implements RestParameter {

    private transient RestService restService;

    private boolean readOnly;

    private transient String path;

    private String name;

    private boolean forClusterDisplay;

    private Map<String, String> links;

    @Override
    @XmlTransient
    public RestService getRestService() {
        return restService;
    }

    public void setRestService(RestService restService) {
        this.restService = restService;
    }

    @Override
    @XmlAttribute
    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    @Override
    public void setLinks(Map<String, String> links) {
        this.links = links;
    }

    @Override
    @XmlAttribute
    public boolean isForClusterDisplay() {
        return forClusterDisplay;
    }

    public void setForClusterDisplay(boolean forClusterDisplay) {
        this.forClusterDisplay = forClusterDisplay;
    }

    public abstract void initialize();

}
