package com.iorga.iraj.security;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.BooleanUtils;

import com.iorga.iraj.annotation.AllowSecurityBypassedByToken;
import com.iorga.iraj.message.MessagesBuilder;

@Provider
public class BypassSecurityContainerRequestFilter implements ContainerRequestFilter {

	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		Boolean securityBypassed = (Boolean) requestContext.getProperty(SecurityUtils.SECURITY_BYPASSED_BY_TOKEN_ATTRIBUTE_NAME);

		if (BooleanUtils.isTrue(securityBypassed)) {
			// the security has been bypassed by token, must check if target method allows it
			final Method resourceMethod = resourceInfo.getResourceMethod();
			AllowSecurityBypassedByToken allowed = resourceMethod.getAnnotation(AllowSecurityBypassedByToken.class);
			if (allowed == null) {
				allowed = resourceMethod.getClass().getAnnotation(AllowSecurityBypassedByToken.class);
			}
			if (allowed == null) {
				requestContext.abortWith(
						Response.status(Status.UNAUTHORIZED)
						.entity(new MessagesBuilder().appendError("Method unauthorized without Authentication (@AllowSecurityBypassedByToken not set on target method)").build().toStreamingOutput()).build());
			} else {
				// allowed, nothing to do
			}
		}
	}

}
