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
package de.visiom.carpc.asb.rest;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.SseFeature;

@Path("/")
public interface ServiceEndpoint {

    static final String JSON_UTF8 = "application/json; charset=UTF-8";

    @GET
    @Produces(JSON_UTF8)
    Response getServices();

    @GET
    @Produces(JSON_UTF8)
    @Path("/{service}")
    Response getService(@PathParam("service") String serviceName);

    @GET
    @Produces(JSON_UTF8)
    @Path("/{service}/parameters")
    Response getParameters(@PathParam("service") String serviceName);

    @GET
    @Produces(SseFeature.SERVER_SENT_EVENTS + "; charset=UTF-8")
    @Path("/{service}/parameters/{name}/subscription")
    EventOutput subscribeToParameterValueChange(
            @PathParam("service") String serviceName,
            @PathParam("name") String parameterName);

    @GET
    @Path("/{service}/parameters/{name}")
    @Produces(JSON_UTF8)
    Response getParameterValue(@PathParam("service") String serviceName,
            @PathParam("name") String parameterName);

    @PUT
    @Path("/{service}/parameters/{name}")
    void setParameterValue(String value,
            @Suspended final AsyncResponse asyncResponse,
            @PathParam("service") String serviceName,
            @PathParam("name") String parameterName);

    @GET
    @Produces(JSON_UTF8)
    @Path("/{service}/preferences/{name}")
    Response getPreferenceValue(@Context SecurityContext securityContext,
            @PathParam("service") String serviceName,
            @PathParam("name") String parameterName);

    @PUT
    @Path("/{service}/preferences/{name}")
    Response setPreferenceValue(String value,
            @Context SecurityContext securityContext,
            @PathParam("service") String serviceName,
            @PathParam("name") String parameterName);

    @GET
    @Path("/storedPreferences")
    Response loadPreferences(@Context SecurityContext securityContext);
}