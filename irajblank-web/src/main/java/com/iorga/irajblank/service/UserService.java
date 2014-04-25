package com.iorga.irajblank.service;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.apache.commons.codec.digest.DigestUtils;

import com.iorga.iraj.exception.FunctionalException;
import com.iorga.iraj.message.MessagesBuilder;
import com.iorga.iraj.service.JPAEntityService;
import com.iorga.iraj.service.SearchQuery;
import com.iorga.irajblank.model.entity.QUser;
import com.iorga.irajblank.model.entity.User;
import com.iorga.irajblank.model.service.UserSaveRequest;
import com.iorga.irajblank.model.service.UserSearchRequest;
import com.mysema.query.jpa.impl.JPAQuery;

public class UserService extends JPAEntityService<User, Integer> {

	@Inject
	private ProfileService profileService;

	public User findWithLoginAndPassword(final String login, final String password) {
		final String digestedPassword = digestPassword(login, password);

		try {
			return getEntityManager().createNamedQuery(User.FIND_ACTIVE_USER_FOR_LOGIN_AND_PASSWORD, User.class)
				.setParameter("login", login)
				.setParameter("password", digestedPassword)
				.getSingleResult();
		} catch (final NoResultException e) {
			return null;
		} catch (final NonUniqueResultException e) {
			return null;
		}
	}

	public static String digestPassword(final String login, final String clearPassword) {
		return clearPassword != null ? DigestUtils.sha1Hex(login+"|"+clearPassword) : null;	// En cas de null, DigestUtils envoie un NPE
	}

	public User changeBlActi(final User user, final boolean activate) {
		user.setActive(activate);
		return update(user);
	}

	@Override
	public void create(final User entity) throws FunctionalException {
		if (entity.getActive() == null) {
			entity.setActive(true);
		}
		if (checkLoginExists(entity.getLogin())) {
			throw new FunctionalException(new MessagesBuilder().appendFieldError("Un utilisateur avec ce login existe déjà", "login").build());
		}
		super.create(entity);
	}

	public void create(final User user, final String clearPassword) {
		user.setPassword(digestPassword(user.getLogin(), clearPassword));
		create(user);
	}

	public void update(final User user, final String clearPassword) {
		user.setPassword(digestPassword(user.getLogin(), clearPassword));
		update(user);
	}

	public Boolean checkLoginExists(final String login) {
		try {
			getEntityManager().createNamedQuery(User.EXISTS_FOR_LOGIN)
				.setParameter("login", login)
				.getSingleResult();
		} catch (final NoResultException e) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	public void changeBlActi(final Collection<User> users, final boolean active) {
		if (users != null && users.size() > 0) {
			final int updatedUsers = getEntityManager().createNamedQuery(User.UPDATE_SET_ACTIVE_FOR_USERS)
				.setParameter("active", active)
				.setParameter("users", users)
				.executeUpdate();
			assert updatedUsers == users.size();
		}
	}

	private final SearchQuery<User, QUser, UserSearchRequest> userSearchQuery = new SearchQuery<User, QUser, UserSearchRequest>() {
		@Override
		public void configureSearchBaseQuery(QUser qUser, UserSearchRequest searchRequest, JPAQuery jpaQuery) {
			jpaQuery.from(qUser);

			if (searchRequest.getNom() != null){
				jpaQuery.where(qUser.lastName.containsIgnoreCase(searchRequest.getNom()));
			}
			if (searchRequest.getProfileId() != null){
				jpaQuery.where(qUser.profile.id.eq(searchRequest.getProfileId()));
			}
			if (searchRequest.getLogin() != null){
				jpaQuery.where(qUser.lastName.containsIgnoreCase(searchRequest.getLogin()));
			}
		}
		@Override
		public void configureSearchQuery(QUser qUser, UserSearchRequest searchRequest, JPAQuery jpaQuery) {
			jpaQuery.join(qUser.profile).fetch();
		}
	};
	public long searchCount(final UserSearchRequest userSearchRequest) {
		return userSearchQuery.searchCount(QUser.user, userSearchRequest, getEntityManager());
	}
	public List<User> search(final UserSearchRequest userSearchRequest) {
		return userSearchQuery.search(QUser.user, userSearchRequest, userSearchRequest.getSearchScope(), getEntityManager());
	}

	public Integer save(final UserSaveRequest request) throws FunctionalException {
		User user = new User();
		boolean newUser = true;
		final Integer userId = request.getUserId();
		if (userId != null && userId != 0) {
			user = this.find(userId);
			newUser = false;
		}

		user.setLogin(request.getLogin());
		user.setPassword(request.getPassword());
		user.setLastName(request.getLastName());
		user.setFirstName(request.getFirstName());
		user.setActive(request.getActive());
		user.setProfile(profileService.getReference(request.getProfileId()));

		if (newUser) {
			this.create(user);
		}

		return user.getUserId();
	}

	public String findPasswordForLogin(final String login) throws NoResultException, NonUniqueResultException {
		return (String) getEntityManager().createNamedQuery(User.FIND_PASSWORD_FOR_LOGIN)
			.setParameter("login", login)
			.getSingleResult();
	}

}
