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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.visiom.carpc.asb.rest.impl.filters.CORSFilter;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		context.registerService(CORSFilter.class.getName(), new CORSFilter(),
				null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub

	}

}
