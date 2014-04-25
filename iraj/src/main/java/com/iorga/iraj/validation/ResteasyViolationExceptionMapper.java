/*
 * Copyright (C) 2014 Iorga Group
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
package com.iorga.iraj.validation;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.jboss.resteasy.api.validation.ConstraintType.Type;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;

import com.iorga.iraj.util.JaxRsUtils;

@Provider
public class ResteasyViolationExceptionMapper implements ExceptionMapper<ResteasyViolationException> {

	private static final JsonFactory JSON_FACTORY = new JsonFactory();

	@Override
	public Response toResponse(final ResteasyViolationException exception) {
		return JaxRsUtils.throwableToIrajResponse(new StreamingOutput() {

			@Override
			public void write(OutputStream outputStream) throws IOException, WebApplicationException {
				JsonGenerator jsonGenerator = JSON_FACTORY.createJsonGenerator(outputStream);

				jsonGenerator.writeStartObject();
				jsonGenerator.writeArrayFieldStart("irajFieldMessages");

				List<ResteasyConstraintViolation> parameterViolations = exception.getParameterViolations();
				for (ResteasyConstraintViolation violation : parameterViolations) {

					jsonGenerator.writeStartObject();
					jsonGenerator.writeStringField("type", "error");
					jsonGenerator.writeStringField("message", violation.getMessage());

					if (Type.PARAMETER.equals(violation.getConstraintType())) {
						jsonGenerator.writeArrayFieldStart("propertyPath");

						String[] pathElems = violation.getPath().split("\\.");
						int index = 1;
						for (String pathElem : pathElems) {
							if (index > 2) {
								jsonGenerator.writeString(pathElem);
							} else {
								index++;
							}
						}
					}
					jsonGenerator.writeEndArray();
					jsonGenerator.writeEndObject();
				}
				jsonGenerator.writeEndArray();
				jsonGenerator.writeEndObject();
				jsonGenerator.flush();
			}
		}, exception);
	}
}
