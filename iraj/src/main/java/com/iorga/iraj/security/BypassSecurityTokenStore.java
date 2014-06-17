package com.iorga.iraj.security;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.enterprise.context.ApplicationScoped;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@ApplicationScoped
public class BypassSecurityTokenStore {

	private final Cache<String, TokenContext> tokensCache = CacheBuilder.newBuilder()
		.expireAfterWrite(AbstractSecurityFilter.TIME_SHIFT_ALLOWED_DURATION, AbstractSecurityFilter.TIME_SHIFT_ALLOWED_TIME_UNIT)
		.build();

	private final Cache<String, String> principalToTokenMapping = CacheBuilder.newBuilder()
		.expireAfterWrite(AbstractSecurityFilter.TIME_SHIFT_ALLOWED_DURATION, AbstractSecurityFilter.TIME_SHIFT_ALLOWED_TIME_UNIT)
		.build();

	public static class TokenContext {
		private final String token;
		private final String principalName;

		public TokenContext(final String token, final String principalName) {
			this.token = token;
			this.principalName = principalName;
		}

		String getToken() {
			return token;
		}
		String getPrincipalName() {
			return principalName;
		}
	}

	TokenContext getOrCreateTokenContextForPrincipalName(final String principalName) throws ExecutionException {
		return tokensCache.getIfPresent(
			principalToTokenMapping.get(principalName, new Callable<String>() {
				@Override
				public String call() throws Exception {
					// no token for that principal, let's create a new one
					final String token = SecurityUtils.generateSecretAccessKey();
					final TokenContext tokenContext = new TokenContext(token, principalName);
					tokensCache.put(token, tokenContext);
					return token;
				}
		}));
	}

	TokenContext removeToken(final String token) {
		final TokenContext tokenContext = tokensCache.getIfPresent(token);
		if (tokenContext != null) {
			// remove that token
			principalToTokenMapping.invalidate(tokenContext.principalName);
			tokensCache.invalidate(token);
		}
		return tokenContext;
	}
}
