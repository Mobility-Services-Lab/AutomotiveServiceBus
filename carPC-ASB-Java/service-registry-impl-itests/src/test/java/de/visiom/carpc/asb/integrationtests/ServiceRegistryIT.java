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
package de.visiom.carpc.asb.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import de.visiom.carpc.asb.servicemodel.Service;
import de.visiom.carpc.asb.servicemodel.parameters.Parameter;
import de.visiom.carpc.asb.serviceregistry.ServiceRegistry;
import de.visiom.carpc.asb.serviceregistry.exceptions.NoSuchServiceException;

@RunWith(PaxExam.class)
public class ServiceRegistryIT {

    @Inject
    protected ServiceRegistry serviceRegistry;

    @Configuration
    public static Option[] configure() throws Exception {
        return OptionConfiguration.getOptions();
    }

    private static final String TEST_SERVICE_NAME = "testService";

    private static final String TEST_PARAMETER_NAME = "testParameter";

    @Mock
    private Service testService;

    @Mock
    private Parameter testParameter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(testParameter.getName()).thenReturn(TEST_PARAMETER_NAME);
        when(testService.getParameters()).thenReturn(
                Arrays.asList(new Parameter[] { testParameter }));
        when(testService.getName()).thenReturn(TEST_SERVICE_NAME);
    }

    @Test
    public void testGetServices() {
        assertEquals(0, serviceRegistry.getServices().size());
    }

    @Test(expected = NoSuchServiceException.class)
    public void canNotFindNonExistingService() throws NoSuchServiceException {
        serviceRegistry.getService(TEST_SERVICE_NAME);
    }

    // @Test
    // public void canRegisterService() throws NoSuchServiceException,
    // NoSuchParameterException {
    // serviceRegistry.registerService(testService);
    // assertEquals(1, serviceRegistry.getServices().size());
    // Service returnedService = serviceRegistry.getService(TEST_SERVICE_NAME);
    // assertEquals(returnedService, testService);
    // assertEquals(returnedService.getParameters().size(), 1);
    // Parameter returnedParameter = returnedService.getParameters().get(0);
    // assertNotNull(returnedParameter);
    // assertEquals(returnedParameter.getName(), TEST_PARAMETER_NAME);
    // }
    //
    // @Test(expected = NoSuchServiceException.class)
    // public void canUnregisterService() throws NoSuchServiceException {
    // serviceRegistry.registerService(testService);
    // serviceRegistry.unregisterService(testService);
    // serviceRegistry.getService(TEST_SERVICE_NAME);
    // assertEquals(serviceRegistry.getServices().size(), 1);
    // }
}