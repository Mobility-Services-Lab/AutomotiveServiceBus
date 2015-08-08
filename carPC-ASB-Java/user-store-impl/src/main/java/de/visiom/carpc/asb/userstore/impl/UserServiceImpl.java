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
package de.visiom.carpc.asb.userstore.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.visiom.carpc.asb.messagebus.EventPublisher;
import de.visiom.carpc.asb.messagebus.events.ValueChangeEvent;
import de.visiom.carpc.asb.servicemodel.Service;
import de.visiom.carpc.asb.servicemodel.exceptions.NoSuchParameterException;
import de.visiom.carpc.asb.servicemodel.parameters.Parameter;
import de.visiom.carpc.asb.servicemodel.valueobjects.StringValueObject;
import de.visiom.carpc.asb.serviceregistry.ServiceRegistry;
import de.visiom.carpc.asb.serviceregistry.exceptions.NoSuchServiceException;
import de.visiom.carpc.asb.userstore.UserService;
import de.visiom.carpc.asb.userstore.entities.User;
import de.visiom.carpc.asb.userstore.exceptions.BadCredentialsException;
import de.visiom.carpc.asb.userstore.exceptions.NoSuchUserException;
import de.visiom.carpc.asb.userstore.impl.entities.UserImpl;

public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory
            .getLogger(UserServiceImpl.class);

    private EventPublisher eventPublisher;

    private EntityManagerFactory entityManagerFactory;

    public void setEntityManagerFactory(
            EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    private ServiceRegistry serviceRegistry;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    private User currentUser;

    public void init() throws NoSuchUserException {
        try {
            currentUser = getUser("default");
        } catch (NoSuchUserException nsue) {
            createUser("default");
            currentUser = getUser("default");
        }
    }

    @Override
    public List<User> getUsers() {
        EntityManager entityManager = getEntityManager();
        Query query = entityManager.createQuery("select u from User u");
        return query.getResultList();
    }

    @Override
    public User getUser(String name) throws NoSuchUserException {
        EntityManager entityManager = getEntityManager();
        Query query = entityManager
                .createQuery("select u from User u where u.userName = :userName");
        query.setParameter("userName", name);
        try {
            User user = (User) query.getSingleResult();
            return user;
        } catch (NoResultException nre) {
            throw new NoSuchUserException(name);
        }
    }

    @Override
    public void createUser(String name) {
        EntityManager entityManager = getEntityManager();
        entityManager.getTransaction().begin();
        User user = new UserImpl(name);
        entityManager.persist(user);
        entityManager.getTransaction().commit();
    }

    @Override
    public void saveUser(User user) {
        EntityManager entityManager = getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(user);
        entityManager.getTransaction().commit();
    }

    @Override
    public void deleteUser(User user) {
        EntityManager entityManager = getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.remove(user);
        entityManager.getTransaction().commit();
    }

    private EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void login(String userName, String password)
            throws BadCredentialsException, NoSuchUserException {
        try {
            User user = getUser(userName);
            if (user.getPassword().equals(password)) {
                currentUser = user;
                Service service = serviceRegistry.getService("UserStore");
                Parameter parameter = service.getParameter("currentUser");
                eventPublisher.publishValueChange(ValueChangeEvent
                        .createValueChangeEvent(parameter,
                                StringValueObject.valueOf(user.getUserName())));
            } else {
                LOG.error("Tried to login as {} but the password was wrong!");
                throw new BadCredentialsException(userName);
            }
        } catch (NoSuchUserException e) {
            LOG.error(
                    "Tried to login as {} but no user was found with this name!",
                    userName);
            throw e;
        } catch (NoSuchServiceException e) {
            LOG.error("An exception has occured:", e);
        } catch (NoSuchParameterException e) {
            LOG.error("An exception has occured:", e);
        }
    }
}
