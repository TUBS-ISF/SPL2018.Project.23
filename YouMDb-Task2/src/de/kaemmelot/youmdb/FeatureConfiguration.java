package de.kaemmelot.youmdb;

public class FeatureConfiguration {
	private static FeatureConfiguration instance = null;
	
	private final boolean posters;
	private final boolean ratings;
	
	public FeatureConfiguration(boolean posters, boolean ratings) {
		if (instance != null)
			throw new IllegalStateException("Cannot create a second FeatureConfiguration");
		this.posters = posters;
		this.ratings = ratings;
		instance = this;
	}
	
	public static FeatureConfiguration getInstance() {
		return instance;
	}
	
	public boolean usePosters() {
		return posters;
	}
	
	public boolean useRatings() {
		return ratings;
	}
}
