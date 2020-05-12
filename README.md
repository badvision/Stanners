# Stanners
Combines MSR tools for finding correlations in sentiment and code bug proneness, works with Percival, SentiStrength-SE, and SZZUnleashed.

## Project structure

Stanners is organized as an [Apache Maven](https://maven.apache.org/) project.  This simplifies dependency management, code compilation, and program execution.

The source is organized such that the primary application and its controller are in the top-level package (arguably not a suitable place, but it's also a small project.)  Various data processing steps are managed as individual classes under the process package, and finally all the models representing the data going in and out of the program are in the model package.

The user interface itself is described in the resources folder as app.fxml and was created using [the Scene Builder application provided by Gluon](https://gluonhq.com/products/scene-builder/).

## Building the project

From inside the project folder, invoke `mvn build`.

## Running Stanners

The jar file produced by the build is not a self-executing jar file.  To execute Stanners, use `mvn javafx:run` which invokes the [JavaFX Maven plugin](https://github.com/openjfx/javafx-maven-plugin) to start the program and prepare the java classpath.

## Data sources

The data sources come from three other research tools:

* [Perceval](https://github.com/chaoss/grimoirelab-perceval) processes the Git commit log and produces a more navigable JSON representation.

* [SentiStrength-SE](https://laser.cs.uno.edu/Projects/Projects.html) analyzes the sentiment of commit messages and produces positive and negative sentiment scores.

* [SSZUnleashed](https://github.com/wogscpar/SZZUnleashed) analyzes the Jira issues and commit history to deduce which commits were fix-inducing, bug-inducing, or fix-contributing.


The goal of Stanners is to combine the data of these three programs (Perceval provides commit messages, which are prepared for SentiStrength-SE; SZZUnleashed provides commit classification which is then summarized and combined with SentiStrength-SE and Perceval results.)

## Example
The example folder contains a full list of commits from Jenkins, prepared by Perceval.  

Note that the Perceval json file was compressed in order to be uploaded to Github and prior to using it you must first decompress the file.

The selected commits (those which actually contain java classes) were provided to and analyzed by SentiStrength-SE.  The SZZUnlimited processing output is also provided in the results folder, but it is worth noting that SZZUnlimited froze before fully outputting all results so only partial results were available for analysis.
The collated output is provided in the Final results folder for further inspection.  The Excel spreadsheet filtered out the data such that any rows not analyzed by SentiStrength-SE were not reflected in the final output.

## What's with the name?

<img src="https://vignette.wikia.nocookie.net/villains/images/e/e8/Angry_Kid_Transparent.png/revision/latest?cb=20170429122503" alt="Angry Kid, Copyright Aardman Animations" style="zoom:25%;" />

Stanners is the name of the titular character of the Aardman Animations series "Angry Kid."  He's frequently getting himself into trouble due to his uncontrollable and puerile actions, usually with flagrant disregard for the damage caused in his wake.

Since my initial hypothesis was to look for a correlation between negative sentiment and bug-inducing behavior, it seemed a fitting enough name.  Also because "Utility to collate data from three other programs" doesn't really roll off the tongue as nicely as "Stanners."
