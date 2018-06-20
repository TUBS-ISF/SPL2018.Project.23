package de.kaemmelot.youmdb;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;

import de.kaemmelot.youmdb.gui.YoumdbWindow;
import de.kaemmelot.youmdb.plugins.DatabasePlugin;
import de.kaemmelot.youmdb.plugins.DefaultGenresPlugin;
import de.kaemmelot.youmdb.plugins.GenrePlugin;
import de.kaemmelot.youmdb.plugins.MySQLPlugin;
import de.kaemmelot.youmdb.plugins.PosterPlugin;
import de.kaemmelot.youmdb.plugins.RatingPlugin;
import de.kaemmelot.youmdb.plugins.SQLitePlugin;

public class YouMDb extends YoumdbWindow {
	
	public static void main(String[] args) {
		loadPlugins(args);
		YouMDb me = new YouMDb();
		try {
			me.startup();
			me.run();
		} finally {
			me.shutdown();
		}
	}
	
	private void startup() {
		for (Plugin p: plugins) {
			if (p instanceof YouMDbPlugin)
				((YouMDbPlugin) p).startup();
		}
		open(); // init window
	}
	
	private void shutdown() {
		for (Plugin p: plugins) {
			if (p instanceof YouMDbPlugin)
				((YouMDbPlugin) p).shutdown();
		}
		SWTResourceManager.dispose();
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
	
	private static List<Plugin> plugins = new ArrayList<Plugin>();
	
	public static Plugin[] GetPlugins() {
		return plugins.toArray(new Plugin[plugins.size()]);
	}
	
	@Override
	protected void addOverviewTabs() {
		for (Plugin p: plugins)
			p.registerOverviewTabs(this);
	}
	
	private static void loadPlugins(String[] args) {
		Plugin p;
		
		p = new DatabasePlugin();
		plugins.add(p);
		p.registerPlugin(args);
		
		/*
		p = new MySQLPlugin();
		plugins.add(p);
		p.registerPlugin(args);
		*/
		
		p = new SQLitePlugin();
		plugins.add(p);
		p.registerPlugin(args);
		
		p = new PosterPlugin();
		plugins.add(p);
		p.registerPlugin(args);
		
		p = new RatingPlugin();
		plugins.add(p);
		p.registerPlugin(args);
		
		p = new GenrePlugin();
		plugins.add(p);
		p.registerPlugin(args);

		p = new DefaultGenresPlugin();
		plugins.add(p);
		p.registerPlugin(args);
	}
}
