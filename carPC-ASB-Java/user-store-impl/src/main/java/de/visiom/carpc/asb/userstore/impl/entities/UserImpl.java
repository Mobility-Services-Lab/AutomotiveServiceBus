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

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import de.visiom.carpc.asb.userstore.entities.User;
import de.visiom.carpc.asb.userstore.entities.UserPreference;

@Entity(name = "User")
@Access(AccessType.FIELD)
public class UserImpl implements User {

    @Id
    private String userName;

    private String password;

    private String salt;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = UserPreferenceImpl.class)
    private List<UserPreference> userPreferences;

    public UserImpl() {
    }

    public UserImpl(String userName) {
        this.userName = userName;
    }

    public List<UserPreference> getUserPreferences() {
        return userPreferences;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        return userName;
    }

    @Override
    @Transient
    public String getName() {
        return userName;
    }

}
