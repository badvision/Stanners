package org.badvision.stanners.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import lombok.Data;

/**
 * Represent shared objects used in different modules of the app such as commits
 */
@Data
public class AppState {
    String currentDirectory;
    Map<String, Commit> allCommits = new HashMap<>();
    SzzResults szzResults = new SzzResults();
    Map<String, Sentiment> sentimentScores = new HashMap();
    Map<String, CommitStatistics> commitStats = new HashMap();

    public void updateCurrentDirectory(File selected) {
        if (selected != null) {
            currentDirectory = selected.getParentFile().getAbsolutePath();
        }
    }

    public void setToCurrentDirectory(FileChooser chooser) {
        if (currentDirectory != null) {
            chooser.setInitialDirectory(new File(currentDirectory));
        }
    }

    public void setToCurrentDirectory(DirectoryChooser chooser) {
        if (currentDirectory != null) {
            chooser.setInitialDirectory(new File(currentDirectory));
        }
    }

    public void resetState() {
        allCommits.clear();
        szzResults.clear();
        sentimentScores.clear();
        commitStats.clear();
    }
}
