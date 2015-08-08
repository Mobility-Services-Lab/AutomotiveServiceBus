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
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity
@Access(AccessType.FIELD)
public class Signal implements Comparable<Signal> {

    private int startByte;

    private int startBit;

    private int length;

    private double lowerBound;

    private double stepSize;

    private String name;

    private boolean signed;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Message message;

    @OneToOne(optional = false, cascade = CascadeType.PERSIST)
    private ParameterReference parameterReference;

    public Signal() {
    }

    public Signal(int startBit, int startByte, int length, double lowerBound,
            double stepSize, String name, boolean signed, Message message) {
        super();
        this.startByte = startByte;
        this.startBit = startBit;
        this.length = length;
        this.lowerBound = lowerBound;
        this.stepSize = stepSize;
        this.name = name;
        this.signed = signed;
        this.message = message;
    }

    public int getStartByte() {
        return startByte;
    }

    public void setStartByte(int startByte) {
        this.startByte = startByte;
    }

    public int getStartBit() {
        return startBit;
    }

    public void setStartBit(int startBit) {
        this.startBit = startBit;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public double getStepSize() {
        return stepSize;
    }

    public void setStepSize(double stepSize) {
        this.stepSize = stepSize;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public ParameterReference getParameterReference() {
        return parameterReference;
    }

    public void setParameterReference(ParameterReference parameterReference) {
        this.parameterReference = parameterReference;
    }

    @Transient
    public boolean isReadable() {
        return getParameterReference() != null
                && getParameterReference().getReadSignal().equals(this);
    }

    @Transient
    public boolean isWriteable() {
        return getParameterReference() != null
                && getParameterReference().getWriteSignal().equals(this);
    }

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public String getParameterName() {
        return getParameterReference().getName();
    }

    @Override
    public int compareTo(Signal s) {
        if (s.getStartByte() != this.getStartByte()) {
            return Integer.valueOf(s.getStartByte()).compareTo(
                    Integer.valueOf(this.getStartByte()));
        }
        return Integer.valueOf(s.getStartBit()).compareTo(
                Integer.valueOf(this.getStartBit()));
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + length;
        long temp;
        temp = Double.doubleToLongBits(lowerBound);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (signed ? 1231 : 1237);
        result = prime * result + startBit;
        result = prime * result + startByte;
        temp = Double.doubleToLongBits(stepSize);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        if (!(obj instanceof Signal)) {
            return false;
        }
        Signal other = (Signal) obj;
        if (length != other.length) {
            return false;
        }
        if (Double.doubleToLongBits(lowerBound) != Double
                .doubleToLongBits(other.lowerBound)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (signed != other.signed) {
            return false;
        }
        if (startBit != other.startBit) {
            return false;
        }
        if (startByte != other.startByte) {
            return false;
        }
        if (Double.doubleToLongBits(stepSize) != Double
                .doubleToLongBits(other.stepSize)) {
            return false;
        }
        return true;
    }

}
