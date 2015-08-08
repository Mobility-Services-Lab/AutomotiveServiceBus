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
package de.visiom.carpc.asb.userstore.impl.entities;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class PreferenceImplId implements Serializable {

    private static final long serialVersionUID = -7888648887617006300L;

    private String serviceName;

    private String parameterName;

    public PreferenceImplId() {
    }

    public PreferenceImplId(String serviceName, String parameterName) {
        this.serviceName = serviceName;
        this.parameterName = parameterName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    @Override
    public int hashCode() {
        return serviceName.hashCode() + parameterName.hashCode() * 7;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof PreferenceImplId)) {
            return false;
        }
        PreferenceImplId otherID = (PreferenceImplId) other;
        return otherID.getServiceName().equals(this.getServiceName())
                && otherID.getParameterName().equals(this.getParameterName());
    }

}
