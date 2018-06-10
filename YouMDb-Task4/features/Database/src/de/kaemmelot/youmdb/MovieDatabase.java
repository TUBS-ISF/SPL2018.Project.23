package de.kaemmelot.youmdb;

import java.util.HashMap;
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

import de.kaemmelot.youmdb.models.Genre;
import de.kaemmelot.youmdb.models.Movie;

public class MovieDatabase {
	private static Map<String, String> configuration = null;
	private static MovieDatabase instance = null;
	
	public static MovieDatabase getInstance() {
		if (instance == null)
			instance = new MovieDatabase();
		return instance;
	}
	
	/**
	 * Configure the {@link MovieDatabase} before first use.
	 * @param host The host to connect to
	 * @param port The port to connect to
	 * @param name The name of the database
	 * @param user The username to login
	 * @param password The password to login
	 */
	public static void configure(
			//#if MySQL
//@			String host, Integer port,
			//#endif
			String name
			//#if MySQL
//@			, String user, String password
			//#endif
			) {
		if (instance != null)
			throw new UnsupportedOperationException("Cannot reconfigure database after first use");
		
		// put all settings in a map
		Map<String, String> newConf = new HashMap<String, String>();
		//#if SQLite
   		// https://docs.jboss.org/hibernate/orm/5.0/manual/en-US/html/ch03.html
 		newConf.put(Environment.DRIVER,     "org.sqlite.JDBC");
 		newConf.put(Environment.DIALECT,    "org.hibernate.dialect.SQLiteDialect");
 		newConf.put(Environment.URL,        "jdbc:sqlite:" + name);
 		newConf.put(Environment.AUTOCOMMIT, "true"); // needed, since SQLite would block itself
 		// host, port, user and password isn't needed
 		
 		VERSION_QUERY = "select 'SQLite ' || sqlite_version();";
 		//#elif MySQL
//@		newConf.put(Environment.URL, "jdbc:mysql://" + host + ':' + port + '/' + name + "?useLegacyDatetimeCode=false&serverTimezone=UTC");
//@		newConf.put(Environment.USER, user);
//@		if (password != null)
//@			newConf.put(Environment.PASS, password);
//@		
//@		VERSION_QUERY = "select version();";
		//#else
//@		throw new UnsupportedOperationException("Missing implementation of used driver");
		//#endif

		newConf.put(Environment.HBM2DDL_AUTO, "update");
		//newConf.put(Environment.SHOW_SQL, "true");
		
		configuration = newConf; // save as global conf
	}
	
	private SessionFactory sessionFactory;
	private EntityManager em;
	private StandardServiceRegistry registry;
	
	/**
	 * Register all classes used by this application.
	 */
	private void registerClasses(MetadataSources sources) {
		// keep them here even if the feature isn't enabled
		sources.addAnnotatedClass(Movie.class);
		sources.addAnnotatedClass(Genre.class);
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
	 * Refresh a movie and all its attributes by overriding all local changes.
	 * @param movie the movie to refresh
	 * @return this
	 */
	public MovieDatabase refreshMovie(Movie movie) {
		em.refresh(movie);
		return this;
	}
	
	//#if Genres
	/**
	 * Get all genres within the database.
	 * @return All persistent genres
	 */
	public List<Genre> getGenres() {
		CriteriaQuery<Genre> query = em.getCriteriaBuilder().createQuery(Genre.class);
		query.select(query.from(Genre.class));
		return em.createQuery(query).getResultList();
	}
	
	/**
	 * Get genre by its name.
	 * @return The genre if found, null otherwise
	 */
	public Genre getGenre(String name) {
		CriteriaQuery<Genre> query = em.getCriteriaBuilder().createQuery(Genre.class);
		Root<Genre> root = query.from(Genre.class);
		query.select(root)
			.where(em.getCriteriaBuilder().equal(root.get("name"), name));
		try {
			return em.createQuery(query).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
	
	/**
	 * Add a genre to the database.
	 * Requires an active transaction!
	 * @param genre Genre to persist
	 * @return this
	 */
	public MovieDatabase addGenre(Genre genre) {
		em.persist(genre);
		return this;
	}
	
	/**
	 * Remove the genre from the database.
	 * Requires an active transaction!
	 * @param genre Genre to remove
	 * @return this
	 */
	public MovieDatabase removeGenre(Genre genre) {
		em.remove(genre);
		return this;
	}
	
	/**
	 * Refresh a genre by overriding all local changes.
	 * @param genre the genre to refresh
	 * @return this
	 */
	public MovieDatabase refreshGenre(Genre genre) {
		em.refresh(genre);
		return this;
	}
	//#endif
}
