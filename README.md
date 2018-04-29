# YouMDb

## About
This project is educational and aims at learning about software product lines. It's part of the [lecture at the Technical University Braunschweig](https://www.tu-braunschweig.de/isf/teaching/2018s/spl) and uses Java and [FeatureIDE](https://featureide.github.io/).

It's meant to be a *simple, local and private* movie database, like [IMDb](https://www.imdb.com/) but without the community nature.

## Tasks

The project is splitted in tasks, to try out different things. In short the tasks are:

### Task 1 - Domain analysis
- Create a feature model for your domain (with descriptions)
- Create 5 typical configurations

[Task 1](YouMDb-Task1/)

### Task 2 - Runtime variability
- Implement at least 2 features
- Use runtime variability (main-method arguments) to (de)activate these features
- Mark unused features as abstract
- Test your implementation using the 5 typical configurations and document errors in a text file

[Task 2](YouMDb-Task2/)

### Task 3 - Preprocessors
- Implement more features so there are at least 4 available
- Use the [Antenna](http://antenna.sourceforge.net/wtkpreprocess.php) or [Munge](https://github.com/sonatype/munge-maven-plugin) preprocessor to configure the application
- Mark unused features as abstract
- Test your implementation using the 5 typical configurations and document errors in a text file

[Task 3](YouMDb-Task3/)

### Task 4 - Black-box framework
- Implement more features so there are at least 6 available
- Change the application to be a configurable framework (with plugins)
- Avoid creating one interface per feature or one interface for all features
- You could avoid a plugin loader by commenting out the feature loading

[Task 4](YouMDb-Task4/)

### Task 5 - Feature-oriented programming
- Implement more features so there are at least 8 available
- Change the application to use feature-oriented programming
- Use a FeatureIDE project (Composer: FeatureHouse or AHEAD)
- Test your implementation using the 5 typical configurations and document errors in a text file

[Task 5](YouMDb-Task5/)

### Task 6 - Aspect-oriented programming
- Implement more features so there are at least 10 available
- Change the application to use aspect-oriented programming
- Use a FeatureIDE project (Composer: AspectJ)
- Test your implementation using the 5 typical configurations and document errors in a text file

[Task 6](YouMDb-Task6/)
