package de.kaemmelot.youmdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import de.kaemmelot.youmdb.models.Movie;
import de.kaemmelot.youmdb.models.MovieAttribute;

public class Database {
	private static Map<String, String> configuration = null;
	private static Database instance = null;
	
	public static Database getInstance() {
		if (instance == null)
			instance = new Database();
		return instance;
	}
	
	public static void setConfiguration(String[] args) {
		if (instance != null)
			throw new UnsupportedOperationException("Cannot reconfigure database after first use");
		if (configuration == null)
			throw new UnsupportedOperationException("There should be a database configuration");
		//configuration.put(Environment.SHOW_SQL, "true");
	}
	
	public static List<Class<?>> getClasses() {
		List<Class<?>> result = new ArrayList<Class<?>>();
		result.add(Movie.class);
		result.add(MovieAttribute.class);
		return result;
	}
	
	private SessionFactory sessionFactory;
	private EntityManager em;
	private StandardServiceRegistry registry;
	private boolean isTransaction = false;
	
	private Database() {
		if (configuration == null)
			throw new IllegalStateException("Set database configuration first");

		// build new factory from global conf
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySettings(configuration);
		registry = builder.build();
		try {
			MetadataSources sources = new MetadataSources(registry);
			for (Class<?> cl : getClasses())
				sources.addAnnotatedClass(cl);
			Metadata metadata = sources.getMetadataBuilder().build();
			sessionFactory = metadata.getSessionFactoryBuilder().build();
			em = sessionFactory.createEntityManager();
		} catch (HibernateException e) {
			StandardServiceRegistryBuilder.destroy(registry);
			throw e;
		}
	}
	
	/**
	 * Use this before adding, updating or removing any persistent object.
	 * @return this
	 */
	public Database startTransaction() {
		em.getTransaction().begin();
		isTransaction = true;
		return this;
	}
	
	/**
	 * Use this to actually save changes made since {@link #startTransaction}.
	 * @return this
	 */
	public Database endTransaction() {
		em.getTransaction().commit();
		isTransaction = false;
		return this;
	}
	
	/**
	 * Use this to revert any changes made since {@link #startTransaction}.
	 * @return this
	 */
	public Database abortTransaction() {
		em.getTransaction().rollback();
		isTransaction = false;
		return this;
	}
	
	/**
	 * Returns if an transaction is currently running.
	 */
	public boolean isWithinTransaction() {
		return isTransaction;
	}
	
	/**
	 * Get the database and version as String.
	 */
	public String getDatabaseVersion() {
		Session session = null;
		String result;
		try {
			session = sessionFactory.openSession();
			result = (String) session.createNativeQuery("select version();").getSingleResult();
		} finally {
			if (session != null) session.close();
		}
		return result;
	}
	
	/**
	 * Close all connections.
	 */
	public void dispose() {
		em.close();
		StandardServiceRegistryBuilder.destroy(registry);
	}
	
	/**
	 * Get all elements of class T within the database.
	 * @param cl Class of element
	 * @return All persistent elements
	 */
	public <T> List<T> getAll(Class<T> cl) {
		CriteriaQuery<T> query = em.getCriteriaBuilder().createQuery(cl);
		query.select(query.from(cl));
		return em.createQuery(query).getResultList();
	}
	
	/**
	 * Add an element to the database.
	 * Requires an active transaction!
	 * @param entity Element to persist
	 * @return this
	 */
	public <T> Database add(T entity) {
		em.persist(entity);
		return this;
	}
	
	/**
	 * Remove the element from the database.
	 * Requires an active transaction!
	 * @param entity Element to remove
	 * @return this
	 */
	public <T> Database remove(T entity) {
		em.remove(entity);
		return this;
	}
	
	/**
	 * Refresh an element and all its attributes by overriding all local changes.
	 * @param entity the element to refresh
	 * @return this
	 */
	public <T> Database refresh(T entity) {
		em.refresh(entity);
		return this;
	}
	
	/**
	 * Get element by its property 'name'.
	 * @param cl Class of element
	 * @param name The property name of the element to find
	 * @return The element if found, null otherwise
	 */
	public <T> T getByName(Class<T> cl, String name) {
		CriteriaQuery<T> query = em.getCriteriaBuilder().createQuery(cl);
		Root<T> root = query.from(cl);
		query.select(root)
			.where(em.getCriteriaBuilder().equal(root.get("name"), name));
		try {
			return em.createQuery(query).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
}
