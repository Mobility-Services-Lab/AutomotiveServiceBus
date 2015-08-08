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
package de.visiom.carpc.asb.rest.impl.security;

import java.security.Principal;
import java.util.StringTokenizer;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.jaxrs.provider.security.AuthenticationHandler;

import de.visiom.carpc.asb.userstore.UserService;
import de.visiom.carpc.asb.userstore.entities.User;
import de.visiom.carpc.asb.userstore.exceptions.NoSuchUserException;

public class AuthenticationHandlerImpl implements AuthenticationHandler {

    private static final Logger LOG = LoggerFactory
            .getLogger(AuthenticationHandlerImpl.class);

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Principal authenticate(ContainerRequestContext requestContext) {
        LOG.info("Starting the authentication handlers...");
        String requestHeader = requestContext.getHeaderString("Authorization");
        if (requestHeader == null || requestHeader.isEmpty()) {
            LOG.info("No authorization header was found!");
            return null;
        }
        String encodedUserPassword = requestHeader.replaceFirst("Basic ", "");
        String usernameAndPassword = DatatypeConverter
                .printBase64Binary(encodedUserPassword.getBytes());
        final StringTokenizer tokenizer = new StringTokenizer(
                usernameAndPassword, ":");
        final String username = tokenizer.nextToken();
        final String password = tokenizer.nextToken();
        try {
            User principal = userService.getUser(username);
            if (principal.getPassword().equals(password)) {
                LOG.info("Successfully authenticated {}", username);
                return principal;
            } else {
                LOG.info("Wrong password given for user {}", username);
                return null;
            }
        } catch (NoSuchUserException e) {
            LOG.error("An exception occured during authentication:", e);
            return null;
        }
    }

    @Override
    public String getAuthenticationScheme() {
        return SecurityContext.BASIC_AUTH;
    }

}
