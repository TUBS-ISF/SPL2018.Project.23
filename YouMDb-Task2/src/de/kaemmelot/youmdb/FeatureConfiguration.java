package de.kaemmelot.youmdb;

public class FeatureConfiguration {
	private static FeatureConfiguration instance = null;
	
	private boolean posters;
	
	public FeatureConfiguration(boolean posters) {
		if (instance != null)
			throw new IllegalStateException("Cannot create a second FeatureConfiguration");
		this.posters = posters;
		instance = this;
	}
	
	public static FeatureConfiguration GetInstance() {
		return instance;
	}
	
	public boolean UsePosters() {
		return posters;
	}
}
