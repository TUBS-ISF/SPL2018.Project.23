package de.kaemmelot.youmdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import de.kaemmelot.youmdb.models.ImageAttribute;
import de.kaemmelot.youmdb.models.Movie;
import de.kaemmelot.youmdb.models.MovieAttribute;
import de.kaemmelot.youmdb.models.RatingAttribute;

public class MovieDatabase {
	private static Map<String, String> configuration = null;
	private static MovieDatabase instance = null;
	
	public static MovieDatabase getInstance() {
		if (instance == null)
			instance = new MovieDatabase();
		return instance;
	}
	
	public enum DatabaseDriver {
		SQLite;
	}
	
	/**
	 * Configure the {@link MovieDatabase} before first use.
	 * @param driver The driver to use
	 * @param host The host to connect to
	 * @param port The port to connect to
	 * @param name The name of the database
	 * @param user The username to login
	 * @param password The password to login
	 */
	public static void configure(DatabaseDriver driver, String host, Integer port, String name, String user, String password) {
		if (instance != null)
			throw new UnsupportedOperationException("Cannot reconfigure database after first use");
		
		// put all settings in a map
		Map<String, String> newConf = new HashMap<String, String>();
		if (driver == DatabaseDriver.SQLite) {
			// https://docs.jboss.org/hibernate/orm/5.0/manual/en-US/html/ch03.html
			newConf.put(Environment.DRIVER,     "org.sqlite.JDBC");
			newConf.put(Environment.DIALECT,    "org.hibernate.dialect.SQLiteDialect");
			newConf.put(Environment.URL,        "jdbc:sqlite:" + name);
			newConf.put(Environment.AUTOCOMMIT, "true"); // needed, since SQLite would block itself
			// host, port, user and password isn't needed
			
			VERSION_QUERY = "select 'SQLite ' || sqlite_version();";
		} else
			throw new UnsupportedOperationException("Missing implementation of used driver");
		
		newConf.put(Environment.HBM2DDL_AUTO, "update");

		//newConf.put(Environment.URL, "jdbc:TODO://" + host + ':' + port + '/' + name);
		//newConf.put(Environment.USER, user);
		//newConf.put(Environment.PASS, password);
		// VERSION_QUERY = "select version();";
		
		configuration = newConf; // save as global conf
	}
	
	private SessionFactory sessionFactory;
	private EntityManager em;
	private StandardServiceRegistry registry;
	
	/**
	 * Register all classes used by this application.
	 */
	private void registerClasses(MetadataSources sources) {
		sources.addAnnotatedClass(Movie.class);
		sources.addAnnotatedClass(MovieAttribute.class);
		sources.addAnnotatedClass(ImageAttribute.class);
		sources.addAnnotatedClass(RatingAttribute.class);
	}
	
	private MovieDatabase() {
		if (configuration == null)
			throw new IllegalStateException("Set database configuration first");

		// build new factory from global conf
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySettings(configuration);
		registry = builder.build();
		try {
			MetadataSources sources = new MetadataSources(registry);
			registerClasses(sources);
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
	public MovieDatabase startTransaction() {
		em.getTransaction().begin();
		return this;
	}
	
	/**
	 * Use this to actually save changes made since {@link #startTransaction}.
	 * @return this
	 */
	public MovieDatabase endTransaction() {
		em.getTransaction().commit();
		return this;
	}
	
	/**
	 * Use this to revert any changes made since {@link #startTransaction}.
	 * @return this
	 */
	public MovieDatabase abortTransaction() {
		em.getTransaction().rollback();
		return this;
	}
	
	private static String VERSION_QUERY;
	
	/**
	 * Get the database and version as String.
	 */
	public String getDatabaseVersion() {
		Session session = null;
		String result;
		try {
			session = sessionFactory.openSession();
			result = (String) session.createNativeQuery(VERSION_QUERY).getSingleResult();
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
	 * Get all movies within the database.
	 * @return All persistent movies
	 */
	public List<Movie> getMovies() {
		CriteriaQuery<Movie> query = em.getCriteriaBuilder().createQuery(Movie.class);
		query.select(query.from(Movie.class));
		return em.createQuery(query).getResultList();
	}
	
	/**
	 * Add a movie to the database.
	 * Requires an active transaction!
	 * @param movie Movie to persist
	 * @return this
	 */
	public MovieDatabase addMovie(Movie movie) {
		em.persist(movie);
		return this;
	}
	
	/**
	 * Remove the movie from the database.
	 * Requires an active transaction!
	 * @param movie Movie to remove
	 * @return this
	 */
	public MovieDatabase removeMovie(Movie movie) {
		em.remove(movie);
		return this;
	}
	
	/**
	 * Add a {@link MovieAttribute} to the database.
	 * Requires an active transaction!
	 * @param movieAttribute {@link MovieAttribute} to persist
	 * @return this
	 */
	public MovieDatabase addMovieAttribute(MovieAttribute movieAttribute) {
		em.persist(movieAttribute);
		return this;
	}
	
	/**
	 * Refresh a movie and all its attributes by overriding all local changes.
	 * @param movie the movie to refresh
	 * @return this
	 */
	public MovieDatabase refreshMovie(Movie movie) {
		em.refresh(movie);
		for (MovieAttribute ma : movie.getAttributes())
			em.refresh(ma);
		return this;
	}
}
