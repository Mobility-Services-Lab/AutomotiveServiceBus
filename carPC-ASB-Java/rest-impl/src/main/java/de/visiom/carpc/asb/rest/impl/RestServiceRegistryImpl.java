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
package de.visiom.carpc.asb.rest.impl;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.visiom.carpc.asb.rest.RestServiceRegistry;
import de.visiom.carpc.asb.rest.exceptions.NoSuchServiceException;
import de.visiom.carpc.asb.rest.impl.entities.RestNumericParameterImpl;
import de.visiom.carpc.asb.rest.impl.entities.RestParameterImpl;
import de.visiom.carpc.asb.rest.impl.entities.RestServiceImpl;
import de.visiom.carpc.asb.rest.impl.entities.RestSetParameterImpl;
import de.visiom.carpc.asb.rest.impl.entities.RestStateParameterImpl;
import de.visiom.carpc.asb.rest.impl.entities.RestStringParameterImpl;
import de.visiom.carpc.asb.rest.impl.entities.RestSwitchParameterImpl;
import de.visiom.carpc.asb.rest.impl.entities.adapters.ParameterAdapter;
import de.visiom.carpc.asb.rest.impl.factories.LinkMapFactory;
import de.visiom.carpc.asb.rest.parameters.RestParameter;
import de.visiom.carpc.asb.rest.parameters.RestService;
import de.visiom.carpc.asb.servicemodel.Service;
import de.visiom.carpc.asb.serviceregistry.ServiceRegistryListener;

public class RestServiceRegistryImpl implements RestServiceRegistry,
        ServiceRegistryListener {

    private static final Logger LOG = LoggerFactory
            .getLogger(RestServiceRegistryImpl.class);

    private final Map<String, RestService> serviceNames = new HashMap<String, RestService>();

    private final Map<Bundle, List<RestService>> bundleServices = new HashMap<Bundle, List<RestService>>();

    private Unmarshaller constructUnmarshaler(Service service) {
        try {
            JAXBContext restServiceContext = JAXBContext
                    .newInstance(RestServiceImpl.class,
                            RestParameterImpl.class,
                            RestNumericParameterImpl.class,
                            RestSwitchParameterImpl.class,
                            RestStateParameterImpl.class,
                            RestStringParameterImpl.class,
                            RestSetParameterImpl.class);
            Unmarshaller unmarshaller = restServiceContext.createUnmarshaller();
            unmarshaller.setAdapter(new ParameterAdapter(service));
            return unmarshaller;
        } catch (JAXBException e) {
            LOG.error(
                    "An exeption occured while constructing the service loader: {}",
                    e);
            return null;
        }
    }

    @Override
    public List<RestService> getRestServices() {
        List<RestService> result = Arrays.asList(serviceNames.values().toArray(
                new RestService[serviceNames.values().size()]));
        Collections.sort(result, new RestServiceComparator());
        return result;
    }

    class RestServiceComparator implements Comparator<RestService> {

        @Override
        public int compare(RestService o1, RestService o2) {
            return o1.getRanking() - o2.getRanking();
        }

    }

    @Override
    public void registerRestService(RestService restService) {
        LOG.info("added service: {}", restService.getName());
        for (RestParameter restParameter : restService.getParameters()) {
            restParameter.setLinks(LinkMapFactory.createParametersLinkMap(
                    restService.getPath(), restParameter.getPath()));
        }
        serviceNames.put(restService.getPath(), restService);
    }

    @Override
    public void unregisterRestService(RestService restService) {
        serviceNames.remove(restService.getPath());
    }

    @Override
    public RestService getService(String serviceName)
            throws NoSuchServiceException {
        RestService result = serviceNames.get(serviceName);
        if (result == null) {
            throw new NoSuchServiceException();
        }
        return result;
    }

    private List<RestService> loadRestServices(Service service, Bundle bundle) {
        List<RestService> restServices = new LinkedList<RestService>();
        Enumeration<String> restFiles = bundle.getEntryPaths("/OSGI-INF/rest");
        if (restFiles != null) {
            Unmarshaller unmarshaller = constructUnmarshaler(service);
            while (restFiles.hasMoreElements()) {
                URL currentRestFile = bundle.getEntry(restFiles.nextElement());
                LOG.info("Reading REST service from {}", currentRestFile);
                try {
                    RestService newRestService = loadRestService(unmarshaller,
                            currentRestFile);
                    restServices.add(newRestService);
                } catch (JAXBException e) {
                    LOG.error("Rest file {} could not be loaded:",
                            currentRestFile, e);
                }
            }
        } else {
            LOG.info("Bundle {} does not define any rest services!",
                    bundle.getSymbolicName());
        }
        return restServices;
    }

    private RestService loadRestService(Unmarshaller unmarshaller, URL url)
            throws JAXBException {
        RestServiceImpl restService = (RestServiceImpl) unmarshaller
                .unmarshal(url);
        LOG.info("Found {} parameters for REST service {}...", restService
                .getParameters().size(), restService.getName());
        for (RestParameterImpl restParameter : (List<RestParameterImpl>) (List<?>) restService
                .getParameters()) {
            LOG.info("Adding parameter {}...", restParameter.getName());
            restParameter.initialize();
            restParameter.setRestService(restService);
        }
        return restService;
    }

    @Override
    public void addedService(Service service, Bundle bundle) {
        List<RestService> restServices = loadRestServices(service, bundle);
        for (RestService restService : restServices) {
            registerRestService(restService);
        }
        bundleServices.put(bundle, restServices);
    }

    @Override
    public void removedService(Service service, Bundle bundle) {
        List<RestService> restServices = bundleServices.get(bundle);
        for (RestService restService : restServices) {
            unregisterRestService(restService);
        }
        bundleServices.remove(bundle);
    }
}
