package com.iorga.irajblank.ws.administration;

import java.util.HashMap;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.StreamingOutput;

import com.iorga.iraj.annotation.ContextParam;
import com.iorga.iraj.annotation.ContextParams;
import com.iorga.iraj.annotation.ContextPath;
import com.iorga.iraj.json.JsonWriter;
import com.iorga.irajblank.model.entity.Profile;
import com.iorga.irajblank.model.entity.User;
import com.iorga.irajblank.model.service.UserSearchRequest;
import com.iorga.irajblank.service.ProfileService;
import com.iorga.irajblank.service.UserService;

@Path("/administration/userSearch")
@ApplicationScoped
public class UserSearchWS {
	@Inject
	private UserService userService;

	@Inject
	private ProfileService profileService;

	@Inject
	private JsonWriter jsonWriter;

	@GET
	@Path("/findAll")
	public List<User> findAll() {
		return userService.findAll();
	}

	@ContextParams({
		@ContextParam(name = "profileList", value = List.class, parameterizedArguments = Profile.class)
	})
	public static class InitTemplate {
		List<ProfileTemplate> profileList;

		@ContextParam(Profile.class)
		public static class ProfileTemplate {
			Integer id;
			String label;
		}
	}
	@GET
	@Path("/init")
	public StreamingOutput init() {
		final List<Profile> profileList = profileService.findAll();
		final HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("profileList", profileList);
		return jsonWriter.writeWithTemplate(InitTemplate.class, context);
	}

	@ContextParam(User.class)
	public static class UserTemplate {
		Integer userId;
		String lastName;
		String firstName;
		@ContextPath("profile.label")
		String profileLabel;
	}
	@POST
	@Path("/search")
	public StreamingOutput search(final UserSearchRequest userSearchRequest) {
		return jsonWriter.writeIterableWithTemplate(UserTemplate.class, userService.search(userSearchRequest));
	}
	@POST
	@Path("/count")
	public long count(final UserSearchRequest userSearchRequest) {
		return userService.searchCount(userSearchRequest);
	}
}
