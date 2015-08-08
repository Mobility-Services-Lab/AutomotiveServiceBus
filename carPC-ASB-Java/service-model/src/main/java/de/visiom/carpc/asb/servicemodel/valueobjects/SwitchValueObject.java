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
package de.visiom.carpc.asb.servicemodel.valueobjects;

import java.text.NumberFormat;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchValueObject implements ValueObject {

    private static final Logger LOG = LoggerFactory
            .getLogger(SwitchValueObject.class);

    private final Switch value;

    public SwitchValueObject(Switch value) {
        this.value = value;
    }

    public Switch getValue() {
        return value;
    }

    public static SwitchValueObject valueOf(Number n) {
        if (n.intValue() == 0) {
            return new SwitchValueObject(Switch.OFF);
        }
        return new SwitchValueObject(Switch.ON);
    }

    public static SwitchValueObject valueOf(Boolean b) {
        if (b) {
            return new SwitchValueObject(Switch.ON);
        } else {
            return new SwitchValueObject(Switch.OFF);
        }
    }

    public static SwitchValueObject valueOf(String s) {
        if (s.equals("ON")) {
            return new SwitchValueObject(Switch.ON);
        } else if (s.equals("OFF")) {
            return new SwitchValueObject(Switch.OFF);
        } else {
            try {
                Number n = NumberFormat.getInstance().parse(s);
                return SwitchValueObject.valueOf(n);
            } catch (NumberFormatException nfe) {
                return null;
            } catch (ParseException e) {
                return null;
            }
        }
    }

    public enum Switch {
        ON, OFF;
    }

    @Override
    public String toString() {
        if (value.equals(Switch.ON)) {
            return "ON";
        }
        return "OFF";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        if (!(obj instanceof SwitchValueObject)) {
            return false;
        }
        SwitchValueObject other = (SwitchValueObject) obj;
        if (value != other.value) {
            return false;
        }
        return true;
    }

}
