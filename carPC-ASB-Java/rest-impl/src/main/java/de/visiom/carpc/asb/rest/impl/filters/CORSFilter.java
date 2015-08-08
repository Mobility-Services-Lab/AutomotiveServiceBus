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
package de.visiom.carpc.asb.rest.impl.filters;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class CORSFilter implements ContainerResponseFilter {

	@Override
	public void filter(final ContainerRequestContext requestContext,
			final ContainerResponseContext cres) throws IOException {
		cres.getHeaders().add("Access-Control-Allow-Origin", "*");
		cres.getHeaders().add("Access-Control-Allow-Headers",
				"origin, content-type, accept, authorization");
		cres.getHeaders().add("Access-Control-Allow-Credentials", "true");
		cres.getHeaders().add("Access-Control-Allow-Methods",
				"GET, POST, PUT, DELETE, OPTIONS, HEAD");
		cres.getHeaders().add("Access-Control-Max-Age", "1209600");
	}

}