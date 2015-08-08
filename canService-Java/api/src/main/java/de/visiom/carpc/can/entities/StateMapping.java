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
package de.visiom.carpc.can.entities;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Access(AccessType.FIELD)
public class StateMapping {

    public StateMapping() {

    }

    public StateMapping(String state, Double internalValue) {
        this.state = state;
        this.internalValue = internalValue;
    }

    private String state;

    private Double internalValue;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getInternalValue() {
        return internalValue;
    }

    public void setInternalValue(Double internalValue) {
        this.internalValue = internalValue;
    }

    @ManyToOne
    private ParameterReference parameter;

    public ParameterReference getParameter() {
        return parameter;
    }

    public void setParameter(ParameterReference parameter) {
        this.parameter = parameter;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((internalValue == null) ? 0 : internalValue.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
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
        if (!(obj instanceof StateMapping)) {
            return false;
        }
        StateMapping other = (StateMapping) obj;
        if (internalValue == null) {
            if (other.internalValue != null) {
                return false;
            }
        } else if (!internalValue.equals(other.internalValue)) {
            return false;
        }
        if (state == null) {
            if (other.state != null) {
                return false;
            }
        } else if (!state.equals(other.state)) {
            return false;
        }
        return true;
    }

}
