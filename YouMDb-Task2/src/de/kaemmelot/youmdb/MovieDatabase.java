package de.kaemmelot.youmdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import de.kaemmelot.youmdb.models.Movie;

public class MovieDatabase {
	private static Map<String, String> configuration = null;
	private static MovieDatabase instance = null;
	
	public static MovieDatabase GetInstance() {
		if (instance == null)
			instance = new MovieDatabase();
		return instance;
	}
	
	public enum DatabaseDriver {
		SQLite;
	}
	
	/**
	 * Configure the {@see MovieDatabase} before first use.
	 * @param driver The driver to use
	 * @param host The host to connect to
	 * @param port The port to connect to
	 * @param name The name of the database
	 * @param user The username to login
	 * @param password The password to login
	 */
	public static void Configure(DatabaseDriver driver, String host, Integer port, String name, String user, String password) {
		if (instance != null)
			throw new UnsupportedOperationException("Cannot reconfigure database after first use");
		
		// put all settings in a map
		Map<String, String> newConf = new HashMap<String, String>();
		if (driver == DatabaseDriver.SQLite) {
			newConf.put(Environment.DRIVER,  "org.sqlite.JDBC");
			newConf.put(Environment.DIALECT, "org.hibernate.dialect.SQLiteDialect");
			newConf.put(Environment.URL,     "jdbc:sqlite:" + name);
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
	
	private MovieDatabase() {
		if (configuration == null)
			throw new IllegalStateException("Set database configuration first");

		// build new factory from global conf
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySettings(configuration);
		StandardServiceRegistry registry = builder.build();
		MetadataSources sources = new MetadataSources(registry);
		sources.addAnnotatedClass(Movie.class);
		Metadata metadata = sources.getMetadataBuilder().build();
		sessionFactory = metadata.getSessionFactoryBuilder().build();
		em = sessionFactory.createEntityManager();
		//em.close();
		//StandardServiceRegistryBuilder.destroy(registry);
	}
	
	public void StartTransaction() {
		em.getTransaction().begin();
	}
	
	public void EndTransaction() {
		em.getTransaction().commit();
	}
	
	public void AbortTransaction() {
		em.getTransaction().rollback();
	}
	
	private static String VERSION_QUERY;
	public String GetDatabaseVersion() {
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
	 * Get all movies within the database.
	 * @return All persistent movies
	 */
	public List<Movie> GetMovies() {
		CriteriaQuery<Movie> query = em.getCriteriaBuilder().createQuery(Movie.class);
		query.select(query.from(Movie.class));
		return em.createQuery(query).getResultList();
	}
	
	/**
	 * Add a movie to the database.
	 * Requires an active transaction!
	 * @param movie Movie to persist
	 */
	public void AddMovie(Movie movie) {
		em.persist(movie);
	}
	
	/**
	 * Remove the movie from the database.
	 * Requires an active transaction!
	 * @param movie Movie to remove
	 */
	public void RemoveMovie(Movie movie) {
		em.remove(movie);
	}
}
