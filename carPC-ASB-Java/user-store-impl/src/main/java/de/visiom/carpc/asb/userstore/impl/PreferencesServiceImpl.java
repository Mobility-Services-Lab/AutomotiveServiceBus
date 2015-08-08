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

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.visiom.carpc.asb.messagebus.EventPublisher;
import de.visiom.carpc.asb.servicemodel.Service;
import de.visiom.carpc.asb.servicemodel.parameters.Parameter;
import de.visiom.carpc.asb.servicemodel.valueobjects.ValueObject;
import de.visiom.carpc.asb.serviceregistry.ServiceRegistry;
import de.visiom.carpc.asb.userstore.PreferencesService;
import de.visiom.carpc.asb.userstore.entities.Preference;
import de.visiom.carpc.asb.userstore.entities.User;
import de.visiom.carpc.asb.userstore.entities.UserPreference;
import de.visiom.carpc.asb.userstore.exceptions.NoSuchPreferenceException;
import de.visiom.carpc.asb.userstore.exceptions.NoSuchUserPreferenceException;
import de.visiom.carpc.asb.userstore.impl.entities.PreferenceImpl;
import de.visiom.carpc.asb.userstore.impl.entities.PreferenceImplId;
import de.visiom.carpc.asb.userstore.impl.entities.UserPreferenceImpl;

public class PreferencesServiceImpl implements PreferencesService {

    private static final Logger LOG = LoggerFactory
            .getLogger(PreferencesServiceImpl.class);

    private static final String NO_PREFERENCE_FOUND_LOG_MESSAGE = "Could not find an internal parameter for the preference for {}/{}. "
            + "This probably means that the requested preference was not defined in the service file.";

    private EntityManagerFactory entityManagerFactory;

    private ServiceRegistry serviceRegistry;

    private EventPublisher eventPublisher;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setEntityManagerFactory(
            EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Preference getPreference(String serviceName, String preferenceName)
            throws NoSuchPreferenceException {
        Parameter parameter = getInternalPreferenceParameter(serviceName,
                preferenceName);
        if (parameter == null) {
            throw new NoSuchPreferenceException(serviceName, preferenceName);
        }
        EntityManager entityManager = getEntityManager();
        Query query = entityManager
                .createQuery("select p from Preference p where p.id.serviceName = :serviceName and p.id.parameterName = :parameterName");
        query.setParameter("serviceName", serviceName).setParameter(
                "parameterName", preferenceName);
        PreferenceImpl result = null;
        List<PreferenceImpl> resultList = query.getResultList();
        if (resultList.size() == 0) {
            LOG.info("Preference {}/{} was not found, creating an entry...",
                    serviceName, preferenceName);
            PreferenceImpl preference = new PreferenceImpl();
            PreferenceImplId id = new PreferenceImplId(serviceName,
                    preferenceName);
            preference.setId(id);
            preference.setParameter(parameter);
            entityManager.getTransaction().begin();
            entityManager.persist(preference);
            entityManager.getTransaction().commit();
            result = preference;
        } else {
            result = resultList.get(0);
        }
        result.setParameter(parameter);
        return result;
    }

    @Override
    public List<UserPreference> getUserPreferences(User user) {
        EntityManager entityManager = getEntityManager();
        Query query = entityManager
                .createQuery("select up from UserPreference up where up.user = :user");
        query.setParameter("user", user);
        List<UserPreference> userPreferences = query.getResultList();
        for (UserPreference up : userPreferences) {
            PreferenceImpl preference = (PreferenceImpl) up.getPreference();
            String serviceName = preference.getId().getServiceName();
            String parameterName = preference.getId().getParameterName();
            Parameter parameter = getInternalPreferenceParameter(serviceName,
                    parameterName);
            preference.setParameter(parameter);
        }
        return userPreferences;
    }

    @Override
    public void savePreference(Preference preference, User user,
            ValueObject valueType) {
        EntityManager entityManager = getEntityManager();
        Preference persistedPreference = entityManager.find(
                PreferenceImpl.class, ((PreferenceImpl) preference).getId());
        // for whatever reason, using user.getUserName() as an ID in find()
        // would result in a NumberFormatException here
        Query query = entityManager
                .createQuery("select u from User u where u.userName = :userName");
        query.setParameter("userName", user.getUserName());
        User persistedUser = (User) query.getSingleResult();
        UserPreferenceImpl userPreference = null;
        Query existingPreferenceQuery = entityManager
                .createQuery("select up from UserPreference up where up.user = :user and up.preference = :preference");
        existingPreferenceQuery.setParameter("user", persistedUser)
                .setParameter("preference", persistedPreference);
        List<UserPreferenceImpl> resultList = existingPreferenceQuery
                .getResultList();
        boolean isNewValue = false;
        boolean alreadyExists = !resultList.isEmpty();
        if (alreadyExists) {
            userPreference = resultList.get(0);
            isNewValue = !userPreference.getStringValue().equals(
                    valueType.toString());
        } else {
            isNewValue = true;
            userPreference = new UserPreferenceImpl();
            userPreference.setPreference(persistedPreference);
            userPreference.setUser(persistedUser);
        }
        userPreference.setStringValue((valueType.toString()));
        entityManager.getTransaction().begin();
        if (alreadyExists) {
            entityManager.merge(userPreference);
        } else {
            entityManager.persist(userPreference);
        }
        entityManager.getTransaction().commit();
    }

    private EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    private Parameter getInternalPreferenceParameter(UserPreference up) {
        PreferenceImpl preference = (PreferenceImpl) up.getPreference();
        String serviceName = preference.getServiceName();
        String parameterName = preference.getId().getParameterName();
        return getInternalPreferenceParameter(serviceName, parameterName);
    }

    private Parameter getInternalPreferenceParameter(String serviceName,
            String preferenceName) {
        for (Service service : serviceRegistry.getServices()) {
            if (service.getName().equals(serviceName)) {
                for (Parameter parameter : service.getPreferences()) {
                    if (parameter.getName().equals(preferenceName)) {
                        return parameter;
                    }
                }
            }
        }
        LOG.info(NO_PREFERENCE_FOUND_LOG_MESSAGE, serviceName, preferenceName);
        return null;
    }

    @Override
    public UserPreference getUserPreference(User user, Preference preference)
            throws NoSuchUserPreferenceException {
        EntityManager entityManager = getEntityManager();
        Query query = entityManager
                .createQuery("select up from UserPreference up where up.user = :user and up.preference = :preference");
        query.setParameter("user", user).setParameter("preference", preference);
        try {
            UserPreference up = (UserPreference) query.getSingleResult();
            ((PreferenceImpl) up.getPreference())
                    .setParameter(getInternalPreferenceParameter(up));
            return up;
        } catch (NoResultException nre) {
            throw new NoSuchUserPreferenceException(user, preference);
        }
    }

    @Override
    public List<Preference> getPreferences() {
        List<Preference> preferences = new LinkedList<Preference>();
        for (Service service : serviceRegistry.getServices()) {
            for (Parameter parameter : service.getPreferences()) {
                PreferenceImpl newPreference = new PreferenceImpl();
                newPreference.setParameter(parameter);
                newPreference.setId(new PreferenceImplId(service.getName(),
                        parameter.getName()));
                preferences.add(newPreference);
            }
        }
        return preferences;
    }

    @Override
    public List<Preference> getPreferences(String serviceName) {
        List<Preference> preferences = new LinkedList<Preference>();
        for (Service service : serviceRegistry.getServices()) {
            if (service.getName().equals(service.getName())) {
                for (Parameter p : service.getPreferences()) {
                    PreferenceImpl newPreference = new PreferenceImpl();
                    newPreference.setParameter(p);
                    newPreference.setId(new PreferenceImplId(service.getName(),
                            p.getName()));
                    preferences.add(newPreference);
                }
            }
        }
        return preferences;
    }
}
