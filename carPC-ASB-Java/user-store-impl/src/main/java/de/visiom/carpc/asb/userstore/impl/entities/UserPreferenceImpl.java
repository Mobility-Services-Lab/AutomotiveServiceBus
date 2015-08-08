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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.visiom.carpc.asb.servicemodel.exceptions.IncompatibleValueException;
import de.visiom.carpc.asb.servicemodel.valueobjects.ValueObject;
import de.visiom.carpc.asb.userstore.entities.Preference;
import de.visiom.carpc.asb.userstore.entities.User;
import de.visiom.carpc.asb.userstore.entities.UserPreference;

@Entity(name = "UserPreference")
@Access(AccessType.FIELD)
public class UserPreferenceImpl implements UserPreference {

    private static final Logger LOG = LoggerFactory
            .getLogger(UserPreferenceImpl.class);

    @ManyToOne(optional = false, targetEntity = PreferenceImpl.class)
    private Preference preference;

    @ManyToOne(optional = false, targetEntity = UserImpl.class)
    private User user;

    private String stringValue;

    @Transient
    private ValueObject valueObject;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Preference getPreference() {
        return preference;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
    }

    @Override
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    @Transient
    public ValueObject getValueObject() {
        if (valueObject == null) {
            try {
                valueObject = preference.getParameter().createValueObject(
                        stringValue);
            } catch (IncompatibleValueException e) {
                LOG.error("Could not convert {} to a ValueObject!", e);
            }
        }
        return valueObject;
    }

    @Override
    public String toString() {
        return stringValue;
    }

}
