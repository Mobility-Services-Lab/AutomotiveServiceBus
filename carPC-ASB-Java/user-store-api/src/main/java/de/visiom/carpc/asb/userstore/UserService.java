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
package de.visiom.carpc.asb.userstore;

import java.util.List;

import de.visiom.carpc.asb.userstore.entities.User;
import de.visiom.carpc.asb.userstore.exceptions.BadCredentialsException;
import de.visiom.carpc.asb.userstore.exceptions.NoSuchUserException;

/**
 * Interface for performing basic CRUD operations on the user store. Provides a
 * method for performing a login of a specific user.
 * 
 * @author david
 * 
 */
public interface UserService {

    /**
     * Tries to perform a login of the given user, i.e. puts a notification on
     * the message bus which indicates that a new user has logged in. It is then
     * the task of the services to react to this notifiction, e.g. by loading
     * the preferences which were associated with the new user.
     * 
     * @param userName
     *            Name of the user
     * @param password
     *            Unencoded password of the user
     * @throws BadCredentialsException
     *             If the given credentials doesn't match the users credentials
     * @throws NoSuchUserException
     *             If the requested user does not exist
     */
    void login(String userName, String password)
            throws BadCredentialsException, NoSuchUserException;

    /**
     * Returns the user who is currently logged in
     * 
     * @return The user
     */
    User getCurrentUser();

    /**
     * Returns a list of all the available users on the system.
     * 
     * @return The user list
     */
    List<User> getUsers();

    /**
     * Return a specific user.
     * 
     * @param name
     *            The name of the requested user
     * @return The requested user
     * @throws NoSuchUserException
     *             If the requested user does not exist
     */
    User getUser(String name) throws NoSuchUserException;

    /**
     * Creates a new user.
     * 
     * @param name
     *            The name of the user who should be created
     */
    void createUser(String name);

    /**
     * Merges the changes of the given user object into the object in the user
     * store.
     * 
     * @param user
     *            User who should be merged.
     */
    void saveUser(User user);

    void deleteUser(User user);

}
