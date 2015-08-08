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

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Transient;

import de.visiom.carpc.asb.servicemodel.parameters.Parameter;
import de.visiom.carpc.asb.userstore.entities.Preference;

@Entity(name = "Preference")
@Access(AccessType.FIELD)
public class PreferenceImpl implements Preference {

    private static final String TO_STRING = "%s/%s";

    @EmbeddedId
    private PreferenceImplId id;

    private Parameter parameter;

    public PreferenceImplId getId() {
        return id;
    }

    public void setId(PreferenceImplId id) {
        this.id = id;
    }

    @Override
    @Transient
    public String getServiceName() {
        return id.getServiceName();
    }

    @Override
    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return String.format(TO_STRING, id.getServiceName(),
                id.getParameterName());
    }

}
