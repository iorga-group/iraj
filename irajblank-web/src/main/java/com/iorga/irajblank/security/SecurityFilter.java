package com.iorga.irajblank.security;

import javax.inject.Inject;
import javax.servlet.annotation.WebFilter;

import com.iorga.iraj.security.AbstractSecurityWithWebServiceFilter;
import com.iorga.iraj.security.SimpleSecurityContext;
import com.iorga.irajblank.service.UserService;


@WebFilter(urlPatterns = "/api/*")
public class SecurityFilter extends AbstractSecurityWithWebServiceFilter<SimpleSecurityContext> {
	@Inject
	private UserService userService;

	@Override
	protected SimpleSecurityContext findSecurityContext(final String login) {
		// Here we will use the login as the accessKeyId and the encrypted password as the secretAccessKey
		return new SimpleSecurityContext(login, userService.findPasswordForLogin(login));
	}
}
