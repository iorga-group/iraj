package com.iorga.irajblank.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.hibernate.cfg.Environment;
import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.internal.PersistenceXmlParser;

@ApplicationScoped
public class EntityManagerFactoryProducer {

	@Produces @ApplicationScoped
	public EntityManagerFactory createEntityManagerFactory() throws NamingException {
		// workaround for https://hibernate.atlassian.net/browse/HHH-8818
		List<ParsedPersistenceXmlDescriptor> persistenceUnits = PersistenceXmlParser.locatePersistenceUnits(new HashMap<>());
		if (persistenceUnits.size() > 1) {
			throw new IllegalStateException("There is more than one persistence unit. Aborting.");
		}
		String dataSourceJndiLocation = (String) persistenceUnits.get(0).getNonJtaDataSource();
		DataSource dataSource = (DataSource) new InitialContext().lookup(dataSourceJndiLocation);
		Map<String, Object> props = new HashMap<>();
		props.put(Environment.DATASOURCE, dataSource);

		return Persistence.createEntityManagerFactory("com.iorga.irajblank", props);
	}

	public void disposeEntityManagerFactory(@Disposes final EntityManagerFactory entityManagerFactory) {
		if (entityManagerFactory.isOpen()) {
			entityManagerFactory.close();
		}
	}

	@Produces @RequestScoped
	public EntityManager createEntityManager(final EntityManagerFactory entityManagerFactory) {
		return entityManagerFactory.createEntityManager();
	}

	public void disposeEntityManager(@Disposes final EntityManager entityManager) {
		if (entityManager.isOpen()) {
			entityManager.close();
		}
	}
}
