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

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
@Access(AccessType.FIELD)
public class ParameterReference {

    private String serviceName;

    private String name;

    @OneToOne(optional = true, cascade = CascadeType.PERSIST)
    private Signal readSignal;

    @OneToOne(optional = true, cascade = CascadeType.PERSIST)
    private Signal writeSignal;

    @OneToMany(mappedBy = "parameter", cascade = CascadeType.ALL)
    private List<StateMapping> stateMappings;

    public ParameterReference() {

    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Signal getReadSignal() {
        return readSignal;
    }

    public void setReadSignal(Signal readSignal) {
        this.readSignal = readSignal;
    }

    public Signal getWriteSignal() {
        return writeSignal;
    }

    public void setWriteSignal(Signal writeSignal) {
        this.writeSignal = writeSignal;
    }

    public List<StateMapping> getStateMappings() {
        return stateMappings;
    }

    public void setStateMappings(List<StateMapping> stateMappings) {
        this.stateMappings = stateMappings;
    }

    public boolean isReadable() {
        return getReadSignal() != null;
    }

    public boolean isWriteable() {
        return getWriteSignal() != null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result
                + ((serviceName == null) ? 0 : serviceName.hashCode());
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
        if (!(obj instanceof ParameterReference)) {
            return false;
        }
        ParameterReference other = (ParameterReference) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (serviceName == null) {
            if (other.serviceName != null) {
                return false;
            }
        } else if (!serviceName.equals(other.serviceName)) {
            return false;
        }
        return true;
    }

}
