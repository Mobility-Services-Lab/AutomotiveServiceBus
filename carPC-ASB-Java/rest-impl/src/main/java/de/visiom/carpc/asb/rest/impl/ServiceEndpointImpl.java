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

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.jersey.media.sse.EventOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.visiom.carpc.asb.messagebus.CommandPublisher;
import de.visiom.carpc.asb.messagebus.commands.ValueChangeRequest;
import de.visiom.carpc.asb.parametervalueregistry.ParameterValueRegistry;
import de.visiom.carpc.asb.parametervalueregistry.exceptions.UninitalizedValueException;
import de.visiom.carpc.asb.rest.RestServiceRegistry;
import de.visiom.carpc.asb.rest.ServiceEndpoint;
import de.visiom.carpc.asb.rest.entities.RestParameters;
import de.visiom.carpc.asb.rest.entities.RestServices;
import de.visiom.carpc.asb.rest.exceptions.NoSuchServiceException;
import de.visiom.carpc.asb.rest.impl.async.ResponseCallback;
import de.visiom.carpc.asb.rest.impl.handlers.CommandResponseHandler;
import de.visiom.carpc.asb.rest.impl.handlers.ValueUpdateHandler;
import de.visiom.carpc.asb.rest.impl.sse.EventOutputWrapper;
import de.visiom.carpc.asb.rest.parameters.RestParameter;
import de.visiom.carpc.asb.rest.parameters.RestService;
import de.visiom.carpc.asb.servicemodel.exceptions.IncompatibleValueException;
import de.visiom.carpc.asb.servicemodel.exceptions.NoSuchParameterException;
import de.visiom.carpc.asb.servicemodel.parameters.Parameter;
import de.visiom.carpc.asb.servicemodel.valueobjects.ValueObject;
import de.visiom.carpc.asb.userstore.PreferencesService;
import de.visiom.carpc.asb.userstore.UserService;
import de.visiom.carpc.asb.userstore.entities.Preference;
import de.visiom.carpc.asb.userstore.entities.User;
import de.visiom.carpc.asb.userstore.entities.UserPreference;
import de.visiom.carpc.asb.userstore.exceptions.BadCredentialsException;
import de.visiom.carpc.asb.userstore.exceptions.NoSuchPreferenceException;
import de.visiom.carpc.asb.userstore.exceptions.NoSuchUserException;
import de.visiom.carpc.asb.userstore.exceptions.NoSuchUserPreferenceException;

public class ServiceEndpointImpl implements ServiceEndpoint {

	private static final Logger LOG = LoggerFactory
			.getLogger(ServiceEndpointImpl.class);

	private ValueUpdateHandler valueUpdateHandler;

	private ParameterValueRegistry parameterValueRegistry;

	private RestServiceRegistry restServiceRegistry;

	private CommandPublisher commandPublisher;

	private CommandResponseHandler commandResponseHandler;

	private UserService userService;

	private PreferencesService preferencesService;

	public void setPreferencesService(PreferencesService preferencesService) {
		this.preferencesService = preferencesService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setRestServiceRegistry(RestServiceRegistry restServiceRegistry) {
		this.restServiceRegistry = restServiceRegistry;
	}

	public void setCommandPublisher(CommandPublisher commandPublisher) {
		this.commandPublisher = commandPublisher;
	}

	public void setCommandResponseHandler(
			CommandResponseHandler genericResponseHandler) {
		this.commandResponseHandler = genericResponseHandler;
	}

	public void setParameterValueRegistry(
			ParameterValueRegistry parameterValueRegistry) {
		this.parameterValueRegistry = parameterValueRegistry;
	}

	public void setValueUpdateHandler(ValueUpdateHandler valueUpdateHandler) {
		this.valueUpdateHandler = valueUpdateHandler;
	}

	@Override
	public Response getServices() {
		List<RestService> restServices = restServiceRegistry.getRestServices();
		RestServices services = new RestServices();
		services.setServices(restServices);
		return Response.ok().entity(services).build();
	}

	@Override
	public EventOutput subscribeToParameterValueChange(String serviceName,
			String parameterName) {
		try {
			RestService restService = restServiceRegistry
					.getService(serviceName);
			RestParameter restParameter = restService
					.getParameter(parameterName);
			EventOutput eventOutput = new EventOutput();
			Observable obs = valueUpdateHandler.getObservable(restParameter
					.getSubscriptionParameter());
			EventOutputWrapper eventOutputWrapper = new EventOutputWrapper(obs,
					eventOutput);
			return eventOutputWrapper.getEventOutput();
		} catch (NoSuchParameterException nspe) {
			throw new NotFoundException("Parameter " + serviceName + "/"
					+ parameterName + " does not exist!");
		} catch (NoSuchServiceException e) {
			throw new NotFoundException("Service " + serviceName
					+ " does not exist!");
		}
	}

	@Override
	public Response getParameters(@PathParam("service") String serviceName) {
		try {
			RestService service = restServiceRegistry.getService(serviceName);
			List<RestParameter> parameterEntities = service.getParameters();
			RestParameters result = new RestParameters();
			result.setParameters(parameterEntities);
			return Response.ok(result).build();
		} catch (NoSuchServiceException e) {
			throw new NotFoundException("Service " + serviceName
					+ " does not exist!");
		}
	}

	@Override
	public void setParameterValue(String value,
			final AsyncResponse asyncResponse, String serviceName,
			String parameterName) {
		try {
			RestService restService = restServiceRegistry
					.getService(serviceName);
			RestParameter restParameter = restService
					.getParameter(parameterName);
			if (restParameter.isReadOnly()) {
				asyncResponse.resume(Response.status(Status.FORBIDDEN));
				return;
			}
			Map<Parameter, ValueObject> changes = restParameter
					.getChanges(value);

			for (Map.Entry<Parameter, ValueObject> entry : changes.entrySet()) {
				ResponseCallback responseCallback = new ResponseCallback(
						asyncResponse, commandResponseHandler);
				commandPublisher.publishRequest(
						ValueChangeRequest.createValueChangeRequest(
								entry.getKey(), entry.getValue()),
						responseCallback, entry.getKey().getService());
			}
		} catch (NoSuchParameterException e) {
			asyncResponse.resume(Response.status(Status.NOT_FOUND).build());
		} catch (IncompatibleValueException e) {
			asyncResponse.resume(Response.status(Status.BAD_REQUEST)
					.entity(e.getMessage()).build());
		} catch (NoSuchServiceException e) {
			asyncResponse.resume(Response.status(Status.NOT_FOUND).build());
		}

	}

	@Override
	public Response getParameterValue(String serviceName, String parameterName) {
		try {
			RestService restService = restServiceRegistry
					.getService(serviceName);
			RestParameter restParameter = restService
					.getParameter(parameterName);
			LOG.info("restService: " + restService + ", restParameter: "
					+ restParameter);
			Map<Parameter, ValueObject> referencedParameterValues = new HashMap<Parameter, ValueObject>();
			LOG.info("Referenced parameters for " + restParameter + " are :");
			for (Parameter referencedParameter : restParameter
					.getReferencedParameters())
				LOG.info(referencedParameter.toString());
			for (Parameter referencedParameter : restParameter
					.getReferencedParameters()) {
				referencedParameterValues.put(referencedParameter,
						parameterValueRegistry.getValue(referencedParameter));
			}
			return Response.ok(
					restParameter.createValue(referencedParameterValues))
					.build();
		} catch (UninitalizedValueException e) {
			throw new NotFoundException();
		} catch (NoSuchParameterException e) {
			throw new NotFoundException();
		} catch (NoSuchServiceException e) {
			throw new NotFoundException();
		}
	}

	@Override
	public Response getPreferenceValue(SecurityContext securityContext,
			String serviceName, String parameterName) {
		User user = getUser(securityContext);
		try {
			Preference preference = preferencesService.getPreference(
					serviceName, parameterName);
			UserPreference userPreference = preferencesService
					.getUserPreference(user, preference);
			return Response.ok(userPreference.toString()).build();
		} catch (NoSuchUserPreferenceException e) {
			return Response.status(Status.NOT_FOUND).build();
		} catch (NoSuchPreferenceException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@Override
	public Response setPreferenceValue(String value,
			@Context SecurityContext securityContext, String serviceName,
			String parameterName) {
		User user = getUser(securityContext);
		try {
			Preference preference = preferencesService.getPreference(
					serviceName, parameterName);
			Parameter parameter = preference.getParameter();
			ValueObject valueType = parameter.createValueObject(value);
			preferencesService.savePreference(preference, user, valueType);
			return Response.ok().build();
		} catch (NoSuchPreferenceException e) {
			return Response.status(Status.NOT_FOUND).build();
		} catch (IncompatibleValueException e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	private User getUser(SecurityContext securityContext) {
		Principal principal = securityContext.getUserPrincipal();
		String userName = "";
		if (principal != null) {
			userName = principal.getName();
		} else {
			userName = "default";
		}
		try {
			LOG.info("Loading the user {}", userName);
			return userService.getUser(userName);
		} catch (NoSuchUserException e) {
			return null;
		}
	}

	@Override
	public Response loadPreferences(SecurityContext securityContext) {
		Principal principal = securityContext.getUserPrincipal();
		try {
			userService.login(principal.getName(),
					((User) principal).getPassword());
			return Response.ok().build();
		} catch (BadCredentialsException e) {
			return Response.status(Status.FORBIDDEN).build();
		} catch (NoSuchUserException e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@Override
	public Response getService(String serviceName) {
		try {
			RestService service = restServiceRegistry.getService(serviceName);
			return Response.ok().entity(service).build();
		} catch (NoSuchServiceException e) {
			throw new NotFoundException();
		}
	}
}
