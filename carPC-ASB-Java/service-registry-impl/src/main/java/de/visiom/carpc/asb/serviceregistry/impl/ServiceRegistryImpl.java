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
package de.visiom.carpc.asb.serviceregistry.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.visiom.carpc.asb.servicemodel.Service;
import de.visiom.carpc.asb.servicemodel.ServiceRepository;
import de.visiom.carpc.asb.servicemodel.parameters.Parameter;
import de.visiom.carpc.asb.serviceregistry.ServiceRegistry;
import de.visiom.carpc.asb.serviceregistry.ServiceRegistryListener;
import de.visiom.carpc.asb.serviceregistry.exceptions.NoSuchServiceException;
import de.visiom.carpc.asb.serviceregistry.impl.entities.NumericParameterImpl;
import de.visiom.carpc.asb.serviceregistry.impl.entities.ParameterImpl;
import de.visiom.carpc.asb.serviceregistry.impl.entities.ParameterReference;
import de.visiom.carpc.asb.serviceregistry.impl.entities.ServiceImpl;
import de.visiom.carpc.asb.serviceregistry.impl.entities.ServiceImpl.ServiceList;
import de.visiom.carpc.asb.serviceregistry.impl.entities.SetParameterImpl;
import de.visiom.carpc.asb.serviceregistry.impl.entities.StateParameterImpl;
import de.visiom.carpc.asb.serviceregistry.impl.entities.StringParameterImpl;
import de.visiom.carpc.asb.serviceregistry.impl.entities.SwitchParameterImpl;

public class ServiceRegistryImpl implements ServiceRegistry {

    private final static Logger LOG = LoggerFactory
            .getLogger(ServiceRegistryImpl.class);

    private static final String EMBEDDED_SERVICES_DIRECTORY = "./services";

    private final Map<String, Service> serviceNames = new HashMap<String, Service>();

    private final Map<Bundle, Service> bundleServices = new HashMap<Bundle, Service>();

    private final List<ServiceRepository> serviceRepositories = new LinkedList<ServiceRepository>();

    private final List<ServiceRegistryListener> serviceRegistryListeners = new LinkedList<ServiceRegistryListener>();

    private Unmarshaller serviceUnmarshaller;

    private void loadEmbeddedServices() {
        File serviceDirectory = new File(EMBEDDED_SERVICES_DIRECTORY);
        if (serviceDirectory.exists()) {
            for (File f : serviceDirectory.listFiles()) {
                try {
                    Service loadedService = loadService(f.toURI().toURL());
                    serviceNames.put(loadedService.getName(), loadedService);
                } catch (MalformedURLException e) {
                    LOG.error(
                            "An exception occured while reading the embedded services:",
                            e);
                    ;
                } catch (JAXBException e) {
                    LOG.error(
                            "An exception occured while reading the embedded services:",
                            e);
                }
            }
        }
    }

    public void init() {
        loadEmbeddedServices();
    }

    public void bindServiceRegistryListener(
            ServiceRegistryListener serviceRegistryListener) {
        serviceRegistryListeners.add(serviceRegistryListener);
    }

    public void unbindServiceRegistryListener(
            ServiceRegistryListener serviceRegistryListener) {
        serviceRegistryListeners.remove(serviceRegistryListener);
    }

    public void bindServiceRepository(
            ServiceReference<ServiceRepository> serviceReference) {
        Bundle bundle = serviceReference.getBundle();
        URL serviceURL = bundle.getEntry("/OSGI-INF/service.xml");
        ServiceRepository serviceRepository = bundle.getBundleContext()
                .getService(serviceReference);
        serviceRepositories.add(serviceRepository);
        try {
            Service loadedService = loadService(serviceURL);
            serviceNames.put(loadedService.getName(), loadedService);
            bundleServices.put(bundle, loadedService);
            for (ServiceRegistryListener serviceRegistryListener : serviceRegistryListeners) {
                serviceRegistryListener.addedService(loadedService, bundle);
            }
            LOG.info("Bound service {} with {} parameters", loadedService
                    .getName(), loadedService.getParameters().size());
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public void unbindServiceRepository(
            ServiceReference<ServiceRepository> serviceReference) {
        if (serviceReference != null) {
            Bundle bundle = serviceReference.getBundle();
            ServiceRepository serviceRepository = bundle.getBundleContext()
                    .getService(serviceReference);
            serviceRepositories.remove(serviceRepository);
            Service service = bundleServices.get(bundle);
            bundleServices.remove(bundle);
            serviceNames.remove(service.getName());
            for (ServiceRegistryListener serviceRegistryListener : serviceRegistryListeners) {
                serviceRegistryListener.removedService(service, bundle);
            }
        }
    }

    public ServiceRegistryImpl() {
        initializeMarshaler();
    }

    private void initializeMarshaler() {
        try {
            JAXBContext serviceContext = JAXBContext.newInstance(
                    ServiceImpl.class, ServiceList.class, ParameterImpl.class,
                    ParameterReference.class, NumericParameterImpl.class,
                    StateParameterImpl.class, SwitchParameterImpl.class,
                    StringParameterImpl.class, SetParameterImpl.class);
            serviceUnmarshaller = serviceContext.createUnmarshaller();
        } catch (JAXBException e) {
            LOG.error(
                    "An exeption occured while constructing the service loader: {}",
                    e);
        }
    }

    @Override
    public List<Service> getServices() {
        return Arrays.asList(serviceNames.values().toArray(
                new Service[serviceNames.values().size()]));
    }

    @Override
    public Service getService(String name) throws NoSuchServiceException {
        Service service = serviceNames.get(name);
        if (service == null) {
            throw new NoSuchServiceException(name);
        }
        return service;
    }

    private Service loadService(URL xmlFile) throws JAXBException {
        ServiceImpl service = (ServiceImpl) serviceUnmarshaller
                .unmarshal(xmlFile);
        LOG.info("Loading service {} ({})", service.getName(),
                service.getDescription());
        LOG.info("Found {} parameters...", service.getParameters().size());
        Map<String, Parameter> nameParameters = new HashMap<String, Parameter>();
        for (ParameterImpl parameter : (List<ParameterImpl>) (List<?>) service
                .getParameters()) {
            LOG.info("Adding parameter {}...", parameter.getName());
            parameter.setService(service);
            nameParameters.put(parameter.getName(), parameter);
        }
        List<Parameter> preferences = new LinkedList<Parameter>();
        LOG.info("Found {} preferences...", service.getPreferences().size());
        for (Parameter parameter : service.getPreferences()) {
            ParameterImpl newPreference = null;
            if (parameter instanceof ParameterReference) {
                String refParameterName = ((ParameterReference) parameter)
                        .getName();
                newPreference = (ParameterImpl) nameParameters
                        .get(refParameterName);
            } else {
                newPreference = (ParameterImpl) parameter;
            }
            newPreference.setService(service);
            preferences.add(newPreference);
        }
        service.setPreferences(preferences);
        return service;
    }

    public static void main(String[] args) throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ServiceImpl.class,
                ServiceList.class, ParameterImpl.class,
                ParameterReference.class, NumericParameterImpl.class,
                StateParameterImpl.class, SwitchParameterImpl.class,
                StringParameterImpl.class);
        SchemaOutputResolver sor = new MySchemaOutputResolver();
        jaxbContext.generateSchema(sor);
    }

    static class MySchemaOutputResolver extends SchemaOutputResolver {

        @Override
        public Result createOutput(String namespaceURI, String suggestedFileName)
                throws IOException {
            File file = new File("/home/david/service.xsd");
            StreamResult result = new StreamResult(file);
            result.setSystemId(file.toURI().toURL().toString());
            return result;
        }
    }
}
