/*
 * Copyright (C) 2013 Iorga Group
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see [http://www.gnu.org/licenses/].
 */
package com.iorga.iraj.security;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.StreamingOutput;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

import com.iorga.iraj.security.BypassSecurityTokenStore.TokenContext;

@ApplicationScoped
@Path(SecurityWebService.SECURITY_WEB_SERVICE_PATH_PREFIX)
public class SecurityWebService {
	public static final String SECURITY_WEB_SERVICE_PATH_PREFIX = "/security";
	public static final String GET_TIME_METHOD_PATH = "/getTime";

	@Inject
	private BypassSecurityTokenStore bypassSecurityTokenStore;

	@GET
	@Path(GET_TIME_METHOD_PATH)
	public long getTime() {
		return new Date().getTime();
	}

	@GET
	@Path("/createBypassSecurityToken")
	public StreamingOutput createBypassSecurityToken(@Context final HttpServletRequest request) throws ExecutionException {
		// return the current token for the principal or build a new one
		// will use the secret access key as a shared secret to give out the token to the client
		// that token will be allowed to be used only once by the AbstractSecurityFilter
		final SecurityContext securityContext = (SecurityContext)request.getUserPrincipal();
		final String principalName = securityContext.getName();
		final TokenContext tokenContext = bypassSecurityTokenStore.getOrCreateTokenContextForPrincipalName(principalName);

		final String salt = AesUtil.generateSalt();
		final String iv = AesUtil.generateIV();
		final int keySize = 128;
		final int iterationCount = 50;

		final AesUtil util = new AesUtil(keySize, iterationCount);
		final String encryptedToken = util.encrypt(salt, iv, securityContext.getSecretAccessKey(), tokenContext.getToken());

		return new StreamingOutput() {
			@Override
			public void write(final OutputStream output) throws IOException, WebApplicationException {
				final JsonGenerator generator = new JsonFactory().createJsonGenerator(output);
				generator.writeStartObject();
				generator.writeStringField("salt", salt);
				generator.writeStringField("iv", iv);
				generator.writeNumberField("keySize", keySize);
				generator.writeNumberField("iterationCount", iterationCount);
				generator.writeStringField("encryptedToken", encryptedToken);
				generator.writeEndObject();
				generator.flush();
			}
		};
	}
}
