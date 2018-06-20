package de.kaemmelot.youmdb;

public interface Plugin {
	void registerPlugin(String[] args);
	void registerOverviewTabs(YouMDb window);
}
