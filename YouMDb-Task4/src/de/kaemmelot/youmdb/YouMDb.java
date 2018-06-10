package de.kaemmelot.youmdb;

import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import de.kaemmelot.youmdb.gui.YoumdbWindow;

public class YouMDb extends YoumdbWindow {
	
	//#if MySQL
//@	@Parameter(names="--host", description="MySQL host. Default: localhost")
//@	private String host = "localhost";
//@	
//@	@Parameter(names="--port", description="MySQL port. Default: 3306")
//@	private Integer port = 3306;
//@	
//@	@Parameter(names={"--database", "-d"}, description="MySQL database name. Default: YouMDb")
//@	private String database = "YouMDb";
//@	
//@	@Parameter(names={"--user", "--username", "-u"}, description="MySQL username for authentication")
//@	private String user;
//@	
//@	@Parameter(names={"--password", "-p"}, description="MySQL password for authentication", password = true)
//@	private String password;
	//#elif SQLite
	@Parameter(names={"--database", "-d"}, description="SQLite database file. Default: ./YouM.db")
	private String database = "./YouM.db";
	//#endif
	
	@Parameter(names = "--help", help = true)
	private boolean help;
	
	public static void main(String[] args) {
		YouMDb me = new YouMDb();
		
		JCommander com = JCommander.newBuilder()
			.addObject(me)
			.build();
		com.parse(args);
		
		//#if MySQL
//@		if (me.user == null)
//@			com.usage();
		//#endif
		
		MovieDatabase.configure(
				//#if MySQL
//@				me.host, me.port,
				//#endif
				//#if MySQL || SQLite
				me.database
				//#endif
				//#if MySQL
//@				, me.user, me.password
				//#endif
				);
		
		try {
			me.startup();
			me.run();
		} finally {
			me.shutdown();
		}
	}

	private MovieDatabase md = null;
	
	private void startup() {
		md = MovieDatabase.getInstance();
		String version = md.getDatabaseVersion();
		System.out.printf("Connected to database: %s%n", version);
		open();
	}
	
	private void shutdown() {
		SWTResourceManager.dispose();
		if (md != null) {
			System.out.println("Shutting down database.");
			md.dispose();
		}
	}

	private void run() {
		// wait until window is closed
		Display display = Display.getDefault();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
